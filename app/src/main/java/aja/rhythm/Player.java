package aja.rhythm;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.lang.Math;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.os.SystemClock.elapsedRealtime;

/**
 * Created by adrianlim on 2016-02-27.
 */
public class Player {
    public static final long MILLIS_PER_S = 1000;
    public static final int SECONDS_PER_MINUTE = 60;
    public static final long TICKS_PER_BEAT = 480;
    final int[] ALLOWABLE_NOTES = {3,4}; // allowable notes at == 1/n
    public Sounder sounder;

    private volatile int tempo;
    private CopyOnWriteArrayList<Beat> beatlist;
    private boolean isRepeat;
    private int numBeats;
    private AtomicBoolean isPlaying;
    private Thread perfThread;
    private volatile double currentTick = 0;

    private static Player instance;

    private Random rand;

    private Player(){
        this.tempo = 60;
        this.beatlist = new CopyOnWriteArrayList<Beat>();
        this.numBeats =4;
        this.isRepeat = false;
        rand = new Random();
        isPlaying = new AtomicBoolean(false);
    }

    public static Player getPlayer(){
        if (instance == null){
            instance = new Player();
            return instance;
        }
        else{
            return instance;
        }
    }

    public int getTempo(){
        return this.tempo;
    }

    public void setTempo(int newtempo){
        this.tempo = newtempo;
    }

    public Beat[] getBeatData(){
        Beat[] outputArray = new Beat[beatlist.size()];
        beatlist.toArray(outputArray);
        return outputArray;
    }

    public void addBeat(int index,int beatpattern, int subdivision){
        Beat addedbeat = new Beat(beatpattern, subdivision);
        beatlist.remove(index);
        beatlist.add(index, addedbeat);
    }

    public void addBeat(int beatpattern, int subdivision){
        Beat addedbeat = new Beat(beatpattern, subdivision);
        beatlist.add(addedbeat);
    }

    public boolean getRepeat(){
        return this.isRepeat;
    }

    public void toggleRepeat(){
        this.isRepeat = !this.isRepeat;
    }

    public int getNumBeats(){
        return this.numBeats;
    }

    public void setNumBeats(int numBeats){
        this.numBeats = numBeats;
    }

    public void start() {
        isPlaying.compareAndSet(false, true);
        //Start the player
        perfThread = new Thread(new Runnable() {
            long prevTime = elapsedRealtime();
            @Override
            public void run() {
                currentTick = 0;
                int currentBeatNum = 0;
                ArrayList<Note> notes;
                Note nextNote = null;
                Iterator<Note> it = null;
                boolean notesToPlay = false;
                boolean firstNotePassed = false;
                Beat thisBeat = beatlist.get(currentBeatNum);
                Beat nextBeat = beatlist.get(currentBeatNum  + 1);
                while(isPlaying.get() && currentBeatNum < numBeats) {
                    long newTime = elapsedRealtime();
                    currentTick += secondsToTicks((newTime - prevTime) / (double) MILLIS_PER_S);
                    currentTick %= (TICKS_PER_BEAT * numBeats);
                    prevTime = newTime;
                    // Play next note
                    if (notesToPlay) {
                        if (nextNote != null) {
                            double nextNoteTick = nextNote.getTick() + currentBeatNum * TICKS_PER_BEAT;
                            if (currentTick - nextNoteTick > 0 && currentTick - nextNoteTick < TICKS_PER_BEAT) {
                                int stress = 0;
                                // Play note
                                if (!firstNotePassed) { // Stress first note of beat
                                    if ((thisBeat.getBeatPattern() & 7) != 6) { // Unless 2nd pos note present, 3rd pos note not present
                                        stress = 2;
                                    }
                                    firstNotePassed = true;
                                } else if (nextNote.getTick() == TICKS_PER_BEAT/2) {
                                    stress = 1;
                                } else if (!it.hasNext() && (nextBeat.getBeatPattern() & 1) == 0) { // Last note of beat but next note not at beginning
                                    stress = 2;
                                }
                                switch (stress) {
                                    case 2:
                                        sounder.playNote(11, -1, 72, 127);
                                        break;
                                    case 1:
                                        sounder.playNote(11, -1, 67, 100);
                                        break;
                                    default:
                                        sounder.playNote(11, -1, 60, 80);
                                }
                                nextNote = null;
                            }
                        } else if (it.hasNext()) {
                            nextNote = it.next();
                        } else {
                            currentBeatNum++;
                            if (isRepeat) {
                                currentBeatNum %= numBeats;
                            }
                            notesToPlay = false;
                        }
                    } else {
                        // Start of next beat
                        if (currentBeatNum < numBeats) {
                            thisBeat = beatlist.get(currentBeatNum);
                            nextBeat = beatlist.get((currentBeatNum + 1) % numBeats);
                            notes = getNotesFromBeat(thisBeat);
                            it = notes.iterator();
                            notesToPlay = true;
                            firstNotePassed = false;
                        }
                    }
                }
                isPlaying.compareAndSet(true, false);
            }
        });
        perfThread.start();
    }

    private ArrayList<Note> getNotesFromBeat(Beat beat) {
        ArrayList<Note> notes = new ArrayList<>();
        int pattern = beat.getBeatPattern();
        int step = (int)TICKS_PER_BEAT / beat.getSubDivision();
        for (int i = 0; i < beat.getSubDivision(); i++) {
            if ((1 & (pattern >> i)) != 0) {
                notes.add(new Note(step * i));
            }
        }
        return notes;
    }

    public void stop() {
        isPlaying.compareAndSet(true, false);
    }

    public AtomicBoolean isPlaying() {
        return isPlaying;
    }

    public void random(){
        for (int numBeats=0; numBeats< this.numBeats;numBeats++){
            int subdivision = ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)];
            int beatpattern = rand.nextInt((int)Math.pow((double)2,(double)ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)]));
            addBeat(beatpattern,subdivision);
        }
    }

    public void initSounder(File f, String nativeLibraryDir) {
        sounder = new Sounder(nativeLibraryDir);
        sounder.csoundObj.startCsound(f);
        sounder.csoundObj.pause();
        sounder.csoundObj.play();
    }

    public double secondsToTicks(double seconds) {
        // S / S/M = M
        // M * Q/M = Q
        // Q * T/Q = T
        return (seconds / (double) SECONDS_PER_MINUTE) * (double) tempo * (double) TICKS_PER_BEAT;
    }

    public double getCurrentTick() {
        return currentTick;
    }

    public long getMaxTicks(){
        return numBeats*TICKS_PER_BEAT;
    }
}

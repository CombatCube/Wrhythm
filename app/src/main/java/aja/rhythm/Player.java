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

    private int tempo;
    private CopyOnWriteArrayList<Beat> beatlist;
    private boolean isRepeat;
    private int numBeats;
    private boolean isPlaying;
    private Thread perfThread;

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

    public void start(){
        isPlaying.compareAndSet(false,true);
        //Start the player
        perfThread = new Thread(new Runnable() {
            double currentTick = 0;
            long prevTime = elapsedRealtime();
            @Override
            public void run() {
                int currentBeat = 0;
                ArrayList<Note> notes = null;
                Note nextNote = null;
                Iterator<Note> it = null;
                boolean notesToPlay = false;
                while(isPlaying && currentBeat < numBeats) {
                    long newTime = elapsedRealtime();
                    currentTick += secondsToTicks((newTime - prevTime) / (double) MILLIS_PER_S);
                    currentTick %= (TICKS_PER_BEAT * numBeats);
                    prevTime = newTime;
                    // Play next note
                    if (notesToPlay) {
                        if (nextNote != null) {
                            double nextNoteTick = nextNote.getTick() + currentBeat * TICKS_PER_BEAT;
                            if (currentTick - nextNoteTick > 0 && currentTick - nextNoteTick < TICKS_PER_BEAT) { // && currentBeat != numBeats - 1
                                sounder.playNote(11, 2.0, 60, 100);
                                Log.d("NWHACKS-Debug", "currentBeat = " + currentBeat + "; currentTick = " + currentTick);
                                nextNote = null;
                            }
                        } else if (it.hasNext()) {
                            nextNote = it.next();
                        } else {
                            currentBeat++;
                            if (isRepeat) {
                                currentBeat %= numBeats;
                            }
                            notesToPlay = false;
                        }
                    } else {
                        if (currentBeat < numBeats) {
                            notes = getNotesFromBeat(beatlist.get(currentBeat));
                            it = notes.iterator();
                            notesToPlay = true;
                        }
                    }
                }
            isPlaying = false;
            }
        });
        perfThread.start();
    }

    private ArrayList<Note> getNotesFromBeat(Beat beat) {
        ArrayList<Note> notes = new ArrayList<>();
        notes.add(new Note(0));
        notes.add(new Note(120));
        notes.add(new Note(240));
        notes.add(new Note(360));
        return notes;
    }

    public void stop(){
        isPlaying.compareAndSet(true,false);
        //stop the player
    }

    public AtomicBoolean isPlaying() {
        return isPlaying;
    }

    //Wrong i do not understand concept of ticks
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

    //TESTS
    /*
    public static void main(String args[]){
        Player player = getPlayer();
        player.random();
        Beat[] holder = player.getBeatData();
        System.out.println("Hello World");
    }
    */

    public double secondsToTicks(double seconds) {
        // S / S/M = M
        // M * Q/M = Q
        // Q * T/Q = T
        return (seconds / (double) SECONDS_PER_MINUTE) * (double) tempo * (double) TICKS_PER_BEAT;
    }
}

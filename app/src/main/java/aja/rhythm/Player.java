package aja.rhythm;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

/**
 * Created by adrianlim on 2016-02-27.
 */
public class Player {
    final int[] ALLOWABLE_NOTES = {3,4}; // allowable notes at == 1/n
    public Sounder sounder;

    private int tempo;
    private ArrayList<Beat> beatlist;
    private boolean isRepeat;
    private int ticks;
    private boolean tie;

    private static Player instance;

    private Random rand;

    private Player(){
        this.tempo = 120;
        this.beatlist = new ArrayList<Beat>();
        this.ticks =4;
        rand = new Random();
        tie = false;
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
        beatlist.add(index, addedbeat);
    }

    public void addBeat(int beatpattern, int subdivision){
        Beat addedbeat = new Beat(beatpattern, subdivision);
        beatlist.add(addedbeat);
    }

    public boolean getRepeat(){
        return this.isRepeat;
    }

    public void setRepeat(boolean repeat){
        this.isRepeat = repeat;
    }

    public int getTicks(){
        return this.ticks;
    }

    public void setTicks(int newticks){
        this.ticks = newticks;
    }

    public void start(){
    }

    public void stop(){
        //stop the player
    }

    public void random(){
        for (int tick=0; tick<ticks;tick++){
            int subdivision = ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)];
            int beatpattern = rand.nextInt((int)Math.pow((double)2,(double)ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)]));
            addBeat(beatpattern,subdivision);
        }
    }

    public boolean isTie() {
        return tie;
    }

    public boolean toggleTie() {
        this.tie = !this.tie;
        return this.tie;
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

}

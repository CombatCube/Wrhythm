package aja.rhythm;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by adrianlim on 2016-02-27.
 */
public class Player {
    final int[] ALLOWABLE_NOTES = {3,4}; // allowable notes at == 1/n

    private int tempo;
    private ArrayList<Beat> beatlist;
    private boolean isRepeat;
    private int ticks;

    private static Player instance;

    private Player(){
        this.tempo = 120;
        this.beatlist = new ArrayList<Beat>();
        this.ticks =4;
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
        beatlist.add(index,addedbeat);
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
        //Start the player
    }

    public void stop(){
        //stop the player
    }

    public void random(){
        Random rand = new Random();
        for (int tick=0; tick<ticks;tick++){
            int subdivision = ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)];
            int beatpattern = rand.nextInt(ALLOWABLE_NOTES[rand.nextInt(ALLOWABLE_NOTES.length)]);
            addBeat(beatpattern,subdivision);
        }
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

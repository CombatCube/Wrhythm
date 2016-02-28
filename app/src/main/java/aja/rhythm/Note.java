package aja.rhythm;

/**
 * Created by Andrew on 2016-02-28.
 */
public class Note {
    private double tick;

    public Note(int tick) {
        this.tick = tick;
    }

    public double getTick() {
        return tick;
    }

    public void setTick(double tick) {
        this.tick = tick;
    }
}

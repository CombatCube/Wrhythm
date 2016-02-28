package aja.rhythm;

/**
 * Created by adrianlim on 2016-02-27.
 */
public class Beat {
    private int beatPattern;
    private int subDivision;

    public Beat(int beatpattern, int subdivision){
        this.beatPattern = beatpattern;
        this.subDivision = subdivision;
    }

    public int getBeatPattern() {
        return beatPattern;
    }

    public int getSubDivision() {
        return subDivision;
    }
}

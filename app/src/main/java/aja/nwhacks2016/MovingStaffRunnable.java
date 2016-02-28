package aja.nwhacks2016;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ToggleButton;

/**
 * Created by adrianlim on 2016-02-28.
 */
public class MovingStaffRunnable implements Runnable {
    private static MovingStaffRunnable instance = new MovingStaffRunnable();
    private MainActivity view;

    private MovingStaffRunnable() {};

    public static MovingStaffRunnable getMovingStaffRunnable(){
        return instance;
    }

    private StaffSurfaceView sv;
    public MovingStaffRunnable(StaffSurfaceView surfaceview){
        sv = surfaceview;
    }

    public void run(){
        if (!sv.isRunning()) {
            sv.toggleRunning();
            sv.runLine();
        }
        else{
            sv.toggleRunning();
        }
    }
}

package aja.nwhacks2016;

import android.os.Handler;
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
    private Handler.Callback cb;

    private MovingStaffRunnable() {};

    public static MovingStaffRunnable getMovingStaffRunnable(){
        return instance;
    }

    private StaffSurfaceView sv;
    public MovingStaffRunnable(StaffSurfaceView surfaceview, Handler.Callback callback){
        sv = surfaceview;
        cb = callback;
    }

    public void run(){
        if (!sv.isRunning()) {
            sv.toggleRunning();
            sv.runLine();
            cb.handleMessage(null);
        }
        else{
            sv.toggleRunning();
        }
    }
}

package aja.nwhacks2016;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.SystemClock;
import android.widget.ToggleButton;

/**
 * Created by adrianlim on 2016-02-28.
 */
public class StaffSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder sh;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int LASTPOSITION = 950;
    private final int MOVINGLEFTSTART = 20;
    private final int MOVINGLEFTEND = 27;
    private final int STARTPOSITION = 20;
    private int movingLeftPosition=20;
    private int movingRightPosition=27;
    private boolean isRunning;

    public StaffSurfaceView(Context context) {
        super(context);
        sh = getHolder();
        sh.addCallback(this);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
    }
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = sh.lockCanvas();
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(20, 500, 27, 50, paint); //Animate this line
        canvas.drawRect(70,320,80,190,paint);
        canvas.drawRect(100, 320, 110, 190, paint);
        canvas.drawRect(10, 260, LASTPOSITION, 270, paint);
        sh.unlockCanvasAndPost(canvas);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    private int scale(double input){
        //NOT FINISHED
        int width = LASTPOSITION-STARTPOSITION;
        return (int)input;
    }

    public void runLine(){
        while (movingRightPosition<LASTPOSITION && isRunning()){
            Canvas canvas = sh.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawRect(70, 320, 80, 190, paint);
            canvas.drawRect(100,320,110,190,paint);
            canvas.drawRect(10, 260, LASTPOSITION, 270, paint);
            canvas.drawRect(movingLeftPosition, 500, movingRightPosition, 50, paint); //Animate this line
            movingLeftPosition+=7;
            movingRightPosition+=7;
            sh.unlockCanvasAndPost(canvas);
            SystemClock.sleep(15);
        }
        if (isRunning()) {
            movingLeftPosition = MOVINGLEFTSTART;
            movingRightPosition = MOVINGLEFTEND;
            this.isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void toggleRunning(){
        System.out.println("Toggled");
        this.isRunning = !isRunning;
    }
}

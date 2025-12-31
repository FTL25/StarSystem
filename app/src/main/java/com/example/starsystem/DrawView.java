package com.example.starsystem;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {
    private DrawThread drawThread;
    private static final float MIN_ZOOM = ((float) 1 / 255);
    private static final float MAX_ZOOM = 2f;

    private ScaleGestureDetector detector;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private boolean dragged = false;
    private int mode;
    private float startX = 0f;
    private float startY = 0f;
    private float tempX;
    private float tempY;
    Data AppData;
    public Data SavingData(){
        Data saveData = new Data(drawThread.getPlanetInfo(), drawThread.getSpeed(),
                drawThread.getOrbitalAngles(), drawThread.getDate());
        return saveData;
    }
    public void LoadingData(Data LoadData){
        AppData = LoadData;
    }

    public DrawView(Context context) {
        super(context);
        getHolder().addCallback(this);
        detector = new ScaleGestureDetector(getContext(), new ScaleListener());

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int width = getContext().getResources().getDisplayMetrics().widthPixels;
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        drawThread = new DrawThread(getContext(), getHolder(), width, height);
        drawThread.start();
        if (AppData != null){
            drawThread.setPlanetInfo(AppData.getPlanetInfo());
            drawThread.setSpeed(AppData.getSpeed());
            drawThread.setOrbitalAngles(AppData.getOrbitalAngles());
            drawThread.setDate(AppData.getDate());
            drawThread.setPause(true);
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        drawThread.setViewWidth(width);
        drawThread.setViewHeight(height);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        AppData = new Data(drawThread.getPlanetInfo(), drawThread.getSpeed(),
                drawThread.getOrbitalAngles(), drawThread.getDate());
        drawThread.setPause(true);
        drawThread.requestStop();
        boolean retry = true;
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException ignored) {

            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) { // Обработка нажатий
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                Log.i("solarX", "" + (drawThread.getTranslateX()));
                Log.i("solarY", "" + (drawThread.getTranslateY()));
                tempX = drawThread.getTranslateX();
                tempY = drawThread.getTranslateY();
                SpaceBody[] Bodies = drawThread.getSpaceBodies();
                if ((-drawThread.getTranslateY() + drawThread.getViewHeight() - 190)  / drawThread.getScaleFactor()
                        <= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                    (-drawThread.getTranslateY() + drawThread.getViewHeight() - 80)  / drawThread.getScaleFactor()
                        >= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                    (-drawThread.getTranslateX() + 70) / drawThread.getScaleFactor()
                        <= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor() &&
                    (-drawThread.getTranslateX() + 140) / drawThread.getScaleFactor()
                        >= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor()) {
                        drawThread.setPause(!drawThread.isPause());
                } else if ((-drawThread.getTranslateY() + drawThread.getViewHeight() - 190)  / drawThread.getScaleFactor()
                        <= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateY() + drawThread.getViewHeight() - 80)  / drawThread.getScaleFactor()
                                >= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateX() + 190) / drawThread.getScaleFactor()
                                <= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateX() + 420) / drawThread.getScaleFactor()
                                >= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor()) {
                    drawThread.setPause(false);
                    switch (drawThread.getSpeed()){
                        case -1:
                            drawThread.setSpeed(-2);
                            break;
                        case -2:
                            drawThread.setSpeed(-3);
                            break;
                        default:
                            drawThread.setSpeed(-1);
                            break;
                    }
                } else if ((-drawThread.getTranslateY() + drawThread.getViewHeight() - 190)  / drawThread.getScaleFactor()
                        <= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateY() + drawThread.getViewHeight() - 80)  / drawThread.getScaleFactor()
                                >= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateX() + 470) / drawThread.getScaleFactor()
                                <= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor() &&
                        (-drawThread.getTranslateX() + 700) / drawThread.getScaleFactor()
                                >= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor()) {
                    drawThread.setPause(false);
                    switch (drawThread.getSpeed()){
                        case 1:
                            drawThread.setSpeed(2);
                            break;
                        case 2:
                            drawThread.setSpeed(3);
                            break;
                        default:
                            drawThread.setSpeed(1);
                            break;
                    }
                } else if (drawThread.getPlanetInfo() >= 0 &&
                        30 < event.getX() && event.getX() < drawThread.getViewWidth() - 30 &&
                        40 < event.getY() && event.getY() < (float) drawThread.getViewHeight() / 4 ){
                    drawThread.setPlanetInfo(-1);
                } else {
                    mode = DRAG;
                    Log.e("solar", "");
                    startX = event.getX() - drawThread.getPreviousTranslateX();
                    startY = event.getY() - drawThread.getPreviousTranslateY();
                }
                for (int i = 0; i < 9; i++){
                    if (Bodies[i].getBoundingBoxRect().top - 1000 * drawThread.getScaleFactor()
                            <= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                            Bodies[i].getBoundingBoxRect().bottom + 1000 * drawThread.getScaleFactor()
                                    >= (event.getY() - drawThread.getPreviousTranslateY()) / drawThread.getScaleFactor() &&
                            Bodies[i].getBoundingBoxRect().left - 1000 * drawThread.getScaleFactor()
                                    <= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor() &&
                            Bodies[i].getBoundingBoxRect().right + 1000 * drawThread.getScaleFactor()
                                    >= (event.getX() - drawThread.getPreviousTranslateX()) / drawThread.getScaleFactor()){
                        drawThread.setPlanetInfo(i);
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG){
                    drawThread.setTranslateX(event.getX() - startX);
                    drawThread.setTranslateY(event.getY() - startY);

                    drawThread.CheckBorders();
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                break;
        }
        detector.onTouchEvent(event);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
    {
        @Override
        public boolean onScale(ScaleGestureDetector detector)
        {
            mode = ZOOM;

            Log.d("solarDetect", "" + (detector.getScaleFactor()));

            if (drawThread.getScaleFactor() * detector.getScaleFactor() < MAX_ZOOM){
                drawThread.setScaleFactor(Math.max(MIN_ZOOM, drawThread.getScaleFactor() * detector.getScaleFactor()));

                drawThread.setTranslateX(detector.getScaleFactor() * drawThread.getTranslateX());
                drawThread.setTranslateY(detector.getScaleFactor() * drawThread.getTranslateY());
            }

            drawThread.CheckBorders();
            return true;
        }
    }
}
package com.example.starsystem;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import androidx.core.content.ContextCompat;
import java.util.Calendar;

public class DrawThread extends Thread {
    private float AU;
    private int controlPanelColor;
    private int[] orbitColors = new int[8];
    private final float MIN_ZOOM = ((float) 1 / 255);
    private  int viewWidth;
    private  int viewHeight;
    private  float translateX;
    private  float translateY;
    private  float previousTranslateX;
    private  float previousTranslateY;
    private  float scaleFactor = (float) 1 / 10;
    private Bitmap background;
    private  final int timerInterval = 30;
    private  boolean pause = false;
    private  int PlanetInfo = -10;
    private  int speed = -10;
    private int updateSpeed = -10;
    private int millisecond = 0;
    private SurfaceHolder surfaceHolder;
    private volatile boolean running = true;
    private int Orientation;
    private  double[] OrbitalSpeeds = new double[8];
    private  double[] OrbitalAngles;

    private final SpaceBody[] SpaceBodies = new SpaceBody[9];
    private Calendar Date;

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isPause() {
        return pause;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public float getTranslateX() {
        return translateX;
    }

    public float getTranslateY() {
        return translateY;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setTranslateX(float translateX) {
        this.translateX = translateX;
    }

    public void setTranslateY(float translateY) {
        this.translateY = translateY;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    public float getPreviousTranslateX() {
        return previousTranslateX;
    }

    public void setPreviousTranslateX(float previousTranslateX) {
        this.previousTranslateX = previousTranslateX;
    }

    public  float getPreviousTranslateY() {
        return previousTranslateY;
    }

    public  void setPreviousTranslateY(float previousTranslateY) {
        this.previousTranslateY = previousTranslateY;
    }

    public  int getPlanetInfo() {
        return PlanetInfo;
    }

    public SpaceBody[] getSpaceBodies() {
        return SpaceBodies;
    }

    public  void setPlanetInfo(int planetInfo) {
        PlanetInfo = planetInfo;
    }

    public boolean isRunning() {
        return running;
    }

    public DrawThread(Context context, SurfaceHolder surfaceHolder, int width, int height) {
        this.surfaceHolder = surfaceHolder;

        viewWidth = width;
        viewHeight = height;
        Orientation = context.getResources().getConfiguration().orientation;

        controlPanelColor = ContextCompat.getColor(context, R.color.blue);
        orbitColors[0] = ContextCompat.getColor(context, R.color.mercury);
        orbitColors[1] = ContextCompat.getColor(context, R.color.venus);
        orbitColors[2] = ContextCompat.getColor(context, R.color.earth);
        orbitColors[3] = ContextCompat.getColor(context, R.color.mars);
        orbitColors[4] = ContextCompat.getColor(context, R.color.jupiter);
        orbitColors[5] = ContextCompat.getColor(context, R.color.saturn);
        orbitColors[6] = ContextCompat.getColor(context, R.color.uran);
        orbitColors[7] = ContextCompat.getColor(context, R.color.neptun);

        OrbitalSpeeds[0] = -0.0714E-3 * timerInterval;
        OrbitalSpeeds[1] = -0.028E-3 * timerInterval;
        OrbitalSpeeds[2] = -0.017E-3 * timerInterval;
        OrbitalSpeeds[3] = -9.15E-6 * timerInterval;
        OrbitalSpeeds[4] = -1.445E-6 * timerInterval;
        OrbitalSpeeds[5] = -8.03E-7 * timerInterval;
        OrbitalSpeeds[6] = -2.048E-7 * timerInterval;
        OrbitalSpeeds[7] = -1.04E-7 * timerInterval;

        double scale = Math.max(viewWidth, viewHeight);
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.space);
        background = Bitmap.createScaledBitmap(background, (int) scale, (int) scale, true);

        translateX = - ((scaleFactor - MIN_ZOOM) * (1 / MIN_ZOOM) * viewWidth) / 2;
        translateY = - ((scaleFactor - MIN_ZOOM) * (1 / MIN_ZOOM) * viewHeight) / 2;
        previousTranslateX = translateX;
        previousTranslateY = translateY;

        if (Date == null){
            Date = Calendar.getInstance();
            Date.set(Calendar.YEAR, 2026);
            Date.set(Calendar.MONTH, 0);
            Date.set(Calendar.DAY_OF_MONTH, 1);
        }

        if (PlanetInfo == -10)
            PlanetInfo = -1;
        if (updateSpeed == -10){
            speed = 1;
            updateSpeed = 1;
        }
        else
            updateSpeed();

        if (OrbitalAngles == null){
            OrbitalAngles = new double[8];
            OrbitalAngles[0] = 4;
            OrbitalAngles[1] = 4.54;
            OrbitalAngles[2] = 1.57;
            OrbitalAngles[3] = 4.71;
            OrbitalAngles[4] = 1.75;
            OrbitalAngles[5] = 6.11;
            OrbitalAngles[6] = 0.87;
            OrbitalAngles[7] = 6.11;
        }

        scale = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.4f, context.getResources().getDisplayMetrics());
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.sun);
        AU = (float) (b.getWidth() * scale);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * scale), (int)(b.getHeight() * scale), true);
        Rect Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Sun = new SpaceBody(0, 0, 0, 0, Image, b);
        Sun.setInfo("Солнце", "696000 км", "25 суток", "273,1 м/с^2", "-", "-", "-");
        SpaceBodies[0] = Sun;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.mercury);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.03f * scale), (int)(b.getHeight() * 0.03f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Mercury = new SpaceBody(OrbitalAngles[0], OrbitalSpeeds[0], AU, 0.21, Image, b);
        Mercury.setInfo("Меркурий", "2439 км", "58 суток", "3,7 м/с^2", "5,8 * 10^7 км", "88 суток", "0");
        SpaceBodies[1] = Mercury;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.venus);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.1f * scale), (int)(b.getHeight() * 0.1f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Venus = new SpaceBody(OrbitalAngles[1], OrbitalSpeeds[1], 1.9 * AU, 0.01, Image, b);
        Venus.setInfo("Венера", "6052 км", "243 суток", "8,88 м/с^2", "1,1 * 10^8 км", "224,7 суток", "0");
        SpaceBodies[2] = Venus;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.earth);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.1f * scale), (int)(b.getHeight() * 0.1f  * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Earth = new SpaceBody(OrbitalAngles[2], OrbitalSpeeds[2], 2.6 * AU, 0.02, Image, b);
        Earth.setInfo("Земля", "6371 км", "23 часа 56 минут", "9,81 м/с^2", "1,5 * 10^8 км", "365,25 суток", "1");
        SpaceBodies[3] = Earth;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.mars);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.05f * scale), (int)(b.getHeight() * 0.05f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Mars = new SpaceBody(OrbitalAngles[3], OrbitalSpeeds[3], 3.9 * AU, 0.09, Image, b);
        Mars.setInfo("Марс", "3386 км", "24 часа 37 минут", "3,86 м/с^2", "2,28 * 10^8 км", "687 суток", "2");
        SpaceBodies[4] = Mars;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.jupiter);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.5f * scale), (int)(b.getHeight() * 0.5f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Jupiter = new SpaceBody(OrbitalAngles[4], OrbitalSpeeds[4], 13 * AU, 0.05, Image, b);
        Jupiter.setInfo("Юпитер", "71492 км", "10 часов", "24,79 м/с^2", "7,78 * 10^8 км", "11,8 лет", "97");
        SpaceBodies[5] = Jupiter;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.saturn);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.4f * scale), (int)(b.getHeight() * 0.4f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Saturn = new SpaceBody(OrbitalAngles[5], OrbitalSpeeds[5], 25 * AU, 0.06, Image, b);
        Saturn.setInfo("Сатурн", "60268 км", "10 часов 34 минуты", "10,44 м/с^2", "1,43 * 10^9 км", "29,5 лет", "274");
        SpaceBodies[6] = Saturn;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.uran);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.37f * scale), (int)(b.getHeight() * 0.37f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Uran = new SpaceBody(OrbitalAngles[6], OrbitalSpeeds[6], 50 * AU, 0.04, Image, b);
        Uran.setInfo("Уран", "25559 км", "17 часов", "8,86 м/с^2", "2,877 * 10^9 км", "84 года", "29");
        SpaceBodies[7] = Uran;

        b = BitmapFactory.decodeResource(context.getResources(), R.drawable.neptune);
        b = Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.36f * scale), (int)(b.getHeight() * 0.36f * scale), true);
        Image = new Rect(0, 0, b.getWidth(), b.getHeight());
        SpaceBody Neptune = new SpaceBody(OrbitalAngles[7], OrbitalSpeeds[7], 75 * AU, 0.01, Image, b);
        Neptune.setInfo("Нептун", "24764 км", "16 часов", "11,09 м/с^2", "4,5 * 10^9 км", "164,8 года", "16");
        SpaceBodies[8] = Neptune;

        SpaceBodies[0].setCenterX((double) (((1 / MIN_ZOOM) * viewWidth) / 2) - (double) SpaceBodies[0].getImageWidth() / 2);
        SpaceBodies[0].setCenterY((double) (((1 / MIN_ZOOM) * viewHeight) / 2) - (double) SpaceBodies[0].getImageHeight() / 2);
        for (int i = 1; i < 9; i++){
            SpaceBodies[i].setCenterX((double) (((1 / MIN_ZOOM) * viewWidth) / 2));
            SpaceBodies[i].setCenterY((double) (((1 / MIN_ZOOM) * viewHeight) / 2));
        }

        for (int i = 0; i < 9; i++){
            SpaceBodies[i].update(timerInterval);
        }

        Timer t = new Timer();
        t.start();
    }
    private void CreateSpaceBody() {

    }
    protected void update() { // Обновление значений переменных
        if(!pause){
            millisecond += 30;
            for (int i = 0; i < 9; i++){
                SpaceBodies[i].update(timerInterval);
            }
            if (millisecond >= 1000){
                switch (speed){
                    case -3:
                        Date.add(Calendar.YEAR, -1);
                        break;
                    case -2:
                        Date.add(Calendar.MONTH, -1);
                        break;
                    case -1:
                        Date.add(Calendar.DAY_OF_MONTH, -1);
                        break;
                    case 1:
                        Date.add(Calendar.DAY_OF_MONTH, 1);
                        break;
                    case 2:
                        Date.add(Calendar.MONTH, 1);
                        break;
                    case 3:
                        Date.add(Calendar.YEAR, 1);
                        break;
                }
                millisecond = 0;
            }
            if (millisecond == 0)
                updateSpeed();
        }
    }
    public void requestStop() {
        running = false;
    }
    public void requestStart() {
        running = true;
    }

    public  void CheckBorders(){
        if (-1 * translateX > (scaleFactor - MIN_ZOOM) * (1 / MIN_ZOOM) * viewWidth) // Правая стенка
            translateX = (MIN_ZOOM - scaleFactor) * (1 / MIN_ZOOM) * viewWidth;
        else if (translateX > 0) // Левая стенка
            translateX = 0;

        if(-1 * translateY > (scaleFactor - MIN_ZOOM) * (1 / MIN_ZOOM) * viewHeight) // Нижняя стенка
            translateY = (MIN_ZOOM - scaleFactor) * (1 / MIN_ZOOM) * viewHeight;
        else if(translateY > 0) // Верхняя стенка
            translateY = 0;

        previousTranslateX = translateX;
        previousTranslateY = translateY;
    }
    @Override
    public void run() { // Отрисовка
        while (running) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    Paint p = new Paint();
                    p.setAntiAlias(true);
                    p.setColor(Color.WHITE);
                    p.setTypeface(Typeface.MONOSPACE);

                    canvas.drawBitmap(background, 0, 0, p);

                    canvas.translate(translateX, translateY);

                    canvas.scale(scaleFactor, scaleFactor);


                    for (int i = 1; i < 9; i++){
                        p.setColor(orbitColors[i - 1]);
                        p.setStyle(Paint.Style.STROKE);
                        canvas.drawOval((float) (SpaceBodies[i].getCenterX() - SpaceBodies[i].getA() + ((double) SpaceBodies[i].getImageWidth() / 2)),
                                (float) (SpaceBodies[i].getCenterY() - SpaceBodies[i].getB() + ((double) SpaceBodies[i].getImageHeight() / 2)),
                                (float) (SpaceBodies[i].getCenterX() + SpaceBodies[i].getA() + ((double) SpaceBodies[i].getImageWidth() / 2)),
                                (float) (SpaceBodies[i].getCenterY() + SpaceBodies[i].getB() + ((double) SpaceBodies[i].getImageHeight() / 2)), p);
                    }
                    for (int i = 0; i < 9; i++){
                        SpaceBodies[i].draw(canvas);
                    }

                    // Отрисовка нижнего меню
                    p.setColor(controlPanelColor);
                    p.setStyle(Paint.Style.FILL);
                    if (Orientation == Configuration.ORIENTATION_PORTRAIT){
                        canvas.drawRoundRect((-translateX + 30) / scaleFactor,
                                (-translateY + viewHeight - 240) / scaleFactor,
                                (-translateX + viewWidth - 30) / scaleFactor,
                                (-translateY + viewHeight - 30)  / scaleFactor, 15 / scaleFactor, 15 / scaleFactor, p);
                    }
                    else {
                        canvas.drawRoundRect((-translateX + 30) / scaleFactor,
                                (-translateY + viewHeight - 240) / scaleFactor,
                                (-translateX + (float) viewWidth / 2) / scaleFactor,
                                (-translateY + viewHeight - 30)  / scaleFactor, 15 / scaleFactor, 15 / scaleFactor, p);
                    }
                    drawPause(canvas, p); // Отрисовка паузы

                    drawBackward(canvas, p); // Отрисовка перемотки назад

                    drawForward(canvas, p); // Отрисовка перемотки вперёд

                    p.setColor(Color.WHITE);
                    p.setTextSize(55.0f / scaleFactor);
                    canvas.drawText(Date.get(Calendar.DAY_OF_MONTH) + "." + (Date.get(Calendar.MONTH) + 1) + "." + Date.get(Calendar.YEAR),
                            (-translateX + 710) / scaleFactor, (-translateY + viewHeight - 115)  / scaleFactor, p);

                    if (PlanetInfo >= 0){
                        int num = PlanetInfo;
                        p.setColor(controlPanelColor);
                        if (Orientation == Configuration.ORIENTATION_PORTRAIT){
                            canvas.drawRoundRect((-translateX + 30) / scaleFactor,
                                    (-translateY + 40) / scaleFactor,
                                    (-translateX + viewWidth - 30) / scaleFactor,
                                    (-translateY + (float) viewHeight / 4)  / scaleFactor, 15 / scaleFactor, 15 / scaleFactor, p);
                        }
                        else{
                            canvas.drawRoundRect((-translateX + 30) / scaleFactor,
                                    (-translateY + 40) / scaleFactor,
                                    (-translateX + viewWidth - 30) / scaleFactor,
                                    (-translateY + (float) viewHeight / 3)  / scaleFactor, 15 / scaleFactor, 15 / scaleFactor, p);
                        }
                        p.setColor(Color.WHITE);
                        p.setTextSize(48.0f / scaleFactor);
                        String[] output = SpaceBodies[num].getInfo();
                        if (Orientation == Configuration.ORIENTATION_PORTRAIT){
                            canvas.drawText(output[0],
                                    (-translateX + 40) / scaleFactor, (-translateY + 80) / scaleFactor, p);
                            canvas.drawText("Средний радиус: " + output[1],
                                    (-translateX + 40) / scaleFactor, (-translateY + 150) / scaleFactor, p);
                            canvas.drawText("Период вращения: " + output[2],
                                    (-translateX + 40) / scaleFactor, (-translateY + 210) / scaleFactor, p);
                            canvas.drawText("Ускорение свободного ",
                                    (-translateX + 40) / scaleFactor, (-translateY + 270) / scaleFactor, p);
                            canvas.drawText("падения: " + output[3],
                                    (-translateX + 40) / scaleFactor, (-translateY + 330) / scaleFactor, p);
                            canvas.drawText("Большая полуось: " + output[4],
                                    (-translateX + 40) / scaleFactor, (-translateY + 390) / scaleFactor, p);
                            canvas.drawText("Сидерический период: " + output[5],
                                    (-translateX + 40) / scaleFactor, (-translateY + 450) / scaleFactor, p);
                            canvas.drawText("Количество спутников: " + output[6],
                                    (-translateX + 40) / scaleFactor, (-translateY + 510) / scaleFactor, p);
                        }
                        else{
                            canvas.drawText(output[0] + "; средний радиус: " + output[1] + "; период вращения: " + output[2],
                                    (-translateX + 40) / scaleFactor, (-translateY + 80) / scaleFactor, p);
                            canvas.drawText("Ускорение свободного падения: "  + output[3],
                                    (-translateX + 40) / scaleFactor, (-translateY + 140) / scaleFactor, p);
                            canvas.drawText("Большая полуось: " + output[4] + "; сидерический период: " + output[5],
                                    (-translateX + 40) / scaleFactor, (-translateY + 200) / scaleFactor, p);
                            canvas.drawText("Количество спутников: " + output[6],
                                    (-translateX + 40) / scaleFactor, (-translateY + 260) / scaleFactor, p);
                        }
                    }
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
    private void drawPause(Canvas canvas, Paint p) {
        if (pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        canvas.drawRect((-translateX + 70) / scaleFactor,
                (-translateY + viewHeight - 190)  / scaleFactor,
                (-translateX + 90) / scaleFactor,
                (-translateY + viewHeight - 80)  / scaleFactor, p);
        canvas.drawRect((-translateX + 120) / scaleFactor,
                (-translateY + viewHeight - 190)  / scaleFactor,
                (-translateX + 140) / scaleFactor,
                (-translateY + viewHeight - 80)  / scaleFactor, p);
    }
    private void drawBackward(Canvas canvas, Paint p) {
        if (updateSpeed == -3 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        Path path = new Path();
        path.moveTo((-translateX + 260) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 190) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 260) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 260) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);

        if (updateSpeed <= -2 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        path = new Path();
        path.moveTo((-translateX + 340) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 270) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 340) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 340) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);

        if (updateSpeed <= -1 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        path = new Path();
        path.moveTo((-translateX + 420) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 350) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 420) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 420) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);
    }

    private void drawForward(Canvas canvas, Paint p) {
        if (updateSpeed >= 1 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        Path path = new Path();
        path.moveTo((-translateX + 470) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 540) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 470) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 470) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);


        if (updateSpeed >= 2 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        path = new Path();
        path.moveTo((-translateX + 550) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 620) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 550) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 550) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);

        if (updateSpeed == 3 && !pause)
            p.setColor(Color.WHITE);
        else
            p.setColor(Color.argb(100, 0, 0, 0));
        path = new Path();
        path.moveTo((-translateX + 630) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        path.lineTo((-translateX + 700) / scaleFactor, (-translateY + viewHeight - 135)  / scaleFactor);
        path.lineTo((-translateX + 630) / scaleFactor, (-translateY + viewHeight - 80)  / scaleFactor);
        path.lineTo((-translateX + 630) / scaleFactor, (-translateY + viewHeight - 190)  / scaleFactor);
        canvas.drawPath(path, p);
    }
    public int getSpeed() {
        return speed;
    }

    public int getMillisecond() {
        return millisecond;
    }

    public void setTempSpeed(int speed) {
        this.updateSpeed = speed;
    }
    public void updateSpeed(){
        switch (updateSpeed){
            case -3:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(-OrbitalSpeeds[i - 1] * 365);
                }
                break;
            case -2:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(-OrbitalSpeeds[i - 1] * 30);
                }
                break;
            case -1:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(-OrbitalSpeeds[i - 1]);
                }
                break;
            case 1:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(OrbitalSpeeds[i - 1]);
                }
                break;
            case 2:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(OrbitalSpeeds[i - 1] * 30);
                }
                break;
            case 3:
                speed = updateSpeed;
                for (int i = 1; i < 9; i++){
                    SpaceBodies[i].setW(OrbitalSpeeds[i - 1] * 365);
                }
                break;
        }
    }
    public double[] getOrbitalAngles() {
        double[] Angles = new double[8];
        Angles[0] = SpaceBodies[0].getW();
        Angles[1] = SpaceBodies[0].getW();
        Angles[2] = SpaceBodies[0].getW();
        Angles[3] = SpaceBodies[0].getW();
        Angles[4] = SpaceBodies[0].getW();
        Angles[5] = SpaceBodies[0].getW();
        Angles[6] = SpaceBodies[0].getW();
        Angles[7] = SpaceBodies[0].getW();
        return Angles;
    }

    public Calendar getDate() {
        return Date;
    }

    public void setMillisecond(int millisecond) {
        this.millisecond = millisecond;
    }

    public void setOrbitalAngles(double[] orbitalAngles) {
        OrbitalAngles = orbitalAngles;
    }

    public void setDate(Calendar date) {
        Date = date;
    }

    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }
        @Override
        public void onFinish() {
        }
    }
}

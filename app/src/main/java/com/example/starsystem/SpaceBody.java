package com.example.starsystem;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class SpaceBody {
    private String name;
    private String radius;
    private String period;
    private String gravity;
    private String semi_axis;
    private String sidereal_period;
    private String satellites;
    private Bitmap bitmap;
    private Rect image;
    private int imageWidth;
    private int imageHeight;
    private double x;
    private double y;
    private double W; // Угловая скорость
    private double t; // Угол в радианах
    private double centerX;
    private double centerY;
    private double e; // Эксцентриситет
    private double a; // Большая полуось
    private double  b; // Малая полуось
    private int padding;
    public void update (int ms) {
        t += W;
        x = centerX + a * Math.cos(t);
        y = centerY + b * Math.sin(t);
    }
    public void draw (Canvas canvas) {
        Paint p = new Paint();
        Rect destination = new Rect((int)x, (int)y, (int)(x + imageWidth), (int)(y + imageHeight));
        canvas.drawBitmap(bitmap, image, destination,  p);
    }
    public Rect getBoundingBoxRect () {
        return new Rect((int)x+padding, (int)y+padding, (int)(x + imageWidth - 2 * padding), (int)(y + imageHeight - 2 * padding));
    }
    public SpaceBody(double t, double W, double a, double e, Rect Image, Bitmap bitmap) {
        this.a = a;
        this.e = e;
        this.b = a * Math.sqrt(1 - e * e);
        this.W = W;
        this.t = t;
        this.bitmap = bitmap;
        this.image = Image;
        this.imageWidth = Image.width();
        this.imageHeight = Image.height();
        this.padding = 20;
    }
    public String[] getInfo(){
        return new String[] { name, radius, period, gravity, semi_axis, sidereal_period, satellites };
    }
    public void setInfo(String name, String radius, String period, String gravity, String semi_axis, String sidereal_period, String satellites){
        this.name = name;
        this.radius = radius;
        this.period = period;
        this.gravity = gravity;
        this.semi_axis = semi_axis;
        this.sidereal_period = sidereal_period;
        this.satellites = satellites;
    }
    public double getA() {
        return a;
    }
    public int getImageWidth() {
        return imageWidth;
    }
    public int getImageHeight() {
        return imageHeight;
    }
    public double getCenterX() {
        return centerX;
    }
    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }
    public double getCenterY() {
        return centerY;
    }
    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }
    public double getB() {
        return b;
    }
    public void setW(double w) {
        W = w;
    }

    public double getW() {
        return W;
    }
}

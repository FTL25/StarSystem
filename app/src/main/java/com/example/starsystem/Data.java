package com.example.starsystem;

import java.io.Serializable;
import java.util.Calendar;

public class Data implements Serializable {
    private  int PlanetInfo;
    private  int speed;
    private  double[] OrbitalAngles;
    private Calendar Date;

    Data(int planetInfo, int speed, double[] OrbitalAngles, Calendar Date) {
        this.PlanetInfo = planetInfo;
        this.speed = speed;
        this.OrbitalAngles = OrbitalAngles;
        this.Date = Date;
    }

    public int getPlanetInfo() {
        return PlanetInfo;
    }

    public int getSpeed() {
        return speed;
    }

    public double[] getOrbitalAngles() {
        return OrbitalAngles;
    }

    public Calendar getDate() { return Date; }
}

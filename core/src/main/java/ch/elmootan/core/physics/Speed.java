package ch.elmootan.core.physics;

import static java.lang.Math.sqrt;

/**
 * @brief Permet de modéliser la vitesse d'un corps.
 */
public class Speed {
    // Vitesse horizontale.
    double x;

    // Vitesse verticale.
    double y;

    public Speed(){}

    public Speed(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getSpeed()
    {
        return sqrt(x*x+y*y);
    }

    @Override
    public String toString()
    {
        return "Speed : (" + x + ";" + y + ")";
    }
}
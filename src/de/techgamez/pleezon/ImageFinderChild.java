package de.techgamez.pleezon;

import java.awt.*;

public class ImageFinderChild
{
    private Point pt;
    private Point midPt;
    private float time;

    ImageFinderChild(final Point pt, final Point midPt, final float time) {
        this.pt = pt;
        this.time = time;
        this.midPt = midPt;
    }

    public Point getImageStartingPoint() {
        return this.pt;
    }

    public Point getMidPoint() {
        return this.midPt;
    }

    public String getTime() {
        return this.time + "ms";
    }

    @Override
    public String toString() {
        return "StartPoint:" + this.pt.getX() + ":" + this.pt.getY() + " MidPoint:" + this.midPt.getX() + ":" + this.midPt.getY() + " Time:" + this.time + "ms";
    }
}

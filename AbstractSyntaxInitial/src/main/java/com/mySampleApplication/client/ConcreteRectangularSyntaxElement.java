package com.mySampleApplication.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * Brings together all the rectangular shaped elements (boundary rectangles, curves and zones) which share things
 * such as width and height, but more importantly
 */
public abstract class ConcreteRectangularSyntaxElement extends ConcreteSyntaxElement {


    private double width, height;

    private double borderWidth;
    private double cornerRadius;

    protected Boolean topLeftIsCircle, botLeftIsCircle, topRightIsCircle, botRightIsCircle;


    // this code and the code for the drawing library are a bit at odds ...
    // I've got constructors with the locations ... it has them with the width height.
    public ConcreteRectangularSyntaxElement(double x, double y, ConcreteSyntaxElement_TYPES type) {
        super(x, y, type);
        topLeftIsCircle = botLeftIsCircle = topRightIsCircle = botRightIsCircle = false;
    }


    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setBorderWidth(double width) {
        this.borderWidth = width;
    }

    public double getBorderWidth() {
        return borderWidth;
    }

    public double getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(double newRadius) {
        cornerRadius = newRadius;
    }

    public void setupConcreteRepresentation() {
        getConcreteRepresentation().setX(getX());
        getConcreteRepresentation().setY(getY());
        getConcreteRepresentation().setStrokeWidth(getBorderWidth());
        paintColoursOnConcreteRepresentation();
        getConcreteRepresentation().setDraggable(false);
    }


//    public abstract void makeAbstractRepresentation();
//    public abstract void makeConcreteRepresentation();
//    public abstract void drawOnLayer(Layer layer);

    // FIXME
    public void setAsSelected() {}




    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------



    // Center point of the top left corner curve
    // These centre points are meaningless if that corner isn't curved.
    public Point2D topLeftCentre() {
        return new Point2D(getX() + getCornerRadius(), getY() + getCornerRadius());
    }

    public Point2D topRightCentre() {
        return new Point2D(getX() + getWidth() - getCornerRadius(), getY() + getCornerRadius());
    }

    public Point2D bottomLeftCentre() {
        return new Point2D(getX() + getCornerRadius(), getY() + getHeight() - getCornerRadius());
    }

    public Point2D bottomRightCentre() {
        return new Point2D(getX() + getWidth() - getCornerRadius(), getY() + getHeight() - getCornerRadius());
    }


    // topLeft is defined in ConcreteSyntaxElement

    public Point2D topRight() {
        return new Point2D(getX() + getWidth(), getY());
    }

    public Point2D bottomLeft() {
        return new Point2D(getX(), getY() + getHeight());
    }

    public Point2D bottomRight() {
        return new Point2D(getX() + getWidth(), getY() + getHeight());
    }

    // Is the point in the given rectangle (axis aligned rectangle)
    // Includes on the lines
    public static boolean rectangleContainment(Point2D p, Point2D topLeft, double width, double height) {
        if(p.getX() < topLeft.getX() || (topLeft.getX() + width) < p.getX()) {
            return false;
        }

        if(p.getY() < topLeft.getY() || (topLeft.getY() + height) < p.getY()) {
            return false;
        }

        return true;
    }

    // Is the point in the bounding rectangle of this (possibly) corner-curved rectangle?
    // Doesn't care about line width or corner curves.
    protected boolean rectangleContainment(Point2D p) {
        return rectangleContainment(p, new Point2D(getX(), getY()), getWidth(), getHeight());
    }

    // Does this rectangle contain the given point - is it in the area enclosed, not on the line.
    // The point is relative to the enclosing boundary rectangle, not the whole canvas
    // accounts for line thickness and (potentially) rounded corners
    public boolean containsPoint(Point2D p) {
        // simple rectangle containment
        if(p.getX() <= (getX() + getBorderWidth()) || (getX() + getWidth() - getBorderWidth()) <= p.getX()) {
            return false;
        }

        if(p.getY() <= (getY() + getBorderWidth()) || (getY() + getHeight() - getBorderWidth()) <= p.getY()) {
            return false;
        }
        return true;
    }

    // Does this rectangular element completely enclose the other.
    // Doesn't care about if there is any zone between the two, just is one inside the other
    //
    // Assumes
    // no small curves constraint
    public boolean completelyEncloses(ConcreteRectangularSyntaxElement other) {
        return rectangleContainment(new Point2D(other.getX(), other.getY())) &&
                rectangleContainment(new Point2D(other.getX() + other.getWidth(), other.getY() + other.getHeight()));
    }


    // Do the spaces enclosed by the curves contain any points in common
    // Accounts for the rounded corners and the line thicknesses
    // (hhmmm what about curves exactly overlapping?? FIXME???)
    //
    // This definition ASSUMES the
    // - no small curves constraint
    // - no small intersections constraint
    public boolean intersectsRectangularElement(ConcreteRectangularSyntaxElement other) {

        // assumes no small curves constraint
        if(completelyEncloses(other) || other.completelyEncloses(this)) {
            return true;

            // FIXME : This may not be the end of the problem.  There are little side cases where because of the rounded
            // corners a tiny curve
            // could be completely enclosed, but actually live in the space between the sharp corner and the curve.
            // For the moment, I'm assuming large enough curves for that not to happen.
            // On the front end, I'll enforce anyway that the width/height of a curve has to be greater than 2*radius
            // or similar to make them at least be rectangles with rounded corners.

        }

        // because of the no small intersections constraint, can check the corners just by checking where the
        // centre points of the curves are
        if(containsPoint(other.topLeftCentre()) || other.containsPoint(topLeftCentre())
                || containsPoint(other.bottomLeftCentre()) || other.containsPoint(bottomLeftCentre())
                || containsPoint(other.bottomRightCentre()) || other.containsPoint(bottomRightCentre()))  {
            return true;
        }

        // last case is if they slice right through each other
        if(getX() < other.getX() && other.getX() < getX() + getWidth()
                && other.getY() < getY() && getY() < other.getY() + other.getHeight()) {
            return true;
        }
        if(other.getX() < getX() && getX() < other.getX() + other.getWidth()
                && getY() < other.getY() && other.getY() < getY() + getHeight()) {
            return true;
        }

        return false;
    }



}

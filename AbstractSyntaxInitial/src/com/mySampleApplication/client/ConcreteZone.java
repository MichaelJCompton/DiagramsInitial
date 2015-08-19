package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Color;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 * This superclass represents the regular zone that is inside each curve.  The zones arising from intersections
 * are dealt with in the subclass ConcreteIntersectionZone.
 *
 * Hence the zones here are the same shape as the curves, just a bit smaller to account for the border of the square.
 * There are no borders on zones.
 */
public class ConcreteZone extends ConcreteRectangularSyntaxElement {

    // might want to store intersecting zones ... when I create one, use the background grid, test potential
    // intersections and store.  When a spider is added, is it in me, if yes, test the intersecting zones too.
    // a spider's dot can be in only one zone (but the spider could be joined by a line to another of itself, and
    // thus each of those dots is in a different zone).


    private AbstractSet<ConcreteCurve> curvesImIn;

    private Boolean isShaded;
    Integer drawingLevel;




    ConcreteZone(double x, double y) {
        // This is the actual x,y of the zone the offset from the enclosing curves has already been taken away
        super(x, y, ConcreteSyntaxElement_TYPES.CONCRETEZONE);

        setNOTShaded();
        setBorderSelectedColour(zoneSelectedColor);
        setFillSelectedColour(zoneSelectedColor);
        setCornerRadius(zoneCornerRadius);
        setBorderWidth(0);
        drawingLevel = 0;
        curvesImIn = new HashSet<ConcreteCurve>();
        topLeftIsCircle = botLeftIsCircle = topRightIsCircle = botRightIsCircle = true;
    }


    public void swapShading() {
        if(shaded()) {
            setNOTShaded();
        } else {
            setShaded();
        }
    }

    public void setShaded() {
        isShaded = true;
        setBorderColour(zoneShadedColor);
        setFillColour(zoneShadedColor);
    }

    public void setNOTShaded() {
        isShaded = false;
        setBorderColour(zoneStandardColour);
        setFillColour(zoneStandardColour);
    }

    public boolean shaded() {
        return isShaded;
    }

    public Integer getLevel() {
        return drawingLevel;
    }

    protected AbstractSet<ConcreteCurve> getCurves() {
        return curvesImIn;
    }

    protected void addEnclosingCurve(ConcreteCurve curve) {
        getCurves().add(curve);
    }

    protected void removeEnclosingCurve(ConcreteCurve curve) {
        getCurves().remove(curve);
    }

    public void increaseLevel() {
        getBoundaryRectangle().removeZone(getLevel(), this);
        drawingLevel++;
        getBoundaryRectangle().addZone(getLevel(), this);
    }

    public void decreaseLevel() {
        getBoundaryRectangle().removeZone(getLevel(), this);
        drawingLevel--;
        getBoundaryRectangle().addZone(getLevel(), this);
    }

    public void setLevel(Integer newLevel) {
        getBoundaryRectangle().removeZone(getLevel(), this);
        drawingLevel = newLevel;
        getBoundaryRectangle().addZone(getLevel(), this);
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addZone(getLevel(), this);
    }


    @Override
    public void makeAbstractRepresentation() {

    }


    @Override
    public void makeConcreteRepresentation() {

        if(hasChangedOnScreen()) {
            final Rectangle concreteZone = new Rectangle(getWidth(), getHeight(), getCornerRadius());
            setConcreteRepresentation(concreteZone);
            setupConcreteRepresentation();

            concreteZone.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    paintSelectedColoursOnConcreteRepresentation();
                    setIsUnderMouse();
                    concreteZone.getLayer().draw();
                }
            });
            concreteZone.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    paintColoursOnConcreteRepresentation();
                    concreteZone.getLayer().batch();
                }
            });
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
    }


    public void deleteMe() {
        getConcreteRepresentation().setListening(false);
        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
        getBoundaryRectangle().removeZone(getLevel(), this);
    }


    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------


    // FIXME ... some of this geometry code needs to be refactored into CONCRETERECTANGLESYNTAXELEMENT



    public boolean intersectsZone(ConcreteZone other) {

        // assumes no small curves constraint
        if(completelyEncloses(other) || other.completelyEncloses(this)) {
            return true;
        }

        // still true for zones???
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
        if(other.getX() < getX()  && getX() < other.getX() + other.getWidth()
                && getY() < other.getY() && other.getY() < getY() + getHeight()) {
            return true;
        }


        return false;
    }




    // Computes the zone arising from the intersection.  Draws it and adds it where required.
    //
    // Assumes
    // no small intersections constraint
    // that the zones do actually intersect
    public void computeIntersection(ConcreteZone other) {
        Point2D topLeft, bottomRight;

        // are any of the intersection corners half circles
        Boolean leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle;


        if(completelyEncloses(other)) {
            other.increaseLevel();
            return;     // no new zones to add
        }

        if(other.completelyEncloses(this)) {
            increaseLevel();
            return;
        }

        // figure out the corners ... call a zone to make itself
        // Just going to do it corner by corner to calculate the extent of the intersection and work out what's
        // round and what's square.

        // these are points in the original curve.  To make them the top left of the zone, need to subtract the
        // line widths.
        topLeft = new Point2D(Math.max(getX(), other.getX()) + getBorderWidth(), Math.max(getY(), other.getY()) + getBorderWidth());
        bottomRight = new Point2D(Math.min(getX() + getWidth(), other.getX() + other.getWidth()) - getBorderWidth(),
                Math.min(getY() + getHeight(), other.getY() + other.getHeight()) - getBorderWidth());

        // what's round and what's square?

        leftTopIsCircle = (containsPoint(other.topLeftCentre()) && other.topLeftIsCircle)
                || (other.containsPoint(topLeftCentre()) && topLeftIsCircle);
        leftBotIsCircle = (containsPoint(other.bottomLeftCentre()) && other.botLeftIsCircle)
                || (other.containsPoint(bottomLeftCentre()) && botLeftIsCircle);
        rightTopIsCircle = (containsPoint(other.topRightCentre()) && other.topRightIsCircle)
                || (other.containsPoint(topRightCentre()) && topRightIsCircle);
        rightBotIsCircle = (containsPoint(other.bottomRightCentre()) && other.botRightIsCircle)
                || (other.containsPoint(bottomRightCentre()) && botRightIsCircle);

        ConcreteZone z = new ConcreteIntersectionZone(topLeft, bottomRight, leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle);

        z.setBoundaryRectangle(getBoundaryRectangle());

        z.setLevel(Math.max(getLevel(), other.getLevel()) + 1);

        for(ConcreteCurve curve : getCurves()) {
            curve.addEnclosedZone(z);
        }
        for(ConcreteCurve curve : other.getCurves()) {
            curve.addEnclosedZone(z);
        }

        z.makeConcreteRepresentation();
    }
}

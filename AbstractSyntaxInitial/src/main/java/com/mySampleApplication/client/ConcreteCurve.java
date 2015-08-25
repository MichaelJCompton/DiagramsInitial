package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLine;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.ColorName;
import michael.com.Curve;

import java.util.AbstractSet;
import java.util.HashSet;

/**
 *
 */
public class ConcreteCurve extends ConcreteRectangularSyntaxElement {

    private AbstractSet<ConcreteZone> zonesInMe;
    private ConcreteZone mainZone;

    private AbstractSet<ConcreteCurve> intersectingCurves;


    public ConcreteCurve(double x, double y) {
        super(x, y, ConcreteSyntaxElement_TYPES.CONCRETECURVE);

        setBorderColour(curveBorderColour);
        setFillColour(curveFillColour);
        setBorderSelectedColour(curveBorderSelectedColor);
        setFillSelectedColour(curveFillColour);
        setBorderWidth(curveBorderWidth);
        setCornerRadius(curveCornerRadius);

        zonesInMe = new HashSet<ConcreteZone>();
        intersectingCurves = new HashSet<ConcreteCurve>();
    }

    protected ConcreteZone getMainZone () {
        return mainZone;
    }

    protected void makeMainZone() {
        mainZone = new ConcreteZone(getX() + getBorderWidth(), getY() + getBorderWidth());
        getMainZone().setHeight(getHeight() - (2 * getBorderWidth()));
        getMainZone().setWidth(getWidth() - (2 * getBorderWidth()));
        getMainZone().setBoundaryRectangle(getBoundaryRectangle());
        getMainZone().addEnclosingCurve(this);
    }

    protected AbstractSet<ConcreteCurve> getIntersectingCurves() {
        return intersectingCurves;
    }

    protected void addIntersectingCurve(ConcreteCurve other) {
        getIntersectingCurves().add(other);
    }


    @Override
    public void makeAbstractRepresentation() {
        if (! isAbstractRepresentationSyntaxUpToDate()) {
            Curve result = new Curve();
            if (hasLabel()) {
                result.setLabel(labelText());
            }
            setAbstractSyntaxRepresentation(result);
        }
    }


    @Override
    public void makeConcreteRepresentation() {
        if(hasChangedOnScreen()) {

            Point2DArray points = new Point2DArray(topLeft(), topRight(), bottomRight(), bottomLeft(), topLeft());
            final OrthogonalPolyLine theCurve = new OrthogonalPolyLine(points);
            theCurve.setCornerRadius(getCornerRadius());
            setConcreteRepresentation(theCurve);
            setupConcreteRepresentation();
            getConcreteRepresentation().setX(0);
            getConcreteRepresentation().setY(0);

            theCurve.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    paintSelectedColoursOnConcreteRepresentation();
                    setIsUnderMouse();
                    theCurve.getLayer().batch();
                }
            });
            theCurve.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    paintColoursOnConcreteRepresentation();
                    theCurve.getLayer().batch();
                }
            });

//            final Rectangle theCurve = new Rectangle(getWidth(), getHeight(), getCornerRadius());
//            setConcreteRepresentation(theCurve);
//            setupConcreteRepresentation();
//
//            theCurve.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
//                @Override
//                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
//                    paintSelectedColoursOnConcreteRepresentation();
//                    theCurve.getLayer().batch();
//                }
//            });
//            theCurve.addNodeMouseExitHandler(new NodeMouseExitHandler() {
//                @Override
//                public void onNodeMouseExit(NodeMouseExitEvent event) {
//                    paintColoursOnConcreteRepresentation();
//                    theCurve.getLayer().batch();
//                }
//            });


            // FIXME : have editable labels, placed sensibly .... maybe allow the user to move them to 'sensible' locations near the curve
            labelText = new  Text("My Curve").setFillColor(ColorName.BLACK).setX(getX()).setY(getY() - 10).setFontSize(7);
            labelText.setEditable(true);

            makeMainZone();
            getMainZone().makeConcreteRepresentation();
            getMainZone().setBoundaryRectangle(getBoundaryRectangle());
        }
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addCurve(this);
    }


    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
        layer.add(labelText);
        // drawZonesOnLayer(layer);
    }

    public void deleteMe() {
        getConcreteRepresentation().setListening(false);
        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
        getBoundaryRectangle().removeCurve(this);
        mainZone.deleteMe();
        for(ConcreteZone z : zonesInMe) {
            z.deleteMe();
        }
        for(ConcreteCurve c : getIntersectingCurves()) {
            c.getIntersectingCurves().remove(this);
        }

    }

    public void drawZonesOnLayer(Layer layer) {
        mainZone.drawOnLayer(layer);
        for(ConcreteZone z : zonesInMe) {
            z.drawOnLayer(layer);
        }
    }

    public AbstractSet<ConcreteZone> getEnclosedZones() {
        return zonesInMe;
    }

    public void addEnclosedZone(ConcreteZone zone) {
        getEnclosedZones().add(zone);
        zone.addEnclosingCurve(this);
    }


    // ---------------------------------------------------------------------------------------
    //                          Geometry
    // ---------------------------------------------------------------------------------------



    // Does this curve contain the given point - is it in the area enclosed by the curve, not on the line.
    // The point is relative to the enclosing element of the curve (the boundary rectangle), not the whole canvas
    // accounts for line thickness and the rounded corners
    public boolean containsPoint(Point2D p) {

        // first simple rectangle containment
        if(!super.containsPoint(p)) {
            return false;
        }

        // So the point is in the bounding box of the rectangle ... does it lie just outside one of
        // those rounded corners

        if((p.getX() < (getX() + getCornerRadius())) || (p.getX() > (getX() + getWidth() - getCornerRadius()))
                && ((p.getY() < (getY() + getCornerRadius())) || (p.getY() > (getY() + getHeight() - getCornerRadius())))) {

            // From my reading of the source, Lienzo curved corners are a radius to the outside of the rectangle's
            // representation.  So if we satisfy this constraint the point is in one of the corners, either just in,
            // or just out, of the corner curves, so use point distance to the centre of the
            // curve to find if it's in or out.

            double xCentre = p.getX() < (getX() + getCornerRadius()) ?
                    getX() + getCornerRadius() : getX() + getWidth() - getCornerRadius();
            double yCentre = p.getY() < (getY() + getCornerRadius()) ?
                    getY() + getCornerRadius() : getY() + getHeight() - getCornerRadius();

            return p.distance(new Point2D(xCentre, yCentre)) < (getCornerRadius() - getBorderWidth());
        }
        return true;
    }

//    // Do the spaces enclosed by the curves contain any points in common
//    // Accounts for the rounded corners and the line thicknesses - having curves with lines overlapping, but not
//    // resulting in an area doesn't count as an intersection.
//    //
//    // This definition ASSUMES the
//    // - no small curves constraint
//    // - no small intersections constraint
//    public boolean intersectsCurve(ConcreteCurve other) {
//
//        // assumes no small curves constraint
//        if(completelyEncloses(other) || other.completelyEncloses(this)) {
//            return true;
//
//            // FIXME : This may not be the end of the problem.  There are little side cases where because of the rounded
//            // corners a tiny curve
//            // could be completely enclosed, but actually live in the space between the sharp corner and the curve.
//            // For the moment, I'm assuming large enough curves for that not to happen.
//            // On the front end, I'll enforce anyway that the width/height of a curve has to be greater than 2*radius
//            // or similar to make them at least be rectangles with rounded corners.
//
//        }
//
//        // because of the no small intersections constraint, can check the corners just by checking where the
//        // centre points of the curves are
//        if(containsPoint(other.topLeftCentre()) || other.containsPoint(topLeftCentre())
//                || containsPoint(other.bottomLeftCentre()) || other.containsPoint(bottomLeftCentre())
//                || containsPoint(other.bottomRightCentre()) || other.containsPoint(bottomRightCentre()))  {
//            return true;
//        }
//
//        // last case is if they slice right through each other
//        if(getX() < other.getX() && other.getX() < getX() + getWidth()
//                && other.getY() < getY() && getY() < other.getY() + other.getHeight()) {
//            return true;
//        }
//        if(other.getX() < getX() && getX() < other.getX() + other.getWidth()
//                && getY() < other.getY() && other.getY() < getY() + getHeight()) {
//            return true;
//        }
//
//        return false;
//    }



    public void computeAllIntersections(AbstractSet<ConcreteCurve> curves) {
        HashSet<ConcreteZone> intersectingZones = new HashSet<ConcreteZone>();

        for(ConcreteCurve other : curves) {
            if(getMainZone().intersectsZone(other.getMainZone())) {
                intersectingZones.add(other.getMainZone());
            }
            for(ConcreteZone z : other.getEnclosedZones()) {
                if(getMainZone().intersectsZone(z)) {
                    intersectingZones.add(z);
                }
            }
        }

        for(ConcreteZone z : intersectingZones) {
            getMainZone().computeIntersection(z);
        }
    }

//    // Computes the zone arising from the intersection.  Draws it and adds it where required.
//    //
//    // Assumes
//    // no small intersections constraint
//    // that the curves do actually intersect
//    public void computeIntersection(ConcreteCurve other) {
//        Point2D topLeft, bottomRight;
//
//        // are any of the intersection corners half circles
//        Boolean leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle;
//
//
//        if(completelyEncloses(other)) {
//            other.getMainZone().increaseLevel();
//            return;     // no new zones to add
//        }
//
//        if(other.completelyEncloses(this)) {
//            getMainZone().increaseLevel();
//            return;
//        }
//
//
//
//
//
//        // figure out the corners ... call a zone to make itself
//        // Just going to do it corner by corner to calculate the extent of the intersection and work out what's
//        // round and what's square.
//
//        // these are points in the original curve.  To make them the top left of the zone, need to subtract the
//        // line widths.
//        topLeft = new Point2D(Math.max(getX(), other.getX()) + getBorderWidth(), Math.max(getY(), other.getY()) + getBorderWidth());
//        bottomRight = new Point2D(Math.min(getX() + getWidth(), other.getX() + other.getWidth()) - getBorderWidth(),
//                Math.min(getY() + getHeight(), other.getY() + other.getHeight()) - getBorderWidth());
//
//        // what's round and what's square?
//
//        leftTopIsCircle = containsPoint(other.topLeftCentre()) || other.containsPoint(topLeftCentre());
//        leftBotIsCircle = containsPoint(other.bottomLeftCentre()) || other.containsPoint(bottomLeftCentre());
//        rightTopIsCircle = containsPoint(other.topRightCentre()) || other.containsPoint(topRightCentre());
//        rightBotIsCircle = containsPoint(other.bottomRightCentre()) || other.containsPoint(bottomRightCentre());
//
//        ConcreteZone z = new ConcreteIntersectionZone(topLeft, bottomRight, leftTopIsCircle, leftBotIsCircle, rightTopIsCircle, rightBotIsCircle);
//        z.makeConcreteRepresentation();
//        addEnclosedZone(z);
//
//        // FIXME : add the level of these zones
//
//        addIntersectingCurve(other);
//        other.addIntersectingCurve(this);
//    }
//


    // so how to run this?
    //
    // need to be a bit smart to not increase the zone numbers too many times each go.
    // connected components would help here.???
    //
    //
    // need to find all the intersections with existing curves ... cause they don't have to be related to each other
    // so maybe find those first and keep a list of what I have made
    // intersect those with with every zone that intersects the new curve
    //
    // any of those zones that are completely contained in the new curve can have level increased and not used further
    //
}

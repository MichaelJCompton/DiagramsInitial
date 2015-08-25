package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Spline;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.shared.core.types.Color;

import java.util.HashSet;

/**
 * Created by Michael on 6/08/2015.
 */
public class ConcreteArrow extends ConcreteSyntaxElement {

    private double lineWidth;
    private ConcreteSyntaxElement source;
    private ConcreteSyntaxElement target;

    private Point2D endPoint;  // startpoint is the x,y of the super
    private Point2DArray points;

    private Rectangle[] controlPoints;


    // FIXME : need to make attached to it's source and target so if they move, this can move too

    public ConcreteArrow(double x, double y, double xEnd, double yEnd) {
        super(x, y, ConcreteSyntaxElement_TYPES.CONCRETEARROW);
        endPoint = new Point2D(xEnd, yEnd);

        setLineColour(arrowColour);
        setLineSelectedColour(arrowSelectedColour);
        lineWidth = arrowLineWidth;
    }


    public void setSource(ConcreteSyntaxElement newSource) {
        source = newSource;
    }

    public ConcreteSyntaxElement getSource() {
        return source;
    }

    public void setTarget(ConcreteSyntaxElement newTarget) {
        target = newTarget;
    }

    public ConcreteSyntaxElement getTarget() {
        return target;
    }

    public Point2D getEndPoint() {
        return endPoint;
    }

    private void setLineColour(Color lineColour) {
        setBorderColour(lineColour);
    }

    private void setLineSelectedColour(Color lineColour) {
        setBorderSelectedColour(lineColour);
    }

    private double getLineWidth() {
        return lineWidth;
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addArrow(this);
    }

    @Override
    public void makeAbstractRepresentation() {

    }

    @Override
    public void makeConcreteRepresentation() {
        double[] xpoints = new double[pointsInArrowLine + 2];  // +2 for start and end points
        double[] ypoints = new double[pointsInArrowLine + 2];
        double xdiff = (getX() < getEndPoint().getX()) ? getEndPoint().getX() - getX() : getX() - getEndPoint().getX();
        double ydiff = (getY() < getEndPoint().getY()) ? getEndPoint().getY() - getY() : getY() - getEndPoint().getY();

        xpoints[0] = getX();
        ypoints[0] = getY();
        for(int i = 1; i <= pointsInArrowLine; i++) {
            if(getX() < getEndPoint().getX()) {
                xpoints[i] = getX() + (xdiff / (pointsInArrowLine+1)) * i;
            } else {
                xpoints[i] = getX() - (xdiff / (pointsInArrowLine+1)) * i;
            }
            if(getY() < getEndPoint().getY()) {
                ypoints[i] = getY() + (ydiff / (pointsInArrowLine+1)) * i;
            } else {
                ypoints[i] = getY() - (ydiff / (pointsInArrowLine+1)) * i;
            }
        }
        xpoints[pointsInArrowLine+1] = getEndPoint().getX();
        ypoints[pointsInArrowLine+1] = getEndPoint().getY();

        points = new Point2DArray(xpoints, ypoints);

        final Spline spline = new Spline(points);
        spline.setStrokeColor(getBorderColour());
        spline.setStrokeWidth(getLineWidth());
        spline.setDraggable(false);

        spline.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
            @Override
            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                spline.setStrokeColor(getBorderSelectedColour());
                setIsUnderMouse();
                spline.getLayer().batch();
            }
        });
        spline.addNodeMouseExitHandler(new NodeMouseExitHandler() {
            @Override
            public void onNodeMouseExit(NodeMouseExitEvent event) {
                spline.setStrokeColor(getBorderColour());
                spline.getLayer().batch();
            }
        });

        setConcreteRepresentation(spline);
    }


    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
    }

    public void removeFromLayer(Layer layer) {
        layer.remove(getConcreteRepresentation());
    }

    public void setAsUnSelected() {
        //getBoundaryRectangle().getDragLayer(). remove...
    }

    public void setAsSelected() {
        // FIXME : should be done without the concretes knowing about the layers
        removeFromLayer(getBoundaryRectangle().getCurveLayer());

        getBoundaryRectangle().getDragLayer().add(getConcreteRepresentation());

//        Point2DArray splinepoints =
//        controlPoints = new Rectangle[pointsInArrowLine];
//        for(int i = 0; i < pointsInArrowLine; i++) {
//            final Rectangle controlPoint = new
//        }

        // draw the rubberbanding one
    }

    public void deleteMe() {
        getConcreteRepresentation().setListening(false);
        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
        getBoundaryRectangle().removeArrow(this);
    }
}

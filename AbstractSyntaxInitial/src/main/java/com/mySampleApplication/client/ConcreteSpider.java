package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.types.Point2D;
import michael.com.Spider;

/**
 * A concrete spider is a filled circle.
 */
public class ConcreteSpider extends ConcreteSyntaxElement {


    // This is the centre point of the spider ... breaks the abstraction a bit cause everything else is top left
    ConcreteSpider(double x, double y) {
        super(x,y, ConcreteSyntaxElement_TYPES.CONCRETESPIDER);
        setFillColour(spiderColour);
        setBorderColour(spiderColour);
    }

    public Point2D getCentrePoint() {
        //return new Point2D(getX() - spiderRadius, getY() - spiderRadius);
        return new Point2D(getX(), getY());
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
        rect.addSpider(this);
    }


    @Override
    public void makeAbstractRepresentation() {
        if (! isAbstractRepresentationSyntaxUpToDate()) {
            Spider result = new Spider();
            if (hasLabel()) {
                result.setLabel(labelText());
            }
            setAbstractSyntaxRepresentation(result);
        }
    }

    @Override
    public void makeConcreteRepresentation() {
        if(hasChangedOnScreen()) {
            final Circle theSpider = new Circle(spiderRadius);
            theSpider.setX(getCentrePoint().getX()).setY(getCentrePoint().getY());
            theSpider.setFillColor(getFillColour());
            theSpider.setStrokeColor(getBorderColour());
            theSpider.setDraggable(false);

            theSpider.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    theSpider.setStrokeColor(spiderSelectedColor);
                    theSpider.setFillColor(spiderSelectedColor);
                    setIsUnderMouse();
                    theSpider.getLayer().batch();
                }
            });
            theSpider.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    theSpider.setStrokeColor(getBorderColour());
                    theSpider.setFillColor(getFillColour());
                    theSpider.getLayer().batch();
                }
            });

            setConcreteRepresentation(theSpider);
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
    }

    public void setAsSelected() {}

    public void deleteMe() {
        getConcreteRepresentation().setListening(false);  // seems this is required otherwise it crashes trying to
        // respond to events while deleting

        getBoundaryRectangle().getCurveLayer().remove(getConcreteRepresentation());
        getBoundaryRectangle().removeSpider(this);
    }
}

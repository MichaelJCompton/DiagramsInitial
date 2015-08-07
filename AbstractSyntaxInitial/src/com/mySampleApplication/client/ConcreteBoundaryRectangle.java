package com.mySampleApplication.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import michael.com.BoundaryRectangle;

/**
 * Created by Michael on 6/08/2015.
 */
public class ConcreteBoundaryRectangle extends ConcreteSyntaxElement {

    private static final double borderWidth = 1;

    private double width, height;


    ConcreteBoundaryRectangle () {
        super();
    }

    ConcreteBoundaryRectangle(double x, double y) {
        super(x,y);
    }


    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }


    @Override
    public void makeAbstractRepresentation() {
        if (! isAbstractRepresentationSyntaxUpToDate()) {
            BoundaryRectangle result = new BoundaryRectangle();
            setAbstractSyntaxRepresentation(result);
        }
    }

    @Override
    public void makeConcreteRepresentation() {
        if(hasChangedOnScreen()) {
            Rectangle result = new Rectangle(getX(), getY());
            result.setStrokeWidth(borderWidth);
            result.setWidth(width);
            result.setHeight(height);
            result.setFillColor(getFillColour());
            result.setStrokeColor(getBorderColour());
            setConcreteRepresentation(result);
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
    }
}

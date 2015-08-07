package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import michael.com.Curve;
import michael.com.Spider;

/**
 * Created by Michael on 6/08/2015.
 */
public class ConcreteSpider extends ConcreteSyntaxElement {


    private static final double radius = 7;

    ConcreteSpider () {
        super();
        setFillColour(new Color(0,0,0));
    }

    ConcreteSpider(double x, double y) {
        super(x,y);
        setFillColour(new Color(0,0,0));
    }


    //public void setRadius(double radius) { this.radius = radius; }



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
            final Circle result = new Circle(radius);
            result.setX(getX()).setY(getY());
            //result.setFillColor(ColorName.BLACK);
            result.setFillColor(getFillColour());
            result.setStrokeColor(getBorderColour());
            result.setDraggable(false);


            result.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    result.setStrokeColor(ColorName.RED);
                    result.setFillColor(ColorName.RED);
                    result.getLayer().batch();
                }
            });
            result.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    result.setStrokeColor(getBorderColour());
                    result.setFillColor(getFillColour());
                    result.getLayer().batch();
                }
            });

            setConcreteRepresentation(result);
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(getConcreteRepresentation());
    }
}

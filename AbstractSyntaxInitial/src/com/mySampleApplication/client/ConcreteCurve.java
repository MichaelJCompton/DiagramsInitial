package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import michael.com.AbstractDiagram;
import michael.com.Curve;

/**
 * Created by Michael on 6/08/2015.
 */
public class ConcreteCurve extends ConcreteSyntaxElement {


    private static final double cornerRadius = 5;
    private static final double borderWidth = 2;

    private double width, height;


    public ConcreteCurve () {
        super();
    }

    public ConcreteCurve(double x, double y) {
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

            final Rectangle theCurve = new Rectangle(width, height, cornerRadius);
            theCurve.setStrokeWidth(borderWidth);
            theCurve.setX(getX());
            theCurve.setY(getY());
            theCurve.setFillColor(getFillColour());
            theCurve.setStrokeColor(getBorderColour());
            theCurve.setDraggable(false);


            theCurve.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    theCurve.setStrokeColor(ColorName.RED);

                    theCurve.getLayer().batch();
                }
            });
            theCurve.addNodeMouseExitHandler(new NodeMouseExitHandler() {
                @Override
                public void onNodeMouseExit(NodeMouseExitEvent event) {
                    theCurve.setStrokeColor(getBorderColour());

                    theCurve.getLayer().batch();
                }
            });

            labelText = new  Text("My Curve").setFillColor(ColorName.BLACK).setX(getX()).setY(getY() - 10).setFontSize(7);
            labelText.setEditable(true);

            // FIXME maybe better if the concrete representation is something like a node, so I can have groups in there??
            setConcreteRepresentation(theCurve);
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {
        //Group result = new Group();
        //result.add(theCurve).add(labelText);
        layer.add(getConcreteRepresentation());
        layer.add(labelText);
    }


}

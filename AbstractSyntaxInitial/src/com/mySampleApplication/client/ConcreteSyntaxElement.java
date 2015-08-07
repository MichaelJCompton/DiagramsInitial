package com.mySampleApplication.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import michael.com.AbstractDiagram;
import michael.com.DiagramElement;

import java.util.AbstractSet;

import com.ait.lienzo.shared.core.types.Color;

/**
 * Created by Michael on 6/08/2015.
 */
public abstract class ConcreteSyntaxElement {

    private Boolean onScreenChanged;
    private Boolean abstractSyntaxUpToDate;

    private AbstractSet<ConcreteSyntaxElement> intersectsWith;

    // Maybe need to clean up the types around this.
    private DiagramElement abstractSyntaxRepresentation;
    protected Text labelText;

    private double xCoord, yCoord;
    private double boundingBoxX, getBoundingBoxY;  // FIXME : the shape gets this for free in Lienzo

    private Color borderColour;
    private Color fillColour;

    private String label;
    private double labelTextSize;
    private double labelX, labelY;  //relative to elements x,y ??

    private Shape concreteRepresentation;

    ConcreteSyntaxElement() {
        setHasChangedOnScreen();
        setAbstractSyntaxNOTUpToDate();
        //setFillColour(new Color(255, 255, 255));
        setBorderColour(new Color(0, 0, 0));
    }

    ConcreteSyntaxElement(double x, double y) {
        xCoord = x;
        yCoord = y;
        setHasChangedOnScreen();
        setAbstractSyntaxNOTUpToDate();
        //setFillColour(new Color(255, 255, 255));
        setBorderColour(new Color(0, 0, 0));
    }

    public boolean hasChangedOnScreen() {
        return onScreenChanged;
    }

    public void setHasChangedOnScreen() {
        onScreenChanged = true;
    }

    public void setHasNOTChangedOnScreen() {
        onScreenChanged = false;
    }

    public Boolean isAbstractRepresentationSyntaxUpToDate() {
        return abstractSyntaxUpToDate;
    }

    protected void setAbstractSyntaxUpToDate() {
        abstractSyntaxUpToDate = true;
    }

    protected void setAbstractSyntaxNOTUpToDate() {
        abstractSyntaxUpToDate = false;
    }

    protected void setAbstractSyntaxRepresentation(DiagramElement representation) {
        abstractSyntaxRepresentation = representation;
        setAbstractSyntaxUpToDate();
    }

    public DiagramElement getAbstractSyntaxRepresentation() {
        return abstractSyntaxRepresentation;
    }

    public void setConcreteRepresentation(Shape s) {
        concreteRepresentation = s;
        setHasNOTChangedOnScreen();
    }

    public Shape getConcreteRepresentation() {
        return concreteRepresentation;
    }

    public String labelText() {
        return label;
    }

    protected Color getBorderColour() {
        return borderColour;
    }

    protected void setBorderColour(Color colour) {
        borderColour = colour;
    }

    protected Color getFillColour() {
        return fillColour;
    }

    protected void setFillColour(Color colour) {
        fillColour = colour;
    }

    public Boolean hasLabel() {
        return !(label == null);
    }

    public double getX() {
        return xCoord;
    }

    public double getY() {
        return yCoord;
    }


    public void setMyDiagram(AbstractDiagram diagram) {
        getAbstractSyntaxRepresentation().setParent(diagram);
    }



    public abstract void makeAbstractRepresentation();
    public abstract void makeConcreteRepresentation();
    public abstract void drawOnLayer(Layer layer);

}

package com.mySampleApplication.client;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.ColorName;
import michael.com.AbstractDiagram;
import michael.com.DiagramElement;

import java.util.AbstractSet;

import com.ait.lienzo.shared.core.types.Color;

/**
 * Created by Michael on 6/08/2015.
 */
public abstract class ConcreteSyntaxElement {

    // FIXME : use point2d for (x,y)


    protected enum ConcreteSyntaxElement_TYPES {
        CONCRETEARROW, CONCRETEBOUNDARYRECTANGE, CONCRETECURVE, CONCRETESPIDER, CONCRETEZONE
    }
    private ConcreteSyntaxElement_TYPES myType;


    protected static final Color visibleWhite = new Color(255,255,255,1);     // 1 = invisible
    protected static final Color invisibleWhite = new Color(255,255,255,0);     // 0 = invisible


    protected static final ColorName curveBorderColourName = ColorName.BLACK;
    protected static final Color curveBorderColour = curveBorderColourName.getColor();
    protected static final Color curveFillColour = invisibleWhite;
    protected static final ColorName curveBorderSelectedColorName = ColorName.RED;
    protected static final Color curveBorderSelectedColor = curveBorderSelectedColorName.getColor();

    protected static final double curveCornerRadius = 10;
    protected static final double curveBorderWidth = 2;
    protected static final double curveMinWidth = (curveCornerRadius * 4);  // no small curves constraint
    protected static final double curveMinHeight = curveMinWidth;           // no small curves constraint


    protected static final ColorName spiderColourName = ColorName.BLACK;
    protected static final Color spiderColour = spiderColourName.getColor();
    protected static final ColorName spiderSelectedColorName = ColorName.RED;
    protected static final Color spiderSelectedColor = spiderSelectedColorName.getColor();

    protected static final double spiderRadius = 7;


    protected static final ColorName boundaryRectangleColourName = ColorName.BLACK;
    protected static final Color boundaryRectangleColour = boundaryRectangleColourName.getColor();
    protected static final Color boundaryRectangleFillColour = invisibleWhite;
    protected static final ColorName boundaryRectangleSelectedColourName = ColorName.RED;
    protected static final Color boundaryRectangleSelectedColour = boundaryRectangleSelectedColourName.getColor();

    protected static final double boundaryRectangleBorderWidth = curveBorderWidth * 2;
    protected static final double boundaryRectangleMinWidth = (curveMinWidth * 2) + (curveCornerRadius * 3);
    protected static final double boundaryRectangleBorderHeight = boundaryRectangleMinWidth;


    protected static final Color zoneStandardColour = visibleWhite;
    protected static final ColorName zoneSelectedColorName = ColorName.LIGHTGRAY;
    protected static final Color zoneSelectedColor = zoneSelectedColorName.getColor().setA(1);
    protected static final ColorName zoneShadedColorName = ColorName.DARKGRAY;
    protected static final Color zoneShadedColor = zoneShadedColorName.getColor().setA(1);

    protected static final double zoneCornerRadius = curveCornerRadius - curveBorderWidth;


    protected static final ColorName arrowColourName = ColorName.BLACK;
    protected static final Color arrowColour = arrowColourName.getColor();
    protected static final ColorName arrowSelectedColourName = ColorName.RED;
    protected static final Color arrowSelectedColour = arrowSelectedColourName.getColor();

    protected static final double arrowLineWidth = 2;
    protected static final int pointsInArrowLine = 4;


    // seems easiest way to find what is under the mouse at any point is to keep a record in the mouse handlers
    private static ConcreteSyntaxElement underMouse;

    private Boolean onScreenChanged;
    private Boolean abstractSyntaxUpToDate;

    private AbstractSet<ConcreteSyntaxElement> intersectsWith;
    protected ConcreteBoundaryRectangle myBoundaryRectangle; // everything should have only one except arrows, which pick source
            // can always get from an arrow any way with destination's boundary rectangle.

    // Maybe need to clean up the types around this.
    private DiagramElement abstractSyntaxRepresentation;
    protected Text labelText;

    private double xCoord, yCoord;
    //private double boundingBoxX, boundingBoxY;  // FIXME : the shape gets this for free in Lienzo

    private Color borderColour, borderSelectedColour;
    private Color fillColour, fillSelectedColour;

    private String label;
    private double labelTextSize;
    private double labelX, labelY;  //relative to elements x,y ??


    // should this be a node ??
    private Shape concreteRepresentation;

//    ConcreteSyntaxElement() {
//        setHasChangedOnScreen();
//        setAbstractSyntaxNOTUpToDate();
//        //setFillColour(new Color(255, 255, 255));
//        //setBorderColour(new Color(0, 0, 0));  // FIXME : to constants
//    }

    ConcreteSyntaxElement(double x, double y, ConcreteSyntaxElement_TYPES type) {
        xCoord = x;
        yCoord = y;
        setType(type);
        setHasChangedOnScreen();
        setAbstractSyntaxNOTUpToDate();
        //setFillColour(new Color(255, 255, 255));
        // it's the alpha value on the constructor that makes it invisible = 0
        //setBorderColour(new Color(0, 0, 0));  // FIXME : to constants
    }

    private void setType(ConcreteSyntaxElement_TYPES newType) {
        myType = newType;
    }

    public ConcreteSyntaxElement_TYPES getType() {
        return myType;
    }

    public abstract void setBoundaryRectangle(ConcreteBoundaryRectangle rect);// {
//        myBoundaryRectangle = rect;
//        rect.addChild(this);
//    }

    public ConcreteBoundaryRectangle getBoundaryRectangle() {
        return myBoundaryRectangle;
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

    protected Color getBorderSelectedColour() {
        return borderSelectedColour;
    }

    protected void setBorderSelectedColour(Color colour) {
        borderSelectedColour = colour;
    }

    protected Color getFillSelectedColour() {
        return fillSelectedColour;
    }

    protected void setFillSelectedColour(Color colour) {
        fillSelectedColour = colour;
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

    protected void setIsUnderMouse() {
        underMouse = this;
    }

    public static ConcreteSyntaxElement getElementUnderMouse() {
        return underMouse;
    }

    protected void paintColoursOnConcreteRepresentation() {
        concreteRepresentation.setStrokeColor(getBorderColour());
        concreteRepresentation.setFillColor(getFillColour());
    }

    protected void paintSelectedColoursOnConcreteRepresentation() {
        concreteRepresentation.setStrokeColor(getBorderSelectedColour());
        concreteRepresentation.setFillColor(getFillSelectedColour());
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

    public Point2D topLeft() {
        return new Point2D(getX(), getY());
    }

    public void setMyDiagram(AbstractDiagram diagram) {
        getAbstractSyntaxRepresentation().setParent(diagram);
    }

    // only really used for zones, but easier to implement like this than investigating types at runtime
    public void swapShading() {}

    public abstract void makeAbstractRepresentation();
    public abstract void makeConcreteRepresentation();
    public abstract void drawOnLayer(Layer layer);
    public abstract void setAsSelected();
    public abstract void deleteMe();

}

package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import michael.com.BoundaryRectangle;

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * .
 */
public class ConcreteBoundaryRectangle extends ConcreteRectangularSyntaxElement {


    // Which zones are 'on top' for spider zone detection and also drawing.  Taking advantage of Lienzo's drawing and
    // picking algorithms to draw zones in order such that we can find and pick the visible parts of a zone.  This is
    // partly because I'm not drawing all the different shaped zones we could get, just (possibly-)curved cornered
    // rectangles and then using the drawing to occlude with covering zones to make the shapes.
    private AbstractList<AbstractSet<ConcreteZone>> zoneHeights;

    //private AbstractSet<ConcreteSyntaxElement> myChildren;
    private AbstractSet<ConcreteSpider> mySpiders;
    private AbstractSet<ConcreteCurve> myCurves;
    private AbstractSet<ConcreteArrow> myArrows;

    private Layer myLayer;
    private Layer curveLayer;
    private Layer zoneLayer;

    ConcreteBoundaryRectangle(double x, double y) {
        super(x, y, ConcreteSyntaxElement_TYPES.CONCRETEBOUNDARYRECTANGE);
        setBorderColour(boundaryRectangleColour);
        setFillColour(boundaryRectangleFillColour);
        setBorderSelectedColour(boundaryRectangleSelectedColour);
        setFillSelectedColour(boundaryRectangleFillColour);
        setBorderWidth(boundaryRectangleBorderWidth);

        zoneHeights = new ArrayList<AbstractSet<ConcreteZone>>();
        mySpiders = new HashSet<ConcreteSpider>();
        myCurves = new HashSet<ConcreteCurve>();
        myArrows = new HashSet<ConcreteArrow>();

        setBoundaryRectangle(this);
    }


    public void setCurveLayer(Layer layer) {
        curveLayer = layer;
    }

    protected Layer getCurveLayer() {
        return curveLayer;
    }

    public void setZoneLayer(Layer layer) {
        zoneLayer = layer;
    }

    public Layer getZoneLayer() {
        return zoneLayer;
    }

    public void setBoundaryRectangleLayer(Layer layer) {
        myLayer = layer;
    }

    public Layer getBoundaryRectangleLayer() {
        return myLayer;
    }

    public Layer getDragLayer() {
        return myLayer.getViewport().getDragLayer();
    }

//    public void addChild(ConcreteSyntaxElement child) {
//        getAllChildren().add(child);
//    }
//
//    public void removeChild(ConcreteSyntaxElement child) {
//        getAllChildren().remove(child);
//    }

    protected AbstractSet<ConcreteSyntaxElement> getAllChildren() {
        AbstractSet<ConcreteSyntaxElement> result = new HashSet<ConcreteSyntaxElement>();
        result.addAll(getCurves());
        result.addAll(getSpiders());
        result.addAll(getArrows());
        result.addAll(getZones());
        return result;
    }

    public AbstractSet<ConcreteSpider> getSpiders() {
        return mySpiders;
    }

    public AbstractSet<ConcreteCurve> getCurves() {
        return myCurves;
    }

    public AbstractSet<ConcreteArrow> getArrows() {
        return myArrows;
    }

    public AbstractSet<ConcreteZone> getZones() {
        AbstractSet<ConcreteZone> result = new HashSet<ConcreteZone>();
        for(AbstractSet<ConcreteZone> zones : getZoneHeights()) {
            result.addAll(zones);
        }
        return result;
    }

    public void addCurve(ConcreteCurve curve) {
        getCurves().add(curve);
    }

    public void removeCurve(ConcreteCurve curve) {
        getCurves().remove(curve);
    }

    public void addSpider(ConcreteSpider spider) {
        getSpiders().add(spider);
    }

    public void removeSpider(ConcreteSpider spider) {
        getSpiders().remove(spider);
    }

    public void addArrow(ConcreteArrow arrow) {
        getArrows().add(arrow);
    }

    public void removeArrow(ConcreteArrow arrow) {
        getArrows().remove(arrow);
    }


    private AbstractList<AbstractSet<ConcreteZone>> getZoneHeights() {
        return zoneHeights;
    }

    public void addZone(int level, ConcreteZone zone) {
        if(getZoneHeights().size() <= level) {
            // need to extend the list to accommodate
            for(int i = getZoneHeights().size(); i <= level; i++) {
                getZoneHeights().add(new HashSet<ConcreteZone>());
            }
        }

        getZoneHeights().get(level).add(zone);
    }

    public void removeZone(int level, ConcreteZone zone) {
        if(level < getZoneHeights().size()) {
            getZoneHeights().get(level).remove(zone);
        }
    }

    @Override
    public void setBoundaryRectangle(ConcreteBoundaryRectangle rect) {
        myBoundaryRectangle = rect;
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
            final Rectangle theRectangle = new Rectangle(getWidth(), getHeight());
            setConcreteRepresentation(theRectangle);
            setupConcreteRepresentation();

            theRectangle.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
                @Override
                public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                    setIsUnderMouse();
                }
            });
        }
    }

    @Override
    public void drawOnLayer(Layer layer) {

        getZoneLayer().clear();
        //layer.clear();
        getBoundaryRectangleLayer().add(getConcreteRepresentation());
        setCurveLayer(layer);

        // now draw all the children in the right order.
        // FIXME : eventually it would be better if these were split by type, for this first cut, I'll just sort them

        AbstractSet<ConcreteZone> allZones = getZones();  // save recomputing it
//        for(AbstractSet<ConcreteZone> zones : getZoneHeights()) {
//            allZones.addAll(zones);
//
//            for(ConcreteZone z : zones) {
//                //layer.remove(z.getConcreteRepresentation());
//            }
//        }

        AbstractSet<ConcreteSyntaxElement> drawLast = new HashSet<ConcreteSyntaxElement>();
        for(ConcreteSyntaxElement elmnt : getAllChildren()) {
            if(!allZones.contains(elmnt) && elmnt != this) {
                drawLast.add(elmnt);
                //layer.remove(elmnt.getConcreteRepresentation());   // FIXME : element should remove itself I think
            }
        }
//
////        // draw the zones
        for(AbstractSet<ConcreteZone> zones : getZoneHeights()) {
            for(ConcreteZone z : zones) {
                z.drawOnLayer(getZoneLayer());
            }
        }

        //draw the rest
        for(ConcreteSyntaxElement elmnt : drawLast) {
            elmnt.drawOnLayer(layer);
        }

        getZoneLayer().batch();
        getZoneLayer().batch();
        getCurveLayer().batch();
    }

    public void deleteMe() {
        // not yet
    }
}

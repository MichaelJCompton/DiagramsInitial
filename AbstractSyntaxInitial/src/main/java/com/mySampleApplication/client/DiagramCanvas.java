package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Point2DArray;
import com.ait.lienzo.client.widget.LienzoPanel;

import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.*;

/**
 * The Canvas has the responsibility for the placement and drawing of the concrete elements.  It controls the zoom
 * and pan, and also draws the miniature map representation.
 *
 * Each Canvas could have one diagram (making it represent a single class or datatype diagram in the abstract syntax)
 * or could have multiple diagrams (multiple boundary rectangles) on it (making the whole thing a concept diagram).
 *
 * There could be multiple canvases in an application, each drawing some different aspect of the ontology.  Each canvas
 * is independent of the others.
 */
public class DiagramCanvas {

    // make the concrete elements parametric on the main type of thing they represent

    // TODO :
    // - 1) calcuate curve intersections  ... works, but not for multi curve intersections & some highlights don't quite work e.g. one curve contained in other, the inner should blank the highlight of the outer
    // - 2) calculate abstract zones
    // - 3) editable/moveable  curves (drag and resize dots)
    // - 4) attach labels
    // - 4) editable labels
    // - all inside a boundary rectangle .. maybe all the shapes in here should be relative to the shapes x,y , like a
    //      group??? so a place on boundry would adjust the x,y ... when pick up group them and move together??
    // - set drag bounds to keep things inside the boundary rectangle c.setDragBounds(new DragBounds(x1, y1, x2, y2));
    // - add new boundary rectangles - only on clean space (use min size)
    // - 5) arrows ... how to do snapping ... and then dragging when the curves/spiders are moved
    //          drawArcJoinedLines in Geometry might help
    // - 6) group select and move
    // - 7) zoom
    // - 7) pan
    // - smaller map representation (just of the curves I think)
    // - how to adjust so that everything is clickable even if another curve is drawn over
    //  i.e. at the moment draw inside curve, then outside curve and the inside isn't selectable.s
    //  they have move up and down functions
    // - enforce a minimum size for curves 2*radius plus a bit (no small curves constraint)
    // - enforce snapping so that intersections are at least 2*radius, i.e. a curcle at the corner (no small intersections constraint)
    // - refactor to have zones, boundary and curves, inheret from a common to cut down on some duplicate code; not essential, but they are rectangular things .. but then the intersection code could all be in one place


    // TODO : Open Questions
    //
    // - How to handle moving a curve that has a shaded zone in it?  If it's the curve that's easy enough.  If it's in
    //      an intersection and the whole intersection moves, that's fine too.  But how to handle moves otherwise.
    //      I would say remove it, but what if the move was just adjusting the curve a bit to make the zone biggier/smaller???
    //      First cut move a curve, just delete all the zones then add back at the new location
    // - How to resize the panel & have a view port that is only part of the panel?  Can't see methods to resize a
    //      panel at the moment ... maybe the idea is start with something say 2X the width and hight of the screen
    //      space and then if we need to resize it, then have to move everything to a new panel of the correct size
    //      and swap the panels?  So panel would be created, then us the view port to zoom in on part of it?
    //      When all this is going on, I'll have to convert viewport coordinates to layer coordinates
    // - When to find the zone a spider resides in?  If it's done on placement of the spider it needs to promoted
    //      if another curve is drawn and makes a zone covering that spider.  But do I need to know the zone of a spider
    //      in the concrete syntax?  Maybe only to ensure that two spiders with a line between them don't appear in
    //      the one zone, but that's probably not so important at the moment.





    //private AbstractSet<ConcreteSyntaxElement> elementsOnCanvas;

    // things that have changed in some way to make them obsolete and in need of deleting in the abstract syntax once
    // that is refreshed
    private AbstractSet<ConcreteSyntaxElement> deleteSet;

    // just for curves.  I think I get the intersections the way we need them either for free or pretty quickly
    // for everything else on the canvas.
    //
    // Lists because the size can change as the layer gets bigger because of pans and more things being drawn
    // FIXME : this should be per boundary rectangle in the end
    private List<List<AbstractSet<ConcreteCurve>>> intersectionGrid;
    private static final double gridSquareSize = 400;

    // need all this to be dynamic in the end
    private static final int panelWidth = 900;
    private static final int panelHeight = 600;

    private static final double initBoundaryRectangleXoffset = 20;
    private static final double initBoundaryRectangleYoffset = 20;
    private static final double initBoundaryRectangleWidth = panelWidth - (2 * initBoundaryRectangleXoffset);
    private static final double initBoundaryRectangleHeight = panelHeight - (2 * initBoundaryRectangleYoffset);


    private static final ColorName rubberbandRetangleColorName = ColorName.LIGHTSLATEGREY;
    private static final Color rubberbandRetangleColor = rubberbandRetangleColorName.getColor();

    private AbstractSet<ConcreteSyntaxElement> selectedElements;

    // FIXME : need to remove once we have more than one
    private ConcreteBoundaryRectangle initialBoundaryRectangle;
    private AbstractSet<ConcreteBoundaryRectangle> boundaryRectangles;

    private enum ModeTypes {
        SELECTION, DRAGSELECT, PAN, PANNING, ZOOM, ZOOMING,
        DRAWCURVE, DRAWINGCRVE, DRAWSPIDER, DRAWINGSPIDER, SHADE,
        DRAWARROW, DRAWINGARROW, DRAGINGARROW,
        DRAWBOUNDARYRECTANGLE, DRAWINGBOUNDARYRECTANGLE,
        DELETE
    }
    private ModeTypes mode;

    private int clickX, clickY;

    private Rectangle rubberbandRectangle;
    private Circle rubberbandSpider;
    private Line rubberbandArrow;
    private ConcreteSyntaxElement arrowSource;

    private LienzoPanel panel;
    private Layer boundaryRectangleLayer;
    private Layer curveLayer;
    private Layer zoneLayer;
    private VerticalPanel mainPanel;
    private HorizontalPanel buttonPanel;

    // quick debugging output
    private Label textOutLabel;
    private DateTimeFormat dateFormat;


    public DiagramCanvas() {
        //elementsOnCanvas = new HashSet<ConcreteSyntaxElement>();
        boundaryRectangles = new HashSet<ConcreteBoundaryRectangle>();
        mode = ModeTypes.SELECTION;
        clearSelection();

        createCanvas();
    }

    private void createCanvas() {

        mainPanel = new VerticalPanel();
        buttonPanel = new HorizontalPanel();
        textOutLabel = new Label();

        // need to size this correctly ... but how???
        panel = new LienzoPanel(panelWidth, panelHeight);

        // Display timestamp showing last refresh.
        dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        textOutLabel.setText("Nothing to say yet. " + dateFormat.format(new Date()));

        addToolMenu(buttonPanel);

        curveLayer = new Layer();
        addLayerHandlers();
        panel.add(curveLayer);

        zoneLayer = new Layer();
        panel.add(zoneLayer);
        boundaryRectangleLayer = new Layer();
        panel.add(boundaryRectangleLayer);

        zoneLayer.moveToTop();
        curveLayer.moveToTop();

        addInitialBoundaryRectangle(curveLayer, zoneLayer, boundaryRectangleLayer);

        mainPanel.add(buttonPanel);
        mainPanel.add(panel);
        mainPanel.add(textOutLabel);
        RootPanel.get().add(mainPanel);

        // create the grid for faster intersection checking
        intersectionGrid = new ArrayList<List<AbstractSet<ConcreteCurve>>>();
        for(int i = 0; i < (int) Math.ceil(curveLayer.getHeight() / gridSquareSize); i++) {
            intersectionGrid.add(new ArrayList<AbstractSet<ConcreteCurve>>());
            for(int j = 0; j < (int) Math.ceil(curveLayer.getWidth() / gridSquareSize); j++) {
                intersectionGrid.get(i).add(new HashSet<ConcreteCurve>());
            }
        }

        boundaryRectangleLayer.batch();
        curveLayer.batch();
        zoneLayer.batch();
    }

    private void addInitialBoundaryRectangle(Layer curveLayer, Layer zoneLayer, Layer boundaryRectangleLayer) {
        initialBoundaryRectangle =
                new ConcreteBoundaryRectangle(initBoundaryRectangleXoffset, initBoundaryRectangleYoffset);
        initialBoundaryRectangle.setWidth(initBoundaryRectangleWidth);
        initialBoundaryRectangle.setHeight(initBoundaryRectangleHeight);

        initialBoundaryRectangle.makeConcreteRepresentation();
        //elementsOnCanvas.add(initialBoundaryRectangle);
        initialBoundaryRectangle.setZoneLayer(zoneLayer);
        initialBoundaryRectangle.setBoundaryRectangleLayer(boundaryRectangleLayer);
        initialBoundaryRectangle.drawOnLayer(curveLayer);
    }

    private void addCurve(double x, double y, double width, double height) {
        ConcreteCurve curve = new ConcreteCurve(x, y);
        curve.setHeight(height);
        curve.setWidth(width);
        curve.setBoundaryRectangle(initialBoundaryRectangle);
        curve.makeConcreteRepresentation();

        // ConcreteZone zone = curve.makeMainZone();
        // zone.makeConcreteRepresentation();

        //elementsOnCanvas.add(curve);
        //elementsOnCanvas.add(curve.getMainZone());

        addCurveToIntersectionGrid(curve);

        // now work out the intersections with existing curves and add the resulting zones
        HashSet<ConcreteCurve> intersectingCurves = new HashSet<ConcreteCurve>();
        for(int i = 0; i < intersectionGrid.size(); i++) {
            for (int j = 0; j < intersectionGrid.get(i).size(); j++) {
                if(intersectionGrid.get(i).get(j).contains(curve)) {
                     for(ConcreteCurve other : intersectionGrid.get(i).get(j)) {
                        if(other != curve) {
                            if(curve.intersectsRectangularElement(other)) {
                                intersectingCurves.add(other);
                                curve.addIntersectingCurve(other);
                            }
                        }
                    }

                }
            }
        }
        curve.computeAllIntersections(intersectingCurves);

        curve.drawOnLayer(curveLayer);
        curve.drawZonesOnLayer(curveLayer);
        // redraw the whole boundary rectangle that this is on ... maybe with connected components or something could make nicer
        // FIXME : need to pick the rectangle here once we have many
        curve.getBoundaryRectangle().drawOnLayer(curveLayer);

    }
//
//    private void computeAllZoneIntersections(ConcreteCurve curve, AbstractSet<ConcreteCurve> curves) {
//        for(ConcreteCurve other : curves) {
//            if(other != curve) {
//                if(curve.intersectsCurve(other)) {
//                    curve.computeIntersection(other);
//                }
//            }
//        }
//    }

    private void addCurveToIntersectionGrid(ConcreteCurve curve) {
        for(int i = 0; i < intersectionGrid.size(); i++) {
            for(int j = 0; j < intersectionGrid.get(i).size(); j++) {
                if(curveIsInIntersectionGridSquare(curve, j, i)) {
                    intersectionGrid.get(i).get(j).add(curve);
                }
            }
        }
    }

    private boolean curveIsInIntersectionGridSquare(ConcreteCurve curve, int across, int down) {
        return (pointInGridSquare(curve.topLeft(), across, down) ||
                pointInGridSquare(curve.topRight(), across, down) ||
                pointInGridSquare(curve.bottomLeft(), across, down) ||
                pointInGridSquare(curve.bottomRight(), across, down));
    }

    public boolean pointInGridSquare(Point2D p, int across, int down) {
        return ConcreteRectangularSyntaxElement.rectangleContainment(p,
                new Point2D(across * gridSquareSize, down * gridSquareSize),
                gridSquareSize,
                gridSquareSize);
    }

    public void compileAllToAbstractSyntax() {
        //for(ConcreteSyntaxElement e : elementsOnCanvas) {
          //  e.makeAbstractRepresentation();
        //}
    }

    public void clearSelection() {
        selectedElements = new HashSet<ConcreteSyntaxElement>();
    }

    public AbstractSet<ConcreteSyntaxElement> getSelectedElements() {
        return selectedElements;
    }

    public void addSelectedElement(ConcreteSyntaxElement elmnt) {
        getSelectedElements().add(elmnt);
    }

    private void addLayerHandlers() {

        // FIXME : ...maybe all things should just be dragable false anyway, cause I want to implement my own dragging

        curveLayer.addNodeMouseDownHandler(new NodeMouseDownHandler() {
            @Override
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                clickX = event.getX();
                clickY = event.getY();

                if (event.isButtonLeft()) {

                    textOutLabel.setText("Left Click Event at: (" + event.getX() + "," + event.getY() + ")" + dateFormat.format(new Date()));

                    switch (mode) {
                        case SELECTION:
                            mode = ModeTypes.DRAGSELECT;
                            startRubberBandRectangle();
                            break;
                        case PAN:
                            mode = ModeTypes.PANNING;
                            break;
                        case DRAWCURVE:
                            mode = ModeTypes.DRAWINGCRVE;
                            startRubberBandRectangle();
                            break;
                        case DRAWSPIDER:
                            mode = ModeTypes.DRAWINGSPIDER;
                            startRubberBandSpider();
                            break;
                        case DRAWBOUNDARYRECTANGLE:
                            mode = ModeTypes.DRAWINGBOUNDARYRECTANGLE;
                            startRubberBandRectangle();
                            break;
                        case DRAWARROW:
                            if(ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETECURVE
                                    || ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETESPIDER) {
                                mode = ModeTypes.DRAWINGARROW;
                                startRubberBandArrow();
                                arrowSource = ConcreteSyntaxElement.getElementUnderMouse();
                            }
                            break;
                    }
                }
                curveLayer.batch();
            }
        });

        curveLayer.addNodeMouseMoveHandler(new NodeMouseMoveHandler() {
            @Override
            public void onNodeMouseMove(NodeMouseMoveEvent event) {
                int x = event.getX();
                int y = event.getY();

                switch (mode) {
                    case DRAGSELECT:
                        dragRubberBandRectangle(x, y);
                        break;
                    case DRAWINGCRVE:
                        dragRubberBandRectangle(x, y);
                        break;
                    case DRAWINGSPIDER:
                        dragRubberBandSpider(x, y);
                        break;
                    case DRAWINGBOUNDARYRECTANGLE:
                        dragRubberBandRectangle(x, y);
                        break;
                    case DRAWINGARROW:
                        dragRubberBandArrow(x, y);
                        break;
                }
                curveLayer.batch();
            }

        });

        curveLayer.addNodeMouseUpHandler(new NodeMouseUpHandler() {
            @Override
            public void onNodeMouseUp(NodeMouseUpEvent event) {
                int x = event.getX();
                int y = event.getY();

                if (event.isButtonLeft()) {

                    switch (mode) {
                        case DRAGSELECT:
                            // FIXME : selection not implemented
                            // how do I know if it's shift or not???

                            clearSelection();
                            // might have been a drag select or a regular click, check which
                            if (x == clickX && y == clickY) {

                                if(ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETEARROW) {
                                    mode = ModeTypes.DRAGINGARROW;
                                    addSelectedElement(ConcreteSyntaxElement.getElementUnderMouse());
                                    (ConcreteSyntaxElement.getElementUnderMouse()).setAsSelected();
                                }

                                // find what's under the mouse
                                // could do this in reverse and have objects register that they are under the mouse in the mouse over routines
                                //ConcreteSyntaxElement.getElementUnderMouse().swapShading();
                            } else {
                                //drag select
                            }

                            panel.getDragLayer().remove(rubberbandRectangle);
                            break;
                        case DRAWINGCRVE:
                            mode = ModeTypes.DRAWCURVE;

                            panel.getDragLayer().remove(rubberbandRectangle);

                            // no small curves constraint
                            if (Math.abs(x - clickX) > ConcreteSyntaxElement.curveMinWidth &&
                                    Math.abs(y - clickY) > ConcreteSyntaxElement.curveMinHeight) {

                                addCurve((x >= clickX) ? clickX : x,    // x
                                        (y >= clickY) ? clickY : y,     // y
                                        (x >= clickX) ? (x - clickX) : (clickX - x),    // width
                                        (y >= clickY) ? (y - clickY) : (clickY - y));   // height
                            }
                            break;
                        case DRAWINGSPIDER:
                            mode = ModeTypes.DRAWSPIDER;

                            ConcreteSpider spider = new ConcreteSpider(x, y);
                            //elementsOnCanvas.add(spider);
                            spider.makeConcreteRepresentation();
                            panel.getDragLayer().remove(rubberbandSpider);
                            spider.drawOnLayer(curveLayer);

                            spider.setBoundaryRectangle(initialBoundaryRectangle);

                            break;
                        case DRAWINGBOUNDARYRECTANGLE:
                            mode = ModeTypes.DRAWBOUNDARYRECTANGLE;
                            // FIXME : new boundry rectangles not implemented - needs draging and zooming
                            panel.getDragLayer().remove(rubberbandRectangle);
                            break;
                        case SHADE:
                            // FIXME : This might need changing once there are multiple boundary rectangles
                            ConcreteSyntaxElement.getElementUnderMouse().swapShading();
                            break;
                        case DELETE:
                            if(ConcreteSyntaxElement.getElementUnderMouse().getType() != ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETEZONE) {
                                ConcreteSyntaxElement.getElementUnderMouse().deleteMe();
                                // incase it's a curve
                                for(int i = 0; i < intersectionGrid.size(); i++) {
                                    for(int j = 0; j < intersectionGrid.get(i).size(); j++) {
                                        intersectionGrid.get(i).get(j).remove(ConcreteSyntaxElement.getElementUnderMouse());
                                    }
                                }
                            }
                            break;
                        case DRAWINGARROW:
                            if(ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETECURVE
                                    || ConcreteSyntaxElement.getElementUnderMouse().getType() == ConcreteSyntaxElement.ConcreteSyntaxElement_TYPES.CONCRETESPIDER) {
                                drawArrow(x, y, arrowSource, ConcreteSyntaxElement.getElementUnderMouse());
                            }
                            mode = ModeTypes.DRAWARROW;
                            panel.getDragLayer().remove(rubberbandArrow);
                            break;
                    }
                }
                panel.getDragLayer().batch();
                curveLayer.batch();
            }
        });

    }

    // assumes that a clickX and clickY have been set
    private void startRubberBandRectangle() {
        rubberbandRectangle = new Rectangle(0, 0);
        rubberbandRectangle.setX(clickX).setY(clickY);
        panel.getDragLayer().add(rubberbandRectangle);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandRectangle(int newX, int newY) {
        double newWidth = (newX >= clickX) ? (newX - clickX) : (clickX - newX);
        double newHeight = (newY >= clickY) ? (newY - clickY) : (clickY - newY);
        rubberbandRectangle.setHeight(newHeight);
        rubberbandRectangle.setWidth(newWidth);

        rubberbandRectangle.setX((newX >= clickX) ? clickX : newX);
        rubberbandRectangle.setY((newY >= clickY) ? clickY : newY);

        panel.getDragLayer().batch();
    }

    private void startRubberBandSpider() {
        rubberbandSpider = new Circle(ConcreteSyntaxElement.spiderRadius);
        rubberbandSpider.setX(clickX).setY(clickY);
        panel.getDragLayer().add(rubberbandSpider);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandSpider(int newX, int newY) {
        rubberbandSpider.setX(newX).setY(newY);
        panel.getDragLayer().batch();
    }

    private void startRubberBandArrow() {
        rubberbandArrow = new Line(new Point2D(clickX, clickY), new Point2D(clickX, clickY));
        panel.getDragLayer().add(rubberbandArrow);
        panel.getDragLayer().batch();
    }

    private void dragRubberBandArrow(int newX, int newY) {
        rubberbandArrow.setPoints(new Point2DArray(new Point2D(clickX, clickY), new Point2D(newX, newY)));
        panel.getDragLayer().batch();
    }

    private void drawArrow(int endX, int endY, ConcreteSyntaxElement source, ConcreteSyntaxElement target) {
        ConcreteArrow arrow = new ConcreteArrow(clickX, clickY, endX, endY);
        arrow.setBoundaryRectangle(initialBoundaryRectangle);
        arrow.makeConcreteRepresentation();
        arrow.setSource(source);
        arrow.setTarget(target);
        arrow.drawOnLayer(curveLayer);
    }


    private void addToolMenu(Panel toolPanel) {


        // FIXME : ...maybe all things should just be dragable false anyway, cause I want to implement my own dragging
        //

        Button PanModeButton = new Button("Pan");
        toolPanel.add(PanModeButton);
        PanModeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.PAN;
                //for (ConcreteSyntaxElement e : elementsOnCanvas) {
                  //  e.getConcreteRepresentation().setDraggable(false);
                //}
                curveLayer.setListening(true);
            }
        });

        Button selectModeButton = new Button("Select");
        toolPanel.add(selectModeButton);
        selectModeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.SELECTION;
                //for (ConcreteSyntaxElement e : elementsOnCanvas) {
                  //  e.getConcreteRepresentation().setDraggable(true);
                //}
                //layer.draw();
            }
        });

        Button drawCurveButton = new Button("Curve");
        toolPanel.add(drawCurveButton);
        drawCurveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWCURVE;
                //for (ConcreteSyntaxElement e : elementsOnCanvas) {
                  //  e.getConcreteRepresentation().setDraggable(false);
                //}
                curveLayer.setListening(true);
            }
        });



        Button drawSpiderButton = new Button("Spider");
        toolPanel.add(drawSpiderButton);
        drawSpiderButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWSPIDER;
                curveLayer.setListening(true);
            }
        });


        Button drawArrowButton = new Button("Arrow");
        toolPanel.add(drawArrowButton);
        drawArrowButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWARROW;
                curveLayer.setListening(true);
            }
        });


        Button shadeButton = new Button("Shade");
        toolPanel.add(shadeButton);
        shadeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.SHADE;
                curveLayer.setListening(true);
            }
        });

        Button boundaryRectangleButton = new Button("Rectangle");
        toolPanel.add(boundaryRectangleButton);
        boundaryRectangleButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWBOUNDARYRECTANGLE;
                //for (ConcreteSyntaxElement e : elementsOnCanvas) {
                  //  e.getConcreteRepresentation().setDraggable(false);
                //}
                curveLayer.setListening(true);
            }
        });

        Button deleteButton = new Button("Delete");
        toolPanel.add(deleteButton);
        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DELETE;
                //for (ConcreteSyntaxElement e : elementsOnCanvas) {
                  //  e.getConcreteRepresentation().setDraggable(false);
                //}
                curveLayer.setListening(true);
            }
        });
    }
}

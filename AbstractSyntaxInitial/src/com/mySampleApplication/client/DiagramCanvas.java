package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.*;
import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.widget.LienzoPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import java.util.Date;

import java.util.AbstractSet;
import java.util.HashSet;

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

    // TODO :
    // - mouse over for curve
    // - mouse over for spider
    // - editable curves
    // - attach labels
    // - editable labels
    // - all inside a boundry rectangle
    // - set drag bounds to keep things inside the boundary rectangle
    // - add new boundry rectangles
    // - group select and move
    // - zoom
    // - pan
    // - shift click for grouping
    // - how to adjust so that everything is clickable even if another curve is drawn over
    //  i.e. at the moment draw inside curve, then outside curve and the inside isn't selectable.s

    private AbstractSet<ConcreteSyntaxElement> elementsOnCanvas;
    private ConcreteSyntaxElement selectedElement;
    private enum ModeTypes{SELECTION, PAN, ZOOM, DRAWCURVE, DRAWINGCRVE, DRAWSPIDER, DRAWINGSPIDER, DELETE}
    private ModeTypes mode;
    private double clickX, clickY;
    private Rectangle rubberbandRectangle;
    private Circle rubberbandSpider;

    private LienzoPanel panel;
    private Layer layer;
    private VerticalPanel mainPanel;
    private HorizontalPanel buttonPanel;
    private Label textOutLabel;
    private DateTimeFormat dateFormat;


    // TODO : Maybe store an underlying grid and check intersections with that, keepings lists of what's on what
    //      grid square to speed up intersection checking.

    public DiagramCanvas() {
        elementsOnCanvas = new HashSet<ConcreteSyntaxElement>();
        mode = ModeTypes.SELECTION;

        createCanvas();
    }

    private void createCanvas() {

        mainPanel = new VerticalPanel();
        buttonPanel = new HorizontalPanel();
        textOutLabel = new Label();
        // Display timestamp showing last refresh.
        dateFormat = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);
        textOutLabel.setText("Nothing to say yet. " + dateFormat.format(new Date()));
        panel = new LienzoPanel(1200, 900);


        mainPanel.add(buttonPanel);
        mainPanel.add(panel);
        mainPanel.add(textOutLabel);
        RootPanel.get().add(mainPanel);

        layer = new Layer();





        layer.addNodeMouseDownHandler(new NodeMouseDownHandler() {
            @Override
            public void onNodeMouseDown(NodeMouseDownEvent event) {
                double x = event.getX();
                double y = event.getY();

                if (event.isButtonLeft()) {

                    textOutLabel.setText("Left Click Event at: (" + event.getX() + "," + event.getY() + ")" + dateFormat.format(new Date()));
                    switch (mode) {
                        case SELECTION:
                            break;
                        case PAN:
                            break;
                        case ZOOM:
                            break;
                        case DRAWCURVE:
                            mode = ModeTypes.DRAWINGCRVE;
                            clickX = x;
                            clickY = y;
                            rubberbandRectangle = new Rectangle(0, 0);
                            rubberbandRectangle.setX(x).setY(y);
                            layer.add(rubberbandRectangle);
                            layer.draw();
                            break;
                        case DRAWINGCRVE:
                            break;
                        case DRAWSPIDER:
                            mode = ModeTypes.DRAWINGSPIDER;
                            rubberbandSpider = new Circle(7);
                            rubberbandSpider.setX(x).setY(y);
                            layer.add(rubberbandSpider);
                            layer.draw();
                            break;
                    }
                }
            }
        });

        layer.addNodeMouseMoveHandler(new NodeMouseMoveHandler() {
            @Override
            public void onNodeMouseMove(NodeMouseMoveEvent event) {
                double x = event.getX();
                double y = event.getY();


                switch (mode) {
                    case SELECTION:
                        break;
                    case PAN:
                        break;
                    case ZOOM:
                        break;
                    case DRAWCURVE:
                        break;
                    case DRAWINGCRVE:
                        double newWidth = (x >= clickX) ? (x - clickX) : (clickX - x);
                        double newHeight = (y >= clickY) ? (y - clickY) : (clickY - y);
                        rubberbandRectangle.setHeight(newHeight);
                        rubberbandRectangle.setWidth(newWidth);

                        rubberbandRectangle.setX((x >= clickX) ? clickX : x);
                        rubberbandRectangle.setY((y >= clickY) ? clickY : y);

                        layer.draw();
                        break;
                    case DRAWSPIDER:
                        break;
                    case DRAWINGSPIDER:
                        rubberbandSpider.setX(x).setY(y);
                        layer.draw();
                        break;
                }
            }

        });

        layer.addNodeMouseUpHandler(new NodeMouseUpHandler() {
            @Override
            public void onNodeMouseUp(NodeMouseUpEvent event) {
                double x = event.getX();
                double y = event.getY();

                if (event.isButtonLeft()) {

                    switch (mode) {
                        case SELECTION:
                            break;
                        case PAN:
                            break;
                        case ZOOM:
                            break;
                        case DRAWCURVE:
                            break;
                        case DRAWINGCRVE:
                            mode = ModeTypes.DRAWCURVE;
                            if(x != clickX && y != clickY) {
                                ConcreteCurve curve = new ConcreteCurve((x >= clickX) ? clickX : x, (y >= clickY) ? clickY : y);
                                elementsOnCanvas.add(curve);
                                curve.setHeight((y >= clickY) ? (y - clickY) : (clickY - y));
                                curve.setWidth((x >= clickX) ? (x - clickX) : (clickX - x));
                                curve.makeConcreteRepresentation();
                                layer.remove(rubberbandRectangle);
                                //layer.add(curve.getConcreteRepresentation());
                                curve.drawOnLayer(layer);

                                layer.draw();
                            }
                            break;
                        case DRAWSPIDER:
                            break;
                        case DRAWINGSPIDER:
                            mode = ModeTypes.DRAWSPIDER;

                            ConcreteSpider spider = new ConcreteSpider(x, y);
                            elementsOnCanvas.add(spider);
                            spider.makeConcreteRepresentation();
                            layer.remove(rubberbandSpider);
                            //layer.add(spider.getConcreteRepresentation());
                            spider.drawOnLayer(layer);
                            layer.draw();
                            break;
                    }
                }
            }
        });



        Button PanModeButton = new Button("Pan");
        buttonPanel.add(PanModeButton);
        PanModeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.PAN;
                for (ConcreteSyntaxElement e : elementsOnCanvas) {
                    e.getConcreteRepresentation().setDraggable(false);
                }
                layer.setListening(true);
            }
        });

        Button selectModeButton = new Button("Select");
        buttonPanel.add(selectModeButton);
        selectModeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.SELECTION;
                for (ConcreteSyntaxElement e : elementsOnCanvas) {
                    e.getConcreteRepresentation().setDraggable(true);
                }
                //layer.setListening(false);
                layer.draw();
            }
        });

        Button drawCurveButton = new Button("Curve");
        buttonPanel.add(drawCurveButton);
        drawCurveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWCURVE;
                for (ConcreteSyntaxElement e : elementsOnCanvas) {
                    e.getConcreteRepresentation().setDraggable(false);
                }
                layer.setListening(true);
            }
        });



        Button drawSpiderButton = new Button("Spider");
        buttonPanel.add(drawSpiderButton);
        drawSpiderButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DRAWSPIDER;
                for (ConcreteSyntaxElement e : elementsOnCanvas) {
                    e.getConcreteRepresentation().setDraggable(false);
                }
                layer.setListening(true);
            }
        });


        Button deleteButton = new Button("Delete");
        buttonPanel.add(deleteButton);
        deleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                mode = ModeTypes.DELETE;
                for (ConcreteSyntaxElement e : elementsOnCanvas) {
                    e.getConcreteRepresentation().setDraggable(false);
                }
                layer.setListening(true);
            }
        });

        panel.add(layer);
        layer.draw();
    }

    public void compileAllToAbstractSyntax() {
        for(ConcreteSyntaxElement e : elementsOnCanvas) {
            e.makeAbstractRepresentation();
        }
    }
}

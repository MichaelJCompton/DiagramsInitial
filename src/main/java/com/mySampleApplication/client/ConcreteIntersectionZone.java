package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.event.NodeMouseExitEvent;
import com.ait.lienzo.client.core.event.NodeMouseExitHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.shared.core.types.Color;

/**
 * An intersection zone is formed by the intersection of two curves and has two rounded corners and two square corners
 *
 * A zone will be drawn as two rectangles for the square edges and two circles for the round edges in a 'blank colour'
 */
public class ConcreteIntersectionZone extends ConcreteZone {


    private final Rectangle rectangleTopLeft, rectangleBotLeft, rectangleTopRight, rectangleBotRight;


    // should be iterable using find() and a predicate of 'lambda x. true'
    private final Group g = new Group();
    // probably should be able to set as the concrete representation

    ConcreteIntersectionZone(Point2D topLeft, Point2D botRight,
                             Boolean topLeftIsCircle, Boolean botLeftIsCircle,
                             Boolean topRightIsCircle, Boolean botRightIsCircle) {

        // This is the actual x,y of the zone : the offset from the enclosing curves has already been taken away
        super(topLeft.getX(), topLeft.getY());

        setWidth(botRight.getX() - topLeft.getX());
        setHeight(botRight.getY() - topLeft.getY());

        rectangleTopLeft = new Rectangle(1, 1);
        rectangleBotLeft = new Rectangle(1, 1);
        rectangleTopRight = new Rectangle(1, 1);
        rectangleBotRight = new Rectangle(1, 1);

        this.topLeftIsCircle = topLeftIsCircle;
        this.botLeftIsCircle = botLeftIsCircle;
        this.topRightIsCircle = topRightIsCircle;
        this.botRightIsCircle = botRightIsCircle;
    }



    @Override
    public void makeAbstractRepresentation() {

    }

    @Override
    public void makeConcreteRepresentation() {


        if(hasChangedOnScreen()) {
            // easy to calculate a representation with four overlapping rectangles
            // ... just round the corners where necessary

            rectangleTopLeft.setX(getX());
            rectangleTopLeft.setY(getY());
            rectangleTopLeft.setWidth(getWidth() - getCornerRadius());
            rectangleTopLeft.setHeight(getHeight() - getCornerRadius());
            if (topLeftIsCircle) {
                rectangleTopLeft.setCornerRadius(getCornerRadius());
            }

            rectangleBotLeft.setX(getX());
            rectangleBotLeft.setY(getY() + getCornerRadius());
            rectangleBotLeft.setWidth(getWidth() - getCornerRadius());
            rectangleBotLeft.setHeight(getHeight() - getCornerRadius());
            if (botLeftIsCircle) {
                rectangleBotLeft.setCornerRadius(getCornerRadius());
            }

            rectangleTopRight.setX(getX() + getCornerRadius());
            rectangleTopRight.setY(getY());
            rectangleTopRight.setWidth(getWidth() - getCornerRadius());
            rectangleTopRight.setHeight(getHeight() - getCornerRadius());
            if (topRightIsCircle) {
                rectangleTopRight.setCornerRadius(getCornerRadius());
            }

            rectangleBotRight.setX(getX() + getCornerRadius());
            rectangleBotRight.setY(getY() + getCornerRadius());
            rectangleBotRight.setWidth(getWidth() - getCornerRadius());
            rectangleBotRight.setHeight(getHeight() - getCornerRadius());
            if (botRightIsCircle) {
                rectangleBotRight.setCornerRadius(getCornerRadius());
            }
            setStandardColour();

            g.setX(0).setY(0);
            g.add(rectangleTopLeft);
            g.add(rectangleBotLeft);
            g.add(rectangleTopRight);
            g.add(rectangleBotRight);
            g.setListening(true);
            g.setDraggable(false);
            // now add the handlers to the group

            attachHandlers(rectangleTopLeft);
            attachHandlers(rectangleBotLeft);
            attachHandlers(rectangleTopRight);
            attachHandlers(rectangleBotRight);
        }
    }

    // I wanted to attach these handlers to the group, but I can't seem to get it to catch the event?  Maybe it
    // doesn't work the same way for groups as for shapes??
    private void attachHandlers(Node element) {

        element.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
            @Override
            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
                setMouseOverColour();
                setIsUnderMouse();
                g.getLayer().batch();
            }
        });
        element.addNodeMouseExitHandler(new NodeMouseExitHandler() {
            @Override
            public void onNodeMouseExit(NodeMouseExitEvent event) {
                setStandardColour();
                g.getLayer().batch();
            }
        });
    }

    private void setColour(Color c) {
        rectangleTopLeft.setStrokeColor(c);
        rectangleTopLeft.setFillColor(c);

        rectangleBotLeft.setStrokeColor(c);
        rectangleBotLeft.setFillColor(c);

        rectangleTopRight.setStrokeColor(c);
        rectangleTopRight.setFillColor(c);

        rectangleBotRight.setStrokeColor(c);
        rectangleBotRight.setFillColor(c);
    }

    private void setStandardColour() {
        setColour(getFillColour());
    }
    private void setMouseOverColour() {
        setColour(getFillSelectedColour());
    }

    @Override
    public void drawOnLayer(Layer layer) {
        layer.add(g);
    }
}

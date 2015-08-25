package com.mySampleApplication.client;

import com.ait.lienzo.client.core.event.NodeMouseEnterEvent;
import com.ait.lienzo.client.core.event.NodeMouseEnterHandler;
import com.ait.lienzo.client.core.shape.*;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.Color;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;



/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class MySampleApplication implements EntryPoint {

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {

        DiagramCanvas canvas = new DiagramCanvas();

//        final Button button = new Button("blaa");
//        final Label label = new Label();
//
//        Arrow a = null;
//
//        button.addClickHandler(new ClickHandler() {
//            public void onClick(ClickEvent event) {
//                if (label.getText().equals("")) {
//
//                } else {
//                    label.setText("");
//                }
//            }
//        });
//
//
//        RootPanel.get().add(button);


//        LienzoPanel panel = new LienzoPanel(400, 300);
//        RootPanel.get().add(panel);
//
//        Text text = new Text("Hello World!", "Verdana, sans-serif", "italic bold", 40);
//        text.setX(10).setY(100);
//        text.setFillColor(ColorName.CORNFLOWERBLUE);
//        text.setStrokeColor(ColorName.BLUE);
//        text.setShadow(new Shadow(ColorName.DARKMAGENTA, 6, 4, 4));
//
//        Layer layer = new Layer();
//        layer.setListening(true);
//        panel.add(layer);
//
//        layer.add(text);
//
//        Circle circle = new Circle(10);
//        circle.setX(200).setY(200).setDraggable(true);
//        layer.add(circle);
//
//
//        Rectangle rectangle = new Rectangle(50, 50, 10);
//        rectangle.setX(100).setY(100).setDraggable(true);
//
//        layer.add(rectangle);
//        addHandler(rectangle);
//
//        layer.draw();


    }

//    private void addHandler(final Shape shape) {
//        shape.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {
//            @Override
//            public void onNodeMouseEnter(NodeMouseEnterEvent event) {
//                shape.setFillColor(Color.getRandomHexColor());
//            }
//        });
//
//    }
}
package org.reactome.web.fireworks.data.category;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.reactome.web.fireworks.data.Node;

public abstract class EdgeCategory {

    public static void draw(AutoBean<Node> rawNode, Context2d ctx){

    }

    public static void drawText(AutoBean<Node> rawNode, Context2d ctx, double fontSize, double space, boolean selected){

    }
}

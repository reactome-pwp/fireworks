package org.reactome.web.fireworks.data;

import com.google.gwt.canvas.dom.client.Context2d;
import org.reactome.web.fireworks.util.Coordinate;

import java.util.List;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface Node {
    Long getDbId();
    String getStId();
    String getName();
    Double getRatio();
    Double getAngle();
    Double getX();
    Double getY();

    Coordinate getCurrentPosition();
    void setCurrentPosition(Coordinate currentPosition);
    Coordinate getOriginalPosition();
    List<String> getLines();

    List<Node> getParents();
    void setParents(List<Node> parents);

    Double getSize();
    void setSize(Double size);

    Double getAlpha();
    void setAlpha(Double alpha);

    void draw(Context2d ctx, Coordinate delta, double factor);
    void drawText(Context2d ctx, double fontSize, double space, boolean selected);
    void drawThumbnail(Context2d ctx, double factor);
    void highlight(Context2d ctx, double auraSize);

    boolean isTopLevel();
    Set<Node> getTopLevelPathways();
    Set<Node> getAncestors();
    Set<Node> getChildren();

}

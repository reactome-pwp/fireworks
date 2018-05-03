package org.reactome.web.fireworks.data.category;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.web.bindery.autobean.shared.AutoBean;
import org.reactome.web.fireworks.data.Node;
import org.reactome.web.fireworks.util.Coordinate;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class NodeCategory {

    public static Coordinate getOriginalPosition(AutoBean<Node> rawNode){
        Node node = rawNode.as();
        return new Coordinate(node.getX(), node.getY());
    }

    public static Double getAlpha(AutoBean<Node> rawNode){
        Node node = rawNode.as();
        return node.getAlpha() == null ? 1.0 : node.getAlpha();
    }

    public static void draw(AutoBean<Node> rawNode, Context2d ctx){
        Node node = rawNode.as();
        ctx.setGlobalAlpha(node.getAlpha());
        ctx.beginPath();
        ctx.arc(node.getX(), node.getY(), node.getSize(), 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill(); //ctx.stroke();
    }

    public static void drawText(AutoBean<Node> rawNode, Context2d ctx, double fontSize, double space, boolean selected){
        Node node = rawNode.as();
        if(!node.isTopLevel()) {
            ctx.save(); //Needed before the calculations b
            if(selected) ctx.setFont("bold " + ctx.getFont());

            double x = node.getCurrentPosition().getX();
            double y = node.getCurrentPosition().getY();
            double angle = node.getAngle();
            space += node.getSize();
            if(node.getAngle() > (Math.PI/2) && node.getAngle() < (Math.PI * 3 / 2) ) {
                double width = ctx.measureText(node.getName()).getWidth() + space;
                x = x + width * Math.cos(angle);
                y = y + width * Math.sin(angle);
                angle += Math.PI;
            }else{
                x = x + space * Math.cos(angle);
                y = y + space * Math.sin(angle);
            }

            ctx.translate(x, y);
            ctx.rotate(angle);
            ctx.fillText(node.getName(), 0, 0);
            ctx.restore();
        }else {  //Text for top level pathways
            List<String> lines = node.getLines();
            double linesSeparation = fontSize * 0.75;
            double vSpace = lines.size() * (fontSize + linesSeparation);
            for (String line : lines) {
                double width = ctx.measureText(line).getWidth();
                double x = node.getCurrentPosition().getX() - width/2;
                double y = node.getCurrentPosition().getY() - node.getSize() - vSpace;
                ctx.fillText(line, x, y);
                vSpace -= (fontSize + linesSeparation);
            }
        }
    }

    public static void drawThumbnail(AutoBean<Node> rawNode, Context2d ctx, double factor){
        Node node = rawNode.as();
        ctx.beginPath();
        double x = node.getX() * factor;
        double y = node.getY() * factor;
        double size = node.getSize() * factor;
        ctx.arc(x, y, size, 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill();ctx.stroke();
    }

    public static void highlight(AutoBean<Node> rawNode, Context2d ctx, double auraSize){

    }

    public static boolean isTopLevel(AutoBean<Node> rawNode){
        Node node = rawNode.as();
        return (node.getParents() == null || node.getParents().isEmpty());
    }

    public static Set<Node> getTopLevelPathways(AutoBean<Node> rawNode){
        return null;
    }

    public static Set<Node> getAncestors(AutoBean<Node> rawNode){
        return null;
    }

    public static Set<Node> getChildren(AutoBean<Node> rawNode){
        return null;
    }

    public static int compareTo(AutoBean<Node> rawNode, Node o){
        Node _this = rawNode.as();
        int cmp = _this.getName().compareTo(o.getName());
        if(cmp==0){
            cmp = _this.getDbId().compareTo(o.getDbId());
        }
        return cmp;
    }

    public static boolean equals(AutoBean<Node> rawNode, Object o) {
        Node _this = rawNode.as();
        if (_this == o) return true;
        if (o == null || _this.getClass() != o.getClass()) return false;

        Node node = (Node) o;

        //noinspection RedundantIfStatement
        if (_this.getDbId() != null ? !_this.getDbId().equals(node.getDbId()) : node.getDbId() != null) return false;

        return true;
    }

    public static int hashCode(AutoBean<Node> rawNode) {
        Node _this = rawNode.as();
        return _this.getDbId() != null ? _this.getDbId().hashCode() : 0;
    }

    public static List<String> getLines(AutoBean<Node> rawNode){
        Node node = rawNode.as();
        List<String> rtn = new LinkedList<>();
        if(node.getName().length()<=15){ //Using name length behaves better than counting words
            rtn.add(node.getName());
            return rtn;
        }
        //noinspection RegExpRepeatedSpace
        String[] words = node.getName().split("  *");
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length/2 ; i++) {
            line.append(words[i]).append(" ");
        }
        rtn.add(line.toString().trim());
        line = new StringBuilder();
        for (int i = words.length/2; i < words.length; i++) {
            line.append(words[i]).append(" ");
        }
        rtn.add(line.toString().trim());
        return rtn;
    }
}

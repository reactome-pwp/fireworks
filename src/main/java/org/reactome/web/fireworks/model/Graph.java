package org.reactome.web.fireworks.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Graph {
    Long speciesId;
    String speciesName;
    Set<Node> nodes;
    Set<Edge> edges;

    double minX; double maxX;
    double minY; double maxY;

    public Graph(Long speciesId, String speciesName, Set<Node> nodes, Set<Edge> edges) {
        this.speciesId = speciesId;
        this.speciesName = speciesName;
        this.nodes = nodes;
        this.edges = edges;
        this.initialise();
    }

    public Long getSpeciesId() {
        return speciesId;
    }

    public String getSpeciesName() { return speciesName;}

    public Set<Node> getNodes() {
        return nodes;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMinY() {
        return this.minY;
    }

    public double getMaxX(){
        return this.maxX;
    }

    public double getMaxY(){
        return this.maxY;
    }

    private void initialise(){
        this.setMinX();
        this.setMaxX();
        this.setMinY();
        this.setMaxY();
    }

    private void setMinX() {
        this.minX = (Collections.min(this.nodes, Comparator.comparingDouble(Node::getMinX))).getMinX();
    }

    private void setMaxX() {
        this.maxX = (Collections.max(this.nodes, Comparator.comparingDouble(Node::getMaxX))).getMaxX();
    }

    private void setMinY() {
        this.minY = (Collections.min(this.nodes, Comparator.comparingDouble(Node::getMinY))).getMinY();
    }

    private void setMaxY() {
        this.maxY = (Collections.max(this.nodes, Comparator.comparingDouble(Node::getMaxY))).getMaxY();
    }
}

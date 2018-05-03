package org.reactome.web.fireworks.data;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface Graph {
    Long getSpeciesId();
    String getSpeciesName();
    List<Node> getNodes();
    List<Edge> getEdges();

    void initialise();
}

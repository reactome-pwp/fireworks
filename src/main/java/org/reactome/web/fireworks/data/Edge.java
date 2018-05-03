package org.reactome.web.fireworks.data;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface Edge {
    Long getFrom();
    Long getTo();

    Node getFromNode();
    void setFromNode(Node node);

    Node getToNode();
    void setToNode(Node node);
}

package org.reactome.web.fireworks.model.factory;

import org.reactome.web.fireworks.data.Edge;
import org.reactome.web.fireworks.data.Graph;
import org.reactome.web.fireworks.data.Node;
import org.reactome.web.fireworks.data.factory.RawModelException;
import org.reactome.web.fireworks.data.factory.RawModelFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ModelFactory {

    public static org.reactome.web.fireworks.model.Graph getGraph(String json) throws ModelException {
        Graph graph;
        try {
            graph = RawModelFactory.getModelObject(Graph.class, json);
        } catch (RawModelException e) {
            throw new ModelException(e.getMessage(), e);
        }

        Set<org.reactome.web.fireworks.model.Node> nodes = new HashSet<>();
        Map<Long, org.reactome.web.fireworks.model.Node> map = new HashMap<>();
        for (Node rNode : graph.getNodes()) {
            org.reactome.web.fireworks.model.Node node = new org.reactome.web.fireworks.model.Node(rNode);
            //TODO: Fix the bug on the server side and remove the condition here :)
            if(!map.keySet().contains(rNode.getDbId())){
                map.put(rNode.getDbId(), node);
                nodes.add(node);
            }

        }

        Set<org.reactome.web.fireworks.model.Edge> edges = new HashSet<>();
        for (Edge rEdge : graph.getEdges()) {
            org.reactome.web.fireworks.model.Node from = map.get(rEdge.getFrom());
            org.reactome.web.fireworks.model.Node to = map.get(rEdge.getTo());
            edges.add(from.addChild(to));
        }

        return new org.reactome.web.fireworks.model.Graph(graph.getSpeciesId(), graph.getSpeciesName() , nodes, edges);
    }

}

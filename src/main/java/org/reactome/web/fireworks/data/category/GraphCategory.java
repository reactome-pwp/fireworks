package org.reactome.web.fireworks.data.category;

import com.google.web.bindery.autobean.shared.AutoBean;
import org.reactome.web.fireworks.data.Edge;
import org.reactome.web.fireworks.data.Graph;
import org.reactome.web.fireworks.data.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class GraphCategory {

    public static void initialise(AutoBean<Graph> rawGraph){
        Graph graph = rawGraph.as();

        Map<Long, Node> nodeMap = new HashMap<>();
        for (Node node : graph.getNodes()) {
            nodeMap.put(node.getDbId(), node);
        }

        for (Edge edge : graph.getEdges()) {
            Node from = nodeMap.get(edge.getFrom());
            edge.setFromNode(from);

            Node to = nodeMap.get(edge.getTo());
            edge.setToNode(to);

            //Next line needs to be added here because we are using categories
            if(to.getParents() == null) to.setParents(new ArrayList<>());
            to.getParents().add(from);
        }
    }
}

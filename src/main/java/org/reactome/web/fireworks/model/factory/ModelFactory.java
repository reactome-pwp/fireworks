package org.reactome.web.fireworks.model.factory;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.reactome.web.fireworks.model.Edge;
import org.reactome.web.fireworks.model.Graph;
import org.reactome.web.fireworks.model.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class ModelFactory {

    public static org.reactome.web.fireworks.model.Graph getGraph(String json) {
        JSONObject graphObj = JSONParser.parseStrict(json).isObject();

        Long speciesId = (long) graphObj.get("speciesId").isNumber().doubleValue();
        String speciesName = graphObj.get("speciesName").isString().stringValue();

        Map<Long, Node> map = new HashMap<>();
        JSONArray nodesObj = graphObj.get("nodes").isArray();
        Set<Node> nodes = new HashSet<>();
        for(int i=0; i<nodesObj.size(); ++i){
            JSONObject nodeObj = nodesObj.get(i).isObject();
            Node node = new Node(nodeObj);
            map.put(node.getDbId(), node);
            nodes.add(node);
        }

        JSONArray edgesObj = graphObj.get("edges").isArray();
        Set<Edge> edges = new HashSet<>();
        for(int i=0; i<edgesObj.size(); ++i){
            JSONObject edgeObj = edgesObj.get(i).isObject();
            Long f = (long) edgeObj.get("from").isNumber().doubleValue();
            Long t = (long) edgeObj.get("to").isNumber().doubleValue();
            Node from = map.get(f);
            Node to = map.get(t);
            edges.add(from.addChild(to));
        }

        return new Graph(speciesId, speciesName , nodes, edges);
    }

}

package org.reactome.web.fireworks.data.factory;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanFactory.Category;
import org.reactome.web.fireworks.data.Edge;
import org.reactome.web.fireworks.data.Graph;
import org.reactome.web.fireworks.data.Node;
import org.reactome.web.fireworks.data.category.EdgeCategory;
import org.reactome.web.fireworks.data.category.NodeCategory;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public abstract class RawModelFactory {

    @SuppressWarnings("UnusedDeclaration")
    @Category({Graph.class, NodeCategory.class, EdgeCategory.class})
    interface ModelAutoBeanFactory extends AutoBeanFactory {
        AutoBean<Node> node();
        AutoBean<Edge> edge();
        AutoBean<Graph> graph();
    }

    public static <T> T getModelObject(Class<T> cls, String json) throws RawModelException {
        try{
            AutoBeanFactory factory = GWT.create(ModelAutoBeanFactory.class);
            AutoBean<T> bean = AutoBeanCodex.decode(factory, cls, json);
            return bean.as();
        }catch (Throwable e){
            throw new RawModelException("Error mapping json string for [" + cls + "]: " + json, e);
        }
    }
}

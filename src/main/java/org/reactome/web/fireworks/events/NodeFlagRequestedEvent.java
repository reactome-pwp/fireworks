package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.NodeFlagRequestedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class NodeFlagRequestedEvent extends GwtEvent<NodeFlagRequestedHandler> {
    public static final Type<NodeFlagRequestedHandler> TYPE = new Type<>();

    String term;
    Boolean includeInteractors;

    public NodeFlagRequestedEvent(String term, Boolean includeInteractors) {
        this.term = term;
        this.includeInteractors = includeInteractors;
    }

    @Override
    public Type<NodeFlagRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NodeFlagRequestedHandler handler) {
        handler.onNodeFlagRequested(this);
    }

    public String getTerm() {
        return term;
    }

    public Boolean getIncludeInteractors() {
        return includeInteractors;
    }

    @Override
    public String toString() {
        return "NodeFlagRequestedEvent{" +
                "term='" + term + '\'' +
                ", includeInteractors=" + includeInteractors +
                '}';
    }
}

package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.NodeFlaggedHandler;
import org.reactome.web.fireworks.model.Node;

import java.util.Collection;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class NodeFlaggedEvent extends GwtEvent<NodeFlaggedHandler> {
    public static final Type<NodeFlaggedHandler> TYPE = new Type<>();

    String term;
    Boolean includeInteractors;
    Collection<Node> flagged;

    public NodeFlaggedEvent(String term, Boolean includeInteractors, Collection<Node> flagged) {
        this.term = term;
        this.flagged = flagged;
        this.includeInteractors = includeInteractors;
    }

    @Override
    public Type<NodeFlaggedHandler> getAssociatedType() {
        return TYPE;
    }

    public Collection<Node> getFlagged() {
        return flagged;
    }

    public String getTerm() {
        return term;
    }

    public Boolean getIncludeInteractors() {
        return includeInteractors;
    }

    @Override
    protected void dispatch(NodeFlaggedHandler event) {
        event.onNodeFlagged(this);
    }

    @Override
    public String toString() {
        return "NodeFlaggedEvent{" +
                "term='" + term + '\'' +
                ", includeInteractors=" + includeInteractors +
                ", flagged=" + (flagged != null ? flagged.size() : 0) +
                '}';
    }
}

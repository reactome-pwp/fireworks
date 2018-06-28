package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.SearchFilterHandler;

import org.reactome.web.pwp.model.client.classes.DatabaseObject;

import java.util.Collection;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchFilterEvent extends GwtEvent<SearchFilterHandler> {
    public static final Type<SearchFilterHandler> TYPE = new Type<>();

    private Collection<? extends DatabaseObject> pathways;

    public SearchFilterEvent(Collection<? extends DatabaseObject> pathways) {
        this.pathways = pathways;
    }

    @Override
    public Type<SearchFilterHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchFilterHandler handler) {
        handler.onSearchFilterEvent(this);
    }

    public Collection<? extends DatabaseObject> getResult() {
        return pathways;
    }

    @Override
    public String toString() {
        return "SearchFilterEvent{" +
                "numberOfElementsFiltered=" + (pathways != null ? pathways.size() : 0) +
                '}';
    }
}

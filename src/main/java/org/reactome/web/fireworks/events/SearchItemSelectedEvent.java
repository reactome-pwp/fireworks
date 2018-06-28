package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.SearchItemSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchItemSelectedEvent extends GwtEvent<SearchItemSelectedHandler> {
    public static Type<SearchItemSelectedHandler> TYPE = new Type<>();

    private String identifier;

    public SearchItemSelectedEvent(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Type<SearchItemSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchItemSelectedHandler handler) {
        handler.onSearchItemSelected(this);
    }

    public String getSelectedIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "GraphEntrySelectedEvent{" +
                "selectedItem=" + identifier +
                '}';
    }
}

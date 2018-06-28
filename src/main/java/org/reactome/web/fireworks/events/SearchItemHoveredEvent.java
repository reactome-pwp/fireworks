package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.SearchItemHoveredHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchItemHoveredEvent extends GwtEvent<SearchItemHoveredHandler> {
    public static Type<SearchItemHoveredHandler> TYPE = new Type<>();

    private String identifier;

    public SearchItemHoveredEvent(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public Type<SearchItemHoveredHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchItemHoveredHandler handler) {
        handler.onSearchItemHovered(this);
    }

    public String getHoveredIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "GraphEntryHoveredEvent{" +
                "hovered=" + identifier +
                '}';
    }
}
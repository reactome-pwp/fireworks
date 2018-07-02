package org.reactome.web.fireworks.search.events;


import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.search.SearchArguments;
import org.reactome.web.fireworks.search.handlers.SearchPerformedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPerformedEvent extends GwtEvent<SearchPerformedHandler> {
    public static Type<SearchPerformedHandler> TYPE = new Type<>();

    private SearchArguments searchArguments;

    public SearchPerformedEvent(SearchArguments args) {
        this.searchArguments = args;
    }

    @Override
    public Type<SearchPerformedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SearchPerformedHandler handler) {
        handler.onSearchPerformed(this);
    }

    public SearchArguments getSearchArguments() {
        return searchArguments;
    }
}

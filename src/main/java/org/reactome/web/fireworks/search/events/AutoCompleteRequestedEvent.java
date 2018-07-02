package org.reactome.web.fireworks.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.search.handlers.AutoCompleteRequestedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AutoCompleteRequestedEvent extends GwtEvent<AutoCompleteRequestedHandler> {
    public static Type<AutoCompleteRequestedHandler> TYPE = new Type<>();

    private String term;

    public AutoCompleteRequestedEvent(String term) {
        this.term = term;
    }

    @Override
    public Type<AutoCompleteRequestedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AutoCompleteRequestedHandler handler) {
        handler.onAutoCompleteRequested(this);
    }

    public String getTerm() {
        return term;
    }
}

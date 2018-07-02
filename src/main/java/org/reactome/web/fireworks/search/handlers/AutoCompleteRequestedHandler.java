package org.reactome.web.fireworks.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.search.events.AutoCompleteRequestedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface AutoCompleteRequestedHandler extends EventHandler {
    void onAutoCompleteRequested(AutoCompleteRequestedEvent event);
}

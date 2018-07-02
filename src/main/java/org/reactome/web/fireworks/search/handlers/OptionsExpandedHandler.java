package org.reactome.web.fireworks.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.search.events.OptionsExpandedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface OptionsExpandedHandler extends EventHandler {
    void onOptionsExpanded(OptionsExpandedEvent event);
}

package org.reactome.web.fireworks.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.search.events.OptionsCollapsedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface OptionsCollapsedHandler extends EventHandler {
    void onOptionsCollapsed(OptionsCollapsedEvent event);
}

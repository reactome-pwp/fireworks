package org.reactome.web.fireworks.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.events.SearchItemHoveredEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SearchItemHoveredHandler extends EventHandler {
    void onSearchItemHovered(SearchItemHoveredEvent event);
}

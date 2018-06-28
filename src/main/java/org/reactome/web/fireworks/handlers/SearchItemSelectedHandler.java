package org.reactome.web.fireworks.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.events.SearchItemSelectedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface SearchItemSelectedHandler extends EventHandler{
    void onSearchItemSelected(SearchItemSelectedEvent event);
}

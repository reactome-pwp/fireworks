package org.reactome.web.fireworks.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.search.events.FacetsLoadedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface FacetsLoadedHandler extends EventHandler {
    void onFacetsLoaded(FacetsLoadedEvent event);
}

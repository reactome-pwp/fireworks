package org.reactome.web.fireworks.search.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.search.events.FacetsChangedEvent;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface FacetsChangedHandler extends EventHandler {
    void onSelectedFacetsChanged(FacetsChangedEvent event);
}
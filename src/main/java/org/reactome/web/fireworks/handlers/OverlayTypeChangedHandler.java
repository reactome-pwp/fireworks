package org.reactome.web.fireworks.handlers;

import com.google.gwt.event.shared.EventHandler;
import org.reactome.web.fireworks.events.OverlayTypeChangedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public interface OverlayTypeChangedHandler extends EventHandler {

    void onOverlayTypeChanged(OverlayTypeChangedEvent e);

}

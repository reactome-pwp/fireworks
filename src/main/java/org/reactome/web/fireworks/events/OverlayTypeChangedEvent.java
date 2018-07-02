package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.handlers.OverlayTypeChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class OverlayTypeChangedEvent extends GwtEvent<OverlayTypeChangedHandler> {
    public static Type<OverlayTypeChangedHandler> TYPE = new Type<>();

    private boolean coverage;

    public OverlayTypeChangedEvent(Boolean coverage) {
        this.coverage =  coverage;
    }

    @Override
    public Type<OverlayTypeChangedHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isCoverage() {
        return coverage;
    }

    @Override
    protected void dispatch(OverlayTypeChangedHandler handler) {
        handler.onOverlayTypeChanged(this);
    }

    @Override
    public String toString() {
        return "OverlayTypeChangedEvent{" +
                "coverage=" + coverage +
                '}';
    }
}

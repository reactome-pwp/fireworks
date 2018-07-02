package org.reactome.web.fireworks.search.infopanel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventBus;
import org.reactome.web.fireworks.controls.navigation.ControlAction;
import org.reactome.web.fireworks.events.ControlActionEvent;
import org.reactome.web.fireworks.events.SearchItemHoveredEvent;
import org.reactome.web.fireworks.events.SearchItemSelectedEvent;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */

abstract class InfoActionsHelper {

    static ClickHandler getLinkClickHandler(final DatabaseObject entry, final EventBus eventBus, final Object source) {
        return event -> eventBus.fireEventFromSource(new SearchItemSelectedEvent(entry.getStId()), source);
    }

    static DoubleClickHandler getLinkDoubleClickHandler(final DatabaseObject entry, final EventBus eventBus, final Object source) {
        return event -> openPathway(entry, eventBus, source);
    }

    static MouseOutHandler getLinkMouseOut(final EventBus eventBus, final Object source) {
        return event -> eventBus.fireEventFromSource(new SearchItemHoveredEvent(null), source);
    }

    static MouseOverHandler getLinkMouseOver(final DatabaseObject entry, final EventBus eventBus, final Object source) {
        return event -> eventBus.fireEventFromSource(new SearchItemHoveredEvent(entry.getStId()), source);
    }

    static ClickHandler getIconClickHandler(final DatabaseObject entry, final EventBus eventBus, final Object source) {
        return event -> openPathway(entry, eventBus, source);
    }

    private static void openPathway(final DatabaseObject entry, final EventBus eventBus, final Object source) {
        eventBus.fireEventFromSource(new SearchItemSelectedEvent(entry.getStId()), source);
        Scheduler.get().scheduleDeferred(() -> eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.OPEN), source));
    }
}
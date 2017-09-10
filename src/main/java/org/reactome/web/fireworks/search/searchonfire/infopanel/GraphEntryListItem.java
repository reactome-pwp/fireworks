package org.reactome.web.fireworks.search.searchonfire.infopanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.events.GraphEntrySelectedEvent;
import org.reactome.web.fireworks.search.searchonfire.graph.model.GraphEntry;
import org.reactome.web.fireworks.util.Console;
import org.reactome.web.pwp.model.client.factory.DatabaseObjectImages;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class GraphEntryListItem extends FlowPanel implements ClickHandler {
    private EventBus eventBus;
    private GraphEntry entry;
    private Handler handler;

    public interface Handler {
        void onItemSelected(GraphEntryListItem newSelectedListItem);
    }

    private Anchor listItemLink;
    private IconButton cancelBtn;
    private IconButton openPathwayBtn;

    private FlowPanel actionsPanel;

    private boolean isExpanded;

    public GraphEntryListItem(GraphEntry entry, EventBus eventBus, Handler handler) {
        this.eventBus = eventBus;
        this.entry = entry;
        this.handler = handler;

        initialise();
        initialiseHandlers();
    }

    @Override
    public void onClick(ClickEvent event) {
        Object source = event.getSource();
        if (source.equals(cancelBtn)) {
            event.stopPropagation();
            collapse();
        } else if (source.equals(openPathwayBtn)) {
            event.stopPropagation();
            Console.error(" openPathwayBtn clicked");
        } else {
            Console.error(entry.getDisplayName() + " clicked");
            if (isExpanded) {
                collapse();
            } else {
                handler.onItemSelected(this);
                expand();
                eventBus.fireEventFromSource(new GraphEntrySelectedEvent(entry), this);
            }
        }

    }

    private void expand() {
        if (!isExpanded) {
            actionsPanel.addStyleName(RESOURCES.getCSS().actionsPanelExpanded());
            isExpanded = true;
        }
    }

    public void collapse() {
        if (isExpanded) {
            actionsPanel.removeStyleName(RESOURCES.getCSS().actionsPanelExpanded());
            isExpanded = false;
        }
    }

    private void initialise() {
        setStyleName(RESOURCES.getCSS().listItem());

        Image icon = new Image(DatabaseObjectImages.INSTANCE.pathway());
        icon.setStyleName(RESOURCES.getCSS().listItemIcon());

        listItemLink = new Anchor(entry.getDisplayName());
        listItemLink.setStyleName(RESOURCES.getCSS().listItemLink());
        listItemLink.setTitle(entry.getDisplayName());

        cancelBtn = new IconButton("", RESOURCES.clear());
        cancelBtn.setStyleName(RESOURCES.getCSS().listItemButton());
        cancelBtn.setTitle("Cancel");
        cancelBtn.addClickHandler(this);

        openPathwayBtn = new IconButton("", RESOURCES.clear());
        openPathwayBtn.setStyleName(RESOURCES.getCSS().listItemButton());
        openPathwayBtn.setTitle("Go to " + entry.getDisplayName());
        openPathwayBtn.addClickHandler(this);

        actionsPanel = new FlowPanel();
        actionsPanel.setStyleName(RESOURCES.getCSS().actionsPanel());
        actionsPanel.add(cancelBtn);
        actionsPanel.add(openPathwayBtn);

        add(icon);
        add(listItemLink);
        add(actionsPanel);
    }

    private void initialiseHandlers() {
        addDomHandler(this, ClickEvent.getType());
        addDomHandler(InfoActionsHelper.getLinkMouseOver(entry, eventBus, this), MouseOverEvent.getType());
        addDomHandler(InfoActionsHelper.getLinkMouseOut(eventBus, this), MouseOutEvent.getType());
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../../images/cancel.png")
        ImageResource clear();
    }

    @CssResource.ImportedWithPrefix("fireworks-GraphEntryListItem")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/fireworks/search/searchonfire/infopanel/GraphEntryListItem.css";

        String listItem();

        String listItemIcon();

        String listItemLink();

//        String listItemLinkCollapsed();

        String listItemButton();

        String actionsPanel();

        String actionsPanelExpanded();
    }
}

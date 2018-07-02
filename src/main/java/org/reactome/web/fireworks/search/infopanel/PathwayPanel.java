package org.reactome.web.fireworks.search.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.search.details.DetailsInfoPanel;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class PathwayPanel extends FlowPanel {

    public PathwayPanel(DatabaseObject object, EventBus eventBus) {
        DetailsInfoPanel.ResourceCSS css = DetailsInfoPanel.RESOURCES.getCSS();
        setStyleName(css.listItem());

        Anchor listItemLink = new Anchor(object.getDisplayName());
        listItemLink.setStyleName(css.listItemLink());
        listItemLink.setTitle("Double-click to go to " + object.getDisplayName());
        listItemLink.addClickHandler(InfoActionsHelper.getLinkClickHandler(object, eventBus, this));
        listItemLink.addDoubleClickHandler(InfoActionsHelper.getLinkDoubleClickHandler(object, eventBus, this));
        listItemLink.addMouseOverHandler(InfoActionsHelper.getLinkMouseOver(object, eventBus, this));
        listItemLink.addMouseOutHandler(InfoActionsHelper.getLinkMouseOut(eventBus, this));
        add(listItemLink);

        IconButton listItemButton = new IconButton("", DetailsInfoPanel.RESOURCES.openDiagram());
        listItemButton.setStyleName(css.listItemButton());
        listItemButton.setTitle("Go to " + object.getDisplayName());
        listItemButton.addClickHandler(InfoActionsHelper.getIconClickHandler(object, eventBus,this));
        add(listItemButton);
    }
}
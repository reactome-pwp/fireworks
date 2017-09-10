package org.reactome.web.fireworks.search.searchonfire.infopanel;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import org.reactome.web.fireworks.search.searchonfire.graph.model.GraphEntry;

import java.util.Collection;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class GraphEntryListPanel extends FlowPanel implements GraphEntryListItem.Handler {

    private GraphEntryListItem selectedListItem;

    public GraphEntryListPanel(String title, Collection<? extends GraphEntry> objects, EventBus eventBus) {
        DetailsInfoPanel.ResourceCSS css = DetailsInfoPanel.RESOURCES.getCSS();
        this.setStyleName(css.databaseObjectListPanel());

        SimplePanel leftBorder = new SimplePanel();
        leftBorder.setStyleName(css.leftListBorder());
        this.add(leftBorder);

        FlowPanel rightMain = new FlowPanel();
        rightMain.setStyleName(css.rightMain());
        this.add(rightMain);

        Label titleLabel = new Label(title);
        titleLabel.setStyleName(css.databaseObjectListTitle());
        rightMain.add(titleLabel);

        FlowPanel listPanel = new FlowPanel();
        listPanel.setStyleName(css.databaseObjectList());
        objects.forEach(obj -> listPanel.add(new GraphEntryListItem(obj, eventBus, this)));

        rightMain.add(listPanel);
    }

    @Override
    public void onItemSelected(GraphEntryListItem newSelectedListItem) {
        if(selectedListItem != null && selectedListItem != newSelectedListItem) {
            selectedListItem.collapse();
        }
        selectedListItem = newSelectedListItem;
    }
}

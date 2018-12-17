package org.reactome.web.fireworks.search.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.reactome.web.fireworks.controls.common.IconToggleButton;
import org.reactome.web.fireworks.events.NodeFlagRequestedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedEvent;
import org.reactome.web.fireworks.events.NodeFlaggedResetEvent;
import org.reactome.web.fireworks.handlers.NodeFlaggedHandler;
import org.reactome.web.fireworks.handlers.NodeFlaggedResetHandler;
import org.reactome.web.fireworks.search.SearchResultObject;
import org.reactome.web.fireworks.search.results.ResultItem;

/**
 * Creates a title in the DetailsPanel containing various
 * information about the selected item, such as name, accession,
 * type, compartment etc.
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class TitlePanel extends FlowPanel implements ClickHandler,
        NodeFlaggedHandler, NodeFlaggedResetHandler {
    private EventBus eventBus;
    private SearchResultObject selectedItem;

    private Label name;
    private IconToggleButton flagBtn;
    private FlowPanel firstLine;

    private String termToFlagBy;
    private String flaggedTerm;
    private Boolean includeInteractors = false;

    public TitlePanel(EventBus eventBus) {
        this.eventBus = eventBus;

        initialise();

        this.eventBus.addHandler(NodeFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(NodeFlaggedResetEvent.TYPE, this);
    }

    public TitlePanel setSelectedItem(SearchResultObject selectedItem) {
        this.selectedItem = selectedItem;
        this.firstLine.clear();

        if (selectedItem instanceof ResultItem) {
            ResultItem item = (ResultItem) selectedItem;
            termToFlagBy = item.getStId();
            populate(item);
        } else {
            termToFlagBy = null;
        }

        flagBtn.setActive(flaggedTerm!=null && flaggedTerm.equals(termToFlagBy));

        return this;
    }

    @Override
    public void onClick(ClickEvent event) {
        if(flagBtn.isActive()) {
            eventBus.fireEventFromSource(new NodeFlaggedResetEvent(), this);
        } else {
            if (selectedItem instanceof ResultItem) {
                eventBus.fireEventFromSource(new NodeFlagRequestedEvent(((ResultItem) selectedItem).getIdentifier(), this.includeInteractors), this);
            }
        }
    }

    @Override
    public void onNodeFlaggedReset() {
        flaggedTerm = null;
        flagBtn.setActive(false);
    }

    @Override
    public void onNodeFlagged(NodeFlaggedEvent event) {
        this.flaggedTerm = event.getTerm();
        this.includeInteractors = event.getIncludeInteractors();
        flagBtn.setActive(flaggedTerm!=null && flaggedTerm.equals(termToFlagBy));
    }

    private void initialise() {
        setStyleName(RESOURCES.getCSS().container());

        name = new Label();
        name.setStyleName(RESOURCES.getCSS().name());

        flagBtn = new IconToggleButton("", RESOURCES.flag(), RESOURCES.flagClear(), this);
        flagBtn.setStyleName(RESOURCES.getCSS().flagBtn());
        flagBtn.setVisible(true);
        flagBtn.setTitle("Show where this is in the diagram");

        firstLine = new FlowPanel();
        firstLine.setStyleName(RESOURCES.getCSS().line());

        add(flagBtn);
        add(name);
        add(firstLine);
    }

    private void populate(ResultItem item) {
        name.setText(item.getName());
        name.setTitle(item.getName());

        String type = item.getSchemaClass().name.equalsIgnoreCase("Database Object") ? item.getExactType() : item.getSchemaClass().name;
        createAndAddLabel(type, "Type", RESOURCES.getCSS().type());
        createAndAddLabel(item.getStId(), "Id", RESOURCES.getCSS().id());

        if (item.getDatabaseName()!=null && item.getReferenceIdentifier()!=null) {
            String accession = item.getDatabaseName() + ":" + item.getReferenceIdentifier();
            createAndAddLabel(accession, accession, RESOURCES.getCSS().accession());
        }
        createAndAddLabel(item.getCompartments(), "Compartments", RESOURCES.getCSS().compartments());
        if(item.isDisplayed()) {
            createAndAddLabel("This is the displayed pathway diagram", "", RESOURCES.getCSS().general());
        }
    }

    private void createAndAddLabel(String text, String tooltip, String style) {
        if(text != null && !text.isEmpty()) {
            Label label = new Label();
            label.setStyleName(style);
            label.setText(text);
            label.setTitle(tooltip);
            firstLine.add(label);
        }
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/flag.png")
        ImageResource flag();

        @Source("../images/flag_clear.png")
        ImageResource flagClear();
    }

    @CssResource.ImportedWithPrefix("fireworks-TitlePanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/fireworks/search/details/TitlePanel.css";

        String container();

        String name();

        String line();

        String id();

        String compartments();

        String type();

        String genes();

        String accession();

        String general();

        String flagBtn();
    }
}

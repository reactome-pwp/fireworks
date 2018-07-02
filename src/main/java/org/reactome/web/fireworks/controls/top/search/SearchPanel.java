package org.reactome.web.fireworks.controls.top.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.fireworks.model.Graph;
import org.reactome.web.fireworks.search.SearchLauncher;
import org.reactome.web.fireworks.search.autocomplete.AutoCompletePanel;
import org.reactome.web.fireworks.search.details.DetailsInfoPanel;
import org.reactome.web.fireworks.search.results.ResultsPanel;


/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class SearchPanel extends FlowPanel {

    public SearchPanel(EventBus eventBus, Graph graph) {
        //Setting the legend style
        setStyleName(RESOURCES.getCSS().searchPanel());

        final SearchLauncher launcher = new SearchLauncher(eventBus, graph);
        this.add(launcher);

        AutoCompletePanel autoCompletePanel = new AutoCompletePanel();
        autoCompletePanel.addAutoCompleteSelectedHandler(launcher);
//        launcher.addSearchBoxArrowKeysHandler(autoCompletePanel);
        launcher.addSearchPerformedHandler(autoCompletePanel);
        launcher.addAutoCompleteRequestedHandler(autoCompletePanel);
        launcher.addPanelCollapsedHandler(autoCompletePanel);
        launcher.addPanelExpandedHandler(autoCompletePanel);
        launcher.addOptionsCollapsedHandler(autoCompletePanel);
        launcher.addOptionsExpandedHandler(autoCompletePanel);
        this.add(autoCompletePanel);

        ResultsPanel results = new ResultsPanel(eventBus);
        // Listen to click events on results and return focus on SearchLauncher
        results.addClickHandler(event -> launcher.setFocus(true));
        results.addFacetsLoadedHandler(launcher);
        launcher.addSearchPerformedHandler(results);
        launcher.addAutoCompleteRequestedHandler(results);
        launcher.addPanelCollapsedHandler(results);
        launcher.addPanelExpandedHandler(results);
        this.add(results);

        DetailsInfoPanel details = new DetailsInfoPanel(eventBus);
        results.addResultSelectedHandler(details);
        launcher.addSearchPerformedHandler(details);
        launcher.addAutoCompleteRequestedHandler(details);
        launcher.addPanelCollapsedHandler(details);
        launcher.addPanelExpandedHandler(details);
        this.add(details);
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }
    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface Resources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(SearchPanelCSS.CSS)
        SearchPanelCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-SearchPanel")
    public interface SearchPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/controls/top/search/SearchPanel.css";

        String searchPanel();
    }
}

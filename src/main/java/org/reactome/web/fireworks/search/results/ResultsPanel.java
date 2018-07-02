package org.reactome.web.fireworks.search.results;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ProvidesKey;
import org.reactome.web.fireworks.search.SearchArguments;
import org.reactome.web.fireworks.search.SearchResultObject;
import org.reactome.web.fireworks.search.events.AutoCompleteRequestedEvent;
import org.reactome.web.fireworks.search.events.PanelCollapsedEvent;
import org.reactome.web.fireworks.search.events.PanelExpandedEvent;
import org.reactome.web.fireworks.search.events.SearchPerformedEvent;
import org.reactome.web.fireworks.search.facets.SearchSummaryFactory;
import org.reactome.web.fireworks.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.fireworks.search.handlers.FacetsLoadedHandler;
import org.reactome.web.fireworks.search.handlers.ResultSelectedHandler;
import org.reactome.web.fireworks.search.handlers.SearchPerformedHandler;
import org.reactome.web.fireworks.search.panels.AbstractAccordionPanel;
import org.reactome.web.fireworks.search.results.data.model.FacetContainer;
import org.reactome.web.fireworks.search.results.data.model.SearchResult;
import org.reactome.web.fireworks.search.results.data.model.SearchSummary;
import org.reactome.web.fireworks.search.results.global.GlobalSearchResultsWidget;
import org.reactome.web.fireworks.search.results.scopebar.ScopeBarPanel;
import org.reactome.web.fireworks.util.Console;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultsPanel extends AbstractAccordionPanel implements ScopeBarPanel.Handler,
        SearchSummaryFactory.Handler, SearchPerformedHandler, AutoCompleteRequestedHandler {

    private final static int GLOBAL_SEARCH = 0;

    private DeckLayoutPanel content;
    private ScopeBarPanel scopeBar;
    private List<ResultsWidget> resultsWidgets = new ArrayList<>();
    private ResultsWidget activeResultWidget;

    private SearchSummary summary;
    private SearchArguments searchArguments;
    private SearchArguments previousSearchArguments;

    /**
     * The key provider that provides the unique ID of a SearchResult.
     */
    public static final ProvidesKey<SearchResultObject> KEY_PROVIDER = item -> {
        if(item == null) {
            return null;
        } else if (item instanceof ResultItem) {
            ResultItem resultItem = (ResultItem) item;
            return resultItem.getDbId();
        }
        return null;
    };

    public ResultsPanel(EventBus eventBus) {
        this.sinkEvents(Event.ONCLICK);

        scopeBar = new ScopeBarPanel(this);
        scopeBar.addButton("Results", "", ScopeBarPanel.RESOURCES.scopeGlobal());

        resultsWidgets.add(new GlobalSearchResultsWidget(GLOBAL_SEARCH));

        content = new DeckLayoutPanel();
        content.setStyleName(RESOURCES.getCSS().content());
        resultsWidgets.forEach(w -> content.add(w.asWidget()));
        setActiveResultsWidget(GLOBAL_SEARCH);
        content.setAnimationVertical(false);
        content.setAnimationDuration(500);

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().main());
        main.add(scopeBar);
        main.add(content);
        add(main);

        show(false);
    }

    public HandlerRegistration addClickHandler(ClickHandler handler){
        return this.addHandler(handler, ClickEvent.getType());
    }

    public void addResultSelectedHandler(ResultSelectedHandler handler) {
        resultsWidgets.forEach(resultsWidget -> resultsWidget.addResultSelectedHandler(handler));
    }

    public void addFacetsLoadedHandler(FacetsLoadedHandler handler) {
        resultsWidgets.forEach(resultsWidget -> resultsWidget.addFacetsLoadedHandler(handler));
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        searchArguments = null;
        show(false);
    }

    @Override
    public void onSearchSummaryReceived(SearchSummary summary) {
        this.summary = summary;
        updateScopeNumbers(summary);
        updateFacets(summary);
        updateResult();
    }

    @Override
    public void onSearchSummaryError(String msg) {
        Console.warn("Error retrieving search summary");
        summary = null;
        updateScopeNumbers(null);
        updateFacets(null);
        updateResult();
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        boolean clearSelection = previousSearchArguments!=null && !previousSearchArguments.getQuery().equals(event.getSearchArguments().getQuery());

        searchArguments = event.getSearchArguments();
        if(searchArguments.hasValidQuery()) {
            // Get facets and numbers from content service before performing the search query
            SearchSummaryFactory.queryForSummary(searchArguments, this);
            if(clearSelection) {
                clearSelection();
            }
        }

        previousSearchArguments = searchArguments;
    }

    @Override
    public void onScopeChanged(int selected) {
        setActiveResultsWidget(selected);
        updateResult();
    }

    @Override
    public void onPanelCollapsed(PanelCollapsedEvent event) {
        super.onPanelCollapsed(event);
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        show(searchArguments != null);
    }

    private void updateResult() {
        show(searchArguments != null && searchArguments.hasValidQuery());
        activeResultWidget.updateResults(searchArguments);
    }

    private void clearSelection() {
        resultsWidgets.forEach(ResultsWidget::clearSelection);
    }

    private void setActiveResultsWidget(int index) {
        // Suspend the selection before changing scope
        if(activeResultWidget != null) {
            activeResultWidget.suspendSelection();
        }

        activeResultWidget = resultsWidgets.get(index);

        if (activeResultWidget != null) {
            content.showWidget(index);
        }
    }

    private void updateFacets(SearchSummary summary) {
        List<FacetContainer> globalFacets = null;
        if(summary!=null) {
            SearchResult globalResults = summary.getFireworksResult();
            if (globalResults!=null) {
                globalFacets = globalResults.getFacets()!=null ? globalResults.getFacets() : new ArrayList<>();
            }
        }
        resultsWidgets.get(GLOBAL_SEARCH).setFacets(globalFacets);
    }

    private void updateScopeNumbers(SearchSummary summary) {
        int globalResultsFound = 0;
        if (summary!=null) {
            SearchResult globalResults = summary.getFireworksResult();
            if (globalResults!=null && globalResults.getFound()!=null) {
                globalResultsFound = globalResults.getFound();
            }
        }
        scopeBar.setResultsNumber(GLOBAL_SEARCH, globalResultsFound);
    }

    private void show(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
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
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-ResultsPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/search/results/ResultsPanel.css";

        String main();

        String content();

    }

    public static CellListResource CUSTOM_LIST_STYLE;
    static {
        CUSTOM_LIST_STYLE = GWT.create(CellListResource.class);
        CUSTOM_LIST_STYLE.cellListStyle().ensureInjected();
    }

    public interface CellListResource extends CellList.Resources {

        @CssResource.ImportedWithPrefix("fireworks-CellListResource")
        interface CustomCellList extends CellList.Style {
            String CSS = "org/reactome/web/fireworks/search/results/ResultsList.css";
        }

        /**
         * The styles used in this widget.
         */
        @Override
        @Source(CustomCellList.CSS)
        CustomCellList cellListStyle();
    }
}

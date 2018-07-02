package org.reactome.web.fireworks.search.details;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.fireworks.events.SearchItemSelectedEvent;
import org.reactome.web.fireworks.events.SearchFilterEvent;
import org.reactome.web.fireworks.search.SearchArguments;
import org.reactome.web.fireworks.search.events.*;
import org.reactome.web.fireworks.search.handlers.AutoCompleteRequestedHandler;
import org.reactome.web.fireworks.search.handlers.ResultSelectedHandler;
import org.reactome.web.fireworks.search.handlers.SearchPerformedHandler;
import org.reactome.web.fireworks.search.infopanel.DatabaseObjectListPanel;
import org.reactome.web.fireworks.search.infopanel.PathwayPanel;
import org.reactome.web.fireworks.search.panels.AbstractAccordionPanel;
import org.reactome.web.fireworks.search.results.ResultItem;
import org.reactome.web.fireworks.util.Console;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;
import org.reactome.web.pwp.model.client.factory.SchemaClass;
import org.reactome.web.pwp.model.client.util.Ancestors;
import org.reactome.web.pwp.model.client.util.Path;

import java.util.*;
import java.util.function.Predicate;

import static org.reactome.web.fireworks.search.events.ResultSelectedEvent.ResultType.GLOBAL;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class DetailsInfoPanel extends AbstractAccordionPanel implements ResultSelectedHandler,
        SearchPerformedHandler, AutoCompleteRequestedHandler,
        ContentClientHandler.ObjectListLoaded<Pathway> {

    private EventBus eventBus;
    private SearchArguments args;
    private ResultItem selectedResultItem;

    private FlowPanel mainPanel;
    private TitlePanel titlePanel;

    private FlowPanel spinner;

    private List<Widget> resultWidgets = new ArrayList<>();

    public DetailsInfoPanel(EventBus eventBus) {
        this.eventBus = eventBus;
        show(false);

        Label header = new Label("Details");
        header.addStyleName(RESOURCES.getCSS().header());

        FlowPanel main = new FlowPanel();
        main.setStyleName(RESOURCES.getCSS().container());

        mainPanel = new FlowPanel();
        mainPanel.setStyleName(RESOURCES.getCSS().mainPanel());

        main.add(header);
        main.add(mainPanel);
        add(main);

        titlePanel = new TitlePanel(eventBus);
        spinner = getSpinner();
    }

    @Override
    public void onAutoCompleteRequested(AutoCompleteRequestedEvent event) {
        selectedResultItem = null;
        show(false);
    }

    @Override
    public void onResultSelected(ResultSelectedEvent event) {
        selectedResultItem = (ResultItem) event.getSelectedResultItem();

        clearMainPanel();
        mainPanel.add(titlePanel.setSelectedItem(selectedResultItem));
        showSpinner();

        if(selectedResultItem == null) {
            show(false);
        } else if (GLOBAL == event.getResultType()) {
            ResultItem item = selectedResultItem;
            ContentClient.getAncestors(item.getIdentifier(), new AncestorsLoaded() {
                @Override
                public void onAncestorsLoaded(Ancestors ancestors) {
                    Set<Pathway> pathways = new HashSet<>();
                    for (Path ancestor : ancestors) {
                        pathways.add(ancestor.getLastPathwayWithDiagram()); //We do not include subpathways in the list
                    }

                    clearResults();
                    if (!pathways.isEmpty()) {
                        displayResults(pathways);
                    }
                }

                @Override
                public void onContentClientException(Type type, String message) {
                    getPathways();
                }

                @Override
                public void onContentClientError(ContentClientError error) {
                    getPathways();
                }

                private void getPathways() {
                    ContentClient.getPathwaysWithDiagramForEntity(item.getIdentifier(), false, args.getSpecies(), DetailsInfoPanel.this);
                }
            });

            show(true);
        }
    }

    @Override
    public void onPanelCollapsed(PanelCollapsedEvent event) {
        super.onPanelCollapsed(event);
    }

    @Override
    public void onPanelExpanded(PanelExpandedEvent event) {
        show(selectedResultItem!=null);
    }

    @Override
    public void onSearchPerformed(SearchPerformedEvent event) {
        args = event.getSearchArguments();
        selectedResultItem = null;
        show(false);
    }

    @Override
    public void onObjectListLoaded(List<Pathway> list) {
        clearResults();
        if(list == null || list.isEmpty()) return;
        displayResults(list);

    }

    @Override
    public void onContentClientException(Type type, String message) {
        show(false);
        includeResultWidget(new Label("An error has occurred. ERROR: " + message));
    }

    @Override
    public void onContentClientError(ContentClientError error) {
        show(false);
        includeResultWidget(new Label("An error has occurred. ERROR: " + error.getReason()));
    }

    private void displayResults(Collection<Pathway> result){

        eventBus.fireEventFromSource(new SearchFilterEvent(result), this);
        eventBus.fireEventFromSource(new SearchItemSelectedEvent(selectedResultItem.getStId()), this);

        if(selectedResultItem.getSchemaClass() == SchemaClass.PATHWAY || selectedResultItem.getSchemaClass() == SchemaClass.TOP_LEVEL_PATHWAY) {
            if(isItemInList(selectedResultItem.getStId(), result)) {
                hideSpinner();
                includeResultWidget(new PathwayPanel("hi", result.iterator().next(), eventBus));
            } else {
                String title = "Part of " + result.size() + (result.size() > 1 ? " pathways" : " pathway");
                includeResultWidget(new DatabaseObjectListPanel(title, result, eventBus));
            }
        } else {
            String title = "Present in " + result.size() + (result.size() > 1 ? " pathways" : " pathway");
            includeResultWidget(new DatabaseObjectListPanel(title, result, eventBus));
        }

        show(true);
    }

    private void includeResultWidget(Widget widget) {
        hideSpinner();
        mainPanel.add(widget);
        resultWidgets.add(widget);
    }

    private void clearMainPanel() {
        mainPanel.clear();
    }

    private void clearResults() {
        resultWidgets.stream().forEach(w -> mainPanel.remove(w));
        resultWidgets.clear();
    }

    private FlowPanel getSpinner() {
        SimplePanel spinner = new SimplePanel();
        spinner.setStyleName(RESOURCES.getCSS().loader());
        SimplePanel spinnerContainer = new SimplePanel();
        spinnerContainer.setStyleName(RESOURCES.getCSS().loaderContainer());
        spinnerContainer.add(spinner);

        Label msgLabel = new Label("Loading...");
        FlowPanel rtn = new FlowPanel();
        rtn.setStyleName(RESOURCES.getCSS().loaderPanel());
        rtn.add(spinnerContainer);
        rtn.add(msgLabel);
        return rtn;
    }

    private void showSpinner() {
        mainPanel.add(spinner);
    }

    private void hideSpinner() {
        spinner.removeFromParent();
    }

    private void show(boolean visible) {
        if (visible) {
            getElement().getStyle().setDisplay(Style.Display.INLINE);
        } else {
            getElement().getStyle().setDisplay(Style.Display.NONE);
        }
    }

    private boolean isItemInList(String itemStId, Collection<Pathway> list) {
        return list.stream()
                .map(p -> p.getStId())
                .anyMatch(s -> s.equalsIgnoreCase(itemStId));
    }


    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("../images/open_diagram.png")
        ImageResource openDiagram();
    }

    @CssResource.ImportedWithPrefix("fireworks-DetailsInfoPanel")
    public interface ResourceCSS extends CssResource {

        String CSS = "org/reactome/web/fireworks/search/details/DetailsInfoPanel.css";

        String container();

        String header();

        String mainPanel();

        String loaderPanel();

        String loaderContainer();

        String loader();

        String databaseObjectListPanel();

        String databaseObjectListTitle();

        String databaseObjectList();

        String listItem();

        String listItemIcon();

        String listItemLink();

        String listItemButton();
    }
}

package org.reactome.web.fireworks.legends;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.events.AnalysisPerformedEvent;
import org.reactome.web.fireworks.events.AnalysisResetEvent;
import org.reactome.web.fireworks.events.OverlayTypeChangedEvent;
import org.reactome.web.fireworks.handlers.AnalysisPerformedHandler;
import org.reactome.web.fireworks.handlers.AnalysisResetHandler;
import org.reactome.web.fireworks.handlers.OverlayTypeChangedHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class EnrichmentControl extends LegendPanel implements ClickHandler, ChangeHandler,
        AnalysisPerformedHandler, AnalysisResetHandler, OverlayTypeChangedHandler {

    private InlineLabel message;
    private ControlButton closeBtn;
    private Button filterBtn;
    private ListBox selector;
    private FlowPanel infoPanel;

    private ResultFilter filter;
    private boolean isExpanded;

    public EnrichmentControl(final EventBus eventBus) {
        super(eventBus);

        LegendPanelCSS css = RESOURCES.getCSS();
        //Setting the legend style
        addStyleName(css.analysisControl());
        addStyleName(css.enrichmentControl());

        this.message = new InlineLabel();
        this.add(this.message);

        this.closeBtn = new ControlButton("Close", css.close(), this);
        this.add(this.closeBtn);

        this.filterBtn = new IconButton(RESOURCES.filterWarningIcon(), css.filterBtn(), "Analysis results are filtered. Click to find out more.", this);
        this.filterBtn.setVisible(false);
        this.add(this.filterBtn);

        this.selector = new ListBox();
        this.selector.addChangeHandler(this);
        this.selector.addItem("pValue", "false");
        this.selector.addItem("coverage", "true");
        this.add(new InlineLabel("Showing"));
        this.add(this.selector);

        this.infoPanel = new FlowPanel();
        this.infoPanel.setStyleName(RESOURCES.getCSS().infoPanel());
        this.add(infoPanel);

        this.initHandlers();
        this.setVisible(false);
    }

    @Override
    public void onAnalysisPerformed(AnalysisPerformedEvent e) {
        switch (e.getAnalysisType()) {
            case OVERREPRESENTATION:
            case SPECIES_COMPARISON:
                String message = e.getAnalysisType().name().replaceAll("_", " ");
                this.message.setText(message.toUpperCase());
                filter = e.getFilter();
                updateFilterInfo();
                if(isExpanded)
                    collapse();

                setVisible(true);
                break;
            default:
                this.setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset() {
        if (this.isVisible()) {
            this.message.setText("");
            this.setVisible(false);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(this.closeBtn)) {
            eventBus.fireEventFromSource(new AnalysisResetEvent(), this);
        } else if (event.getSource().equals(this.filterBtn)) {
            toggleExpandedPanel();
        }
    }

    @Override
    public void onChange(ChangeEvent event) {
        boolean coverage = Boolean.valueOf(this.selector.getSelectedValue());
        this.eventBus.fireEventFromSource(new OverlayTypeChangedEvent(coverage), this);
    }

    @Override
    public void onOverlayTypeChanged(OverlayTypeChangedEvent e) {
        if (!e.getSource().equals(this)) this.selector.setItemSelected(e.isCoverage() ? 1 : 0, true);
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisPerformedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(OverlayTypeChangedEvent.TYPE, this);
    }

    private void updateFilterInfo() {
        if (filter == null)  {
            filterBtn.setVisible(false);
        } else {
            String resource = filter.getResource().equalsIgnoreCase("TOTAL") ? "" : filter.getResource();
            double pValue =  filter.getpValue() == null ? 1d : filter.getpValue();
            boolean includeDisease = filter.getIncludeDisease();
            Integer min = filter.getMin();
            Integer max = filter.getMax();

            boolean filterApplied = !resource.isEmpty() || pValue != 1d || !includeDisease || min != null || max != null;

            filterBtn.setVisible(filterApplied);

            infoPanel.clear();
            if (filterApplied) {
                Label title = new Label("Applied filter:");
                title.setStyleName(RESOURCES.getCSS().infoPanelTitle());
                infoPanel.add(title);

                if (!resource.isEmpty()) {
                    addFilterTag(resource, "Selected resource is " + resource);
                }

                if (pValue != 1d) {
                    addFilterTag("p ≤ " + pValue, "p-value is set to " + pValue);
                }

                if (!includeDisease) {
                    addFilterTag("No disease", "Disease pathways are excluded");
                }

                if (min != null && max != null) {
                    addFilterTag(min + "≤ size ≤" + max, "Only pathways with sizes between " + min + " and " + max + " are displayed");
                }
            }

        }
    }

    private void addFilterTag(String text, String tooltip) {
        Label lb = new Label(text);
        lb.setStyleName(RESOURCES.getCSS().infoPanelTag());
        lb.setTitle(tooltip);
        infoPanel.add(lb);
    }

    private void toggleExpandedPanel() {
        if (!isExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    private void expand() {
        setHeight("58px");
        isExpanded = true;
    }

    private void collapse() {
        setHeight("28px");
        isExpanded = false;
    }
}

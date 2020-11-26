package org.reactome.web.fireworks.controls.thumbnails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.fireworks.client.StaticIllustrationPanel;
import org.reactome.web.fireworks.events.*;
import org.reactome.web.fireworks.handlers.*;
import org.reactome.web.fireworks.profiles.FireworksColours;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Event;
import org.reactome.web.pwp.model.client.classes.Figure;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

public class StaticIllustrationThumbnail extends FlowPanel
        implements NodeOpenedHandler, NodeSelectedResetHandler, NodeSelectedHandler,
                    ProfileChangedHandler, FireworksResizeHandler,
                    NodeFlaggedResetHandler, NodeFlaggedHandler, NodeFlagRequestedHandler,
                    AnalysisResetHandler, AnalysisPerformedHandler {

    // HELPER FIELDS
    public static final int THUMBNAIL_RESIZE_THRESHOLD_1 = 1000;
    private static final int DEFAULT_WIDTH = 130;
    private static final int DEFAULT_HEIGHT = 75;
    private static final int DEFAULT_VIEWPORT_W = THUMBNAIL_RESIZE_THRESHOLD_1 + 300;
    public static final double FACTOR_06 = 0.58;
    private boolean isFlaggingLegendBarVisible = false;
    private boolean isAnalysisLegendBarVisible = false;

    private double viewportWidth;

    private final EventBus eventBus;
    private FlowPanel mainStaticIllustrationFlowPanel;

    private final StaticIllustrationPanel staticIllustrationPanel;
    private String diagramIllustrationURL = null;

    public StaticIllustrationThumbnail(EventBus eventBus)  {
        this.eventBus = eventBus;
        this.staticIllustrationPanel = new StaticIllustrationPanel();

        initHandlers();

        resize(DEFAULT_VIEWPORT_W);
        this.setStyle();
    }

    private void initHandlers() {
        this.eventBus.addHandler(NodeOpenedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedResetEvent.TYPE, this);
        this.eventBus.addHandler(ProfileChangedEvent.TYPE, this);
        this.eventBus.addHandler(FireworksResizedEvent.TYPE, this);
        this.eventBus.addHandler(NodeFlaggedEvent.TYPE, this);
        this.eventBus.addHandler(NodeFlaggedResetEvent.TYPE, this);
        this.eventBus.addHandler(NodeFlagRequestedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisPerformedEvent.TYPE, this);
    }

    public void addDiagramFigureToThumbnails(Long dbId){
        resetAllStaticIllustration();

        ContentClient.query(dbId, new ContentClientHandler.ObjectLoaded<DatabaseObject>() {
            @Override
            public void onObjectLoaded(DatabaseObject databaseObject) {
                if (databaseObject instanceof Event) {
                    final Event event = (Event) databaseObject;
                    for (Figure figure : event.getFigure()) {
                        diagramIllustrationURL = figure.getUrl();
                        createMainStaticIllustrationFlowPanel(databaseObject, diagramIllustrationURL);
                    }
                }
            }

            @Override
            public void onContentClientException(Type type, String message) {
            }

            @Override
            public void onContentClientError(ContentClientError error) {
            }
        });
    }

    public void createMainStaticIllustrationFlowPanel(final DatabaseObject databaseObject, final String url) {
        if (url == null || url.isEmpty()) return;

        showStaticThumbnail();

        mainStaticIllustrationFlowPanel = new FlowPanel();
        mainStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());

        Image image = new Image(url);
        image.setUrl(url);
        image.setTitle("Illustration for " + databaseObject.getDisplayName());
        image.setAltText("Illustration for " + databaseObject.getDisplayName());
        image.addClickHandler(clickEvent -> {
            staticIllustrationPanel.setPanelElements(url);
            staticIllustrationPanel.toggle();
        });
        mainStaticIllustrationFlowPanel.add(image);
        mainStaticIllustrationFlowPanel.setVisible(true);

        add(mainStaticIllustrationFlowPanel);
    }

    public void resetAllStaticIllustration() {
        resetStaticIllustrationSelection();
        if (mainStaticIllustrationFlowPanel != null ) {
            remove(mainStaticIllustrationFlowPanel);
        }
        hideStaticThumbnail();
    }

    public void resetStaticIllustrationSelection() {
        if (staticIllustrationPanel != null) {
            staticIllustrationPanel.reset();
            staticIllustrationPanel.clear();
        }

        diagramIllustrationURL = null;

    }
    private void showStaticThumbnail() {
        this.setVisible(true);
    }

    private void hideStaticThumbnail() {
        if ((mainStaticIllustrationFlowPanel != null && !mainStaticIllustrationFlowPanel.isVisible())) return;

        this.setVisible(false);
    }

    @Override
    public void onNodeOpened(NodeOpenedEvent event) {
        resetAllStaticIllustration();
    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        addDiagramFigureToThumbnails(event.getNode().getDbId());
    }

    @Override
    public void onNodeSelectionReset() {
        resetAllStaticIllustration();
    }

    @Override
    public void onProfileChanged(ProfileChangedEvent event) {
        if (event != null && event.getProfile() != null) {
            if (mainStaticIllustrationFlowPanel != null ) {
                mainStaticIllustrationFlowPanel.getElement().getStyle().setBorderColor(FireworksColours.PROFILE.getNodeSelectionColour());
            }
        }
    }

    /**
     * It will be called at DiagramCanvas.setSize()
     * when viewport threshold is below certain limit.
     *
     * @param viewportWidth note: don't confuse it with width to resize.
     */
    public void resize(double viewportWidth) {
        this.viewportWidth = viewportWidth;

        int fW = (int) Math.round(DEFAULT_WIDTH * getFactor());
        int fH = (int) Math.round(DEFAULT_HEIGHT * getFactor());

        // adjusting flexing position in case it's too small
        Element parent = this.getElement().getParentElement();
        if (parent != null && parent.getStyle() != null) {
            if (this.viewportWidth <= 800) parent.getStyle().setProperty("alignItems", "unset");
            else parent.getStyle().setProperty("alignItems", "center");
        }

        this.setWidth(fW + "px");
        this.setHeight(fH + "px");
    }

    private double getFactor() {
        if (!isFlaggingLegendBarVisible && !isAnalysisLegendBarVisible) return 1.0;
        return (viewportWidth <= THUMBNAIL_RESIZE_THRESHOLD_1) ? FACTOR_06 : 1.0;
    }

    private void setStyle() {
        Style style = this.getElement().getStyle();

        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);
        style.setBorderWidth(1, Style.Unit.PX);
        style.setBorderColor(FireworksColours.PROFILE.getNodeSelectionColour());
        style.setBottom(0, Style.Unit.PX);
        style.setMarginLeft(5, Style.Unit.PX);
        style.setProperty("boxSizing", "unset");

        // Invisible by default
        style.setDisplay(Style.Display.NONE);
    }

    public StaticIllustrationPanel getStaticIllustrationPanel() {
        return staticIllustrationPanel;
    }

    @Override
    public void onNodeFlaggedReset() {
        isFlaggingLegendBarVisible = false;
        resize(this.viewportWidth == 0 ? DEFAULT_VIEWPORT_W : this.viewportWidth);
    }

    @Override
    public void onNodeFlagged(NodeFlaggedEvent event) {
        isFlaggingLegendBarVisible = true;
        resize(this.viewportWidth);
    }

    @Override
    public void onAnalysisPerformed(AnalysisPerformedEvent e) {
        isAnalysisLegendBarVisible = (e != null && e.getAnalysisType() != null);
        resize(this.viewportWidth);
    }

    @Override
    public void onAnalysisReset() {
        isAnalysisLegendBarVisible = false;
        resize(this.viewportWidth);
    }

    @Override
    public void onFireworksResized(FireworksResizedEvent event) {
        this.viewportWidth = event.getWidth();
        resize(this.viewportWidth);
    }

    @Override
    public void onNodeFlagRequested(NodeFlagRequestedEvent event) {
        isFlaggingLegendBarVisible = (event != null && event.getTerm() != null);
        resize(this.viewportWidth);
    }

    public static Resources RESOURCES;

    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();
    }

    @CssResource.ImportedWithPrefix("diagram-StaticIllustrationThumbnail")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/fireworks/thumbnails/StaticIllustrationThumbnail.css";

        String mainStaticThumbnails();
    }
}

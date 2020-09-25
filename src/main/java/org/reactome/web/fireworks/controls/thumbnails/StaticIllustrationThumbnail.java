package org.reactome.web.fireworks.controls.thumbnails;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import org.reactome.web.fireworks.client.StaticIllustrationPanel;
import org.reactome.web.fireworks.events.NodeOpenedEvent;
import org.reactome.web.fireworks.events.NodeSelectedEvent;
import org.reactome.web.fireworks.events.NodeSelectedResetEvent;
import org.reactome.web.fireworks.events.ProfileChangedEvent;
import org.reactome.web.fireworks.handlers.NodeOpenedHandler;
import org.reactome.web.fireworks.handlers.NodeSelectedHandler;
import org.reactome.web.fireworks.handlers.NodeSelectedResetHandler;
import org.reactome.web.fireworks.handlers.ProfileChangedHandler;
import org.reactome.web.fireworks.profiles.FireworksColours;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Event;
import org.reactome.web.pwp.model.client.classes.Figure;
import org.reactome.web.pwp.model.client.common.ContentClientHandler;
import org.reactome.web.pwp.model.client.content.ContentClient;
import org.reactome.web.pwp.model.client.content.ContentClientError;

public class StaticIllustrationThumbnail extends FlowPanel implements NodeOpenedHandler, NodeSelectedHandler, NodeSelectedResetHandler, ProfileChangedHandler {

    private EventBus eventBus;
    private FlowPanel mainStaticIllustrationFlowPanel;

    private StaticIllustrationPanel staticIllustrationPanel;
    private String diagramIllustrationURL = null;
    private int thumbnailWidth;

    public StaticIllustrationThumbnail(EventBus eventBus, StaticIllustrationPanel staticIllustrationPanel, int thumbnailWidth)  {
        this.eventBus = eventBus;
        this.staticIllustrationPanel = staticIllustrationPanel;
        this.thumbnailWidth = thumbnailWidth;

        initHandlers();
    }

    private void initHandlers() {
        this.eventBus.addHandler(NodeOpenedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedResetEvent.TYPE, this);
        this.eventBus.addHandler(ProfileChangedEvent.TYPE, this);
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
        mainStaticIllustrationFlowPanel = new FlowPanel();
        mainStaticIllustrationFlowPanel.setStyleName(RESOURCES.getCSS().mainStaticThumbnails());
        mainStaticIllustrationFlowPanel.getElement().getStyle().setLeft(thumbnailWidth + 10, Style.Unit.PX);

        if (url != null && !url.isEmpty()) {
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
        }
        add(mainStaticIllustrationFlowPanel);
        refresh();
    }

    public void resetAllStaticIllustration() {
        diagramIllustrationURL = null;
        if (staticIllustrationPanel != null) {
            staticIllustrationPanel.clear();
            staticIllustrationPanel.reset();
        }
        if (mainStaticIllustrationFlowPanel != null ) remove(mainStaticIllustrationFlowPanel);
    }

    @Override
    public void onNodeOpened(NodeOpenedEvent event) {
        resetAllStaticIllustration();
    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        addDiagramFigureToThumbnails(event.getNode().getDbId());
        refresh();
    }

    @Override
    public void onNodeSelectionReset() {
        resetAllStaticIllustration();
    }

    @Override
    public void onProfileChanged(ProfileChangedEvent event) {
        if (event != null && event.getProfile() != null) {
            refresh();
        }
    }

    public void refresh() {
        if (mainStaticIllustrationFlowPanel != null )
            mainStaticIllustrationFlowPanel.getElement().getStyle().setBorderColor(FireworksColours.PROFILE.getNodeSelectionColour());
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

package org.reactome.web.fireworks.controls.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.fireworks.events.NodeSelectedEvent;
import org.reactome.web.fireworks.handlers.NodeSelectedHandler;
import org.reactome.web.fireworks.model.Node;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class RadialMenu extends FlowPanel implements NodeSelectedHandler{
    private EventBus eventBus;

    public RadialMenu(EventBus eventBus) {
        this.eventBus = eventBus;
        this.setStyleName(RESOURCES.getCSS().container());
        this.setVisible(false);

        initialiseHandlers();
    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        Node node = event.getNode();
        if (node!=null) {
            getElement().getStyle().setTop(node.getY(), Style.Unit.PX);
            getElement().getStyle().setLeft(node.getX(), Style.Unit.PX);

            setVisible(true);
        }
    }

    private void initialiseHandlers() {
        this.eventBus.addHandler(NodeSelectedEvent.TYPE, this);
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

    @CssResource.ImportedWithPrefix("fireworks-RadialMenu")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/fireworks/controls/common/RadialMenu.css";

        String container();

    }
}

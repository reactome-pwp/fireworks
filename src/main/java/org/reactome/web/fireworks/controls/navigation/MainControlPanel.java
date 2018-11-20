package org.reactome.web.fireworks.controls.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.fireworks.client.FireworksFactory;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.events.ControlActionEvent;
import org.reactome.web.fireworks.events.NodeSelectedEvent;
import org.reactome.web.fireworks.events.NodeSelectedResetEvent;
import org.reactome.web.fireworks.handlers.NodeSelectedHandler;
import org.reactome.web.fireworks.handlers.NodeSelectedResetHandler;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class MainControlPanel extends FlowPanel implements ClickHandler, NodeSelectedHandler, NodeSelectedResetHandler {

    private EventBus eventBus;
    private Button fitAll;
    private Button open;

    public MainControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        this.addStyleName(RESOURCES.getCSS().mainControlPanel());

        this.fitAll = new IconButton(RESOURCES.fitallIcon(), RESOURCES.getCSS().fitall(), "Show all", this);
        this.add(this.fitAll);

        if (FireworksFactory.SHOW_DIAGRAM_BTN) {
            this.open = new IconButton(RESOURCES.diagramIcon(), RESOURCES.getCSS().diagram(), "Open pathway diagram", this);
            this.open.setEnabled(false);
            this.add(this.open);
        }

        this.eventBus.addHandler(NodeSelectedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedResetEvent.TYPE, this);
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(this.fitAll)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.FIT_ALL), this);
        } else if (btn.equals(this.open)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(ControlAction.OPEN), this);
        }
    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        if (this.open != null) {
            this.open.setEnabled(true);
        }
    }

    @Override
    public void onNodeSelectionReset() {
        if (this.open != null) {
            this.open.setEnabled(false);
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

        @Source("images/fitall.png")
        ImageResource fitallIcon();

        @Source("images/diagram.png")
        ImageResource diagramIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-MainControlPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/controls/navigation/MainControlPanel.css";

        String mainControlPanel();

        String fitall();

        String diagram();
    }
}

package org.reactome.web.fireworks.controls.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.events.ControlActionEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class NavigationControlPanel extends AbsolutePanel implements ClickHandler {

    protected EventBus eventBus;

    private Button zoomIn;
    private Button zoomOut;
    private Button up;
    private Button right;
    private Button down;
    private Button left;

    public NavigationControlPanel(EventBus eventBus) {
        this.eventBus = eventBus;

        //Setting the legend style
        getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
        setStyleName(RESOURCES.getCSS().controlPanel());

        ControlPanelCSS css = RESOURCES.getCSS();

        this.zoomIn = new IconButton(RESOURCES.zoomInIcon(), css.zoomIn(), "Zoom in", this);
        this.add(this.zoomIn);

        this.zoomOut = new IconButton(RESOURCES.zoomOutIcon(), css.zoomOut(), "Zoom out", this);
        this.add(this.zoomOut);

        this.up = new IconButton(RESOURCES.upIcon(), css.up(), "Move up", this);
        this.add(this.up);
        this.left = new IconButton(RESOURCES.leftIcon(), css.left(), "Move left", this);
        this.add(this.left);
        this.right = new IconButton(RESOURCES.rightIcon(), css.right(), "Move right", this);
        this.add(this.right);
        this.down = new IconButton(RESOURCES.downIcon(), css.down(), "Move down", this);
        this.add(this.down);
    }

    @Override
    public void onClick(ClickEvent event) {
        ControlAction action = ControlAction.NONE;
        Button btn = (Button) event.getSource();
        if (btn.equals(this.zoomIn)) {
            action = ControlAction.ZOOM_IN;
        } else if (btn.equals(this.zoomOut)) {
            action = ControlAction.ZOOM_OUT;
        } else if (btn.equals(this.up)) {
            action = ControlAction.UP;
        } else if (btn.equals(this.right)) {
            action = ControlAction.RIGHT;
        } else if (btn.equals(this.down)) {
            action = ControlAction.DOWN;
        } else if (btn.equals(this.left)) {
            action = ControlAction.LEFT;
        }
        if (!action.equals(ControlAction.NONE)) {
            this.eventBus.fireEventFromSource(new ControlActionEvent(action), this);
        }
    }


    public static ControlResources RESOURCES;
    static {
        RESOURCES = GWT.create(ControlResources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    /**
     * A ClientBundle of resources used by this widget.
     */
    public interface ControlResources extends ClientBundle {
        /**
         * The styles used in this widget.
         */
        @Source(ControlPanelCSS.CSS)
        ControlPanelCSS getCSS();

        @Source("images/down.png")
        ImageResource downIcon();

        @Source("images/left.png")
        ImageResource leftIcon();

        @Source("images/right.png")
        ImageResource rightIcon();

        @Source("images/up.png")
        ImageResource upIcon();

        @Source("images/zoomin.png")
        ImageResource zoomInIcon();

        @Source("images/zoomout.png")
        ImageResource zoomOutIcon();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("fireworks-NavigationControlPanel")
    public interface ControlPanelCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/controls/navigation/NavigationControlPanel.css";

        String controlPanel();

        String down();

        String left();

        String right();

        String up();

        String zoomIn();

        String zoomOut();
    }
}

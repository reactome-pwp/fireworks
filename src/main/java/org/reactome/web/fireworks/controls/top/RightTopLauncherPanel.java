package org.reactome.web.fireworks.controls.top;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import org.reactome.web.fireworks.controls.common.IconButton;
import org.reactome.web.fireworks.controls.top.illustrations.Illustrations;
import org.reactome.web.fireworks.controls.top.key.PathwayOverviewKey;
import org.reactome.web.fireworks.events.CanvasExportRequestedEvent;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class RightTopLauncherPanel extends FlowPanel implements ClickHandler {

    private EventBus eventBus;

    private Illustrations diagramIllustrations;
    private PathwayOverviewKey pathwayOverviewKey;

    private Button illustrationsBtn;
    private Button exportBtn;
    private Button pathwayOverviewKeyBtn;

    public RightTopLauncherPanel(EventBus eventBus) {
        this.setStyleName(RESOURCES.getCSS().launcherPanel());

        this.eventBus = eventBus;
        this.diagramIllustrations = new Illustrations(eventBus);
        this.pathwayOverviewKey = new PathwayOverviewKey(eventBus);

        this.illustrationsBtn = new IconButton(RESOURCES.illustrationsIcon(), RESOURCES.getCSS().illustrations(), "Show illustrations", this);
        this.add(illustrationsBtn);

        this.exportBtn = new IconButton(RESOURCES.exportIcon(), RESOURCES.getCSS().export(), "Export", this);
        this.add(exportBtn);

        this.pathwayOverviewKeyBtn = new IconButton(RESOURCES.keyIcon(), RESOURCES.getCSS().key(), "Pathway overview key", this);
        this.add(this.pathwayOverviewKeyBtn);

        this.setVisible(true);
    }

    @Override
    public void onClick(ClickEvent event) {
        Button btn = (Button) event.getSource();
        if (btn.equals(this.exportBtn)) {
            this.eventBus.fireEventFromSource(new CanvasExportRequestedEvent(), this);
        } else if (btn.equals(this.pathwayOverviewKeyBtn)) {
            if (this.pathwayOverviewKey.isShowing()) {
                this.pathwayOverviewKey.hide();
            } else {
                this.pathwayOverviewKey.showRelativeTo(this.pathwayOverviewKeyBtn);
            }
        } else if (btn.equals(this.illustrationsBtn)) {
            if (this.diagramIllustrations.isShowing()) {
                this.diagramIllustrations.hide();
            } else {
                this.diagramIllustrations.showRelativeTo(btn);
            }
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

        @Source("images/illustrations.png")
        ImageResource illustrationsIcon();

        @Source("images/key.png")
        ImageResource keyIcon();

        @Source("images/export.png")
        ImageResource exportIcon();

    }

    @CssResource.ImportedWithPrefix("fireworks-LeftTopLauncher")
    public interface ResourceCSS extends CssResource {
        String CSS = "org/reactome/web/fireworks/controls/top/RightTopLauncherPanel.css";

        String launcherPanel();

        String export();

        String illustrations();

        String key();
    }
}

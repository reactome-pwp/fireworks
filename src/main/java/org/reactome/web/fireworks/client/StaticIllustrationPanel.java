package org.reactome.web.fireworks.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import org.reactome.web.fireworks.legends.ControlButton;

/**
 * This panel is displayed when the user selects one of the static illustrations (figures)
 */
public class StaticIllustrationPanel extends AbsolutePanel implements RequiresResize, ClickHandler {

    private ScrollPanel container;
    private String url;

    public StaticIllustrationPanel(){
        setStyleName(RESOURCES.getCSS().panelHidden());
        this.getElement().addClassName("pwp-FireworksCanvas");
        container = new ScrollPanel();
        container.setStyleName(RESOURCES.getCSS().container());
    }

    public void create(String url) {
        this.url = url;
        this.getElement().addClassName("pwp-FireworksCanvas");
        container.clear();
        Image img = new Image(url);
        img.getElement().addClassName("pwp-FireworksCanvas");
        container.add(img);
        add(container);
        add(new ControlButton("Close", RESOURCES.getCSS().close(), this));
        onResize();
    }

    public void toggle() {
        if (this.getStyleName().contains(StaticIllustrationPanel.RESOURCES.getCSS().panelShown())) {
            this.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelHidden());
        } else {
            this.setStyleName(StaticIllustrationPanel.RESOURCES.getCSS().panelShown());
        }
    }

    public void setPanelElements(String url){
        this.clear();
        create(url);
    }

    @Override
    public void clear() {
        super.clear();
        url = null;
    }

   @Override
    public void onClick(ClickEvent event) {
        reset();
    }

    @Override
    public void onResize() {
        container.setWidth(getElement().getStyle().getWidth());
        container.setHeight(getElement().getStyle().getWidth());
    }

    public void reset(){
        setStyleName(RESOURCES.getCSS().panelHidden());
        this.getElement().addClassName("pwp-FireworksCanvas");
    }

    public static Resources RESOURCES;
    static {
        RESOURCES = GWT.create(Resources.class);
        RESOURCES.getCSS().ensureInjected();
    }

    public interface Resources extends ClientBundle {
        @Source(ResourceCSS.CSS)
        ResourceCSS getCSS();

        @Source("images/close_clicked.png")
        ImageResource closeClicked();

        @Source("images/close_hovered.png")
        ImageResource closeHovered();

        @Source("images/close_normal.png")
        ImageResource closeNormal();
    }

    @CssResource.ImportedWithPrefix("fireworks-StaticIllustrationsPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/client/StaticIllustrationsPanel.css";

        String panelHidden();

        String panelShown();

        String container();

        String close();
    }
}

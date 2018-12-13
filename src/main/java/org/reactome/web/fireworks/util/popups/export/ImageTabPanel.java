package org.reactome.web.fireworks.util.popups.export;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import org.reactome.web.fireworks.util.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ImageTabPanel extends FlowPanel {
    private final String speciesName;
    private final String selected;
    private final String flagged;
    private final Boolean includeInteractors;
    private final String analysisToken;
    private final String resource;
    private final String fireworksProfile;

    private static final int DEFAULT_MARGIN = 15;

    private Image loader;
    private Image preview;
    private FlowPanel imagePanel;

    public ImageTabPanel(final String speciesName,
                         final String selected,
                         final String flagged,
                         final Boolean includeInteractors,
                         final String analysisToken,
                         final String resource,
                         final String fireworksProfile) {
        this.speciesName = speciesName;
        this.selected = selected;
        this.flagged = flagged;
        this.includeInteractors = includeInteractors;
        this.analysisToken = analysisToken;
        this.resource = resource;
        this.fireworksProfile = fireworksProfile;

        // Initialise the panel with a loading animation
        initUI();

        // Retrieve the preview image in svg format from the server
        getImagePreview();

    }

    private void initUI() {
        loader = new Image(RESOURCES.loader());
        loader.setStyleName(RESOURCES.getCSS().image());

        imagePanel = new FlowPanel();
        imagePanel.add(loader);
        imagePanel.setStyleName(RESOURCES.getCSS().imagePanel());

        FlowPanel mainPanel = new FlowPanel();
        mainPanel.addStyleName(RESOURCES.getCSS().mainPanel());
        mainPanel.add(imagePanel);
        mainPanel.add(getButtonsPanel());
        this.add(mainPanel);
    }

    private void getImagePreview() {
        preview = new Image();

        imagePanel.add(preview);
        preview.setVisible(false);
        preview.addLoadHandler(event -> {
            loader.getElement().removeFromParent();
            preview.setStyleName(RESOURCES.getCSS().image());
            preview.setVisible(true);
        });

        String previewUrl = generateUrlList(ImageDownloadType.SVG).get(0) + "&title=false";
        preview.setUrl(UriUtils.encode(previewUrl));
        Console.info(UriUtils.isSafeUri(previewUrl));
//        preview.setUrl(previewUrl);
    }


    private Widget getButtonsPanel() {
        FlowPanel rtn = new FlowPanel();
        rtn.setStyleName(RESOURCES.getCSS().buttonsPanel());

        for (ImageDownloadType format : ImageDownloadType.values()) {
            List<String> urls = generateUrlList(format);
            // Add all buttons with one exception;
            // SBGN should only be added if a node is selected.
            DownloadButton<ImageDownloadType> button =  new DownloadButton<>(format, urls.stream().map(u -> UriUtils.encode(u)).collect(Collectors.toList()));
            if(format != ImageDownloadType.SBGN) {
                rtn.add(button);
            } else {
                if (selected!=null && !selected.isEmpty()) {
                    rtn.add(button);
                }
            }
        }

        return rtn;
    }

    public List<String> generateUrlList(ImageDownloadType type) {
        List<String> rtn = new ArrayList<>();

        String baseUrl = type.getTemplateURL().replace("__SPECIES__", UriUtils.encode(speciesName));

        if(selected!=null && !selected.isEmpty()) {
            baseUrl = baseUrl.replace("__STID__", selected);
        }

        if (baseUrl.contains("__PARAMS__")) {
            List<String> params = new ArrayList<>();

            params.add("margin=" + DEFAULT_MARGIN);
            params.add("diagramProfile=" + fireworksProfile);

            if (selected != null) { params.add("sel=" + selected); }
            if (flagged != null)  {
                params.add("flg=" + flagged);
                if(includeInteractors!=null) {
                    params.add("flgInteractors=" + includeInteractors);
                }
            }
            if (analysisToken != null && !analysisToken.isEmpty()) {
                params.add("token=" + analysisToken);
                params.add("resource=" + resource);
            }

            String paramsStr = "?" + params.stream().collect(Collectors.joining("&"));
            baseUrl = baseUrl.replace("__PARAMS__", paramsStr);

            if (type.hasQualityOptions()) {
                String url = baseUrl;
                type.QUALITIES.forEach(quality -> {
                    rtn.add(url + "&quality=" + quality);
                });
            } else {
                rtn.add(baseUrl);
            }
        } else {
            rtn.add(baseUrl);
        }

        return rtn;
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

        @Source("../images/loader.gif")
        ImageResource loader();
    }

    /**
     * Styles used by this widget.
     */
    @CssResource.ImportedWithPrefix("diagram-ImageTabPanel")
    public interface ResourceCSS extends CssResource {
        /**
         * The path to the default CSS styles used by this resource.
         */
        String CSS = "org/reactome/web/fireworks/util/popups/export/ImageTabPanel.css";

        String mainPanel();

        String unselectable();

        String undraggable();

        String imagePanel();

        String image();

        String buttonsPanel();

    }
}

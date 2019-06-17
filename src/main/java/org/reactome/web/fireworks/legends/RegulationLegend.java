package org.reactome.web.fireworks.legends;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.InlineLabel;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.fireworks.events.*;
import org.reactome.web.fireworks.handlers.*;
import org.reactome.web.fireworks.model.Node;
import org.reactome.web.fireworks.profiles.FireworksColours;
import org.reactome.web.fireworks.util.ColorMap;

import java.util.Map;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class RegulationLegend extends LegendPanel implements AnalysisPerformedHandler, AnalysisResetHandler,
        NodeHoverHandler, NodeHoverResetHandler, NodeSelectedHandler, NodeSelectedResetHandler,
        ExpressionColumnChangedHandler, ProfileChangedHandler, MouseMoveHandler {

    private static String TOP_LABEL  = "Up-regulated";
    private static String BOTTOM_LABEL = "Down-regulated";

    private static String[] LABELS = {  "Significantly up regulated",
                                        "Non significantly up regulated",
                                        "Not found",
                                        "Non significantly down regulated",
                                        "Significantly down regulated"};

    private static String[] SYMBOLS = { "\u25BC\u25BC",
                                        "\u25BC",
                                        "-",
                                        "\u25B2",
                                        "\u25B2\u25B2"};

    private Canvas gradient;
    private Canvas flag;
    private Node hovered;
    private Node selected;

    private double min;
    private double max;
    private int column = 0;

    private InlineLabel topLabel;
    private InlineLabel bottomLabel;

    private int hoveredScaleIndex;

    public RegulationLegend(EventBus eventBus) {
        super(eventBus);
        this.gradient = createCanvas(30, 200);
        this.flag = createCanvas(50, 210);
        this.flag.addMouseMoveHandler(this);
        this.flag.setTitle(LABELS[hoveredScaleIndex]);

        //Setting the legend style
        addStyleName(RESOURCES.getCSS().expressionLegend());

        fillPalette();

        this.topLabel = new InlineLabel("");
        this.topLabel.setStyleName(RESOURCES.getCSS().regulationLabel());
        this.add(this.topLabel, 5, 3);

        this.add(this.gradient, 10, 25);
        this.add(this.flag, 0, 20);

        this.bottomLabel = new InlineLabel("");
        this.bottomLabel.setStyleName(RESOURCES.getCSS().regulationLabel());
        this.add(this.bottomLabel, 5, 230);

        initHandlers();

        this.setVisible(false);
    }

    private Canvas createCanvas(int width, int height) {
        Canvas canvas = Canvas.createIfSupported();
        canvas.setCoordinateSpaceWidth(width);
        canvas.setCoordinateSpaceHeight(height);
        canvas.setPixelSize(width, height);

        //Set text properties once
        Context2d ctx = canvas.getContext2d();
        ctx.setFont("bold 13px Arial");
        ctx.setTextBaseline(Context2d.TextBaseline.MIDDLE);
        ctx.setTextAlign(Context2d.TextAlign.CENTER);
        return canvas;
    }


    @Override
    public void onNodeHover(NodeHoverEvent event) {
        if(!event.getNode().equals(this.selected)) {
            this.hovered = event.getNode();
        }
        this.draw();
    }

    @Override
    public void onAnalysisPerformed(AnalysisPerformedEvent e) {
        switch (e.getAnalysisType()){
            case GSA_REGULATION:
                ExpressionSummary es = e.getExpressionSummary();
                if(es!=null){
                    this.min = es.getMin();
                    this.max = es.getMax();
                    this.topLabel.setText(TOP_LABEL);
                    this.bottomLabel.setText(BOTTOM_LABEL);
                }
                setVisible(true);
                break;
            default:
                setVisible(false);
        }
    }

    @Override
    public void onAnalysisReset() {
        this.setVisible(false);
    }

    @Override
    public void onExpressionColumnChanged(ExpressionColumnChangedEvent e) {
        this.column = e.getColumn();
        draw();
    }

    @Override
    public void onMouseMove(MouseMoveEvent event) {
        int y = event.getRelativeY(gradient.getElement());
        int stepHeight = 40;
        int scaleIndex = (y / stepHeight);
        if (hoveredScaleIndex != scaleIndex) {
            flag.setTitle(LABELS[scaleIndex]);
            hoveredScaleIndex = scaleIndex;
        }
    }

    @Override
    public void onNodeHoverReset() {
        this.hovered = null;
        this.draw();
    }

    @Override
    public void onNodeSelected(NodeSelectedEvent event) {
        this.hovered = null;
        this.selected = event.getNode();
        this.draw();
    }

    @Override
    public void onNodeSelectionReset() {
        this.selected = null;
        this.draw();
    }

    @Override
    public void onProfileChanged(ProfileChangedEvent event) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                fillPalette();
                draw();
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        this.draw();
    }

    private void fillPalette() {
        Map<Integer,String> colorMap = FireworksColours.PROFILE.nodeRegulationColorMap.getPalette();

        Context2d ctx = this.gradient.getContext2d();
        ctx.clearRect(0, 0, this.gradient.getCoordinateSpaceWidth(), this.gradient.getCoordinateSpaceHeight());
        double height = this.gradient.getCoordinateSpaceHeight() / (double) colorMap.size();

        int i = 0;
        for (Integer key : colorMap.keySet()) {
            ctx.setFillStyle(colorMap.get(key));
            ctx.beginPath();
            ctx.fillRect(0, i * height, 30, height);
            ctx.closePath();

            // Labels
            ctx.setShadowColor("rgba(0,0,0,0.5)");
            ctx.setShadowBlur(4);
            ctx.setFillStyle("#FFFFFF");
            ctx.fillText(SYMBOLS[key + 2], 15, i * height + height/2.0);

            i++;
        }
    }

    @SuppressWarnings("Duplicates")
    private void draw(){
        if(!this.isVisible()) return;

        Context2d ctx = this.flag.getContext2d();
        ctx.clearRect(0, 0, this.flag.getOffsetWidth(), this.flag.getOffsetHeight());

        if(this.hovered!=null){
            EntityStatistics statistics = this.hovered.getStatistics();
            if(statistics!=null && statistics.getpValue()<0.05){
                if(statistics.getExp()!=null) {
                    String colour = FireworksColours.PROFILE.getNodeHighlightColour();
                    double expression = statistics.getExp().get(this.column);
                    double p = ColorMap.getPercentage(expression, this.min, this.max);
                    int y = (int) Math.round(200 * p) + 5;
                    ctx.setFillStyle(colour);
                    ctx.setStrokeStyle(colour);
                    ctx.beginPath();
                    ctx.moveTo(5, y - 5);
                    ctx.lineTo(10, y);
                    ctx.lineTo(5, y + 5);
                    ctx.lineTo(5, y - 5);
                    ctx.fill();
                    ctx.stroke();
                    ctx.closePath();

//                    ctx.beginPath();
//                    ctx.moveTo(10, y);
//                    ctx.lineTo(40, y);
//                    ctx.stroke();
//                    ctx.closePath();
                }
            }
        }

        if(this.selected!=null){
            EntityStatistics statistics = this.selected.getStatistics();
            if(statistics!=null && statistics.getpValue()<0.05){
                if(statistics.getExp()!=null) {
                    String colour = FireworksColours.PROFILE.getNodeSelectionColour();
                    double expression = statistics.getExp().get(this.column);
                    double p = ColorMap.getPercentage(expression, this.min, this.max);
                    int y = (int) Math.round(200 * p) + 5;
                    ctx.setFillStyle(colour);
                    ctx.setStrokeStyle(colour);
                    ctx.beginPath();
                    ctx.moveTo(45, y - 5);
                    ctx.lineTo(40, y);
                    ctx.lineTo(45, y + 5);
                    ctx.lineTo(45, y - 5);
                    ctx.fill();
                    ctx.stroke();
                    ctx.closePath();

//                    ctx.beginPath();
//                    ctx.moveTo(10, y);
//                    ctx.lineTo(40, y);
//                    ctx.stroke();
//                    ctx.closePath();
                }
            }
        }
    }

    private void initHandlers() {
        this.eventBus.addHandler(AnalysisPerformedEvent.TYPE, this);
        this.eventBus.addHandler(AnalysisResetEvent.TYPE, this);
        this.eventBus.addHandler(NodeHoverEvent.TYPE, this);
        this.eventBus.addHandler(NodeHoverResetEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedEvent.TYPE, this);
        this.eventBus.addHandler(NodeSelectedResetEvent.TYPE, this);
        this.eventBus.addHandler(ExpressionColumnChangedEvent.TYPE, this);
        this.eventBus.addHandler(ProfileChangedEvent.TYPE, this);
    }
}

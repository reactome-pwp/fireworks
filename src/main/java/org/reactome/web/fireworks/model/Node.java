package org.reactome.web.fireworks.model;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.regexp.shared.RegExp;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.EntityStatistics;
import org.reactome.web.analysis.client.model.SpeciesFilteredResult;
import org.reactome.web.fireworks.interfaces.Drawable;
import org.reactome.web.fireworks.profiles.FireworksColours;
import org.reactome.web.fireworks.util.Coordinate;
import uk.ac.ebi.pwp.structures.quadtree.client.QuadTreeBox;

import java.util.*;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
@SuppressWarnings("UnusedDeclaration")
public class Node extends FireworkObject implements Drawable, QuadTreeBox, Comparable<Node> {

    private Long dbId;
    private String stId;

    private String name;
    private double ratio;
    private EntityStatistics statistics;
    private String searchDisplay;

    private double angle;
    private double currentSize;
    private double originalSize;
    private boolean disease;

    private boolean insideFilter;

    private String enrichmentColour;
    private String coverageColour;

    private double alpha = 1.0;
    private List<String> expColours;

    private Coordinate currentPosition;
    Coordinate originalPosition;

    private Set<Edge> edgesFrom = new HashSet<>();
    private Set<Edge> edgesTo = new HashSet<>();

    private Set<Node> children = new HashSet<>();
    private Set<Node> parents = new HashSet<>();

    public Node(JSONObject raw){
        this.dbId = (long) raw.get("dbId").isNumber().doubleValue();
        this.stId = raw.get("stId").isString().stringValue();
        this.name = raw.get("name").isString().stringValue();
        this.ratio = raw.get("ratio").isNumber().doubleValue();
        this.angle = raw.get("angle").isNumber().doubleValue();
        this.disease = raw.get("disease") != null && raw.get("disease").isBoolean().booleanValue();
        this.currentSize = this.originalSize = (ratio + 0.025) * 15;
        this.currentPosition = this.originalPosition = new Coordinate(raw.get("x").isNumber().doubleValue(), raw.get("y").isNumber().doubleValue());
        initStatistics(); //Colour is set in initStatistics method
    }

    public Edge addChild(Node node){
        if(node==this) return null;
        this.children.add(node);
        node.parents.add(this);
        Edge edge = new Edge(this, node);
        this.edgesFrom.add(edge);
        node.edgesTo.add(edge);
        return edge;
    }

    public boolean isTopLevel(){
        return this.parents.isEmpty();
    }

    @Override
    public void draw(Context2d ctx) {
        ctx.setGlobalAlpha(alpha);
        ctx.beginPath();
        ctx.arc(this.getX(), this.getY(), this.getSize(), 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill(); //ctx.stroke();
    }

    @Override
    public void drawText(Context2d ctx, double fontSize, double space, boolean selected) {
        if(!this.isTopLevel()) {
            ctx.save(); //Needed before the calculations b
            if(selected) ctx.setFont("bold " + ctx.getFont());

            double x = currentPosition.getX();
            double y = currentPosition.getY();
            double angle = this.angle;
            space += getSize();
            if(this.angle > (Math.PI/2) && this.angle < (Math.PI * 3 / 2) ) {
                double width = ctx.measureText(name).getWidth() + space;
                x = x + width * Math.cos(angle);
                y = y + width * Math.sin(angle);
                angle += Math.PI;
            }else{
                x = x + space * Math.cos(angle);
                y = y + space * Math.sin(angle);
            }

            ctx.translate(x, y);
            ctx.rotate(angle);
            ctx.fillText(name, 0, 0);
            ctx.restore();
        }else {  //Text for top level pathways
            List<String> lines = this.getLines();
            double linesSeparation = fontSize * 0.75;
            double vSpace = lines.size() * (fontSize + linesSeparation);
            for (String line : lines) {
                double width = ctx.measureText(line).getWidth();
                double x = currentPosition.getX() - width/2;
                double y = currentPosition.getY() - getSize() - vSpace;
                ctx.fillText(line, x, y);
                vSpace -= (fontSize + linesSeparation);
            }
        }
    }

    @Override
    public void drawThumbnail(Context2d ctx, double factor) {
//        ctx.setGlobalAlpha(alpha);
        ctx.beginPath();
        double x = originalPosition.getX() * factor;
        double y = originalPosition.getY() * factor;
        double size = originalSize * factor;
        ctx.arc(x, y, size, 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill();ctx.stroke();
    }

    public Set<Node> getTopLevelPathways(){
        Set<Node> rtn = new HashSet<>();
        if(this.isTopLevel()) rtn.add(this);
        for (Node parent : this.parents) {
            rtn.addAll(parent.getTopLevelPathways());
        }
        return rtn;
    }

    @Override
    public void highlight(Context2d ctx, double auraSize) {
        ctx.setGlobalAlpha(alpha);
        ctx.beginPath();
        ctx.arc(this.getX(), this.getY(), this.getSize() + auraSize, 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill();ctx.stroke();
    }

    @Override
    public int compareTo(Node o) {
        int cmp = getName().compareTo(o.getName());
        if(cmp==0){
            cmp = getDbId().compareTo(o.getDbId());
        }
        return cmp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        //noinspection RedundantIfStatement
        if (dbId != null ? !dbId.equals(node.dbId) : node.dbId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return dbId != null ? dbId.hashCode() : 0;
    }

    public Set<Node> getAncestors(){
        Set<Node> rtn = new HashSet<>();
        for (Node parent : this.parents) {
            rtn.add(parent);
            rtn.addAll(parent.getAncestors());
        }
        return rtn;
    }

    public Set<Node> getChildren() {
        return children;
    }

    /**
     * Can be used either for normal visualisation, overrepresentation analysis or species comparison.
     *
     * @return the color associated with this node for normal visualisation, overrepresentation
     *         analysis or species comparison.
     */
    public String getEnrichmentColour() {
        return this.enrichmentColour;
    }

    public String getCoverageColour() {
        return coverageColour;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    public Set<Edge> getEdgesFrom() {
        return edgesFrom;
    }

    public Set<Edge> getEdgesTo() {
        return edgesTo;
    }

    public String getIdentifier() {
        return stId == null ? dbId + "" : stId;
    }

    public String getName() {
        return name;
    }

    public Set<Node> getParents() {
        return parents;
    }

    public double getRatio() {
        return ratio;
    }

    public EntityStatistics getStatistics() {
        return statistics;
    }

    public double getSize() {
        return currentSize;
    }

    public String getStId() {
        return stId;
    }

    public double getX() {
        return this.currentPosition.getX();
    }

    public double getY() {
        return this.currentPosition.getY();
    }

    public Coordinate getOriginalPosition() {
        return originalPosition;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public String getExpressionColor(int column){
        if(this.expColours!=null){
            if( column >= 0 && column < this.expColours.size()) {
                return this.expColours.get(column);
            }
        }
        return FireworksColours.PROFILE.getNodeFadeoutColour();
    }

    public String getSearchDisplay() {
        return searchDisplay;
    }

    public void setSearchDisplay(String[] searchTerms) {
        this.searchDisplay = this.name;

        if(searchTerms==null || searchTerms.length==0) return;

        StringBuilder sb = new StringBuilder("(");
        for (String term : searchTerms) {
            sb.append(term).append("|");
        }
        sb.delete(sb.length()-1, sb.length()).append(")");
        String term = sb.toString();
        /*
         * (term1|term2)    : term is between "(" and ")" because we are creating a group, so this group can
         *                    be referred later.
         * gi               : global search and case insensitive
         * <b><u>$1</u></b> : instead of replacing by input, that would change the case, we replace it by $1,
         *                    that is the reference to the first matched group. This means that we want to
         *                    replace it using the exact word that was found.
         */
        RegExp regExp = RegExp.compile(term, "gi");
        this.searchDisplay = regExp.replace(this.searchDisplay, "<u><strong>$1</strong></u>");
    }

    public void clearSearchDisplayValue(){
        this.searchDisplay = null;
    }


    public void initStatistics(){
        this.statistics = null;
        this.expColours = null;
        this.enrichmentColour = FireworksColours.PROFILE.getNodeInitialColour();
        this.coverageColour = FireworksColours.PROFILE.getNodeInitialColour();
        for (Edge edge : this.edgesTo) {
            edge.setEnrichmentColour(FireworksColours.PROFILE.getEdgeInitialColour());
            edge.setExpColours(null);
        }
    }

    public boolean isInsideFilter() {
        return insideFilter;
    }

    public void setAnalysisResultData(SpeciesFilteredResult result, EntityStatistics statistics, ResultFilter filter) {
        this.statistics = statistics;
        placeInsideFilter(filter);

        if (!isInsideFilter()) {
            this.enrichmentColour = FireworksColours.PROFILE.getNodeHitColour();
            for (Edge edge : this.edgesTo){
                edge.setEnrichmentColour(FireworksColours.PROFILE.getEdgeHitColour());
            }
            return;
        }

        double fPvalue = filter.getpValue() == null ? 1d : filter.getpValue();
        switch (result.getAnalysisType()){
            case SPECIES_COMPARISON:
            case OVERREPRESENTATION:
                this.enrichmentColour = FireworksColours.PROFILE.getNodeEnrichmentColour(statistics.getpValue(), fPvalue);
                String edgeEnrichmentColour = FireworksColours.PROFILE.getEdgeEnrichmentColour(statistics.getpValue(), fPvalue);

                double p = statistics.getFound() / (double) statistics.getTotal();
                this.coverageColour = FireworksColours.PROFILE.getNodeCoverageColour(p);
                String edgeCoverageColour = FireworksColours.PROFILE.getEdgeCoverageColour(p);
                for (Edge edge : this.edgesTo){
                    edge.setCoverageColour(edgeCoverageColour);
                    edge.setEnrichmentColour(edgeEnrichmentColour);
                }
                break;
            case EXPRESSION:
            case GSA_STATISTICS:
            case GSVA:
                List<Double> exp = this.statistics.getExp();
                if(exp!=null){
                    double min = result.getExpressionSummary().getMin();
                    double max = result.getExpressionSummary().getMax();
                    this.expColours = new ArrayList<>();
                    List<String> edgeExpColours = new ArrayList<>();
                    for (Double v : exp) {
                        this.expColours.add(FireworksColours.PROFILE.getNodeExpressionColour(statistics.getpValue(), v, min, max, fPvalue));
                        edgeExpColours.add(FireworksColours.PROFILE.getEdgeExpressionColour(statistics.getpValue(), v, min, max, fPvalue));
                    }
                    for (Edge edge : this.edgesTo) edge.setExpColours(edgeExpColours);
                }
                break;
            case GSA_REGULATION:
                List<Double> reg = this.statistics.getExp();
                if(reg!=null){
                    double min = result.getExpressionSummary().getMin();
                    double max = result.getExpressionSummary().getMax();
                    this.expColours = new ArrayList<>();
                    List<String> edgeExpColours = new ArrayList<>();
                    for (Double v : reg) {
                        this.expColours.add(FireworksColours.PROFILE.getNodeRegulationColour(statistics.getpValue(), v, fPvalue));
                        edgeExpColours.add(FireworksColours.PROFILE.getEdgeRegulationColour(statistics.getpValue(), v, fPvalue));
                    }
                    for (Edge edge : this.edgesTo) edge.setExpColours(edgeExpColours);
                }
                break;

            case NONE:
            default:
                //Nothing here
        }
    }

    public void setFadeoutColour(){
        this.enrichmentColour = FireworksColours.PROFILE.getNodeFadeoutColour();
        this.coverageColour = FireworksColours.PROFILE.getNodeFadeoutColour();
    }

    public void setTransparency(double alpha){
        this.alpha = alpha;
    }

    // ####################################
    //  QuadTreeBox methods implementation
    // ####################################

    @Override
    public double getMinX() {
        return originalPosition.getX() - originalSize;
    }

    @Override
    public double getMinY() {
        return originalPosition.getY() - originalSize;
    }

    @Override
    public double getMaxX() {
        return originalPosition.getX() + originalSize;
    }

    @Override
    public double getMaxY() {
        return originalPosition.getY() + originalSize;
    }

    // ####################################################################
    //  Translatable method implementation (inherited from FireworkObject)
    // ####################################################################

    @Override
    public void move(Coordinate delta) {
        this.currentPosition = this.currentPosition.add(delta);
    }

    // ###############################################################
    // Zoomable method implementation (inherited from FireworkObject)
    // ###############################################################

    @Override
    public void zoom(double factor, Coordinate delta){
        this.currentSize = this.originalSize * factor;
        this.currentPosition = this.originalPosition.multiply(factor).minus(delta);
    }

    @Override
    public String toString() {
        return "Node{" +
                "stId='" + stId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    private void placeInsideFilter(ResultFilter filter) {
        boolean inside = true;
        if (filter != null && statistics != null) {
            String resource = filter.getResource();
            Double pValue = filter.getpValue();
            boolean includeDisease = filter.getIncludeDisease();
            Integer min = filter.getMin();
            Integer max = filter.getMax();

            if (!resource.equalsIgnoreCase(statistics.getResource())) {
                inside = false;
            } else if (pValue != null && pValue < statistics.getpValue() ) {
                inside = false;
            } else if (!includeDisease && disease) {
                inside = false;
            } else if ((min != null && statistics.getTotal() < min) || (max != null && statistics.getTotal() > max)) {
                inside = false;
            }
        }
        insideFilter = inside;
    }

    private List<String> getLines(){
        List<String> rtn = new LinkedList<>();
        if(name.length()<=15){ //Using name length behaves better than counting words
            rtn.add(this.name);
            return rtn;
        }
        //noinspection RegExpRepeatedSpace
        String[] words = name.split("  *");
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < words.length/2 ; i++) {
            line.append(words[i]).append(" ");
        }
        rtn.add(line.toString().trim());
        line = new StringBuilder();
        for (int i = words.length/2; i < words.length; i++) {
            line.append(words[i]).append(" ");
        }
        rtn.add(line.toString().trim());
        return rtn;
    }
}

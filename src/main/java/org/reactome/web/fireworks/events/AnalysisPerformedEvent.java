package org.reactome.web.fireworks.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.analysis.client.filter.ResultFilter;
import org.reactome.web.analysis.client.model.AnalysisType;
import org.reactome.web.analysis.client.model.ExpressionSummary;
import org.reactome.web.analysis.client.model.PathwayBase;
import org.reactome.web.analysis.client.model.SpeciesFilteredResult;
import org.reactome.web.fireworks.handlers.AnalysisPerformedHandler;

import java.util.List;

/**
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class AnalysisPerformedEvent extends GwtEvent<AnalysisPerformedHandler> {
    public static Type<AnalysisPerformedHandler> TYPE = new Type<>();

    private SpeciesFilteredResult result;
    private ResultFilter filter;

    public AnalysisPerformedEvent(SpeciesFilteredResult result, ResultFilter filter) {
        this.result = result;
        this.filter = filter;
    }

    @Override
    public Type<AnalysisPerformedHandler> getAssociatedType() {
        return TYPE;
    }

    public AnalysisType getAnalysisType() {
        return AnalysisType.getType(this.result.getType());
    }

    public List<PathwayBase> getPathways() {
        return this.result.getPathways();
    }

    public ExpressionSummary getExpressionSummary(){
        return this.result.getExpressionSummary();
    }

    public ResultFilter getFilter() {
        return filter;
    }

    @Override
    protected void dispatch(AnalysisPerformedHandler handler) {
        handler.onAnalysisPerformed(this);
    }

    @Override
    public String toString() {
        return "AnalysisPerformedEvent{" +
                "analysisType='" + getAnalysisType() + '\'' +
                ", pathwaysHit=" + getPathways().size() +
                '}';
    }
}

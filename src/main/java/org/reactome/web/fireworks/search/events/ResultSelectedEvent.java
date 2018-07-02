package org.reactome.web.fireworks.search.events;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.search.SearchResultObject;
import org.reactome.web.fireworks.search.handlers.ResultSelectedHandler;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class ResultSelectedEvent extends GwtEvent<ResultSelectedHandler> {
    public static Type<ResultSelectedHandler> TYPE = new Type<>();

    private SearchResultObject selectedResultItem;
    private ResultType resultType;

    public enum ResultType {
        GLOBAL
    }

    public ResultSelectedEvent(SearchResultObject selectedResultItem, ResultType resultType) {
        this.selectedResultItem = selectedResultItem;
        this.resultType = resultType;
    }

    @Override
    public Type<ResultSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ResultSelectedHandler handler) {
        handler.onResultSelected(this);
    }

    public SearchResultObject getSelectedResultItem() {
        return selectedResultItem;
    }

    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public String toString() {
        return "ResultSelectedEvent{" +
                "selectedResultItem=" + selectedResultItem.getPrimarySearchDisplay() +
                ", resultType=" + resultType +
                '}';
    }
}

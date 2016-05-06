package org.reactome.web.fireworks.controls.common.pager;

import com.google.gwt.event.shared.GwtEvent;
import org.reactome.web.fireworks.search.searchonfire.solr.model.FireworksResult;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class PageChangedEvent extends GwtEvent<PageChangedHandler> {
    public static Type<PageChangedHandler> TYPE = new Type<>();

    private FireworksResult results;

    public PageChangedEvent(FireworksResult results) {
        this.results = results;
    }

    @Override
    public Type<PageChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PageChangedHandler handler) {
        handler.onPageChanged(this);
    }

    public FireworksResult getResults() {
        return results;
    }
}

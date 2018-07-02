package org.reactome.web.fireworks.search.facets;

import com.google.gwt.http.client.*;
import org.reactome.web.fireworks.client.FireworksFactory;
import org.reactome.web.fireworks.search.SearchArguments;
import org.reactome.web.fireworks.search.results.data.SearchException;
import org.reactome.web.fireworks.search.results.data.SearchResultFactory;
import org.reactome.web.fireworks.search.results.data.model.SearchSummary;
import org.reactome.web.fireworks.util.Console;


/**
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class SearchSummaryFactory {
    private static final String BASE_URL = FireworksFactory.SERVER + "/ContentService/search/diagram/summary?query=##QUERY##&species=##SPECIES##&diagram=##DIAGRAM##";
    private static Request request;

    public interface Handler {
        void onSearchSummaryReceived(SearchSummary summary);
        void onSearchSummaryError(String msg);
    }

    public static void queryForSummary(final SearchArguments arguments, final Handler handler){
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = BASE_URL.replace("##QUERY##", URL.encode(arguments.getQuery()))
                             .replace("##SPECIES##", arguments.getSpecies())
                             .replace("##DIAGRAM##", "dId"); // for the moment diagram param is mandatory.
                                                                               // TODO: Remove it when it becomes optional

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onSearchSummaryReceived(getSummary(response.getText(), handler));
                            break;
                        default:
                            handler.onSearchSummaryError(response.getStatusText());
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    Console.error(exception.getCause());
                    handler.onSearchSummaryError(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
    }

    private static SearchSummary getSummary(final String json, final Handler handler) {
        SearchSummary rtn = null;
        try {
            rtn = SearchResultFactory.getSearchObject(SearchSummary.class, json);
        } catch (SearchException ex) {
            handler.onSearchSummaryError(ex.getMessage());
        }
        return rtn;
    }
}

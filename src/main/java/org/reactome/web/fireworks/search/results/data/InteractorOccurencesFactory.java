package org.reactome.web.fireworks.search.results.data;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.reactome.web.fireworks.client.FireworksFactory;
import org.reactome.web.fireworks.search.SearchArguments;
import org.reactome.web.fireworks.search.results.data.SearchException;
import org.reactome.web.fireworks.search.results.data.SearchResultFactory;
import org.reactome.web.fireworks.search.results.data.model.SearchSummary;
import org.reactome.web.fireworks.util.Console;
import org.reactome.web.pwp.model.client.classes.DatabaseObject;
import org.reactome.web.pwp.model.client.classes.Pathway;
import org.reactome.web.pwp.model.client.classes.Publication;
import org.reactome.web.pwp.model.client.factory.DatabaseObjectFactory;
import org.reactome.web.pwp.model.client.util.ResponseUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 *
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public abstract class InteractorOccurencesFactory {
    private static final String BASE_URL = FireworksFactory.SERVER + "/ContentService/interactors/static/molecule/##QUERY##/pathways?&species=##SPECIES##";
    private static Request request;

    public interface Handler {
        void onInteractorOccurencesReceived(List<Pathway> pathways);
        void onInteractorOccurencesError(String msg);
    }

    public static void query(final String query, final String species, final Handler handler){
        if (request != null && request.isPending()) {
            request.cancel();
        }

        String url = BASE_URL.replace("##QUERY##", URL.encode(query))
                             .replace("##SPECIES##", species);


        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        requestBuilder.setHeader("Accept", "application/json");

        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            handler.onInteractorOccurencesReceived(getPathways(response.getText()));
                            break;
                        default:
                            handler.onInteractorOccurencesError(ResponseUtils.getStatusText(response.getStatusCode()));
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    Console.error(exception.getCause());
                    handler.onInteractorOccurencesError(exception.getMessage());
                }
            });
        } catch (RequestException ex) {
            handler.onInteractorOccurencesError(ex.getMessage());
        }
    }

    private static <T extends DatabaseObject> List<T> getPathways(final String json) {
        JSONArray list = JSONParser.parseStrict(json).isArray();
        if (list != null) {
            DatabaseObjectFactory.cmds.clear();
            List<T> rtn = new LinkedList<>();
            for (int i = 0; i < list.size(); ++i) {
                JSONObject object = list.get(i).isObject();
                //noinspection unchecked
                rtn.add((T) DatabaseObjectFactory.create(object));
            }
            DatabaseObjectFactory.fillUpObjectRefs();
            return rtn;
        }
        return new LinkedList<>();
    }
}

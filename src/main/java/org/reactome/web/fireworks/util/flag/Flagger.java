package org.reactome.web.fireworks.util.flag;

import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.reactome.web.fireworks.client.FireworksFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Fun with flags!
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 */
public class Flagger {
    private static final String SEARCH = "/ContentService/search/fireworks/flag?query=##term##&species=##species##&includeInteractors=##includeInteractors##";
    private static Request request;

    public interface PathwaysToFlagHandler {
        void onPathwaysToFlag(List<String> llps, List<String> interactsWith);
        void onPathwaysToFlagError();
    }

    public static void findPathwaysToFlag(final String term, final String species, final Boolean includeInteractors, final PathwaysToFlagHandler handler) {
        String url = FireworksFactory.SERVER + SEARCH.replace("##term##", term)
                                                     .replace("##species##", species)
                                                     .replace("##includeInteractors##", includeInteractors.toString());

        if (request != null && request.isPending()) request.cancel();

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);

        try {
            request = requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    List<String> llps = new LinkedList<>();
                    List<String> interactsWith = new LinkedList<>();
                    switch (response.getStatusCode()){
                        case Response.SC_OK:
                            JSONObject object = JSONParser.parseLenient(response.getText()).isObject();

                            JSONArray list;
                            if(object.containsKey("llps")) {
                                list = object.get("llps").isArray();
                                for (int i = 0; i < list.size(); ++i) {
                                    llps.add(list.get(i).isString().stringValue());
                                }
                            }

                            if(object.containsKey("interactsWith")) {
                                list = object.get("interactsWith").isArray();
                                for (int i = 0; i < list.size(); ++i) {
                                    interactsWith.add(list.get(i).isString().stringValue());
                                }
                            }

                            handler.onPathwaysToFlag(llps, interactsWith);
                            break;
                        case Response.SC_NOT_FOUND:
                            handler.onPathwaysToFlag(llps, interactsWith);
                        default:
                            handler.onPathwaysToFlagError();
                    }
                }
                @Override
                public void onError(Request request, Throwable exception) {
                    handler.onPathwaysToFlagError();
                }
            });
        }catch (RequestException ex) {
            handler.onPathwaysToFlagError();
        }
    }
}

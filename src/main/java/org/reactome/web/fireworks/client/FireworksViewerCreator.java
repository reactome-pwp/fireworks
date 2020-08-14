package org.reactome.web.fireworks.client;

/**
 * 
 * @author brunsont
 *
 */
public interface FireworksViewerCreator {
	
	public FireworksViewerImpl createFireworksView(String json);
	
}

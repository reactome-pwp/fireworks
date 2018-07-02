package org.reactome.web.fireworks.search.results.data.model;

import java.util.List;

/**
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public interface Entry {

    String getStId();

    String getDbId();

    String getName();

    String getExactType();

    List<String> getCompartmentNames();

    String getReferenceIdentifier();

    String getDatabaseName();

    String getReferenceURL();

    default String getIdentifier() {
        return getStId()!=null ? getStId() : getDbId();
    }
}

package org.reactome.web.fireworks.search;

import com.google.gwt.regexp.shared.RegExp;
import org.reactome.web.fireworks.search.common.RegExpUtil;

import java.util.*;

/**
 * Immutable class that holds the arguments of the specific search.
 * e.g. the query, each of the search terms, the diagramId etc.
 * All query terms are stored in lowercase.
 * <p/>
 * Also this class is responsible for compiling once the regular expression
 * used for highlighting of the results.
 *
 * @author Antonio Fabregat <fabregat@ebi.ac.uk>
 * @author Kostas Sidiropoulos <ksidiro@ebi.ac.uk>
 */
public class SearchArguments {
    private String query;
    private String species;
    private Set<String> facets;
    private int facetsScope;

    private List<String> terms = new ArrayList<>();
    private RegExp highlightingRegExp = null;

    public SearchArguments(String query, String species, Set<String> facets, int facetsScope) {
        this.query = query.toLowerCase();
        this.species = species;
        this.facets = facets;
        this.facetsScope = facetsScope;

        if (hasValidQuery()) {
            String[] allTerms = query.toLowerCase().split("  *");
            if (allTerms.length != 0) {
                terms = Arrays.asList(allTerms);
                highlightingRegExp = RegExpUtil.getHighlightingExpression(terms);
            }
        }
    }

    public boolean hasValidQuery(){
        return query != null && !query.isEmpty();
    }

    public String getQuery() {
        return query;
    }

    public List<String> getTerms() {
        return terms;
    }

    public int sizeOfTerms() {
        return terms.size();
    }

    public String getSpecies() {
        return species;
    }

    public Set<String> getFacets() {
        return facets;
    }

    public int getFacetsScope() {
        return facetsScope;
    }

    public RegExp getHighlightingExpression() {
        return highlightingRegExp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchArguments that = (SearchArguments) o;
        return facetsScope == that.facetsScope &&
                Objects.equals(query, that.query) &&
                Objects.equals(species, that.species) &&
                Objects.equals(facets, that.facets);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, species, facets, facetsScope);
    }

    @Override
    public String toString() {
        return "SearchArguments{" +
                "query='" + query + '\'' +
                " terms='" + terms.size() + '\'' +
                ", species='" + species + '\'' +
                ", facets='" + facets + '\'' +
                '}';
    }
}

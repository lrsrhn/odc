/**
 * The MIT License
 * Copyright © 2018 Lars Storm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dk.ott.dsl.expression;

import dk.ott.core.Edge;
import dk.ott.predicate.Predicate;

public class SearchLocationReference {
    private String searchElement;
    private Predicate predicate;
    private Edge edge;

    public SearchLocationReference(Edge edge, String searchElement) {
        this.edge = edge;
        this.searchElement = searchElement;
    }

    public SearchLocationReference(Edge edge, Predicate predicate) {
        this.edge = edge;
        this.predicate = predicate;
    }

    public String getSearchElement() {
        return searchElement;
    }

    public void setSearchElement(String searchElement) {
        this.searchElement = searchElement;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public boolean isRelative() {
        return edge.isRelative();
    }

    /**
     * Compares two searchLocationReferences by edge.isRelative and (searchElement or predicate)
     */
    public boolean same(SearchLocationReference searchLocationReference) {
        if (searchElement != null ? !searchElement.equals(searchLocationReference.searchElement) : searchLocationReference.searchElement != null)
            return false;
        if (predicate != null ? !predicate.equals(searchLocationReference.predicate) : searchLocationReference.predicate != null)
            return false;
        return edge.isRelative() == searchLocationReference.edge.isRelative();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SearchLocationReference that = (SearchLocationReference) o;

        if (searchElement != null ? !searchElement.equals(that.searchElement) : that.searchElement != null)
            return false;
        if (predicate != null ? !predicate.equals(that.predicate) : that.predicate != null) return false;
        return edge != null ? edge.equals(that.edge) : that.edge == null;
    }

    @Override
    public int hashCode() {
        int result = searchElement != null ? searchElement.hashCode() : 0;
        result = 31 * result + (predicate != null ? predicate.hashCode() : 0);
        result = 31 * result + (edge != null ? edge.hashCode() : 0);
        return result;
    }
}

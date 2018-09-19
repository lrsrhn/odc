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
package dk.simpletools.odc.core.finder;

import dk.simpletools.odc.core.dsl.expression.SearchLocationReference;
import dk.simpletools.odc.core.processing.StructureElement;
import dk.simpletools.odc.core.predicate.Predicate;
import dk.simpletools.odc.core.processing.ElementFinderReference;

import java.util.*;

public final class RootElementFinder implements ElementFinder {
    private String searchElement;
    private SearchLocation searchLocation;
    private ElementFinderReference thisReference;
    private HashMap<String, SearchLocation> relativeElementFinders;

    public RootElementFinder() {
        this.thisReference = new ElementFinderReference(this);
        this.searchLocation = new SearchLocation();
        this.relativeElementFinders = new HashMap<String, SearchLocation>();
    }

    @Override
    public ElementFinder addNextElementFinder(String searchElement, boolean isRelative) {
        if (!isRelative) {
            if (this.searchElement == null) {
                this.searchElement = searchElement.intern();
                searchLocation.setElementFinder(new SingleElementFinder().getReference());
            } else if (!this.searchElement.equals(searchElement)) {
                throw new RuntimeException("Cannot have multiple absolute roots!");
            } else if (searchLocation.getElementFinder() == null) {
                searchLocation.setElementFinder(new SingleElementFinder().getReference());
            }
            return searchLocation.getElementFinder();
        } else {
            SearchLocation searchLocation = relativeElementFinders.get(searchElement);
            if (searchLocation == null) {
                ElementFinder elementFinder = new SingleElementFinder().getReference();
                relativeElementFinders.put(searchElement, new SearchLocation(elementFinder, null, null));
                return elementFinder;
            } else if (searchLocation.getElementFinder() == null) {
                ElementFinder elementFinder = new SingleElementFinder().getReference();
                searchLocation.setElementFinder(elementFinder);
            }
            return searchLocation.getElementFinder();
        }
    }

    @Override
    public ElementFinder addNextElementFinder(Predicate predicate, boolean isRelative) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public ElementFinder addNextPredicate(String searchElement) {
        if (this.searchElement.equals(searchElement)) {
            if (searchLocation.getElementFinder() == null) {
                searchLocation.setElementFinder(new SinglePredicateMatchFinder().getReference());
            }
            return searchLocation.getElementFinder();
        }
        SearchLocation searchLocation = relativeElementFinders.get(searchElement);
        if (searchLocation != null) {
            if (searchLocation.getElementFinder() == null) {
                ElementFinder elementFinder = new SinglePredicateMatchFinder().getReference();
                searchLocation.setElementFinder(elementFinder);
                return elementFinder;
            }
            return searchLocation.getElementFinder();
        }
        throw new IllegalArgumentException("Did not find matching search element");
    }

    @Override
    public ElementFinder setSearchElement(String searchElement, boolean isRelative) {
        if (!isRelative) {
            if (this.searchElement == null) {
                this.searchElement = searchElement.intern();
            } else if (!this.searchElement.equals(searchElement)) {
                throw new RuntimeException("Cannot have multiple absolute roots!");
            }
        } else {
            SearchLocation searchLocation = relativeElementFinders.get(searchElement);
            if (searchLocation == null) {
                relativeElementFinders.put(searchElement, new SearchLocation());
            }
        }
        return thisReference;
    }

    @Override
    public ElementFinder setPredicate(Predicate predicate) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(Predicate predicate) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public SearchLocationBuilder buildSearchLocation(String searchElement, boolean isRelative) {
        if (!isRelative) {
            if (this.searchElement == null) {
                this.searchElement = searchElement.intern();
                this.searchLocation = new SearchLocation();
                return new SearchLocationBuilder(this.searchLocation);
            } else if (this.searchElement.equals(searchElement)) {
                if (this.searchLocation == null) {
                    this.searchLocation = new SearchLocation();
                }
                return new SearchLocationBuilder(this.searchLocation);
            } else {
                throw new RuntimeException("Cannot have multiple absolute roots!");
            }
        } else {
            SearchLocation searchLocation = relativeElementFinders.get(searchElement);
            if (searchLocation == null) {
                searchLocation = new SearchLocation();
                relativeElementFinders.put(searchElement, searchLocation);
            }
            return new SearchLocationBuilder(searchLocation);
        }
    }

    @Override
    public ElementFinderReference getReference() {
        return thisReference;
    }

    @Override
    public SearchLocation lookupSearchLocation(String elementName, boolean isRelative) {
        if (!isRelative && elementName.equals(searchElement)) {
            return searchLocation;
        } else {
            SearchLocation searchLocation = relativeElementFinders.get(elementName);
            if (searchLocation != null) {
                return searchLocation;
            }
        }
        return null;
    }

    @Override
    public SearchLocation lookupSearchLocation(Predicate predicate) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public void buildToString(StringBuilder previousElementsBuilder, Set<ElementFinder> visited, StringBuilder toStringBuilder) {
        if (searchLocation == null && relativeElementFinders.isEmpty()) {
            toStringBuilder.append(previousElementsBuilder).append("/null\n");
        }
        if (searchElement != null) {
            previousElementsBuilder.append("/").append(searchElement);
            PrettyPrintHelper.printSearchLocation(searchLocation, previousElementsBuilder, visited, toStringBuilder);
        } else if (!relativeElementFinders.isEmpty()) {
            int previousElementBuilderLength = previousElementsBuilder.length();
            for (Map.Entry<String, SearchLocation> entries : relativeElementFinders.entrySet()) {
                previousElementsBuilder.setLength(previousElementBuilderLength);
                previousElementsBuilder.append("//").append(entries.getKey());
                PrettyPrintHelper.printSearchLocation(entries.getValue(), previousElementsBuilder, visited, toStringBuilder);
            }
        }
    }

    @Override
    public SearchLocation lookupSearchLocation(StructureElement structureElement, boolean isRelative) {
        if (isRelative) {
            if (relativeElementFinders.isEmpty()) {
                return null;
            }
            return relativeElementFinders.get(structureElement.getElementName());
        } else if (searchElement.equals(structureElement.getElementName())) {
            return searchLocation;
        }
        return null;
    }

    @Override
    public List<SearchLocationReference> getSeachLocationReferences(boolean isRelative) {
        if (isRelative) {
            List<SearchLocationReference> references = new ArrayList<SearchLocationReference>(relativeElementFinders.size());
            for (Map.Entry<String, SearchLocation> entry : relativeElementFinders.entrySet()) {
                references.add(new SearchLocationReference(entry.getValue(), entry.getKey(), true));
            }
            return references;
        } else {
            return Collections.singletonList(new SearchLocationReference(searchLocation, searchElement, false));
        }
    }

    @Override
    public void mergeElementFinder(ElementFinder elementFinder) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public boolean isPredicate() {
        return false;
    }
}

package dk.ott.core.dsl;

import dk.ott.core.dsl.expression.PathReference;
import dk.ott.core.dsl.searchtree.RootAllTreeBuilder;
import dk.ott.core.finder.SingleElementFinder;

import java.util.HashMap;

public class Dsl {

    public static RootAllTreeBuilder treeBuilder() {
        return new RootAllTreeBuilder(new HashMap<String, PathReference>(), new PathReference(new SingleElementFinder().getReference(), false));
    }
}

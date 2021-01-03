package dk.ott.bintree;

import org.junit.Assert;
import org.junit.Test;

public class NodeTest {

    @Test
    public void parentNodeIndex() {
        int parentNodeIndex = 255;
        int node = NodeBuilder.nodeBuilder()
                .parentNodeIndex(parentNodeIndex)
                .build();

        Assert.assertEquals(parentNodeIndex, NodeOperations.getParentIndex(node));
    }

    @Test
    public void completeTest() {
        int parentNodeIndex = 255;
        int node = NodeBuilder.nodeBuilder()
                .parentNodeIndex(parentNodeIndex)
                .childNodeIndex(333)
                .hasTextHandler(false)
                .isPredicate(true)
                .isRelative(false)
                .hasPredicateChildren(true)
                .hasRelativeChildren(false)
                .nameLength(111)
                .build();

        Assert.assertEquals(parentNodeIndex, NodeOperations.getParentIndex(node));
        Assert.assertEquals(333, NodeOperations.getChildIndex(node));
        Assert.assertEquals(111, NodeOperations.getNameLength(node));
        Assert.assertEquals(true, NodeOperations.hasPredicateChildren(node));
        Assert.assertEquals(false, NodeOperations.hasTextHandler(node));
        Assert.assertEquals(false, NodeOperations.hasRelativeChildren(node));
        Assert.assertEquals(true, NodeOperations.isPredicate(node));
        Assert.assertEquals(false, NodeOperations.isRelative(node));
    }
}

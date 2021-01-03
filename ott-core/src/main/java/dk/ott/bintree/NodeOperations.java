package dk.ott.bintree;

public class NodeOperations {
//    public short parentIndex;           // 22-31
//    public short nameLength;            // 14-22
//    hasTextHandler                      // 13
//    public boolean isPredicate;         // 12
//    public boolean isRelative;          // 11
//    public boolean hasRelativeChildren; // 10
//    public boolean hasPredicateChildren;// 09
//    public short childIndex;            // 0-9

    public static int getParentIndex(int node) {
        return node >> 22;
    }

    public static int getParentIndex(long node) {
        return getParentIndex((int) node);
    }

    public static int getNameLength(int node) {
        return node >> 14 & 0x000000FF;
    }

    public static int getNameLength(long node) {
        return getNameLength((int) node);
    }

    public static boolean hasTextHandler(int node) {
        return (node >> 13 & 0x00000001) == 1;
    }

    public static boolean hasTextHandler(long node) {
        return hasTextHandler((int) node);
    }

    public static boolean isPredicate(int node) {
        return (node >> 12 & 0x00000001) == 1;
    }

    public static boolean isPredicate(long node) {
        return isPredicate((int) node);
    }

    public static boolean isRelative(int node) {
        return (node >> 11 & 0x00000001) == 1;
    }

    public static boolean isRelative(long node) {
        return isRelative((int) node);
    }

    public static boolean hasRelativeChildren(int node) {
        return (node >> 10 & 0x00000001) == 1;
    }

    public static boolean hasRelativeChildren(long node) {
        return hasRelativeChildren((int) node);
    }

    public static boolean hasPredicateChildren(int node) {
        return (node >> 9 & 0x00000001) == 1;
    }

    public static boolean hasPredicateChildren(long node) {
        return hasPredicateChildren((int) node);
    }

    public static int getChildIndex(int node) {
        return node & 0x000001FF;
    }

    public static int getChildIndex(long node) {
        return getChildIndex((int) node);
    }

    public static long toPositionalNode(int node, long index) {
        return node | index << 32;
    }

    public static int positionalNodeToNode(long positionalNode) {
        return (int) (positionalNode & 0x00000000FFFFFFFFL);
    }

    public static int positionalNodeToIndex(long positionalNode) {
        return (int) (positionalNode >> 32);
    }
}

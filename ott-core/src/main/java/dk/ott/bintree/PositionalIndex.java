package dk.ott.bintree;

public class PositionalIndex {
    private Index index;
    private int position;

    public PositionalIndex(Index index, int position) {
        this.index = index;
        this.position = position;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

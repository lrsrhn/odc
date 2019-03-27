package dk.ott.core.finder;

import dk.ott.core.predicate.Predicate;

public class TextLocation {
    private Predicate textFilter;
    private OnTextHandler onTextHandler;

    public Predicate getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(Predicate textFilter) {
        this.textFilter = textFilter;
    }

    public OnTextHandler getOnTextHandler() {
        return onTextHandler;
    }

    public void setOnTextHandler(OnTextHandler onTextHandler) {
        this.onTextHandler = onTextHandler;
    }
}

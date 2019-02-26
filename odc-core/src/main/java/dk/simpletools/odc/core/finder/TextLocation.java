package dk.simpletools.odc.core.finder;

import dk.simpletools.odc.core.predicate.TextPredicate;

public class TextLocation {
    private TextPredicate textFilter;
    private OnTextHandler onTextHandler;

    public TextPredicate getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(TextPredicate textFilter) {
        this.textFilter = textFilter;
    }

    public OnTextHandler getOnTextHandler() {
        return onTextHandler;
    }

    public void setOnTextHandler(OnTextHandler onTextHandler) {
        this.onTextHandler = onTextHandler;
    }
}

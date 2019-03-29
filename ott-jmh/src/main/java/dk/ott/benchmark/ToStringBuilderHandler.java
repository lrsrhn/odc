package dk.ott.benchmark;

import dk.ott.core.event.OnTextHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.StructureElement;

public class ToStringBuilderHandler implements OnTextHandler {
    private StringBuilder stringBuilder;

    public ToStringBuilderHandler(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        stringBuilder.append(structureElement.getElementName()).append(": ").append(structureElement.getText());
    }

    public void setStringBuilder(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }
}

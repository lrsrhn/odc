package dk.simpletools.odc.benchmark;

import dk.simpletools.odc.core.finder.OnTextHandler;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

public class ToStringBuilderHandler implements OnTextHandler{
    private StringBuilder stringBuilder;

    public ToStringBuilderHandler(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    @Override
    public void onText(StructureElement structureElement, ObjectStore objectStore) throws Exception {
        stringBuilder.append(structureElement.getElementName()).append(": ").append(structureElement.getText());
    }
}

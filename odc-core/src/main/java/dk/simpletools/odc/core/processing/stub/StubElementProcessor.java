package dk.simpletools.odc.core.processing.stub;

import dk.simpletools.odc.core.finder.ElementFinder;
import dk.simpletools.odc.core.processing.BaseElementProcessor;
import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.StructureElement;

public class StubElementProcessor extends BaseElementProcessor<InputReader, ElementContext> {

    public StubElementProcessor(ElementFinder rootElementFinder, StructureElement structureElement) {
        super(rootElementFinder, structureElement);
    }

    @Override
    public ObjectStore search(InputReader parser, ElementContext structureElement) throws Exception {
        int currentDepth = 0;
        while(parser.hasNext()) {
            Element element = parser.next();
            structureElement.setCurrentElement(element);
            if (element.isStartElement()) {
//                System.out.println("Start: " + structureElement.getElementName());
                super.observablePathTraverser.startElement(structureElement, currentDepth++);
                continue;
            }
//            System.out.println("End: " + structureElement.getElementName());
            super.observablePathTraverser.endElement(structureElement, --currentDepth);
        }
        return structureElement.getObjectStore();
    }
}

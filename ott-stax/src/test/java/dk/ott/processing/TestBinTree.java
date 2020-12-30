package dk.ott.processing;

import com.ctc.wstx.stax.WstxInputFactory;
import dk.ott.bintree.BinTree;
import dk.ott.bintree.Index;
import dk.ott.bintree.PositionalIndex;
import dk.ott.core.BinEdge;
import dk.ott.event.OnTextHandler;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.junit.Test;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.StringReader;

import static dk.ott.xml.XmlStreamBuilderFactory.createXmlStreamBuilderToString;

public class TestBinTree {

    @Test
    public void testing() throws Exception {

        OnTextHandler textHandler = new OnTextHandler() {
            @Override
            public void onText(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
                System.out.println(elementCursor.getText());
            }
        };

        BinTree binTree = new BinTree();
        binTree.buildElementIndex(0, "root");
        binTree.buildElementIndex(1, "row");
        binTree.buildElementIndex(2, "tags").onTextHandler(textHandler).build();
        binTree.buildElementIndex(2, "registered").onTextHandler(textHandler).build();

        String xml = createXmlStreamBuilderToString(true)
                .element("root")
                    .element("row")
                        .element("tags")
                            .value("This is a tag")
                        .elementEnd()
                        .element("registered")
                            .value("This is a registered")
                        .elementEnd()
                        .element("somethingElse")
                            .value("Something else")
                        .elementEnd()
                    .elementEnd()
                    .element("row")
                        .element("tags")
                            .value("This is a tag")
                        .elementEnd()
                        .element("registered")
                            .value("This is a registered")
                        .elementEnd()
                        .element("somethingElse")
                            .value("Something else")
                        .elementEnd()
                    .elementEnd()
                .elementEnd()
            .toString();

        XMLInputFactory2 inputFactory2 = new WstxInputFactory();

        for (int i = 0; i < 1; i++) {
            process((XMLStreamReader2) inputFactory2.createXMLStreamReader(new StringReader(xml)), binTree.getRoot(), binTree);
        }
    }

    private void process(XMLStreamReader2 streamReader, Index root, BinTree binTree) throws Exception {
        XMLElementCursor xmlElement = new XMLElementCursor(streamReader);
        PositionalIndex positionalIndex = new PositionalIndex(root, 0);
        while (streamReader.hasNext()) {
            int eventType = streamReader.next();
            switch (eventType) {
                case XMLStreamReader.START_ELEMENT:
                    xmlElement.setEventType(eventType);
                    xmlElement.clearCache();
                    binTree.lookupIndex(positionalIndex, xmlElement, null, false);
                    continue;
                case XMLStreamConstants.CHARACTERS:
                    if (positionalIndex.getIndex().hasTextHandler) {
                        xmlElement.setEventType(eventType);
                        BinEdge edge = binTree.getEdge(positionalIndex.getPosition());
                        edge.getTextLocation().getOnTextHandler().onText(xmlElement, null);
                    }
                    continue;
                case XMLStreamReader.END_ELEMENT:
                    System.out.println("End element: " + streamReader.getLocalName());
                    xmlElement.setEventType(eventType);
                    int parentIndex = positionalIndex.getIndex().parentIndex;
                    if (parentIndex != -1) {
                        positionalIndex.setPosition(parentIndex);
                        positionalIndex.setIndex(binTree.getIndex(parentIndex));
                        xmlElement.clearCache();
                    }
            }
        }
    }
}

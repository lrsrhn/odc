package dk.ott.core.dsl;

import dk.ott.core.event.EventHandler;
import dk.ott.core.processing.ObjectStore;
import dk.ott.core.processing.ElementCursor;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssertEventHandler implements EventHandler {
    private List<String> expectedStartElements;
    private List<String> expectedEndElements;
    private List<String> startElementsActual;
    private List<String> endElementsActual;
    private List<String> expectedTexts;
    private List<String> textsActual;

    public AssertEventHandler() {
        this.startElementsActual = new ArrayList<String>();
        this.endElementsActual = new ArrayList<String>();
        this.textsActual = new ArrayList<String>();
        this.expectedTexts = new ArrayList<String>();
    }

    public void exptectedStartElements(String... elements) {
        this.expectedStartElements = Arrays.asList(elements);
    }

    public void exptectedEndElements(String... elements) {
        this.expectedEndElements = Arrays.asList(elements);
    }

    public void exptectedTexts(String... texts) {
        this.expectedTexts = Arrays.asList(texts);
    }

    public void verify() {
        Assert.assertEquals(expectedStartElements, startElementsActual);
        Assert.assertEquals(expectedEndElements, endElementsActual);
        Assert.assertEquals(expectedTexts, textsActual);
    }

    @Override
    public void onStart(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        startElementsActual.add(elementCursor.getElementName());
    }

    @Override
    public void onEnd(ElementCursor elementCursor, ObjectStore objectStore) throws Exception {
        endElementsActual.add(elementCursor.getElementName());
    }


    @Override
    public void onText(ElementCursor elementCursor, ObjectStore objectStore) {
        this.textsActual.add(elementCursor.getText());
    }
}

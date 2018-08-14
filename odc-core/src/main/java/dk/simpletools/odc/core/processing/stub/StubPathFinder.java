package dk.simpletools.odc.core.processing.stub;

import dk.simpletools.odc.core.processing.ObjectStore;
import dk.simpletools.odc.core.processing.ObservablePathFinder;

import java.io.Reader;

public class StubPathFinder extends ObservablePathFinder {

    @Override
    public ObjectStore find(Reader reader) {
        throw new UnsupportedOperationException();
    }

    public ObjectStore find(InputReader inputReader) {
        try {
            inputReader.reset();
            ElementContext elementContext = new ElementContext();
            StubElementProcessor stubElementProcessor = new StubElementProcessor(super.rootElementFinder.getElementFinder(), elementContext);
            return stubElementProcessor.search(inputReader, elementContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

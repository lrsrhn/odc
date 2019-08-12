package dk.ott.core.processing;

public abstract class BaseElementCursor implements InternalElementCursor {

  protected String elementTextCache;
  protected String elementNameCache;
  protected String elementNamespaceCache;
  protected boolean stopProcessing;

  public void clearCache() {
    this.elementTextCache = null;
    this.elementNameCache = null;
    this.elementNamespaceCache = null;
  }

  public boolean mustStopProcessing() {return stopProcessing; }

  @Override
  public void stopProcessing() {
    this.stopProcessing = true;
  }
}

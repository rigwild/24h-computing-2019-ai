package org.kaleeis_bears.ai.logging;

public class CategoryLogger implements Logger {
  public final String category;
  private final Logger underlyingLogger;

  public CategoryLogger(Logger underlyingLogger, String category) {
    this.underlyingLogger = underlyingLogger;
    this.category = category.toUpperCase();
  }

  @Override
  public void debug(String message) {
    underlyingLogger.debug(category + " : " + message);
  }

  @Override
  public boolean isDebugEnabled() {
    return underlyingLogger.isDebugEnabled();
  }

  @Override
  public void setDebugEnabled(boolean enabled) {
    underlyingLogger.setDebugEnabled(enabled);
  }

  @Override
  public void exception(String type, String message, Throwable exception) {
    underlyingLogger.exception(type, message, exception);
  }

  public void exception(String message, Throwable exception) {
    this.exception(this.category, message, exception);
  }
}

package org.kaleeis_bears.ai.logging;

public class ConsoleLogger extends PrintStreamLogger {
  public ConsoleLogger() {
    this(false);
  }

  public ConsoleLogger(boolean debug) {
    super(System.out, System.err, debug);
  }
}

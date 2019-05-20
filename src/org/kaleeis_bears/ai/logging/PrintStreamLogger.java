package org.kaleeis_bears.ai.logging;

import java.io.PrintStream;

public class PrintStreamLogger implements Logger {
  private final PrintStream out, err;

  private boolean DEBUG;

  public PrintStreamLogger(PrintStream out, PrintStream err) {
    this(out, err, false);
  }

  public PrintStreamLogger(PrintStream out, PrintStream err, boolean debug) {
    this.DEBUG = debug;
    this.out = out;
    this.err = err;
  }

  public boolean isDebugEnabled() {
    return this.DEBUG;
  }

  public void setDebugEnabled(boolean enabled) {
    this.DEBUG = enabled;
  }

  public void debug(String message) {
    if (DEBUG)
      out.println(message);
  }

  public void exception(String type, String message, Throwable exception) {
    err.print("ERREUR " + type);
    if (message != null)
      err.print(" : " + message);
    if (DEBUG && exception != null) {
      err.print(" : ");
      exception.printStackTrace(err);
    } else {
      if (message != null && message.charAt(message.length() - 1) != '.')
        err.print(".");
      err.println();
    }
  }
}

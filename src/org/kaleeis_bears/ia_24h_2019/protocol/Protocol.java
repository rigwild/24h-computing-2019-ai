package org.kaleeis_bears.ia_24h_2019.protocol;

import java.io.PrintWriter;

public interface Protocol {
  void handle(PrintWriter output, String message);
}
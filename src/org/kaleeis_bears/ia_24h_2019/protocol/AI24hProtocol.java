package org.kaleeis_bears.ia_24h_2019.protocol;

import java.io.PrintWriter;

import org.kaleeis_bears.ia_24h_2019.BaseIA;
import org.kaleeis_bears.ia_24h_2019.Main;

public class AI24hProtocol implements Protocol {
  private final BaseIA ai;

  public AI24hProtocol(BaseIA ai) {
    this.ai = ai;
  }

  @Override
  public void handle(PrintWriter output, String message) {
    // TODO : Implémenter la gestion des messages du serveur ici
    Main.debug(this.ai + " - Message : " + message);
  }
}
package org.kaleeis_bears.ai.protocol;

import org.kaleeis_bears.ai.BaseIA;
import org.kaleeis_bears.ai.logging.Logger;

public class AI24hProtocol implements Protocol {
  private final BaseIA ai;
  private final Logger logger;

  public AI24hProtocol(Logger logger, BaseIA ai) {
    this.logger = logger;
    this.ai = ai;
  }

  @Override
  public String handle(String message) {
    // TODO : Implémenter la gestion des messages du serveur ici
    this.logger.debug(this.ai + " - Message : " + message);
    return null;
  }
}
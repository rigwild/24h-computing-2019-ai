package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.logging.Logger;
import org.kaleeis_bears.ai.protocol.Protocol;

// TODO : Implémenter la gestion des messages du serveur dans ce fichier
public class AI24hProtocol implements Protocol {
  private final BaseIA ai;
  private final Logger logger;

  public AI24hProtocol(Logger logger, BaseIA ai) {
    this.logger = logger;
    this.ai = ai;
  }

  @Override
  public String handle(String message) {
    this.logger.debug(this.ai + " - Message : " + message);
    return null;
  }
}
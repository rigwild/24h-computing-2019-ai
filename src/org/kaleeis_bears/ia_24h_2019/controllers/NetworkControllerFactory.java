package org.kaleeis_bears.ia_24h_2019.controllers;

import org.kaleeis_bears.ia_24h_2019.BaseIA;
import org.kaleeis_bears.ia_24h_2019.commandline.ArgsDefinition;
import org.kaleeis_bears.ia_24h_2019.commandline.PositionalArg;
import org.kaleeis_bears.ia_24h_2019.protocol.AI24hProtocol;

public class NetworkControllerFactory implements ControllerFactory<NetworkController> {
  @Override
  public ArgsDefinition getCommandLineDefinition() {
    return new ArgsDefinition(new PositionalArg[] {
      new PositionalArg<>("HOST", value -> value),
      new PositionalArg<>("PORT", str -> {
        Integer value = new Integer(str);
        if (value <= 0 || value > 65535)
          throw new IndexOutOfBoundsException("Le numéro de port doit être compris entre 1 et 65535 (exclu).");
        return value;
      })
    });
  }

  @Override
  public NetworkController build(BaseIA ai, Object[] args) {
    return new NetworkController((String)args[0], (Integer)args[1], new AI24hProtocol(ai));
  }
}
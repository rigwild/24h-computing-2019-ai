package org.kaleeis_bears.ia_24h_2019.controllers;

import org.kaleeis_bears.ia_24h_2019.BaseIA;
import org.kaleeis_bears.ia_24h_2019.Controller;
import org.kaleeis_bears.ia_24h_2019.commandline.ArgsDefinition;

public interface ControllerFactory<T extends Controller> {
  ArgsDefinition getCommandLineDefinition();

  T build(BaseIA ia, Object[] args);
}
package org.kaleeis_bears.ai.controllers;

import org.kaleeis_bears.ai.commandline.CommandLineDefinition;

public interface ControllerFactory<T extends Controller> {
  boolean isDebugDefault();

  CommandLineDefinition getCommandLineDefinition();

  T build(CommandLineDefinition.CommandLineResult args);
}
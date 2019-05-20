package org.kaleeis_bears.ai.commandline.options;

import org.kaleeis_bears.ai.commandline.arguments.ArgList;

public class OptionDefinition {
  public final Character shortName;
  public final String name, description;
  public final ArgList args;

  public OptionDefinition(Character shortName, String name, String description, ArgList args) {
    this.shortName = shortName;
    this.name = name;
    this.description = description;
    this.args = args;
  }

  @Override
  public String toString() {
    return this.name + ((this.args == null) ? "" : args.toString());
  }
}

package org.kaleeis_bears.ia_24h_2019.commandline;

public class ArgsDefinition {
  public final PositionalArg<?>[] args;

  public ArgsDefinition(PositionalArg<?>[] args) {
    this.args = args;
  }

  public String getUsage() {
    final StringBuilder builder = new StringBuilder();
    for (PositionalArg<?> arg : this.args)
      builder.append(" [" + arg.name + "]");
    return builder.toString().substring(1);
  }
}
package org.kaleeis_bears.ai.commandline.options;

public class OptionValue {
  public final boolean isSet;
  public final Object[] argsValue;

  public OptionValue(boolean isSet, Object[] argsValue) {
    this.isSet = isSet;
    this.argsValue = argsValue;
  }
}

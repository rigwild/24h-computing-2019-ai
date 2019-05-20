package org.kaleeis_bears.ai.commandline.arguments;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;

public class ArgList {
  public final RequiredArgDefinition<?>[] required;
  public final OptionalArgDefinition<?>[] optional;
  protected final Map<String, Integer> argumentNameToIndex = new HashMap<>();
  private final int namePadding;

  public ArgList(RequiredArgDefinition<?>[] required, OptionalArgDefinition<?>[] optional) {
    this.required = required;
    this.optional = (optional == null) ? new OptionalArgDefinition[0] : optional;
    int namePadding = 0;
    for (int i = 0; i < this.required.length; i++) {
      final String name = this.required[i].name;
      if (this.argumentNameToIndex.containsKey(name))
        throw new KeyAlreadyExistsException("L'argument « " + name + " » est définie plusieurs fois.");
      this.argumentNameToIndex.put(name, i);
      if (name.length() > namePadding)
        namePadding = name.length();
    }
    for (int i = 0; i < this.optional.length; i++) {
      final String name = this.optional[i].name;
      if (this.argumentNameToIndex.containsKey(name))
        throw new KeyAlreadyExistsException("L'argument « " + name + " » est définie plusieurs fois.");
      this.argumentNameToIndex.put(name, i + this.required.length);
      if (name.length() > namePadding)
        namePadding = name.length();
    }
    this.namePadding = namePadding;
  }

  public int getIndex(String name) {
    return this.argumentNameToIndex.get(name);
  }

  public String help() {
    final StringBuilder builder = new StringBuilder();
    for (RequiredArgDefinition<?> arg : this.required)
      builder
          .append(String.format("%-" + namePadding + "s", arg.name))
          .append(" : ")
          .append(arg.description)
          .append("\n");
    for (OptionalArgDefinition<?> arg : this.optional)
      builder
          .append(String.format("%-" + namePadding + "s", arg.name))
          .append(" : ")
          .append(arg.description)
          .append("\n");
    return builder.toString();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (RequiredArgDefinition<?> arg : this.required)
      builder.append(" ").append(arg);
    for (OptionalArgDefinition<?> arg : this.optional)
      builder.append(" ").append(arg);
    return builder.toString();
  }
}

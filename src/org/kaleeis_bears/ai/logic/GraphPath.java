package org.kaleeis_bears.ai.logic;

public class GraphPath {
  public final int[] path;
  public final int distance;

  public GraphPath(int[] path, int distance) {
    this.path = path;
    this.distance = distance;
  }
}
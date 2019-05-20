package org.kaleeis_bears.ai.logic;

public class GraphPath {
  public final int[] path, distances;
  public final int totalDistance;

  public GraphPath(int[] path, int[] distances, int totalDistance) {
    this.path = path;
    this.distances = distances;
    this.totalDistance = totalDistance;
  }
}
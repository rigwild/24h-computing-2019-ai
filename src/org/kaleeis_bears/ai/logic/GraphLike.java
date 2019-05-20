package org.kaleeis_bears.ai.logic;

public interface GraphLike {
  int EDGE_NONE = Integer.MAX_VALUE;
  int EDGE_IDENTITY = 0;

  int getOrder();

  boolean exists(int nodeFrom, int nodeTo);
  int getEdge(int nodeFrom, int nodeTo);

  Iterable<Integer> getNeighbour(int node);
}
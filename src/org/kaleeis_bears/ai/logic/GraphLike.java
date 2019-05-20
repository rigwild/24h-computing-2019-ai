package org.kaleeis_bears.ai.logic;

public interface GraphLike {
  int getOrder();

  boolean exists(int nodeFrom, int nodeTo);
  int getWeight(int nodeFrom, int nodeTo);

  Iterable<Integer> getNeighbour(int node);
}
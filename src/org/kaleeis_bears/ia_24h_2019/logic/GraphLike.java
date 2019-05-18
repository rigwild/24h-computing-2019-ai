package org.kaleeis_bears.ia_24h_2019.logic;

public interface GraphLike {
  int getOrder();

  boolean exists(int nodeFrom, int nodeTo);
  int getWeight(int nodeFrom, int nodeTo);

  Iterable<Integer> getNeighbour(int node);
}
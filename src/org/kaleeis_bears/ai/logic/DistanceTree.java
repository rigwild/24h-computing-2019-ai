package org.kaleeis_bears.ai.logic;

import java.util.LinkedList;

public class DistanceTree {
  private final int[] tree;
  public final int[] distances;

  public final int from, visitedNodes, updatedNodes;

  public DistanceTree(int from, int[] tree, int[] distances, int visitedNodes, int updatedNodes) {
    this.from = from;
    this.tree = tree;
    this.distances = distances;
    this.visitedNodes = visitedNodes;
    this.updatedNodes = updatedNodes;
  }

  private void ensureExists(int node) {
    if (node < 0 || node >= distances.length)
      throw new IllegalArgumentException(
          "Le sommet « " + node + " » n'est pas sur le graphe associé à cette arbre des distances minimales.");
  }

  public boolean canAccess(int to) {
    this.ensureExists(to);
    return this.distances[to] != GraphLike.EDGE_NONE;
  }

  public int getDistance(int to) {
    this.ensureExists(to);
    return this.distances[to];
  }

  public int getFather(int cell) {
    this.ensureExists(cell);
    return this.tree[cell];
  }

  public int[] getPath(int to) {
    this.ensureExists(to);
    LinkedList<Integer> list = new LinkedList<>();
    for (int cell = to; cell != this.from; cell = this.getFather(cell))
      list.addFirst(cell);
    int[] result = new int[list.size()];
    int pos = 0;
    for (int cell : list)
      result[pos++] = cell;
    return result;
  }
}
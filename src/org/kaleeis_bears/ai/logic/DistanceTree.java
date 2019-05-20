package org.kaleeis_bears.ai.logic;

import java.util.LinkedList;

public class DistanceTree {
  public static class Builder {
    private final int[] tree, distances;

    public final int from;

    public Builder(int from, int order) {
      this.from = from;
      this.tree = new int[order];
      this.distances = new int[order];
      for (int i = 0; i < order; i++) {
        this.tree[i] = -1;
        this.distances[i] = Integer.MAX_VALUE;
      }
    }

    public int getDistance(int node) {
      return this.distances[node];
    }

    public Builder setDistance(int node, int distance) {
      this.distances[node] = distance;
      return this;
    }

    public Builder setFather(int node, int father) {
      this.tree[node] = father;
      return this;
    }

    public DistanceTree buildTree() {
      return new DistanceTree(this.from, this.tree, this.distances);
    }

    public GraphPath buildPath(int to) {
      return new GraphPath(this.buildTree().getPath(to), this.distances[to]);
    }
  }

  private final int[] tree, distances;

  public final int from;

  public DistanceTree(int from, int[] tree, int[] distances) {
    this.from = from;
    this.tree = tree;
    this.distances = distances;
  }

  private void ensureExists(int node) {
    if (node < 0 || node >= distances.length)
      throw new IllegalArgumentException(
          "Le sommet « " + node + " » n'est pas sur le graphe associé à cette arbre des distances minimales.");
  }

  public boolean canAccess(int to) {
    this.ensureExists(to);
    return this.distances[to] == Integer.MAX_VALUE;
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
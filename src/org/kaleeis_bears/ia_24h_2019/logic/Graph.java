package org.kaleeis_bears.ia_24h_2019.logic;

import java.util.Iterator;

public class Graph implements GraphLike {

  public static final int EDGE_NONE = Integer.MAX_VALUE;
  public static final int EDGE_IDENTITY = 0;

  private class Neighbourhood implements Iterator<Integer> {
    private final int from;
    private int to = -1;

    public Neighbourhood(int from) {
      this.from = from;
    }

    private void consume() {
      while (this.to < Graph.this.order && !Graph.this.exists(this.from, this.to))
        this.to++;
    }

    @Override
    public boolean hasNext() {
      return this.to < Graph.this.order;
    }

    @Override
    public Integer next() {
      try {
        return this.to++;
      } finally {
        this.consume();
      }
    }
  }

  private final int[] matrix;

  public final int order;

  public Graph(int order) {
    this.order = order;
    this.matrix = new int[order * order];
  }

  private int getIndex(int nodeFrom, int nodeTo) {
    if (nodeFrom < 0 || nodeFrom > this.order)
      throw new IndexOutOfBoundsException(
          "« nodeFrom » n'est pas un sommet de ce graphe (condition « 0 ≤ nodeFrom < ordre » non respectée).");
    if (nodeFrom < 0 || nodeFrom > this.order)
      throw new IndexOutOfBoundsException(
          "« nodeTo » n'est pas un sommet de ce graphe (condition « 0 ≤ nodeTo < ordre » non respectée).");
    return nodeFrom * this.order + nodeTo;
  }

  public int getOrder() {
    return this.order;
  }

  public boolean exists(int nodeFrom, int nodeTo) {
    return this.getWeight(nodeFrom, nodeTo) != EDGE_NONE;
  }

  public int getWeight(int nodeFrom, int nodeTo) {
    return this.matrix[this.getIndex(nodeFrom, nodeTo)];
  }

  public Graph setWeight(int nodeFrom, int nodeTo, int weight) {
    this.matrix[this.getIndex(nodeFrom, nodeTo)] = weight;
    return this;
  }

  public Iterable<Integer> getNeighbour(int node) {
    return () -> new Neighbourhood(node);
  }
}
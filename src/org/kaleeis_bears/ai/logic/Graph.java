package org.kaleeis_bears.ai.logic;

import java.util.Iterator;

public class Graph implements GraphLike {

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
    if (nodeTo < 0 || nodeTo > this.order)
      throw new IndexOutOfBoundsException(
          "« nodeTo » n'est pas un sommet de ce graphe (condition « 0 ≤ nodeTo < ordre » non respectée).");
    return nodeFrom * this.order + nodeTo;
  }

  public int getOrder() {
    return this.order;
  }

  public boolean exists(int nodeFrom, int nodeTo) {
    return this.getEdge(nodeFrom, nodeTo) != GraphLike.EDGE_NONE;
  }

  public int getEdge(int nodeFrom, int nodeTo) {
    return this.matrix[this.getIndex(nodeFrom, nodeTo)];
  }

  public Graph setEdge(int nodeFrom, int nodeTo, int weight) {
    this.matrix[this.getIndex(nodeFrom, nodeTo)] = weight;
    return this;
  }

  public Iterable<Integer> getNeighbour(int node) {
    return () -> new Neighbourhood(node);
  }
}
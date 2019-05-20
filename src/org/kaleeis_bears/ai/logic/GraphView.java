package org.kaleeis_bears.ai.logic;

import java.util.NoSuchElementException;

public class GraphView<T extends GraphLike, V> {
  private V[] objects;

  public T graph;

  public GraphView(T graph, V[] objects) {
    if (graph.getOrder() != objects.length)
      throw new IllegalArgumentException(
          "Chaque sommet du graphe doit être représenté par un objet (condition « graph.getOrder() == objects.length » non respectée).");
    this.graph = graph;
    this.objects = objects;
  }

  public GraphView<T, V> rename(V nodeOldName, V nodeNewName) {
    this.objects[this.getIndex(nodeOldName)] = nodeNewName;
    return this;
  }

  private int getIndex(V node) {
    int index = this.getNodeNo(node);
    if (index == -1)
      throw new NoSuchElementException("Le sommet « " + node + " » n'existe pas sur cette vue de graphe.");
    return index;
  }

  public int getNodeNo(V node) {
    if (node != null)
      for (int i = 0; i < this.objects.length; i++)
        if (node.equals(this.objects[i]))
          return i;
    return -1;
  }

  public int getWeight(V nodeFrom, V nodeTo) {
    return this.graph.getEdge(this.getIndex(nodeFrom), this.getIndex(nodeTo));
  }
}
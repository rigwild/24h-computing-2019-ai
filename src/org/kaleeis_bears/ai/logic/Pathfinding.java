package org.kaleeis_bears.ai.logic;

public final class Pathfinding {

  public interface Heuristic {
    int compute(int from, int to);
  }

  public interface StopCriteria {
    boolean check(int node);
  }

  private static class SortedIntLinkedList {
    public static class IntLinkedListNode {
      public int id;
      public IntLinkedListNode prev = null, next = null;
    }

    private final int[] weightMap;
    private IntLinkedListNode first = null;

    public SortedIntLinkedList(int[] weightMap) {
      this.weightMap = weightMap;
    }

    public boolean empty() {
      return this.first == null;
    }

    public IntLinkedListNode push(int id) {
      IntLinkedListNode prev = this.first, toAdd = new IntLinkedListNode();
      toAdd.id = id;
      if (prev == null || prev.id >= id) {
        toAdd.prev = this.first;
        this.first = toAdd;
      } else {
        for (IntLinkedListNode curr = this.first.next; this.weightMap[prev.id] < this.weightMap[id] && curr != null; curr = (prev = curr).next)
          ;
        toAdd.prev = prev;
        toAdd.next = prev.next;
        prev.next = toAdd;
      }
      return toAdd;
    }

    public int pop() {
      if (this.first == null)
        throw new IllegalStateException("La liste est vide : impossible de dépiler le premier élément !");
      int id = this.first.id;
      this.first = this.first.next;
      this.first.prev = null;
      return id;
    }

    public void update(IntLinkedListNode node) {
      final int weight = this.weightMap[node.id];
      if ((node.prev == null && node.next == null) || ((node.prev != null && this.weightMap[node.prev.id] >= weight)
          || (node.next != null && this.weightMap[node.next.id] <= weight)))
        return;
      IntLinkedListNode curr = node.prev == null ? node.next : node.prev;
      while (curr != null
          && (this.weightMap[curr.id] > weight || (curr.next != null && this.weightMap[curr.next.id] < weight)))
        if (this.weightMap[curr.id] > weight)
          curr = curr.prev;
        else if (curr.next != null && this.weightMap[curr.next.id] < weight)
          curr = curr.next;
      this.remove(node);
      node.prev = curr;
      node.next = curr == null ? this.first : curr.next;
      if (curr == null) {
        curr = this.first;
        this.first = node.next;
      }
      curr.next = node;
      if (node.next != null)
        node.next.prev = node;
    }

    public void remove(IntLinkedListNode node) {
      if (node.next != null)
        node.next.prev = node.prev;
      if (node.prev != null)
        node.prev.next = node.next;
      if (this.first == node)
        this.first = node.next;
    }
  }

  public static DistanceTree pathfinding(GraphLike graph, int from, Heuristic heuristic, StopCriteria criteria) {
    int order = graph.getOrder();
    int[] heuristicMap = new int[order], tree = new int[order], distances = new int[order];
    // boolean[] visited = new boolean[order];
    SortedIntLinkedList.IntLinkedListNode[] nodeMap = new SortedIntLinkedList.IntLinkedListNode[order];
    SortedIntLinkedList open = new SortedIntLinkedList(heuristicMap);

    for (int i = 0; i < order; i++) {
      tree[i] = -1;
      distances[i] = Integer.MAX_VALUE;
      heuristicMap[i] = Integer.MIN_VALUE;
    }

    open.push(from);
    for (int node = open.pop(); !open.empty(); node = open.pop()) {
      nodeMap[node] = null;
      if (criteria.check(node))
        break;
      for (int neighbour : graph.getNeighbour(node)) {
        // if (!visited[neighbour]) {
        //   tree[neighbour] = -1;
        //   distances[neighbour] = Integer.MAX_VALUE;
        //   heuristicMap[neighbour] = Integer.MAX_VALUE;
        // }
        if (distances[neighbour] > (distances[node] + 1)) {
          heuristicMap[neighbour] = (distances[neighbour] = distances[node] + 1) + heuristic.compute(from, neighbour);
          tree[neighbour] = node;
          if (nodeMap[neighbour] == null)
            nodeMap[neighbour] = open.push(neighbour);
          else
            open.update(nodeMap[neighbour]);
        }
      }
    }

    return new DistanceTree(from, tree, distances);
  }

  public static boolean EMPTY_CRITERIA(int cell) {
    return false;
  }

  public static int EMPTY_HEURISTIC(int from, int to) {
    return 0;
  }

  public static DistanceTree dijkstra(GraphLike graph, int from) {
    return dijkstra(graph, from, Pathfinding::EMPTY_CRITERIA);
  }

  public static DistanceTree dijkstra(GraphLike graph, int from, StopCriteria criteria) {
    return pathfinding(graph, from, Pathfinding::EMPTY_HEURISTIC, criteria);
  }

  public static GraphPath aStar(GraphLike graph, int from, Heuristic heuristic) {
    return aStar(graph, from, -1, heuristic);
  }

  public static GraphPath aStar(GraphLike graph, int from, int to, Heuristic heuristic) {
    final DistanceTree tree = pathfinding(graph, from, heuristic, node -> node == to);
    if (!tree.canAccess(to))
      return null;
    return new GraphPath(tree.getPath(to), tree.getDistance(to));
  }
}
package org.kaleeis_bears.ai.logic;

public final class Pathfinding {

  public interface Heuristic {
    int compute(int to);
  }

  public interface DistanceFunction {
    int distance(int from, int to);
  }

  public interface DoneCriteria {
    boolean check(int node);
  }

  private static class SortedIntLinkedList {
    private static final IntLinkedListNode REMOVED_PLACEHOLDER = new IntLinkedListNode();

    public static class IntLinkedListNode {
      public int id;
      public int weight;
      public IntLinkedListNode prev = null, next = null;
    }

    private int size = 0, add = 0, update = 0;
    private final boolean enableWeight;
    private final IntLinkedListNode[] cache;
    private IntLinkedListNode first = null;

    public SortedIntLinkedList(int order, boolean enableWeight) {
      this.cache = new IntLinkedListNode[order];
      this.enableWeight = enableWeight;
    }

    public int getAddedNodeCount() {
      return add;
    }

    public int getUpdatedNodeCount() {
      return update;
    }

    public boolean empty() {
      return this.first == null;
    }

    public void push(int id, int weight) {
      if (this.cache[id] == null) {
        this.add++;
        // Append a new node
        IntLinkedListNode prev = this.first, toAdd = new IntLinkedListNode();
        toAdd.id = id;
        toAdd.weight = weight;
        if (prev == null || prev.weight >= weight) {
          if (this.first != null)
            this.first.prev = toAdd;
          toAdd.next = this.first;
          this.first = toAdd;
        } else {
          for (IntLinkedListNode curr = this.first.next; prev.weight < weight && curr != null; curr = (prev = curr).next)
            ;
          toAdd.prev = prev;
          toAdd.next = prev.next;
          if (toAdd.next != null)
            toAdd.next.prev = toAdd;
          prev.next = toAdd;
        }
        this.size++;
        this.cache[id] = toAdd;
      } else if (this.cache[id] != REMOVED_PLACEHOLDER) {
        this.update++;
        // Update the node position
        IntLinkedListNode node = this.cache[id];
        node.weight = weight;
        if (
            node.prev != null && node.prev.weight > weight || node.next != null && node.next.weight < weight
        ) {
          IntLinkedListNode curr = node.prev == null ? node.next : node.prev;
          while (curr != null
              && (curr.weight > weight || (curr.next != null && curr.next.weight < weight)))
            if (curr.weight > weight)
              curr = curr.prev;
            else
              curr = curr.next;
          if (node.next != null)
            node.next.prev = node.prev;
          if (node.prev != null)
            node.prev.next = node.next;
          if (this.first == node)
            this.first = node.next;
          node.prev = curr;
          if (curr == null) {
            node.next = this.first;
            assert this.first != null;
            this.first.prev = node;
            this.first = node;
          } else {
            node.next = node.prev.next;
            node.prev.next = node;
          }
          if (node.next != null)
            node.next.prev = node;
        }
      }
    }

    public int pop() {
      if (this.first == null)
        throw new IllegalStateException("La liste est vide : impossible de défiler le premier élément !");
      int id = this.first.id;
      this.first = this.first.next;
      if (this.first != null)
        this.first.prev = null;
      this.size--;
      this.cache[id] = REMOVED_PLACEHOLDER;
      return id;
    }
  }

  public static boolean EMPTY_CRITERIA(int cell) {
    return false;
  }

  public static Heuristic getDistanceHeuristic(final int to, final DistanceFunction distance) {
    return from -> distance.distance(from, to);
  }

  public static DistanceTree find(GraphLike graph, int from, Heuristic heuristic, DoneCriteria criteria) {
    int order = graph.getOrder();
    int[] heuristicCache = new int[order];
    int[] tree = new int[order], distances = new int[order];
    SortedIntLinkedList open = new SortedIntLinkedList(order, heuristic != null);

    for (int i = 0; i < order; i++) {
      tree[i] = -1;
      distances[i] = Integer.MAX_VALUE;
      if (heuristic != null)
//        heuristicCache[i] = Double.MAX_VALUE;
        heuristicCache[i] = Integer.MAX_VALUE;
    }

    distances[from] = 0;
    open.push(from, 0);
    while (!open.empty()) {
      final int node = open.pop();
      if (criteria.check(node))
        break;
      final int neighbourMinDistance = distances[node] + 1;
      for (int neighbour : graph.getNeighbour(node)) {
        if (distances[neighbour] > neighbourMinDistance) {
          tree[neighbour] = node;
          open.push(neighbour, (distances[neighbour] = neighbourMinDistance)
              + (heuristicCache[neighbour] == Integer.MAX_VALUE
              ? (heuristicCache[neighbour] = heuristic.compute(neighbour))
              : heuristicCache[neighbour]));
        }
      }
    }

    return new DistanceTree(from, tree, distances, open.getAddedNodeCount(), open.getUpdatedNodeCount());
  }

  public static DistanceTree find(GraphLike graph, int from) {
    return find(graph, from, null, Pathfinding::EMPTY_CRITERIA);
  }

  public static DistanceTree find(GraphLike graph, int from, DoneCriteria criteria) {
    return find(graph, from, null, criteria);
  }

  public static GraphPath find(GraphLike graph, int from, int to) {
    return find(graph, from, to, (Heuristic) null);
  }

  public static GraphPath find(GraphLike graph, int from, int to, Heuristic heuristic) {
    final DistanceTree tree = find(graph, from, heuristic, node -> node == to);
    if (!tree.canAccess(to))
      return null;
    return new GraphPath(tree.getPath(to), tree.distances, tree.getDistance(to));
  }

  public static GraphPath find(GraphLike graph, int from, int to, DistanceFunction heuristic) {
    return find(graph, from, to, getDistanceHeuristic(to, heuristic));
  }
}

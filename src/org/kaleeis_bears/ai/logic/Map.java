package org.kaleeis_bears.ai.logic;

import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Map {
  public interface WallCriteria {
    boolean test(int node, int value);
  }

  public class CellView {
    public final int id;
    public final boolean exists;

    public CellView(int cell) {
      this.exists = Map.this.exists(cell);
      this.id = cell;
    }

    public CellView(int x, int y) {
      this.exists = Map.this.exists(x, y);
      this.id = Map.this.getIndex(x, y);
    }

    public int get() {
      return Map.this.getValue(this.id);
    }

    public CellView set(int value) {
      Map.this.setValue(this.id, value);
      return this;
    }

    public void dijkstra(int... walls) {
      Pathfinding.find(Map.this.toGraph(walls), this.id);
    }
  }

  public class MapAsGraph implements GraphLike {
    private class CellIterator implements Iterator<Integer> {

      private final int from;
      private int state = 0, to;

      public CellIterator(int from) {
        this.from = from;
        this.compute();
      }

      private void compute() {
        while (this.state < Map.this.adjacentRelativePos.length) {
          this.to = this.from + Map.this.adjacentRelativePos[this.state];
          if (Map.this.exists(this.to) && Map.this.getManhattanDistance(this.from, this.to) == 1 && !MapAsGraph.this.criteria.test(this.to, Map.this.getValue(this.to)))
            break;
          this.state++;
        }
      }

      @Override
      public boolean hasNext() {
        return this.state < Map.this.adjacentRelativePos.length;
      }

      @Override
      public Integer next() {
        try {
          return this.to;
        } finally {
          this.state++;
          this.compute();
        }
      }
    }

    private final WallCriteria criteria;

    public MapAsGraph(WallCriteria criteria) {
      this.criteria = criteria;
    }

    @Override
    public int getOrder() {
      return Map.this.area;
    }

    @Override
    public boolean exists(int nodeFrom, int nodeTo) {
      return Map.this.exists(nodeFrom) && Map.this.exists(nodeTo) && Map.this.getManhattanDistance(nodeFrom, nodeTo) == 1;
    }

    @Override
    public int getEdge(int nodeFrom, int nodeTo) {
      return this.exists(nodeFrom, nodeTo) ? 1 : GraphLike.EDGE_NONE;
    }

    @Override
    public Iterable<Integer> getNeighbour(final int node) {
      return () -> new CellIterator(node);
    }
  }

  private final int[] cells;

  private final int[] adjacentRelativePos;

  public final int width, height, area;

  public Map(int width, int height) {
    this.cells = new int[width * height];
    this.width = width;
    this.height = height;
    this.area = width * height;
    this.adjacentRelativePos = new int[]{
        -height,
        -1,
        1,
        height
    };
  }

  private void ensureExists(int cell) {
    if (!this.exists(cell))
      throw new IllegalArgumentException(
          "La cellule n°" + cell + " n'existe pas (condition « 0 ≤ cell < aire » non respectée).");
  }

  private void ensureExists(int x, int y) {
    if (!this.exists(x, y))
      throw new IllegalArgumentException("La cellule (" + x + ", " + y
          + ") n'existe pas (condition « 0 ≤ x < longueur ET 0 ≤ y < hauteur » non respectée).");
  }

  public boolean exists(int cell) {
    return (cell >= 0 && cell < this.area);
  }

  public boolean exists(int x, int y) {
    return (x >= 0 && x < this.width && y >= 0 && y < this.height);
  }

  public int getIndex(int x, int y) {
    return x * this.height + y;
  }

  public int getEuclideanDistance(int nodeFrom, int nodeTo) {
    final int dx = (nodeTo / this.height) - (nodeFrom / this.height);
    final int dy = (nodeTo % this.height) - (nodeFrom % this.height);
    return (int) (50 * Math.hypot(dx, dy));
  }

  public int getManhattanDistance(int nodeFrom, int nodeTo) {
    return Math.abs((nodeTo / this.height) - (nodeFrom / this.height)) + Math.abs((nodeTo % this.height) - (nodeFrom % this.height));
  }

  public CellView get(int cell) {
    return new CellView(cell);
  }

  public CellView get(int x, int y) {
    return new CellView(x, y);
  }

  public Stream<CellView> stream() {
    return IntStream.range(0, this.area).mapToObj(CellView::new);
  }

  public int getValue(int cell) {
    this.ensureExists(cell);
    return this.cells[cell];
  }

  public int getValue(int x, int y) {
    this.ensureExists(x, y);
    return this.cells[this.getIndex(x, y)];
  }

  public Map setValue(int cell, int value) {
    this.ensureExists(cell);
    this.cells[cell] = value;
    return this;
  }

  public Map setValue(int x, int y, int value) {
    this.ensureExists(x, y);
    this.cells[this.getIndex(x, y)] = value;
    return this;
  }

  public GraphLike toGraph(int... walls) {
    final HashSet<Integer> set = new HashSet<>();
    for (int wall : walls) set.add(wall);
    return this.toGraph((from, value) -> set.contains(value));
  }
  public GraphLike toGraph(WallCriteria wallDetection) {
    return new MapAsGraph(wallDetection);
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++)
        builder.append(String.format("%2d ", this.getValue(x, y)));
      builder.append("\n");
    }
    return builder.toString();
  }

  public String toString(int min, Character ...display) {
    final StringBuilder builder = new StringBuilder();
    for (int y = 0; y < this.height; y++) {
      for (int x = 0; x < this.width; x++) {
        final int value = this.getValue(x, y), positiveValue = value - min;
        builder.append(String.format("%3s", positiveValue >= 0 && positiveValue < display.length ? display[positiveValue].toString() : value));
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}
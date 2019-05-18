package org.kaleeis_bears.ia_24h_2019.logic;

import java.util.Arrays;
import java.util.stream.Stream;

public class Map {
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
      Pathfinding.dijkstra(Map.this.toGraph(walls), this.id);
    }
  }

  private final int[] cells;

  public final int width, height, area;

  public Map(int width, int height) {
    this.cells = new int[width * height];
    this.width = width;
    this.height = height;
    this.area = width * height;
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
    return x * this.width + y;
  }

  public CellView get(int cell) {
    return new CellView(cell);
  }

  public CellView get(int x, int y) {
    return new CellView(x, y);
  }

  public Stream<CellView> stream() {
    return Arrays.stream(this.cells).mapToObj(CellView::new);
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
    return null;
  }
}
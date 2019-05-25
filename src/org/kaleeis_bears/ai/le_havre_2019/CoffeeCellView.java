package org.kaleeis_bears.ai.le_havre_2019;

import org.kaleeis_bears.ai.logic.Map;

public class CoffeeCellView {
  public final int id;
  public final boolean exists;
  private final Map map;

  public CoffeeCellView(final Map map, int cell) {
    this.map = map;
    this.exists = map.exists(cell);
    this.id = cell;
  }

  public CoffeeCellView(final Map map, int x, int y) {
    this.map = map;
    this.exists = map.exists(x, y);
    this.id = map.getIndex(x, y);
  }

  public CoffeeCellView(final Map map, char line, int column) {
    this(map, line - 'A', column - 1);
  }

  public CoffeeCellView(final Map map, String encoded) {
    this(map, encoded.charAt(0), Integer.parseInt(encoded.substring(2)));
  }

  public int get() {
    return this.map.getValue(this.id);
  }

  public boolean all(CellData... types) {
    return this.all(CellData.all(types));
  }

  public boolean all(int type) {
    return (this.get() & type) == type;
  }

  public boolean any(CellData... types) {
    return this.any(CellData.all(types));
  }

  public boolean any(int type) {
    return (this.get() & type) != 0;
  }

  public boolean hasNorthLimit() {
    return this.all(CellData.NORTH.value);
  }

  public boolean hasWestLimit() {
    return this.all(CellData.WEST.value);
  }

  public boolean hasSouthLimit() {
    return this.all(CellData.SOUTH.value);
  }

  public boolean hasEastLimit() {
    return this.all(CellData.EAST.value);
  }

  public boolean isParcel() {
    return !this.any(CellData.FOREST.value + CellData.SEA.value);
  }

  public boolean isForest() {
    return this.all(CellData.FOREST.value);
  }

  public boolean isSea() {
    return this.all(CellData.SEA.value);
  }

  public boolean isBeanOver() {
    return this.all(CellData.BEAN_OVER.value);
  }

  public boolean isWhiteBeam() {
    return !this.all(CellData.BLACK_BEAN.value);
  }

  public boolean isBlackBeam() {
    return this.all(CellData.BLACK_BEAN.value);
  }

  public boolean isAligned(final CoffeeCellView other) {
    final int Ax = this.map.getX(this.id), Ay = this.map.getY(this.id);
    final int Bx = this.map.getX(other.id), By = this.map.getY(other.id);
    return (Math.abs(Ax - Bx) == 0 || Math.abs(Ay - By) == 0);
  }

  public CoffeeCellView add(CellData... types) {
    return this.add(CellData.all(types));
  }

  public CoffeeCellView add(final int value) {
    return this.set(value | this.get());
  }

  public CoffeeCellView remove(CellData... types) {
    return this.remove(CellData.all(types));
  }

  public CoffeeCellView remove(final int value) {
    return this.set((value | this.get()) ^ value);
  }

  public CoffeeCellView set(int value) {
    this.map.setValue(this.id, value);
    return this;
  }

  public String encode() {
    return String.format("%c:%d", this.map.getX(this.id) + 'A', this.map.getY(this.id) + 1);
  }

  @Override
  public String toString() {
    return String.format("(%d, %d)", this.map.getX(this.id), this.map.getY(this.id));
  }
}

package org.kaleeis_bears.ai.le_havre_2019;


import org.kaleeis_bears.ai.logic.Map;

public class World {
  private Map map;

  public World(String chain) {
    map = new Map(10, 10);
    final String[] lines = chain.split("\\|");
    int y, x;
    for (y = 0; y < lines.length; y++) {
      final String[] cells = lines[y].split(":");
      for (x = 0; x < cells.length; x++)
        map.setValue(x, y, Integer.parseInt(cells[x]));
    }
  }

  public CoffeeCellView get(int x, int y) {
    return new CoffeeCellView(this.map, x, y);
  }

  public CoffeeCellView get(String encoded) {
    return new CoffeeCellView(this.map, encoded);
  }

  public void place(CoffeeCellView cell, boolean white) {
    cell.add(white ? CellData.WHITE_BEAN.value : CellData.BLACK_BEAN.value);
  }

  public CoffeeCellView play() {
    // TODO
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (int y = 0; y < this.map.height; y++) {
      for (int x = 0; x < this.map.width; x++) {
        final CoffeeCellView cell = this.get(x, y);
        builder.append(cell.isParcel() ? cell.isBeanOver() ? "." : " " : "#");
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}

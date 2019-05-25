package org.kaleeis_bears.ai.le_havre_2019;


import org.kaleeis_bears.ai.logic.Map;

public class World {
  private static final int[] ADJACENT = new int[]{
      -1,
      -10,
      1,
      10
  };
  private Map map, heuristics, plots;
  private CoffeeCellView lastPlaced, pastLastPlaced, pendingPlaced;
  private int nextPlotId = 1;

  public World(String chain) {
    map = new Map(10, 10);
    plots = new Map(10, 10);
    final String[] lines = chain.split("\\|");
    int y, x;
    for (y = 0; y < lines.length; y++) {
      final String[] cells = lines[y].split(":");
      for (x = 0; x < cells.length; x++)
        map.setValue(x, y, Integer.parseInt(cells[x]));
    }
    this.map
        .stream(CoffeeCellView::new)
        .filter(cell -> cell.isParcel() && this.isPlotNonInit(cell.id))
        .forEach(cell -> updatePlot(cell.id, this.nextPlotId++));
  }

  private boolean isPlotNonInit(int cell) {
    return this.plots.getValue(cell) == 0;
  }

  private boolean isNotSamePlot(int a, int b) {
    return this.plots.getValue(a) != this.plots.getValue(b);
  }

  private void updatePlot(int cell, final int plotId) {
    this.plots.setValue(cell, plotId);
    for (int i = 0; i < 4; i++) {
      final int neighbour = cell + ADJACENT[i];
      if (this.map.exists(neighbour) && this.isPlotNonInit(neighbour)) {
        final int value = this.map.getValue(cell);
        if ((value & (CellData.FOREST.value + CellData.SEA.value)) == 0 && (value & (1 << i)) == 0)
          this.updatePlot(neighbour, plotId);
      }
    }
  }

  public CoffeeCellView get(int x, int y) {
    return new CoffeeCellView(this.map, x, y);
  }

  public CoffeeCellView get(String encoded) {
    return new CoffeeCellView(this.map, encoded);
  }

  public void place(CoffeeCellView cell, boolean white) {
    if (white) {
      cell.add(CellData.WHITE_BEAN.value);
      if (this.pendingPlaced != null)
        this.pendingPlaced.add(CellData.BLACK_BEAN.value);
      this.pastLastPlaced = this.lastPlaced;
      this.lastPlaced = cell;
      this.pendingPlaced = null;
    } else
      this.pendingPlaced = cell;
  }

  public void rejectPending() {
    this.pendingPlaced = null;
  }

  public CoffeeCellView play() {
    if (this.pendingPlaced != null) {
      this.pendingPlaced.add(CellData.BLACK_BEAN.value);
      this.pastLastPlaced = this.lastPlaced;
      this.lastPlaced = this.pendingPlaced;
      this.pendingPlaced = null;
    }
    heuristics = new Map(10, 10);
    final CoffeeCellView[] cells = this.map
        .stream(CoffeeCellView::new)
        .filter(c ->
            c.isParcel() &&
                !c.isBeanOver() &&
                (this.lastPlaced == null || (
                    this.lastPlaced.isAligned(c) &&
                        this.isNotSamePlot(c.id, this.lastPlaced.id) && (
                        this.pastLastPlaced == null ||
                            this.isNotSamePlot(c.id, this.pastLastPlaced.id)
                    )
                ))
        )
//        .peek(cell -> {
//          if (this.lastPlaced != null) {
//            final int value = this.heuristics.getValue(cell.id);
//            this.heuristics.setValue(cell.id, value + (10 - this.heuristics.getManhattanDistance(cell.id, this.lastPlaced.id)));
//          }
//        })
//        .sorted((a, b) -> this.heuristics.getValue(b.id) - this.heuristics.getValue(a.id))
        .toArray(CoffeeCellView[]::new);
    return cells.length == 0 ? null : cells[0];
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (int y = 0; y < this.map.height; y++) {
      for (int x = 0; x < this.map.width; x++) {
        final CoffeeCellView cell = this.get(x, y);
        builder.append(cell.isParcel() ? (cell.isBeanOver() ? (cell.isWhiteBeam() ? "x" : "o") : " ") : "#");
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}

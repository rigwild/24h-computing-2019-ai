package org.kaleeis_bears.ai.le_havre_2019;


import org.kaleeis_bears.ai.logic.Map;

public class World {
  private static final int[] ADJACENT = new int[]{
      -1,
      -10,
      1,
      10
  };
  private final int[] plotsFreeCeels, plotsOwnedCells, plotsEnnemiCells;
  private final Map map, heuristics, plots;
  private CoffeeCellView lastPlaced, pastLastPlaced, pendingPlaced;
  private int nextPlotId = 1;

  public World(String chain) {
    map = new Map(10, 10);
    plots = new Map(10, 10);
    heuristics = new Map(10, 10);
    final String[] lines = chain.split("\\|");
    int y, x;
    for (y = 0; y < lines.length; y++) {
      final String[] cells = lines[y].split(":");
      for (x = 0; x < cells.length; x++)
        map.setValue(x, y, Integer.parseInt(cells[x]));
    }
    this.plotsFreeCeels = this.map
        .stream(CoffeeCellView::new)
        .filter(cell -> cell.isPlot() && this.isPlotNonInit(cell.id))
        .mapToInt(cell -> updatePlot(cell.id, this.nextPlotId++))
        .toArray();
    this.plotsOwnedCells = new int[this.plotsFreeCeels.length];
    this.plotsEnnemiCells = new int[this.plotsFreeCeels.length];
  }
  
  private boolean isPlotNonInit(int cell) {
    return this.plots.getValue(cell) == 0;
  }

  private boolean isNotSamePlot(int a, int b) {
    return this.plots.getValue(a) != this.plots.getValue(b);
  }

  private double getPlotRate(int plot) {
    final int free = this.plotsFreeCeels[plot - 1];
    if (free == 0)
      return 0;
    return (this.plotsOwnedCells[plot - 1] - this.plotsEnnemiCells[plot - 1]) / (double)this.plotsFreeCeels[plot - 1] - ((double)this.plotsFreeCeels[plot - 1]) / 10;
  }

  private int updatePlot(int cell, final int plotId) {
    this.plots.setValue(cell, plotId);
    int freeCells = 0;
    for (int i = 0; i < 4; i++) {
      final int neighbour = cell + ADJACENT[i];
      if (this.map.exists(neighbour) && this.isPlotNonInit(neighbour)) {
        final int value = this.map.getValue(cell);
        if ((value & (CellData.FOREST.value + CellData.SEA.value)) == 0 && (value & (1 << i)) == 0)
          freeCells += this.updatePlot(neighbour, plotId);
      }
    }
    return freeCells + 1;
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
      {
        final int plot = this.plots.getValue(cell.id) - 1;
        this.plotsEnnemiCells[plot]++;
        this.plotsFreeCeels[plot]--;
      }
      if (this.pendingPlaced != null) {
        this.pendingPlaced.add(CellData.BLACK_BEAN.value);
        final int plot = this.plots.getValue(this.pendingPlaced.id) - 1;
        this.plotsOwnedCells[plot]++;
        this.plotsFreeCeels[plot]--;
        this.lastPlaced = this.pendingPlaced;
      }
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
      final int plot = this.plots.getValue(this.pendingPlaced.id) - 1;
      this.plotsOwnedCells[plot]++;
      this.plotsFreeCeels[plot]--;
      this.pastLastPlaced = this.lastPlaced;
      this.lastPlaced = this.pendingPlaced;
      this.pendingPlaced = null;
    }
    try {
      final CoffeeCellView[] cells = this.map
          .stream(CoffeeCellView::new)
          .filter(c ->
              c.isPlot() &&
                  !c.isBeanOver() &&
                  (this.lastPlaced == null || (
                      this.lastPlaced.isAligned(c) &&
                          this.isNotSamePlot(c.id, this.lastPlaced.id) && (
                          this.pastLastPlaced == null || this.isNotSamePlot(c.id, this.pastLastPlaced.id)
                      )
                  ))
          )
          .sorted((a, b) -> {
            try {
              return (int) (1000 * (this.getPlotRate(this.plots.getValue(b.id)) - this.getPlotRate(this.plots.getValue(a.id))));
            } catch (Throwable ex) {
              return 0;
            }
          })
          .toArray(CoffeeCellView[]::new);
      return cells.length == 0 ? null : cells[0];
    } catch (Throwable ex) {
      System.err.println("EXCEPTION" + ex);
      return this.map
          .stream(CoffeeCellView::new)
          .filter(c -> c.isPlot() && !c.isBeanOver() && (this.lastPlaced == null || this.lastPlaced.isAligned(c)))
          .findAny().orElse(null);
    }
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    for (int y = 0; y < this.map.height; y++) {
      for (int x = 0; x < this.map.width; x++) {
        final CoffeeCellView cell = this.get(x, y);
        builder.append(cell.isPlot() ? (cell.isBeanOver() ? (cell.isWhiteBeam() ? "x" : "o") : " ") : "#");
      }
      builder.append("\n");
    }
    return builder.toString();
  }
}

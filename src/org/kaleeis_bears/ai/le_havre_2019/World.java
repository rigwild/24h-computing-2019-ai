package org.kaleeis_bears.ai.le_havre_2019;


import org.kaleeis_bears.ai.logic.Map;

public class World {
    private Map map;
    private int x;
    private int y;

    public World(String chain) {
        map = new Map(10, 10);
        final String[] lines = chain.split("|");
        for (y = 0; y < lines.length; y++) {
            final String[] cells = lines[y].split(":");
            for (x = 0; x < cells.length; x++)
                map.setValue(x, y, Integer.parseInt(cells[x]));
        }
    }

    public CoffeeCellView get(int x, int y) {
        return new CoffeeCellView(this.map, this.x, this.y);
    }
  /*public void ennemyPlays(){
    get().add();
  }

  public String update(String chainToUpdate){
    String[] cell = chainToUpdate.split(":");
    return "";
  }*/
}

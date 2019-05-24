package org.kaleeis_bears.ai.le_havre_2019;


import org.kaleeis_bears.ai.logic.Map;

public class WorldParser {
  private Map map;

  public WorldParser(String chaine) {
    map = new Map(10, 10);
    final String[] lines = chaine.split("|");
    for (int y = 0; y < lines.length; y++) {
      final String[] cells = lines[y].split(":");
      for (int x = 0; x < cells.length; x++)
        map.setValue(x, y, Integer.parseInt(cells[x]));
    }
  }
}

package org.kaleeis_bears.ai.le_havre_2019;

public enum CellData {
  NORTH(0),
  WEST(1 << 1),
  SOUTH(1 << 2),
  EAST(1 << 3),

  FOREST(1 << 5),
  SEA(1 << 6),

  BEAN_OVER(1 << 7),

  WHITE_BEAN(1 << 7),
  BLACK_BEAN(1 << 7 + 1 << 8);

  public final int value;

  CellData(int value) {
    this.value = value;
  }

  public static int all(CellData... types) {
    int sum = 0;
    for (CellData type : types)
      sum = (sum | type.value);
    return sum;
  }
}
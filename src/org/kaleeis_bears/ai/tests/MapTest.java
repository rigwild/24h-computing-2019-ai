package org.kaleeis_bears.ai.tests;

import org.kaleeis_bears.ai.logic.*;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class MapTest {

  /**
   * Longueur des cartes générées.
   */
  public static final int WIDTH = 70;
  /**
   * Largeur des cartes générées.
   */
  public static final int HEIGHT = 60;

  /**
   * Taux de murs sur les cartes générées (sauf `TEST NOMBRE D'ITÉRATIONS PAR SECONDE`).
   */
  public static final double WALL_RATE = .3;

  /**
   * Pas du taux de murs sur les cartes générées (uniquement `TEST NOMBRE D'ITÉRATIONS PAR SECONDE`).
   */
  public static final double WALL_RATE_STEP = .1;

  public static void main(String[] args) {

    //#region INITIALISATION

    System.out.println("# Création d'un générateur de nombre aléatoire pour la séquence");

    final Random random = new Random();

    long seed = (long) (Math.random() * Long.MAX_VALUE);
    try {
      seed = new Long(args[0]);
    } catch (Exception ignored) {
    } finally {
      random.setSeed(seed);
      System.out.println("Seed de la séquence : " + seed);
    }

    //#endregion

    System.out.println("# Début du test de performance");

//  COMPARAISON DES 2 ALGORITHMES (Dijkstra et A*)
    for (double rate = 1.; rate >= 0; rate -= WALL_RATE_STEP)
      benchmark(random.nextLong(), rate);

//  TEST NOMBRE D'ITÉRATIONS PAR SECONDE
//    {
//      final Map map = new Map(WIDTH, HEIGHT);
//      final int to = map.area - 1, area = map.area;
//
//      // A*
//      final Pathfinding.Heuristic heuristic = Pathfinding.getDistanceHeuristic(to, map::getEuclideanDistance);
//      // Dijkstra
////      final Pathfinding.Heuristic heuristic = null;
//
//      final GraphLike graph = map.toGraph(-1);
//
//      int tryPerSec = 0, succeedTry = 0;
//      long totalTry = 0, lastUpdate = -1_000_000_000;
//      do {
//        if ((System.nanoTime() - lastUpdate) >= 1_000_000_000) {
//          System.out.println(String.format("%7d itérations/s, dont %5d concluantes (seed %20d)", tryPerSec, succeedTry, seed));
//          seed = random.nextLong();
//          final Random localRandom = new Random(seed);
//          for (int cell = 0; cell < area; cell++) {
//            map.setValue(
//                cell,
//                localRandom.nextDouble() < WALL_RATE ? -1 : -2
//            );
//          }
//          map.setValue(to, -2);
//          lastUpdate = System.nanoTime();
//          succeedTry = (tryPerSec = 0);
//        }
//        tryPerSec++;
//        totalTry++;
//        if (Pathfinding.find(graph, random.nextInt(area), heuristic, node -> node == to).canAccess(to))
//          succeedTry++;
//      } while (totalTry < 1_000_000_000);
//    }

//  AFFICHER LA CARTE ET LE RÉSULTAT
//    dijkstra(random.nextLong());
//    aStar(random.nextLong());

//  CAS INTÉRESSANTS
//    dijkstra(-6266056616651958763L);
//    aStar(-6266056616651958763L);
//    dijkstra(-4833651001336000998L);
//    aStar(-4833651001336000998L);
  }

  static void dijkstra(int startingPoint, long seed) {
    System.out.println("## Création d'un générateur de nombre aléatoire");

    final Random random = new Random(seed);

    System.out.println("Seed  : " + seed);

    System.out.println("## Création de la carte (" + WIDTH + ", " + HEIGHT + ")");

    final Map map = new Map(WIDTH, HEIGHT);

    System.out.println("## Choix d'un point de départ");

    System.out.println(String.format("## Placement de murs sur la carte (ratio : %.2f%%)", (MapTest.WALL_RATE * 100)));

    IntStream
        .range(0, map.area)
        .forEach(cell -> map.setValue(
            cell,
            random.nextDouble() < MapTest.WALL_RATE ? -1 : -2
        ));

    map.setValue(startingPoint, -3);

    System.out.print(map.toString(-3, '×', ' ', '.'));

    System.out.println("## Création d'un graphe équivalent");

    final GraphLike graph = map.toGraph(-1);

    System.out.println("## Application de l'algorithme de Dijkstra");

    final int to = map.area - 1;
    final long startDate = System.nanoTime();
    final DistanceTree tree = Pathfinding.find(graph, startingPoint);
    final long elapsed = (System.nanoTime() - startDate);

    System.out.println("Terminé en " + (elapsed / 1_000_000.) + " ms " + (tree.canAccess(to) ? "(trouvé)" : "(non trouvé)"));

    System.out.println("# Résultats");

    map
        .stream()
        .filter(cell -> tree.canAccess(cell.id))
        .forEach(cell -> cell.set(tree.getDistance(cell.id)));

    System.out.print(map.toString(-2, ' ', '.'));
  }

  static void aStar(int startingPoint, long seed) {
    System.out.println("## Création d'un générateur de nombre aléatoire");

    final Random random = new Random(seed);

    System.out.println("Seed  : " + seed);

    System.out.println("## Création de la carte (" + WIDTH + ", " + HEIGHT + ")");

    final Map map = new Map(WIDTH, HEIGHT);

    System.out.println("## Choix d'un point de départ");

    System.out.println(map.getEuclideanDistance(startingPoint, map.area - 1));
    System.out.println(map.getEuclideanDistance(0, map.area - 1));

    System.out.println(String.format("## Placement de murs sur la carte (ratio : %.2f%%)", (MapTest.WALL_RATE * 100)));

    IntStream
        .range(0, map.area)
        .forEach(cell -> map.setValue(
            cell,
            random.nextDouble() < MapTest.WALL_RATE ? -1 : -2
        ));

    map.setValue(startingPoint, -3);

    System.out.print(map.toString(-3, '×', ' ', '.'));

    System.out.println("## Création d'un graphe équivalent");

    final GraphLike graph = map.toGraph(-1);

    System.out.println("## Application de l'algorithme A*");

    final int to = map.area - 1;
    final long startDate = System.nanoTime();
    final DistanceTree tree = Pathfinding.find(graph, startingPoint, Pathfinding.getDistanceHeuristic(to, map::getEuclideanDistance), node -> node == to);
    final long elapsed = (System.nanoTime() - startDate);

    System.out.println("Terminé en " + (elapsed / 1_000_000.) + " ms " + (tree.canAccess(to) ? "(trouvé)" : "(non trouvé)"));

    System.out.println("# Résultats");

    map
        .stream()
        .filter(cell -> tree.canAccess(cell.id))
        .forEach(cell -> cell.set(tree.getDistance(cell.id)));

    System.out.print(map.toString(-2, ' ', '.'));

//    final long startDate = System.nanoTime();
//    final GraphPath path = Pathfinding.find(graph, 0, map.area - 1, map::getEuclideanDistance);
//    final long elapsed = (System.nanoTime() - startDate);
//
//    System.out.println("Terminé en " + (elapsed / 1_000_000.) + "ms");
//
//    System.out.println("# Résultats");
//
//    if (path != null) {
//      Arrays
//          .stream(path.path)
//          .forEach(cell -> map.setValue(cell, path.distances[cell]));
//
//      System.out.print(map.toString(-2, ' ', '.'));
//    }
  }

  static void benchmark(long seed, double wallRate) {
    System.out.println();

    final Random random = new Random(seed);

    final Map map = new Map(WIDTH, HEIGHT);

    final int startingPoint = random.nextInt(map.area);

    IntStream
        .range(0, map.area)
        .forEach(cell -> map.setValue(
            cell,
            random.nextDouble() < wallRate ? -1 : -2
        ));

    map.setValue(startingPoint, -3);

    final GraphLike graph = map.toGraph(-1);

    final int to = map.area - 1;

    System.out.println(String.format("%d × %d, %.2f%%, %d", WIDTH, HEIGHT, (wallRate * 100), seed));

    {
      final long startDate = System.nanoTime();
      final DistanceTree tree = Pathfinding.find(graph, 0, node -> node == to);
      final long elapsed = (System.nanoTime() - startDate);

      System.out.println("Dijkstra en " + String.format("%10.5f", (elapsed / 1_000_000.)) + " ms " + (tree.canAccess(to) ? "(trouvé)" : "(non trouvé)") + " - " + tree.visitedNodes + " nœuds visités, " + tree.updatedNodes + " mises à jour");
    }

    {
      final long startDate = System.nanoTime();
      final DistanceTree tree = Pathfinding.find(graph, 0, Pathfinding.getDistanceHeuristic(to, map::getEuclideanDistance), node -> node == to);
      final long elapsed = (System.nanoTime() - startDate);

      System.out.println("A*       en " + String.format("%10.5f", (elapsed / 1_000_000.)) + " ms " + (tree.canAccess(to) ? "(trouvé)" : "(non trouvé)") + " - " + tree.visitedNodes + " nœuds visités, " + tree.updatedNodes + " mises à jour");
    }
  }
}

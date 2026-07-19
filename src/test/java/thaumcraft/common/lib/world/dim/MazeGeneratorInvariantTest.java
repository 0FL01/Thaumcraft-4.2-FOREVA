package thaumcraft.common.lib.world.dim;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MazeGeneratorInvariantTest {

    @Test
    public void successfulMazesKeepConnectedFeatureRooms() {
        boolean sawNest = false;
        boolean sawSpiderRoom = false;
        int successes = 0;

        for (long seed = 0; seed < 256; seed++) {
            MazeGenerator maze = new MazeGenerator(21, 21, seed);
            if (!maze.generate()) continue;
            successes++;

            int portalX = -1;
            int portalY = -1;
            int[] featureCounts = new int[15];
            for (int y = 0; y < maze.grid.length; y++) {
                for (int x = 0; x < maze.grid[y].length; x++) {
                    Cell cell = new Cell((short) maze.grid[y][x]);
                    if (cell.feature >= 1 && cell.feature <= 14) featureCounts[cell.feature]++;
                    if (cell.feature == 1) {
                        portalX = x;
                        portalY = y;
                    }
                    assertReciprocalConnections(maze.grid, x, y, cell);
                }
            }

            assertEquals(1, featureCounts[1]);
            assertEquals(1, featureCounts[2]);
            assertEquals(1, featureCounts[3]);
            assertEquals(1, featureCounts[4]);
            assertEquals(1, featureCounts[5]);
            assertEquals(1, featureCounts[6]);

            boolean[][] reachable = reachableFrom(maze.grid, portalX, portalY);
            int reachableBossCells = 0;
            for (int y = 0; y < maze.grid.length; y++) {
                for (int x = 0; x < maze.grid[y].length; x++) {
                    Cell cell = new Cell((short) maze.grid[y][x]);
                    if (cell.feature >= 2 && cell.feature <= 5 && reachable[y][x]) reachableBossCells++;
                    if (cell.feature == 6 || cell.feature == 7 || cell.feature == 8 || cell.feature == 14) {
                        assertTrue("feature " + cell.feature + " must be reachable for seed " + seed, reachable[y][x]);
                    }
                }
            }
            assertTrue("one boss quadrant must connect for seed " + seed, reachableBossCells >= 1);
            sawNest |= featureCounts[7] > 0;
            sawSpiderRoom |= featureCounts[14] > 0;
        }

        assertTrue(successes > 0);
        assertTrue("fixed seed corpus must contain a feature-7 nest", sawNest);
        assertTrue("fixed seed corpus must contain a feature-14 spider room", sawSpiderRoom);
    }

    private static void assertReciprocalConnections(int[][] grid, int x, int y, Cell cell) {
        if (cell.north) assertTrue(y > 0 && new Cell((short) grid[y - 1][x]).south);
        if (cell.south) assertTrue(y + 1 < grid.length && new Cell((short) grid[y + 1][x]).north);
        if (cell.east) assertTrue(x + 1 < grid[y].length && new Cell((short) grid[y][x + 1]).west);
        if (cell.west) assertTrue(x > 0 && new Cell((short) grid[y][x - 1]).east);
    }

    private static boolean[][] reachableFrom(int[][] grid, int startX, int startY) {
        boolean[][] seen = new boolean[grid.length][grid[0].length];
        Queue<CellLoc> queue = new ArrayDeque<>();
        queue.add(new CellLoc(startX, startY));
        seen[startY][startX] = true;

        while (!queue.isEmpty()) {
            CellLoc loc = queue.remove();
            Cell cell = new Cell((short) grid[loc.z][loc.x]);
            visit(queue, seen, loc.x, loc.z - 1, cell.north);
            visit(queue, seen, loc.x, loc.z + 1, cell.south);
            visit(queue, seen, loc.x + 1, loc.z, cell.east);
            visit(queue, seen, loc.x - 1, loc.z, cell.west);
        }
        return seen;
    }

    private static void visit(Queue<CellLoc> queue, boolean[][] seen, int x, int y, boolean connected) {
        if (!connected || seen[y][x]) return;
        seen[y][x] = true;
        queue.add(new CellLoc(x, y));
    }
}

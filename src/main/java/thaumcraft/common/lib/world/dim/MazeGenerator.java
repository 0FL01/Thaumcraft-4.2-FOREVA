package thaumcraft.common.lib.world.dim;

import java.util.*;

public class MazeGenerator {
    int width = 0;
    int height = 0;
    long seed = 0L;
    Random rand = null;
    public int[][] grid;

    public static final int N = 1;
    public static final int S = 2;
    public static final int E = 4;
    public static final int W = 8;
    public static final int A = 16;
    public static final int B = 32;

    public static int getOPP(int in) {
        switch (in) {
            case N: return S;
            case S: return N;
            case E: return W;
            case W: return E;
        }
        return -99;
    }

    public static int getDX(int in) {
        switch (in) {
            case N: return 0;
            case S: return 0;
            case E: return 1;
            case W: return -1;
        }
        return -99;
    }

    public static int getDY(int in) {
        switch (in) {
            case N: return -1;
            case S: return 1;
            case E: return 0;
            case W: return 0;
        }
        return -99;
    }

    public MazeGenerator(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.rand = new Random(seed);
        this.grid = new int[height][width];
    }

    public boolean generate() {
        // Initialize grid to 0
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                grid[y][x] = 0;

        // Exit room position (2x2 in a random corner)
        int bx = 0, by = 0;
        switch (rand.nextInt(4)) {
            case 0: bx = 0; by = 0; break;
            case 1: bx = width - 2; by = height - 2; break;
            case 2: bx = width - 2; by = 0; break;
            case 3: bx = 0; by = height - 2; break;
        }
        // Mark exit room cells with high bits (feature marker)
        grid[by][bx] = 512;
        grid[by][bx + 1] = 768;
        grid[by + 1][bx] = 1024;
        grid[by + 1][bx + 1] = 1280;

        // Center portal position
        int px = 1 + width / 2;
        int py = 1 + height / 2;
        grid[py][px] = 256; // marks center cell

        // Scatter some random blocked areas
        ArrayList<Loc> cells = new ArrayList<>();
        int l = (width + height) / 4;
        for (int z = 0; z < l; z++) {
            int w = 1 + rand.nextInt(3);
            if (w > 2) l--;
            int qq = rand.nextInt(width - w);
            int ww = rand.nextInt(height - w);
            for (int a = qq; a < qq + w; a++)
                for (int b = ww; b < ww + w; b++)
                    if (grid[b][a] == 0) grid[b][a] = -1;
        }

        // Carve first passage from center
        List<Integer> directions = Arrays.asList(N, S, E, W);
        Collections.shuffle(directions, rand);
        int xx = px + getDX(directions.get(0));
        int yy = py + getDY(directions.get(0));
        grid[py][px] |= directions.get(0);
        if (grid[yy][xx] < 0) grid[yy][xx] = 0;
        grid[yy][xx] |= getOPP(directions.get(0));
        cells.add(new Loc(xx, yy));

        boolean success = false;
        while (!cells.isEmpty()) {
            int index = getNextIndex(cells.size());
            int x = cells.get(index).x;
            int y = cells.get(index).y;
            Collections.shuffle(directions, rand);
            boolean carved = false;
            for (int dir : directions) {
                int nx = x + getDX(dir);
                int ny = y + getDY(dir);
                if (nx <= 0 || nx >= width - 1 || ny <= 0 || ny >= height - 1) continue;
                if (grid[ny][nx] == 0) {
                    grid[y][x] |= dir;
                    grid[ny][nx] |= getOPP(dir);
                    cells.add(new Loc(nx, ny));
                    carved = true;
                }
                if (carved) {
                    success = true;
                    break;
                }
            }
            if (carved) continue;
            cells.remove(index);
        }

        if (!success) return false;

        // Clean up blocked areas
        for (int aa = 0; aa < height; aa++)
            for (int bb = 0; bb < width; bb++)
                if (grid[aa][bb] < 0) grid[aa][bb] = 0;

        // Add extra connections from center
        Collections.shuffle(directions, rand);
        for (int dir : directions) {
            int nx = px + getDX(dir);
            int ny = py + getDY(dir);
            if (nx <= 0 || nx >= width - 1 || ny <= 0 || ny >= height - 1) continue;
            if (grid[ny][nx] <= 0 || !rand.nextBoolean()) continue;
            grid[ny][nx] |= getOPP(dir);
            grid[py][px] |= dir;
        }

        // Connect exit room (boss room)
        Collections.shuffle(directions, rand);
        boolean connected = false;
        outer:
        for (int ax = 0; ax < 2; ax++) {
            for (int ay = 0; ay < 2; ay++) {
                for (int dir : directions) {
                    int nx = bx + ax + getDX(dir);
                    int ny = by + ay + getDY(dir);
                    if (nx <= 0 || nx >= width - 1 || ny <= 0 || ny >= height - 1) continue;
                    if (grid[ny][nx] <= 0) continue;
                    Cell neighbor = new Cell((short) grid[ny][nx]);
                    if (neighbor.feature != 0) continue;
                    grid[ny][nx] |= getOPP(dir);
                    grid[by + ay][bx + ax] |= dir;
                    connected = true;
                    break outer;
                }
            }
        }

        if (!connected) {
            // Try carving new path to exit
            List<Integer> dirs2 = Arrays.asList(N, S, E, W);
            Collections.shuffle(dirs2, rand);
            boolean carved = false;
            outer2:
            for (int ax = 0; ax < 2; ax++) {
                for (int ay = 0; ay < 2; ay++) {
                    for (int dir2 : dirs2) {
                        int qx = bx + ax + getDX(dir2);
                        int qy = by + ay + getDY(dir2);
                        if (qx <= 0 || qx >= width - 1 || qy <= 0 || qy >= height - 1) continue;
                        if (grid[qy][qx] != 0) continue;
                        cells.clear();
                        cells.add(new Loc(qx, qy));
                        while (!cells.isEmpty()) {
                            int index = getNextIndex(cells.size());
                            int x = cells.get(index).x;
                            int y = cells.get(index).y;
                            Collections.shuffle(directions, rand);
                            carved = false;
                            for (int dir : directions) {
                                int nx = x + getDX(dir);
                                int ny = y + getDY(dir);
                                if (nx <= 0 || nx >= width - 1 || ny <= 0 || ny >= height - 1) continue;
                                if (grid[ny][nx] == 0) {
                                    grid[y][x] |= dir;
                                    grid[y][x] |= 0x6300; // feature markers for new path
                                    grid[ny][nx] |= getOPP(dir);
                                    grid[ny][nx] |= 0x6300;
                                    cells.add(new Loc(nx, ny));
                                    carved = true;
                                } else {
                                    Cell nc = new Cell((short) grid[ny][nx]);
                                    if (nc.feature == 0) {
                                        grid[y][x] |= dir;
                                        grid[ny][nx] |= getOPP(dir);
                                        grid[qy][qx] |= getOPP(dir2);
                                        grid[by + ay][bx + ax] |= dir2;
                                        success = true;
                                        carved = true;
                                        break outer2;
                                    }
                                }
                                if (carved) break;
                            }
                            if (carved) continue;
                            cells.remove(index);
                        }
                    }
                }
            }
            if (!success) return false;
        }

        // Clear temporary feature markers
        for (int aa = 0; aa < height; aa++) {
            for (int bb = 0; bb < width; bb++) {
                Cell c = new Cell((short) grid[aa][bb]);
                if (c.feature == 99) {
                    c.feature = 0;
                    grid[aa][bb] = c.pack();
                }
            }
        }

        // Find dead ends and assign special room features
        ArrayList<CellLoc> deadEnds = new ArrayList<>();
        for (int aa = 0; aa < height; aa++) {
            for (int bb = 0; bb < width; bb++) {
                Cell c = new Cell((short) grid[aa][bb]);
                int exits = (c.north ? 1 : 0) + (c.south ? 1 : 0) + (c.east ? 1 : 0) + (c.west ? 1 : 0);
                if (exits == 1 && c.feature == 0) {
                    deadEnds.add(new CellLoc(aa, bb));
                }
            }
        }

        if (deadEnds.isEmpty()) return false;

        // One dead end becomes key room (feature 6)
        int r = rand.nextInt(deadEnds.size());
        CellLoc ll = deadEnds.get(r);
        Cell c = new Cell((short) grid[ll.x][ll.z]);
        c.feature = 6;
        grid[ll.x][ll.z] = c.pack();
        deadEnds.remove(r);

        // Half of remaining dead ends become random rooms (features 7-8)
        if (!deadEnds.isEmpty()) {
            int count = 0;
            while (count < deadEnds.size() / 2 && !deadEnds.isEmpty()) {
                int r2 = rand.nextInt(deadEnds.size());
                CellLoc ll2 = deadEnds.get(r2);
                Cell c2 = new Cell((short) grid[ll2.x][ll2.z]);
                if (c2.feature != 0) { deadEnds.remove(r2); continue; }
                c2.feature = (byte) (7 + rand.nextInt(3)); // 7=nest, 8=library
                grid[ll2.x][ll2.z] = c2.pack();
                deadEnds.remove(r2);
                count++;
            }
        }

        // Additional random passage features on non-dead-end cells
        for (int aa = 0; aa < height; aa++) {
            for (int bb = 0; bb < width; bb++) {
                c = new Cell((short) grid[aa][bb]);
                if (c.feature != 0) continue;
                if (!c.north && !c.south && !c.west && !c.east) continue;
                if (rand.nextInt(25) != 0) continue;

                switch (rand.nextInt(8)) {
                    case 0: c.feature = 8; break;   // library
                    case 1: c.feature = 10; break;   // passage variant
                    case 2: case 3: c.feature = 11; break; // trapped passage
                    case 4: case 5: c.feature = 12; break; // crust fill
                    case 6: c.feature = 13; break;   // taint
                    case 7: c.feature = 14; break;   // web nest
                }
                grid[aa][bb] = c.pack();
            }
        }

        return true;
    }

    private int getNextIndex(int ceil) {
        float r = rand.nextFloat();
        if (r <= 0.45f) return ceil - 1;
        if (r <= 0.9f) return rand.nextInt(ceil);
        return 0;
    }

    public void print() {
        // Debug print - same ASCII maze visualization as original
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(grid[y][x] + "\t");
            }
            System.out.println();
        }
    }

    class Loc {
        int x, y;
        Loc(int x, int y) { this.x = x; this.y = y; }
    }
}

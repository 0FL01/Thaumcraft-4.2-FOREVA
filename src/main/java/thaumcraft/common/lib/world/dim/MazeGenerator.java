package thaumcraft.common.lib.world.dim;

import java.util.Random;

public class MazeGenerator {
    int width;
    int height;
    long seed;
    Random rand;
    public int[][] grid;
    public static final int N = 1;
    public static final int S = 2;
    public static final int E = 4;
    public static final int W = 8;
    public static final int A = 16;
    public static final int B = 32;

    public static int getOPP(int d) {
        if (d == N) return S; if (d == S) return N;
        if (d == E) return W; if (d == W) return E;
        if (d == A) return B; if (d == B) return A;
        return 0;
    }

    public static int getDX(int d) {
        if (d == E) return 1; if (d == W) return -1;
        return 0;
    }

    public static int getDY(int d) {
        if (d == N) return -1; if (d == S) return 1;
        return 0;
    }

    public MazeGenerator(int w, int h, long s) {
        this.width = w;
        this.height = h;
        this.seed = s;
        this.rand = new Random(s);
    }

    public boolean generate() {
        this.grid = new int[this.height][this.width];
        int total = this.width * this.height;
        int[] cellStack = new int[total];
        int stackSize = 0;
        int current = 0;
        int visited = 1;

        while (visited < total) {
            int cx = current % this.width;
            int cy = current / this.width;
            int[] dirs = new int[]{N, S, E, W};
            for (int i = dirs.length - 1; i > 0; i--) {
                int j = this.rand.nextInt(i + 1);
                int tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
            }
            boolean found = false;
            for (int dir : dirs) {
                int nx = cx + getDX(dir);
                int ny = cy + getDY(dir);
                if (nx >= 0 && nx < this.width && ny >= 0 && ny < this.height
                        && this.grid[ny][nx] == 0) {
                    this.grid[cy][cx] |= dir;
                    this.grid[ny][nx] |= getOPP(dir);
                    cellStack[stackSize++] = current;
                    current = ny * this.width + nx;
                    visited++;
                    found = true;
                    break;
                }
            }
            if (!found && stackSize > 0) {
                current = cellStack[--stackSize];
            }
        }
        return true;
    }

    public void print() {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                System.out.print(this.grid[y][x] + "\t");
            }
            System.out.println();
        }
    }

    public static class Loc {
        public int x;
        public int y;
        public final MazeGenerator this$0;

        public Loc(MazeGenerator mg, int x, int y) {
            this.this$0 = mg;
            this.x = x;
            this.y = y;
        }
    }
}

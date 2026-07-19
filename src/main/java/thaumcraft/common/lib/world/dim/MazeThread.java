package thaumcraft.common.lib.world.dim;

public class MazeThread implements Runnable {
    int x, z, w, h;
    long seed;

    public MazeThread(int x, int z, int w, int h, long seed) {
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.seed = seed;
    }

    @Override
    public void run() {
        MazeGenerator maze = new MazeGenerator(this.w, this.h, this.seed++);
        while (!maze.generate()) {
            maze = new MazeGenerator(this.w, this.h, this.seed++);
        }

        int colOffset = this.x - (1 + this.w / 2);
        int rowOffset = this.z - (1 + this.h / 2);
        for (int col = 0; col < this.w; ++col) {
            for (int row = 0; row < this.h; ++row) {
                if (maze.grid[row][col] <= 0) continue;
                MazeHandler.putToHashMapRaw(new CellLoc(col + colOffset, row + rowOffset), (short) maze.grid[row][col]);
            }
        }
    }
}

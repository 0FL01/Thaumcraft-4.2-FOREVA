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
        MazeGenerator maze = new MazeGenerator(this.w, this.h, this.seed);
        maze.generate();
        for (int row = 0; row < this.h; row++) {
            for (int col = 0; col < this.w; col++) {
                CellLoc loc = new CellLoc(this.x + col, this.z + row);
                Cell cell = new Cell();
                int g = maze.grid[row][col];
                cell.north = (g & MazeGenerator.N) != 0;
                cell.south = (g & MazeGenerator.S) != 0;
                cell.east  = (g & MazeGenerator.E) != 0;
                cell.west  = (g & MazeGenerator.W) != 0;
                cell.feature = 1;
                MazeHandler.putToHashMap(loc, cell);
            }
        }
    }
}

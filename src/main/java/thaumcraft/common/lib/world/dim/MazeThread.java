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

        // Reserve cells (sentinel) so overlapping generation doesn't happen
        MazeHandler.putToHashMapRaw(new CellLoc(x, z), (short) 1);
        MazeHandler.putToHashMapRaw(new CellLoc(x + w, z), (short) 1);
        MazeHandler.putToHashMapRaw(new CellLoc(x, z + h), (short) 1);
        MazeHandler.putToHashMapRaw(new CellLoc(x + w, z + h), (short) 1);
        MazeHandler.putToHashMapRaw(new CellLoc(x + w / 2, z + h / 2), (short) 1);

        // Store maze grid cells into handler, preserving features from generator
        for (int row = 0; row < this.h; row++) {
            for (int col = 0; col < this.w; col++) {
                CellLoc loc = new CellLoc(this.x + col, this.z + row);
                short gridValue = (short) maze.grid[row][col];
                Cell cell = new Cell(gridValue);

                // If this cell has stored gates from the generator, set them from the grid value
                // The grid value contains both wall bits (N,S,E,W) and feature in upper bits
                MazeHandler.putToHashMapRaw(loc, gridValue);
            }
        }

        // Remove sentinel entries if they overlap actual maze cells
        CellLoc[] sentinels = {
            new CellLoc(x, z), new CellLoc(x + w, z),
            new CellLoc(x, z + h), new CellLoc(x + w, z + h),
            new CellLoc(x + w / 2, z + h / 2)
        };
        for (CellLoc s : sentinels) {
            short val = MazeHandler.getFromHashMapRaw(s);
            if (val == 1) {
                MazeHandler.removeFromHashMap(s);
            }
        }
    }
}

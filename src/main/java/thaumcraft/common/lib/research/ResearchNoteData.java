package thaumcraft.common.lib.research;

import java.util.HashMap;

import thaumcraft.common.lib.research.ResearchManager.HexEntry;

public class ResearchNoteData {

    public String key;
    public int color;
    public HashMap<String, HexEntry> hexEntries = new HashMap<>();
    public HashMap<String, HexCoord> hexes = new HashMap<>();
    public boolean complete;
    public int copies;

    public boolean isComplete() {
        return this.complete;
    }

    public static class HexCoord {
        public final int q;
        public final int r;

        public HexCoord(int q, int r) {
            this.q = q;
            this.r = r;
        }

        @Override
        public String toString() {
            return this.q + ":" + this.r;
        }
    }
}

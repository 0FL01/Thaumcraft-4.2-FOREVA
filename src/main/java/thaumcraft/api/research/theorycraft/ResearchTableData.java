package thaumcraft.api.research.theorycraft;

import java.util.TreeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class ResearchTableData {
    public TileEntity table;
    public String player;
    public int inspiration;
    public int inspirationStart;
    public int bonusDraws;
    public final TreeMap<String, Integer> categoryTotals = new TreeMap<>();

    public ResearchTableData(TileEntity table) {
        this.table = table;
    }

    public ResearchTableData(EntityPlayer player, TileEntity table) {
        this(table);
        this.player = player.getName();
    }

    public boolean hasTotal(String category) {
        return categoryTotals.containsKey(category);
    }

    public int getTotal(String category) {
        Integer total = categoryTotals.get(category);
        return total == null ? 0 : total;
    }

    public void addTotal(String category, int amount) {
        categoryTotals.put(category, getTotal(category) + amount);
    }

    public void addInspiration(int amount) {
        inspiration += amount;
    }
}

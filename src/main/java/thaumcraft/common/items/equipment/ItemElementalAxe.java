package thaumcraft.common.items.equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;

public class ItemElementalAxe extends ItemAxe {

    public static List<List<?>> oreDictLogs = new ArrayList<>();

    public ItemElementalAxe(ToolMaterial material) {
        super(material, 8.0f, -3.0f);
    }
}

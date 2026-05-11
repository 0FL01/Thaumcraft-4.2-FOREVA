package thaumcraft.api.internal;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedRandomLoot
extends WeightedRandom.Item {
    public ItemStack item;
    public static ArrayList<WeightedRandomLoot> lootBagCommon = new ArrayList();
    public static ArrayList<WeightedRandomLoot> lootBagUncommon = new ArrayList();
    public static ArrayList<WeightedRandomLoot> lootBagRare = new ArrayList();

    public WeightedRandomLoot(ItemStack stack, int weight) {
        super(weight);
        this.item = stack;
    }
}


package thaumcraft.common.items;

import net.minecraft.item.ItemStack;
import net.minecraft.init.Bootstrap;
import net.minecraft.util.NonNullList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ItemNuggetCreativeParityRuntimeTest {

    private boolean copper;
    private boolean tin;
    private boolean silver;
    private boolean lead;

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Before
    public void rememberFlags() {
        this.copper = Config.foundCopperIngot;
        this.tin = Config.foundTinIngot;
        this.silver = Config.foundSilverIngot;
        this.lead = Config.foundLeadIngot;
    }

    @After
    public void restoreFlags() {
        Config.foundCopperIngot = this.copper;
        Config.foundTinIngot = this.tin;
        Config.foundSilverIngot = this.silver;
        Config.foundLeadIngot = this.lead;
    }

    @Test
    public void creativeVariantsFollowAllOptionalMetalFlagCombinations() {
        ItemNugget item = new ItemNugget();
        List<Integer> base = Arrays.asList(0, 5, 21, 6, 7, 16, 31);

        for (int mask = 0; mask < 16; mask++) {
            Config.foundCopperIngot = (mask & 1) != 0;
            Config.foundTinIngot = (mask & 2) != 0;
            Config.foundSilverIngot = (mask & 4) != 0;
            Config.foundLeadIngot = (mask & 8) != 0;

            List<Integer> expected = new ArrayList<Integer>(base);
            addPair(expected, mask, 1, 1, 17);
            addPair(expected, mask, 2, 2, 18);
            addPair(expected, mask, 4, 3, 19);
            addPair(expected, mask, 8, 4, 20);

            NonNullList<ItemStack> stacks = NonNullList.create();
            item.getSubItems(CreativeTabThaumcraft.tabThaumcraft, stacks);
            List<Integer> actual = new ArrayList<Integer>();
            for (ItemStack stack : stacks) {
                actual.add(stack.getItemDamage());
            }
            assertEquals("optional-metal mask " + mask, expected, actual);
        }
    }

    private static void addPair(List<Integer> expected, int mask, int bit,
            int nugget, int cluster) {
        if ((mask & bit) != 0) {
            expected.add(nugget);
            expected.add(cluster);
        }
    }
}

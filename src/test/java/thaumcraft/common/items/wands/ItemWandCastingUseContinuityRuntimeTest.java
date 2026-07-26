package thaumcraft.common.items.wands;

import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.junit.BeforeClass;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ItemWandCastingUseContinuityRuntimeTest {
    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void continuesOnlyAcrossVisOnlyStackReplacement() {
        ItemWandCasting wand = new ItemWandCasting();
        ItemStack original = new ItemStack(wand, 1, 7);
        ItemStack visUpdate = original.copy();
        assertNotSame(original, visUpdate);
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            ItemWandCasting.ensureTag(visUpdate).setInteger(aspect.getTag(), 100);
        }

        assertTrue(ForgeHooks.canContinueUsing(original, visUpdate));
        assertTrue(ForgeHooks.canContinueUsing(visUpdate, original));

        ItemStack focusChanged = visUpdate.copy();
        ItemWandCasting.ensureTag(focusChanged).setString(ItemWandCasting.TAG_FOCUS, "changed");
        assertFalse(ForgeHooks.canContinueUsing(original, focusChanged));
        assertFalse(ForgeHooks.canContinueUsing(focusChanged, original));
        assertFalse(ForgeHooks.canContinueUsing(original, new ItemStack(wand, 1, 8)));
        assertFalse(ForgeHooks.canContinueUsing(original, new ItemStack(Items.STICK)));
    }
}

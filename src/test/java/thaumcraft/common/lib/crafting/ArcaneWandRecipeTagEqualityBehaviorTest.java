package thaumcraft.common.lib.crafting;

import net.minecraft.init.Bootstrap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ArcaneWandRecipeTagEqualityBehaviorTest {
    private static final Item ITEM = new Item();

    @BeforeClass
    public static void bootstrapMinecraftStatics() {
        Bootstrap.register();
    }

    @Test
    public void checkItemEqualsShouldRequireExactTagMatchWhenTargetHasNbt() throws Exception {
        ItemStack target = new ItemStack(ITEM, 1, 0);
        NBTTagCompound targetTag = new NBTTagCompound();
        targetTag.setInteger("tagA", 1);
        target.setTagCompound(targetTag);

        ItemStack exact = new ItemStack(ITEM, 1, 0);
        NBTTagCompound exactTag = new NBTTagCompound();
        exactTag.setInteger("tagA", 1);
        exact.setTagCompound(exactTag);

        ItemStack extra = new ItemStack(ITEM, 1, 0);
        NBTTagCompound extraTag = new NBTTagCompound();
        extraTag.setInteger("tagA", 1);
        extraTag.setInteger("tagB", 2);
        extra.setTagCompound(extraTag);

        assertTrue(invokeCheckItemEquals(target, exact));
        assertFalse(invokeCheckItemEquals(target, extra));
    }

    private static boolean invokeCheckItemEquals(ItemStack target, ItemStack input) throws Exception {
        ArcaneWandRecipe recipe = new ArcaneWandRecipe();
        Method method = ArcaneWandRecipe.class.getDeclaredMethod("checkItemEquals", ItemStack.class, ItemStack.class);
        method.setAccessible(true);
        return (boolean) method.invoke(recipe, target, input);
    }
}

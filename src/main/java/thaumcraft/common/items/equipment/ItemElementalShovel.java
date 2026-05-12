package thaumcraft.common.items.equipment;

import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.IRepairable;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemElementalShovel extends ItemSpade implements IRepairable {

    public ItemElementalShovel(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    public static byte getOrientation(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("or")) {
            return stack.getTagCompound().getByte("or");
        }
        return 0;
    }

    public static void setOrientation(ItemStack stack, byte orientation) {
        if (stack == null || stack.isEmpty()) return;
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setByte("or", (byte) Math.floorMod(orientation, 3));
    }
}

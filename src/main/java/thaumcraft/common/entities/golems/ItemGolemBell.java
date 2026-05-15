package thaumcraft.common.entities.golems;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

import java.util.ArrayList;

public class ItemGolemBell extends Item {
    public ItemGolemBell() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(false);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);

    }

    public static int getGolemId(ItemStack stack) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("golemid")) {
            return stack.getTagCompound().getInteger("golemid");
        }
        return -1;
    }

    public static void resetMarkers(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || world == null) return;
        int id = getGolemId(stack);
        Entity entity = id >= 0 ? world.getEntityByID(id) : null;
        if (entity instanceof EntityGolemBase) {
            if (stack.hasTagCompound()) {
                stack.getTagCompound().removeTag("markers");
            }
            ((EntityGolemBase) entity).setMarkers(new ArrayList<Marker>());
            world.playSound(null, player.getPosition(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.7F, 1.0F + world.rand.nextFloat() * 0.1F);
        }
    }
}

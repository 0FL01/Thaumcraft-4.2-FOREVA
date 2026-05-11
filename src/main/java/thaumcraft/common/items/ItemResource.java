package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemResource extends Item implements IEssentiaContainerItem {

    public static final int META_ALUMENTUM = 0;
    public static final int META_NITOR = 1;
    public static final int META_THAUMIUM_INGOT = 2;
    public static final int META_QUICKSILVER = 3;
    public static final int META_TALLOW = 4;
    public static final int META_BRAIN = 5;
    public static final int META_AMBER = 6;
    public static final int META_CLOTH = 7;
    public static final int META_FILTER = 8;
    public static final int META_KNOWLEDGE_FRAGMENT = 9;
    public static final int META_MIRROR_GLASS = 10;
    public static final int META_TAINT_SLIME = 11;
    public static final int META_TAINT_TENDRIL = 12;
    public static final int META_LABEL = 13;
    public static final int META_DUST = 14;
    public static final int META_CHARM = 15;
    public static final int META_VOID_INGOT = 16;
    public static final int META_VOID_SEED = 17;
    public static final int META_COIN = 18;

    public static final String[] NAMES = {
            "alumentum", "nitor", "thaumiumingot", "quicksilver", "tallow",
            "brain", "amber", "cloth", "filter", "knowledgefragment",
            "mirrorglass", "taint_slime", "taint_tendril", "label",
            "dust", "charm", "voidingot", "voidseed", "coin"
    };

    public ItemResource() {
        this.setMaxStackSize(64);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        int d = Math.min(stack.getItemDamage(), NAMES.length - 1);
        return super.getTranslationKey() + "." + NAMES[d];
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < NAMES.length; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == META_KNOWLEDGE_FRAGMENT) {
            // Research knowledge fragment behavior - for later
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public AspectList getAspects(ItemStack itemstack) {
        if (itemstack.hasTagCompound()) {
            AspectList aspects = new AspectList();
            aspects.readFromNBT(itemstack.getTagCompound());
            return aspects;
        }
        return null;
    }

    @Override
    public void setAspects(ItemStack itemstack, AspectList aspects) {
        if (!itemstack.hasTagCompound()) {
            itemstack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        aspects.writeToNBT(itemstack.getTagCompound());
    }
}

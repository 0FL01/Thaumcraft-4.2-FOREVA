package thaumcraft.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemEldritchObject extends Item {

    public static final int META_ELDRITCH_OBJECT = 0;
    public static final int META_CRIMSON_RITES = 1;
    public static final int META_ELDRITCH_OBJECT_2 = 2;
    public static final int META_ELDRITCH_OBJECT_3 = 3;
    public static final int META_OB_PLACER = 4;

    public static final String[] NAMES = {
            "eldritch_object", "crimson_rites", "eldritch_object_2",
            "eldritch_object_3", "ob_placer"
    };

    public ItemEldritchObject() {
        this.setMaxStackSize(1);
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
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == META_OB_PLACER) {
            // Place eldritch obelisk - TBD
            RayTraceResult mop = this.rayTrace(world, player, false);
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = mop.getBlockPos();
                if (world.isAirBlock(pos.up())) {
                    world.setBlockState(pos.up(), net.minecraft.init.Blocks.OBSIDIAN.getDefaultState());
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        if (stack.getItemDamage() == META_CRIMSON_RITES) {
            // Open eldritch research - TBD
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }
}

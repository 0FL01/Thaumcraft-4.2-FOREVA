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
import thaumcraft.common.blocks.BlockEldritch;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

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
            RayTraceResult mop = this.rayTrace(world, player, false);
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = mop.getBlockPos().up();
                if (canPlaceObelisk(world, pos)) {
                    if (!world.isRemote) {
                        placeObelisk(world, pos);
                    }
                    if (!player.capabilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        if (stack.getItemDamage() == META_CRIMSON_RITES) {
            if (!world.isRemote) {
                ResearchManager.addResearch(player, "CRIMSON");
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    private boolean canPlaceObelisk(World world, BlockPos pos) {
        for (int y = 0; y < 5; y++) {
            if (!world.isAirBlock(pos.up(y))) return false;
        }
        return ConfigBlocks.blockEldritch != null;
    }

    private void placeObelisk(World world, BlockPos pos) {
        for (int y = 0; y < 5; y++) {
            int meta = y == 4 ? 3 : y == 0 ? 1 : 2;
            world.setBlockState(pos.up(y), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, meta), 3);
        }
    }
}

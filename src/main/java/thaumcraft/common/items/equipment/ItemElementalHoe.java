package thaumcraft.common.items.equipment;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemDye;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thaumcraft.api.IRepairable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockCustomPlant;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;

public class ItemElementalHoe extends ItemHoe implements IRepairable {

    public ItemElementalHoe(ToolMaterial material) {
        super(material);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public int getItemEnchantability() {
        return 5;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack thaumiumIngot = new ItemStack(ConfigItems.itemResource, 1, 2);
        return repair.isItemEqual(thaumiumIngot) || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                                      EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }

        boolean did = false;
        for (int xx = -1; xx <= 1; xx++) {
            for (int zz = -1; zz <= 1; zz++) {
                BlockPos target = pos.add(xx, 0, zz);
                EnumActionResult result = super.onItemUse(player, world, target, hand, facing, hitX, hitY, hitZ);
                if (result != EnumActionResult.SUCCESS) {
                    continue;
                }
                Thaumcraft.proxy.blockSparkle(world, target.getX(), target.getY(), target.getZ(), 8401408, 2);
                did = true;
            }
        }

        if (!did) {
            did = applyBonemealAtLoc(world, player, pos);
            if (!did) {
                IBlockState state = world.getBlockState(pos);
                Block block = state.getBlock();
                int meta = block.getMetaFromState(state);
                if (block == ConfigBlocks.blockCustomPlant && meta == 0 && stack.getItemDamage() + 20 <= stack.getMaxDamage()) {
                    ((BlockCustomPlant) block).growGreatTree(world, pos, world.rand);
                    stack.damageItem(5, player);
                    Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 0, 2);
                    did = true;
                } else if (block == ConfigBlocks.blockCustomPlant && meta == 1 && stack.getItemDamage() + 150 <= stack.getMaxDamage()) {
                    ((BlockCustomPlant) block).growSilverTree(world, pos, world.rand);
                    stack.damageItem(25, player);
                    Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 0, 2);
                    did = true;
                }
            } else {
                stack.damageItem(1, player);
                Thaumcraft.proxy.blockSparkle(world, pos.getX(), pos.getY(), pos.getZ(), 0, 3);
            }

            if (did && !world.isRemote) {
                world.playSound(null, pos, TCSounds.WAND, SoundCategory.PLAYERS, 0.75F, 0.9F + world.rand.nextFloat() * 0.2F);
            }
        }

        return did ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
    }

    private static boolean applyBonemealAtLoc(World world, EntityPlayer player, BlockPos pos) {
        ItemStack fakeBonemeal = new ItemStack(net.minecraft.init.Items.DYE, 1, 15);
        boolean applied = ItemDye.applyBonemeal(fakeBonemeal, world, pos, player, EnumHand.MAIN_HAND);
        if (applied && !world.isRemote) {
            world.playEvent(2005, pos, 0);
        }
        return applied;
    }
}

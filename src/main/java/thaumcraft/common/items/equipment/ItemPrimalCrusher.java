package thaumcraft.common.items.equipment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemResource;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemPrimalCrusher extends ItemTool implements IRepairable, IWarpingGear {

    private static final Set<Block> EFFECTIVE_BLOCKS = new HashSet<>(Arrays.asList(
            Blocks.COBBLESTONE, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB, Blocks.STONE,
            Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE, Blocks.IRON_ORE, Blocks.IRON_BLOCK,
            Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.DIAMOND_ORE,
            Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE,
            Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.RAIL,
            Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.GRASS,
            Blocks.DIRT, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW_LAYER, Blocks.SNOW,
            Blocks.CLAY, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MYCELIUM,
            ConfigBlocks.blockTaint, ConfigBlocks.blockTaintFibres, Blocks.OBSIDIAN
    ));

    private int side = 0;

    public ItemPrimalCrusher(ToolMaterial material) {
        super(3.5F, -2.8F, material, EFFECTIVE_BLOCKS);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return new HashSet<>(Arrays.asList("pickaxe", "shovel"));
    }

    @Override
    public boolean canHarvestBlock(IBlockState state) {
        Material material = state.getMaterial();
        return material != Material.WOOD && material != Material.LEAVES && material != Material.CLOTH;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        Material material = state.getMaterial();
        if (material == Material.ROCK || material == Material.IRON || material == Material.ANVIL) {
            return this.efficiency;
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        RayTraceResult mop = this.rayTrace(player.world, player, true);
        if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK && mop.sideHit != null) {
            this.side = mop.sideHit.getIndex();
        }
        return super.onBlockStartBreak(stack, pos, player);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase entity) {
        if (entity.isSneaking()) {
            return super.onBlockDestroyed(stack, world, state, pos, entity);
        }
        if (!world.isRemote && entity instanceof EntityPlayer
                && (ForgeHooks.isToolEffective(world, pos, stack) || this.isEffectiveAgainst(state))) {
            for (int aa = -1; aa <= 1; ++aa) {
                for (int bb = -1; bb <= 1; ++bb) {
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    if (this.side <= 1) {
                        xx = aa;
                        zz = bb;
                    } else if (this.side <= 3) {
                        xx = aa;
                        yy = bb;
                    } else {
                        zz = aa;
                        yy = bb;
                    }
                    BlockPos target = pos.add(xx, yy, zz);
                    if (!world.isBlockModifiable((EntityPlayer) entity, target)) continue;
                    IBlockState targetState = world.getBlockState(target);
                    if (targetState.getBlockHardness(world, target) < 0.0F
                            || !ForgeHooks.isToolEffective(world, target, stack)
                            && !this.isEffectiveAgainst(targetState)) continue;
                    stack.damageItem(1, entity);
                    thaumcraft.common.lib.utils.BlockUtils.harvestBlock(
                            world, target, (EntityPlayer) entity, true, 2);
                }
            }
        }
        return true;
    }

    private boolean isEffectiveAgainst(IBlockState state) {
        return EFFECTIVE_BLOCKS.contains(state.getBlock())
                || state.getMaterial() == Material.ROCK
                || state.getMaterial() == Material.IRON
                || state.getMaterial() == Material.ANVIL
                || state.getMaterial() == Material.GROUND
                || state.getMaterial() == Material.GRASS
                || state.getMaterial() == Material.SAND
                || state.getMaterial() == Material.CLAY;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return !repair.isEmpty()
                && repair.getItem() == ConfigItems.itemResource
                && repair.getMetadata() == ItemResource.META_CHARM
                || super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability() {
        return 20;
    }

    @Override
    public int getWarp(ItemStack itemstack, EntityPlayer player) {
        return 2;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
        if (!world.isRemote && stack.isItemDamaged() && entity != null && entity.ticksExisted % 20 == 0) {
            stack.setItemDamage(Math.max(0, stack.getItemDamage() - 1));
        }
    }
}

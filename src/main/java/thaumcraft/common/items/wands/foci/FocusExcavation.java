package thaumcraft.common.items.wands.foci;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.utils.BlockUtils;

public class FocusExcavation extends ItemFocusBasic {

    private static final HashMap<String, Float> breakcount = new HashMap<>();
    private static final HashMap<String, BlockPos> lastBlock = new HashMap<>();
    public static final FocusUpgradeType dowsing = new FocusUpgradeType(20, new ResourceLocation("thaumcraft", "textures/foci/dowsing.png"), "focus.upgrade.dowsing.name", "focus.upgrade.dowsing.text", new AspectList().add(Aspect.MINE, 1));

    public FocusExcavation() {
        super();
        this.setMaxStackSize(1);
    }

    @Override
    public int getFocusColor(ItemStack stack) {
        return 0x8B4513;
    }

    @Override
    public AspectList getVisCost(ItemStack stack) {
        AspectList cost = new AspectList().add(Aspect.EARTH, 15);
        if (this.isUpgradedWith(stack, FocusUpgradeType.silktouch)) {
            return new AspectList().add(Aspect.AIR, 1).add(Aspect.FIRE, 1).add(Aspect.EARTH, 1)
                    .add(Aspect.WATER, 1).add(Aspect.ORDER, 1).add(Aspect.ENTROPY, 1).add(cost);
        }
        if (this.isUpgradedWith(stack, dowsing)) {
            return new AspectList().add(Aspect.FIRE, 2).add(Aspect.ORDER, 2).add(cost);
        }
        return cost;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack wandStack, World world, EntityPlayer player, RayTraceResult movingobjectposition) {
        player.setActiveHand(EnumHand.MAIN_HAND);
        return wandStack;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focusstack) {
        return true;
    }

    @Override
    public void onUsingFocusTick(ItemStack wandStack, EntityPlayer player, int count) {
        if (!(wandStack.getItem() instanceof ItemWandCasting)) return;
        ItemWandCasting wand = (ItemWandCasting) wandStack.getItem();
        ItemStack focusStack = wand.getFocusItem(wandStack);
        if (!wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), false, false)) {
            player.resetActiveHand();
            return;
        }
        if (player.world.isRemote) return;

        RayTraceResult mop = this.rayTrace(player.world, player, false);
        if (mop == null || mop.typeOfHit != RayTraceResult.Type.BLOCK || !player.world.isBlockModifiable(player, mop.getBlockPos())) {
            this.resetBreakProgress(player);
            return;
        }

        BlockPos pos = mop.getBlockPos();
        IBlockState state = player.world.getBlockState(pos);
        Block block = state.getBlock();
        int meta = block.getMetaFromState(state);
        float hardness = state.getBlockHardness(player.world, pos);
        if (block == Blocks.AIR || block.isAir(state, player.world, pos) || hardness < 0.0F) {
            this.resetBreakProgress(player);
            return;
        }

        String key = "S" + player.getName();
        BlockPos last = lastBlock.get(key);
        if (!pos.equals(last)) {
            lastBlock.put(key, pos);
            breakcount.put(key, 0.0F);
            return;
        }

        float bc = breakcount.containsKey(key) ? breakcount.get(key) : 0.0F;
        float speed = this.getBreakSpeed(block, state, this.getUpgradeLevel(focusStack, FocusUpgradeType.potency));
        if (bc >= hardness && wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false)) {
            if (this.excavate(player.world, wandStack, player, block, state, meta, pos)) {
                for (int a = 0; a < this.getUpgradeLevel(focusStack, FocusUpgradeType.enlarge); ++a) {
                    if (!wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), false, false)) break;
                    if (this.breakNeighbour(player, pos, block, meta, wandStack)) {
                        wand.consumeAllVis(wandStack, player, this.getVisCost(focusStack), true, false);
                    }
                }
                player.swingArm(EnumHand.MAIN_HAND);
            }
            this.resetBreakProgress(player);
        } else {
            breakcount.put(key, bc + speed);
            if (player.ticksExisted % 24 == 0) {
                player.world.playSound(null, pos, TCSounds.RUMBLE, net.minecraft.util.SoundCategory.BLOCKS, 0.3F, 1.0F);
            }
        }
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wandStack, World world, EntityPlayer player, int count) {
        this.resetBreakProgress(player);
    }

    @Override
    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack itemstack) {
        return ItemFocusBasic.WandFocusAnimation.CHARGE;
    }

    private float getBreakSpeed(Block block, IBlockState state, int potency) {
        float speed = 0.05F + (float) potency * 0.1F;
        Material material = state.getMaterial();
        if (material == Material.ROCK || material == Material.IRON || material == Material.ANVIL || material == Material.GLASS) {
            speed = 0.25F + (float) potency * 0.25F;
        }
        if (block == Blocks.OBSIDIAN) speed *= 3.0F;
        return speed;
    }

    private boolean excavate(World world, ItemStack stack, EntityPlayer player, Block block, IBlockState state, int meta, BlockPos pos) {
        if (!(player instanceof EntityPlayerMP)) return false;
        int xp = ForgeHooks.onBlockBreakEvent(world, ((EntityPlayerMP) player).interactionManager.getGameType(), (EntityPlayerMP) player, pos);
        if (xp < 0) return false;

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        ItemStack focusStack = wand.getFocusItem(stack);
        int fortune = this.getUpgradeLevel(focusStack, FocusUpgradeType.treasure);
        boolean silk = this.isUpgradedWith(focusStack, FocusUpgradeType.silktouch);
        if (silk && block.canSilkHarvest(world, pos, state, player)) {
            ItemStack itemstack = BlockUtils.createStackedBlock(block, meta);
            if (!itemstack.isEmpty()) {
                BlockUtils.dropBlockAsItem(world, pos.getX(), pos.getY(), pos.getZ(), itemstack, block);
            }
        } else {
            BlockUtils.dropBlockAsItemWithChance(world, block, pos.getX(), pos.getY(), pos.getZ(), meta, 1.0F, fortune, player);
            block.dropXpOnBlockBreak(world, pos, xp);
        }
        world.setBlockToAir(pos);
        world.playEvent(2001, pos, Block.getStateId(state));
        return true;
    }

    private boolean breakNeighbour(EntityPlayer player, BlockPos pos, Block block, int meta, ItemStack stack) {
        List<EnumFacing> directions = Arrays.asList(EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST);
        Collections.shuffle(directions, player.world.rand);
        for (EnumFacing dir : directions) {
            BlockPos next = pos.offset(dir);
            IBlockState state = player.world.getBlockState(next);
            if (state.getBlock() != block || state.getBlock().getMetaFromState(state) != meta) continue;
            if (!player.world.isBlockModifiable(player, next)) continue;
            if (this.excavate(player.world, stack, player, block, state, meta, next)) return true;
        }
        return false;
    }

    private void resetBreakProgress(EntityPlayer player) {
        String key = "S" + player.getName();
        breakcount.put(key, 0.0F);
        lastBlock.remove(key);
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.treasure};
            case 2:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.enlarge};
            case 3:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.treasure, dowsing};
            case 4:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.enlarge};
            case 5:
                return new FocusUpgradeType[]{FocusUpgradeType.frugal, FocusUpgradeType.potency, FocusUpgradeType.treasure, FocusUpgradeType.silktouch};
            default:
                return null;
        }
    }

    @Override
    public String getSortingHelper(ItemStack stack) {
        return "BE" + super.getSortingHelper(stack);
    }

    @Override
    public boolean acceptsEnchant(int id) {
        return true;
    }
}

package thaumcraft.common.items;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.common.blocks.BlockEldritch;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.tiles.TileNode;

public class ItemEldritchObject extends Item {

    public static final int META_ELDRITCH_OBJECT = 0;
    public static final int META_CRIMSON_RITES = 1;
    public static final int META_ELDRITCH_OBJECT_2 = 2;
    public static final int META_ELDRITCH_OBJECT_3 = 3;
    public static final int META_OB_PLACER = 4;

    private static final int META_COUNT = 5;

    public ItemEldritchObject() {
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            for (int i = 0; i < META_COUNT; i++) {
                items.add(new ItemStack(this, 1, i));
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        switch (stack.getItemDamage()) {
            case META_ELDRITCH_OBJECT_2:
                return EnumRarity.RARE;
            case META_ELDRITCH_OBJECT_3:
                return EnumRarity.EPIC;
            default:
                return EnumRarity.UNCOMMON;
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        switch (stack.getItemDamage()) {
            case META_ELDRITCH_OBJECT:
                tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemEldritchObject.text.1"));
                break;
            case META_CRIMSON_RITES:
                tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemEldritchObject.text.2"));
                tooltip.add(TextFormatting.DARK_BLUE + I18n.translateToLocal("item.ItemEldritchObject.text.3"));
                break;
            case META_ELDRITCH_OBJECT_2:
                tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemEldritchObject.text.4"));
                break;
            case META_ELDRITCH_OBJECT_3:
                tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemEldritchObject.text.5"));
                tooltip.add(TextFormatting.DARK_PURPLE + I18n.translateToLocal("item.ItemEldritchObject.text.6"));
                break;
            case META_OB_PLACER:
                tooltip.add(TextFormatting.ITALIC + "Creative Mode Only");
                break;
            default:
                break;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == META_CRIMSON_RITES) {
            if (!world.isRemote && !ResearchManager.isResearchComplete(player, "CRIMSON")) {
                ResearchManager.addResearch(player, "CRIMSON");
                world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.LEARN, SoundCategory.PLAYERS, 0.75F, 1.0F);
            }
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItemDamage() == META_ELDRITCH_OBJECT_3) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileNode) {
                player.swingArm(hand);
                if (!world.isRemote) {
                    stack.shrink(1);
                    transformNode(world, pos, (TileNode) tile, player);
                    return EnumActionResult.SUCCESS;
                }
                return EnumActionResult.PASS;
            }
            return EnumActionResult.PASS;
        }

        if (side == EnumFacing.UP && stack.getItemDamage() == META_OB_PLACER) {
            player.swingArm(hand);
            for (int y = 1; y <= 6; y++) {
                if (!world.isAirBlock(pos.up(y))) return EnumActionResult.PASS;
            }
            if (!world.isRemote) {
                placeObelisk(world, pos);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }

        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    private void transformNode(World world, BlockPos pos, TileNode node, EntityPlayer player) {
        boolean research = ResearchManager.isResearchComplete(player.getName(), "PRIMNODE");
        for (Aspect aspect : node.getAspects().getAspects()) {
            if (aspect == null) continue;
            int base = node.getNodeVisBase(aspect);
            if (!aspect.isPrimal()) {
                if (world.rand.nextBoolean()) {
                    node.setNodeVisBase(aspect, (short) (base - 1));
                }
                continue;
            }
            base = base - 2 + world.rand.nextInt(research ? 9 : 6);
            node.setNodeVisBase(aspect, (short) base);
        }
        for (Aspect aspect : Aspect.getPrimalAspects()) {
            if (aspect == null) continue;
            int base = node.getNodeVisBase(aspect);
            int roll = world.rand.nextInt(research ? 4 : 3);
            if (roll > 0 && roll > base) {
                node.setNodeVisBase(aspect, (short) roll);
                node.addToContainer(aspect, 1);
            }
        }
        if (node.getNodeModifier() == NodeModifier.FADING && world.rand.nextBoolean()) {
            node.setNodeModifier(NodeModifier.PALE);
        } else if (node.getNodeModifier() == NodeModifier.PALE && world.rand.nextBoolean()) {
            node.setNodeModifier(null);
        } else if (node.getNodeModifier() == null && world.rand.nextInt(5) == 0) {
            node.setNodeModifier(NodeModifier.BRIGHT);
        }

        node.markDirty();
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        world.createExplosion(null, pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D,
                3.0F + world.rand.nextFloat() * (float) (research ? 3 : 5), true);
        for (int i = 0; i < 33; i++) {
            BlockPos fluxPos = pos.add(world.rand.nextInt(6) - world.rand.nextInt(6),
                    world.rand.nextInt(6) - world.rand.nextInt(6),
                    world.rand.nextInt(6) - world.rand.nextInt(6));
            if (!world.isAirBlock(fluxPos)) continue;
            if (fluxPos.getY() < pos.getY()) {
                if (ConfigBlocks.blockFluxGoo != null) {
                    world.setBlockState(fluxPos, ConfigBlocks.blockFluxGoo.getStateFromMeta(7), 3);
                }
            } else if (ConfigBlocks.blockFluxGas != null) {
                world.setBlockState(fluxPos, ConfigBlocks.blockFluxGas.getStateFromMeta(7), 3);
            }
        }
    }

    private void placeObelisk(World world, BlockPos pos) {
        world.setBlockState(pos.up(1), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 0), 3);
        world.setBlockState(pos.up(3), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 1), 3);
        world.setBlockState(pos.up(4), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 2), 3);
        world.setBlockState(pos.up(5), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 2), 3);
        world.setBlockState(pos.up(6), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 2), 3);
        world.setBlockState(pos.up(7), ConfigBlocks.blockEldritch.getDefaultState().withProperty(BlockEldritch.TYPE, 2), 3);
    }
}

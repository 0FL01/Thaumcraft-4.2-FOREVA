package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraft.client.util.ITooltipFlag;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.tiles.TileMirror;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHandMirror extends Item {

    public ItemHandMirror() {
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
        this.setNoRepair();
        this.setCreativeTab(CreativeTabThaumcraft.tabThaumcraft);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            if (!hasMirrorLink(stack)) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            World linkedWorld = getLinkedWorld(stack);
            if (linkedWorld == null) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            if (!isLinkedMirrorValid(linkedWorld, stack)) {
                clearInvalidLink(stack, player, world);
                return new ActionResult<>(EnumActionResult.PASS, stack);
            }
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_HAND_MIRROR, world,
                    MathHelper.floor(player.posX), MathHelper.floor(player.posY), MathHelper.floor(player.posZ));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound()
                && stack.getTagCompound().hasKey("linkX")
                && stack.getTagCompound().hasKey("linkY")
                && stack.getTagCompound().hasKey("linkZ")
                && stack.getTagCompound().hasKey("linkDim")
                && stack.getTagCompound().hasKey("dimname")) {
            int x = stack.getTagCompound().getInteger("linkX");
            int y = stack.getTagCompound().getInteger("linkY");
            int z = stack.getTagCompound().getInteger("linkZ");
            String dimName = stack.getTagCompound().getString("dimname");
            tooltip.add(new TextComponentTranslation("tc.handmirrorlinkedto").getFormattedText()
                    + " " + x + "," + y + "," + z + " in " + dimName);
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() != ConfigBlocks.blockMirror || !(world.getTileEntity(pos) instanceof TileMirror)) {
            return EnumActionResult.PASS;
        }
        if (world.isRemote) {
            player.swingArm(hand);
            return EnumActionResult.PASS;
        }
        if (!world.isRemote) {
            NBTTagCompound tag = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
            tag.setInteger("linkX", pos.getX());
            tag.setInteger("linkY", pos.getY());
            tag.setInteger("linkZ", pos.getZ());
            tag.setInteger("linkDim", world.provider.getDimension());
            tag.setString("dimname", world.provider.getDimensionType().getName());
            stack.setTagCompound(tag);
            world.playSound(null, pos, TCSounds.JAR, SoundCategory.BLOCKS, 1.0F, 2.0F);
            player.sendStatusMessage(new TextComponentTranslation("tc.handmirrorlinked"), false);
        }
        return EnumActionResult.SUCCESS;
    }

    private static boolean hasMirrorLink(ItemStack stack) {
        return stack.hasTagCompound()
                && stack.getTagCompound().hasKey("linkX")
                && stack.getTagCompound().hasKey("linkY")
                && stack.getTagCompound().hasKey("linkZ")
                && stack.getTagCompound().hasKey("linkDim");
    }

    private static World getLinkedWorld(ItemStack stack) {
        if (!hasMirrorLink(stack)) return null;
        return DimensionManager.getWorld(stack.getTagCompound().getInteger("linkDim"));
    }

    private static boolean isLinkedMirrorValid(World world, ItemStack stack) {
        if (!hasMirrorLink(stack) || world == null) return false;
        NBTTagCompound tag = stack.getTagCompound();
        BlockPos pos = new BlockPos(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ"));
        return world.getTileEntity(pos) instanceof TileMirror;
    }

    private static void clearInvalidLink(ItemStack mirror, EntityPlayer player, World world) {
        mirror.setTagCompound(null);
        world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.ZAP, SoundCategory.PLAYERS, 1.0F, 0.8F);
        player.sendStatusMessage(new TextComponentTranslation("tc.handmirrorerror"), false);
    }

    public static boolean transport(ItemStack mirror, ItemStack items, EntityPlayer player, World worldObj) {
        if (mirror == null || mirror.isEmpty() || items == null || items.isEmpty() || !hasMirrorLink(mirror)) {
            return false;
        }
        World linked = getLinkedWorld(mirror);
        if (!(linked instanceof WorldServer)) {
            return false;
        }
        NBTTagCompound tag = mirror.getTagCompound();
        BlockPos pos = new BlockPos(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ"));
        if (!(linked.getTileEntity(pos) instanceof TileMirror)) {
            clearInvalidLink(mirror, player, worldObj);
            return false;
        }

        int meta = linked.getBlockState(pos).getBlock().getMetaFromState(linked.getBlockState(pos));
        EnumFacing facing = EnumFacing.byIndex(meta % 6);
        EntityItem entityItem = new EntityItem(linked,
                (double) pos.getX() + 0.5D - (double) facing.getXOffset() * 0.3D,
                (double) pos.getY() + 0.5D - (double) facing.getYOffset() * 0.3D,
                (double) pos.getZ() + 0.5D - (double) facing.getZOffset() * 0.3D,
                items.copy());
        entityItem.motionX = (double) facing.getXOffset() * 0.15D;
        entityItem.motionY = (double) facing.getYOffset() * 0.15D;
        entityItem.motionZ = (double) facing.getZOffset() * 0.15D;
        entityItem.setPickupDelay(20);
        linked.spawnEntity(entityItem);
        linked.addBlockEvent(pos, ConfigBlocks.blockMirror, 1, 0);
        worldObj.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 0.1F, 1.0F);
        return true;
    }
}

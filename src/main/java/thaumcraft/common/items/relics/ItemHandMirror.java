package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.tiles.TileMirror;

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
            if (!isLinkedMirrorValid(world, stack)) {
                if (stack.hasTagCompound()) stack.getTagCompound().removeTag("linkX");
                player.sendStatusMessage(new TextComponentTranslation("tc.handmirrorerror"), true);
                return new ActionResult<>(EnumActionResult.FAIL, stack);
            }
            player.openGui(Thaumcraft.instance, CommonProxy.GUI_HAND_MIRROR, world, (int) player.posX, (int) player.posY, (int) player.posZ);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.getBlockState(pos).getBlock() != ConfigBlocks.blockMirror || !(world.getTileEntity(pos) instanceof TileMirror)) {
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
            player.sendStatusMessage(new TextComponentTranslation("tc.handmirrorlinked"), true);
        }
        return EnumActionResult.SUCCESS;
    }

    private static boolean isLinkedMirrorValid(World world, ItemStack stack) {
        if (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("linkX")) return false;
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.getInteger("linkDim") != world.provider.getDimension()) return false;
        BlockPos pos = new BlockPos(tag.getInteger("linkX"), tag.getInteger("linkY"), tag.getInteger("linkZ"));
        return world.getTileEntity(pos) instanceof TileMirror;
    }
}

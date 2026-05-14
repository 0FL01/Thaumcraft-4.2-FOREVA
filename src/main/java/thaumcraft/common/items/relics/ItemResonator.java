package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemResonator extends Item {

    public ItemResonator() {
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
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side,
                                           float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IEssentiaTransport)) return EnumActionResult.PASS;
        if (!world.isRemote) {
            IEssentiaTransport transport = (IEssentiaTransport) tile;
            Aspect essentia = transport.getEssentiaType(side);
            int amount = transport.getEssentiaAmount(side);
            Aspect suction = transport.getSuctionType(side);
            int suctionAmount = transport.getSuctionAmount(side);
            player.sendStatusMessage(new TextComponentTranslation("tc.resonator1", amount, essentia == null ? "-" : essentia.getName()), false);
            player.sendStatusMessage(new TextComponentTranslation("tc.resonator2", suctionAmount, suction == null ? "-" : suction.getName()), false);
            if (tile instanceof IAspectContainer) {
                player.sendStatusMessage(new TextComponentTranslation("tc.resonator3", ((IAspectContainer) tile).getAspects()), false);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}

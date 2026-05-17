package thaumcraft.common.items.relics;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.lib.CreativeTabThaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.tiles.TileTubeBuffer;

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
    public boolean hasEffect(ItemStack stack) {
        return stack.hasTagCompound();
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
        if (world.isRemote) {
            player.swingArm(hand);
            return EnumActionResult.PASS;
        }
        IEssentiaTransport transport = (IEssentiaTransport) tile;
        EnumFacing face = side == null ? EnumFacing.UP : side;
        RayTraceResult hit = RayTracer.retraceBlock(world, player, pos.getX(), pos.getY(), pos.getZ());
        if (hit != null && hit.subHit >= 0 && hit.subHit < 6) {
            face = EnumFacing.byIndex(hit.subHit);
        }
        if (tile instanceof TileTubeBuffer && tile instanceof IAspectContainer) {
            AspectList aspects = ((IAspectContainer) tile).getAspects();
            if (aspects != null && aspects.size() > 0) {
                for (Aspect aspect : aspects.getAspectsSorted()) {
                    player.sendStatusMessage(new TextComponentTranslation("tc.resonator1", aspects.getAmount(aspect), aspect.getName()), false);
                }
            }
        } else {
            Aspect essentia = transport.getEssentiaType(face);
            if (essentia != null) {
                player.sendStatusMessage(new TextComponentTranslation("tc.resonator1", transport.getEssentiaAmount(face), essentia.getName()), false);
            }
        }
        Aspect suction = transport.getSuctionType(face);
        String suctionName = suction == null
                ? new TextComponentTranslation("tc.resonator3").getFormattedText()
                : suction.getName();
        player.sendStatusMessage(new TextComponentTranslation("tc.resonator2", transport.getSuctionAmount(face), suctionName), false);
        world.playSound(null, pos, TCSounds.ALEMBICKNOCK, SoundCategory.BLOCKS, 0.5F, 1.9F + world.rand.nextFloat() * 0.1F);
        return EnumActionResult.SUCCESS;
    }
}

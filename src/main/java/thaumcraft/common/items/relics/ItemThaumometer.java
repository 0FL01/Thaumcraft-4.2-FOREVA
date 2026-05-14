package thaumcraft.common.items.relics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.nodes.INode;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumometer extends Item {

    public ItemThaumometer() {
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setMaxDamage(0);
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
            Entity pointed = EntityUtils.getPointedEntity(world, player, 10.0D, null);
            if (pointed != null) {
                ScanManager.scanEntity(player, pointed);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
            RayTraceResult mop = this.rayTrace(world, player, true);
            if (mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = mop.getBlockPos();
                TileEntity tile = world.getTileEntity(pos);
                if (tile instanceof INode) {
                    INode node = (INode)tile;
                    String id = node.getId();
                    if (id == null || id.isEmpty()) {
                        id = world.provider.getDimension() + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
                    }
                    ScanManager.scanPhenomena(player, "NODE" + id);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
                IBlockState state = world.getBlockState(pos);
                ItemStack target = state.getBlock().getPickBlock(state, mop, world, pos, player);
                if (target == null || target.isEmpty()) {
                    target = BlockUtils.createStackedBlock(state.getBlock(), state.getBlock().getMetaFromState(state));
                }
                if (!target.isEmpty()) {
                    ScanManager.scanItem(player, target);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }
}

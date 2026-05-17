package thaumcraft.common.items.relics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumometer extends Item {
    private ScanResult startScan;

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
    public int getMaxItemUseDuration(ItemStack stack) {
        return 25;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.NONE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            this.startScan = doScan(stack, world, player);
        }
        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUsingTick(ItemStack stack, EntityLivingBase entity, int count) {
        if (!(entity instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer)entity;
        World world = player.world;
        if (!world.isRemote) {
            return;
        }

        ScanResult current = doScan(stack, world, player);
        if (this.startScan != null && current != null && current.equals(this.startScan)) {
            if (count <= 5) {
                this.startScan = null;
                if (ScanManager.completeScan(player, current, "@")) {
                    PacketHandler.INSTANCE.sendToServer(new PacketScannedToServer(current, player, "@"));
                }
            }
            if (count % 2 == 0) {
                world.playSound(player, player.posX, player.posY, player.posZ, TCSounds.CAMERATICKS, SoundCategory.PLAYERS, 0.2f, 0.45f + world.rand.nextFloat() * 0.1f);
            }
        } else {
            this.startScan = null;
        }
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entityLiving, int timeLeft) {
        super.onPlayerStoppedUsing(stack, world, entityLiving, timeLeft);
        this.startScan = null;
    }

    private ScanResult doScan(ItemStack stack, World world, EntityPlayer player) {
        Entity pointed = EntityUtils.getPointedEntity(world, player, 10.0D, null);
        if (pointed != null) {
            ScanResult result = new ScanResult((byte)2, 0, 0, pointed, "");
            return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
        }

        RayTraceResult hit = this.rayTrace(world, player, true);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof INode) {
                INode node = (INode) tile;
                String id = node.getId();
                if (id == null || id.isEmpty()) {
                    id = world.provider.getDimension() + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
                }
                ScanResult result = new ScanResult((byte)3, 0, 0, null, "NODE" + id);
                return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
            }

            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            ItemStack target = ItemStack.EMPTY;
            try {
                target = block.getPickBlock(state, hit, world, pos, player);
            } catch (Exception ignored) {
            }
            if (target == null || target.isEmpty()) {
                target = BlockUtils.createStackedBlock(block, meta);
            }
            if (!target.isEmpty()) {
                ScanResult result = new ScanResult((byte)1, Item.getIdFromItem(target.getItem()), target.getMetadata(), null, "");
                return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
            }
        }

        for (IScanEventHandler handler : ThaumcraftApi.scanEventhandlers) {
            ScanResult result = handler.scanPhenomena(stack, world, player);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}

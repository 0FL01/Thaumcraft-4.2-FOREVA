package thaumcraft.common.items.relics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.CreativeTabThaumcraft;

public class ItemThaumometer extends Item {
    private static final long DEBUG_LOG_INTERVAL_MS = 1500L;
    private static long lastFallbackDebugLogMs = 0L;
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
            this.startScan = doActiveScan(stack, world, player, true);
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

        ScanResult current = doActiveScan(stack, world, player, false);
        if (this.startScan != null && current != null && current.equals(this.startScan)) {
            if (count <= 5) {
                this.startScan = null;
                player.stopActiveHand();
                boolean completedClientSide = ScanManager.completeScan(player, current, "@");
                if (completedClientSide || isNodeScan(current)) {
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

    private ScanResult doActiveScan(ItemStack stack, World world, EntityPlayer player, boolean notifyInvalid) {
        ScanResult result = this.findRawScanTarget(stack, world, player);
        if (result == null || !ScanManager.isValidScanTarget(player, result, "@")) {
            return null;
        }
        thaumcraft.api.aspects.AspectList aspects = ScanManager.getScanAspects(result, world);
        if (!ScanManager.validScan(aspects, player)) {
            if (isNodeScan(result)) {
                this.showScanFeedback(world, player, result);
                return result;
            }
            if (notifyInvalid) {
                ScanManager.notifyInvalidScan(aspects, player);
            }
            return null;
        }
        this.showScanFeedback(world, player, result);
        return result;
    }

    private static boolean isNodeScan(ScanResult result) {
        return result != null && result.type == 3 && result.phenomena != null && result.phenomena.startsWith("NODE");
    }

    public ScanResult findScanTarget(ItemStack stack, World world, EntityPlayer player) {
        ScanResult result = this.findRawScanTarget(stack, world, player);
        return ScanManager.isValidScanTarget(player, result, "@") ? result : null;
    }

    public ScanResult findRawScanTarget(ItemStack stack, World world, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || world == null || player == null) {
            return null;
        }

        Entity pointed = EntityUtils.getPointedEntity(world, player, 0.5D, 10.0D, 0.0F, true);
        if (pointed != null) {
            return new ScanResult((byte)2, 0, 0, pointed, "");
        }

        RayTraceResult hit = this.rayTrace(world, player, true);
        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof INode) {
                return createNodeScanResult((INode) tile, world, pos);
            }
        }

        TileEntity lookedAtNode = findLookedAtNodeTile(world, player, 10.0D);
        if (lookedAtNode instanceof INode) {
            return createNodeScanResult((INode) lookedAtNode, world, lookedAtNode.getPos());
        }

        if (hit != null && hit.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);

            // Try candidates in order, returning first one with actual aspects
            ScanResult result = null;

            // 1. Pick block (what you'd get in creative)
            try {
                result = toTaggedItemScan(block.getPickBlock(state, hit, world, pos, player), world);
            } catch (Exception ignored) {}
            if (result != null) return result;

            // 2. Damage-dropped meta (what you'd get from breaking)
            int dropMeta = meta;
            try {
                dropMeta = block.damageDropped(state);
            } catch (Exception ignored) {}
            result = toTaggedItemScan(BlockUtils.createStackedBlock(block, dropMeta), world);
            if (result != null) return result;

            // 3. Raw meta (if different from damage-dropped)
            if (dropMeta != meta) {
                result = toTaggedItemScan(BlockUtils.createStackedBlock(block, meta), world);
                if (result != null) return result;
            }

            // 4. Explicit fluid fallback (bucket items).
            // Tag phenomena so the renderer can show "Water" / "Lava" instead of bucket names.
            if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                result = toTaggedItemScan(new ItemStack(Items.WATER_BUCKET), world);
                if (result != null) {
                    result.phenomena = "FLUID_WATER";
                    return result;
                }
            }
            if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                result = toTaggedItemScan(new ItemStack(Items.LAVA_BUCKET), world);
                if (result != null) {
                    result.phenomena = "FLUID_LAVA";
                    return result;
                }
            }
        }

        for (IScanEventHandler handler : ThaumcraftApi.scanEventhandlers) {
            ScanResult result = handler.scanPhenomena(stack, world, player);
            if (result != null) {
                logFallbackDebug(player, stack, result, handler);
                return result;
            }
        }
        return null;
    }

    private void showScanFeedback(World world, EntityPlayer player, ScanResult result) {
        if (!world.isRemote || player == null || result == null) {
            return;
        }

        float red = 0.3F + world.rand.nextFloat() * 0.7F;
        float blue = 0.3F + world.rand.nextFloat() * 0.7F;
        if (result.type == 2 && result.entity != null) {
            Entity entity = result.entity;
            Thaumcraft.proxy.blockRunes(
                    world,
                    entity.posX - 0.5D,
                    entity.posY + (double) (entity.getEyeHeight() / 2.0F),
                    entity.posZ - 0.5D,
                    red,
                    0.0F,
                    blue,
                    (int) (entity.height * 15.0F),
                    0.03F);
            return;
        }

        if (result.type == 3 && result.phenomena != null && result.phenomena.startsWith("NODE")) {
            TileEntity nodeTile = findLookedAtNodeTile(world, player, 10.0D);
            if (nodeTile instanceof INode) {
                BlockPos pos = nodeTile.getPos();
                Thaumcraft.proxy.blockRunes(
                        world,
                        pos.getX(),
                        pos.getY() + 0.25D,
                        pos.getZ(),
                        red,
                        0.0F,
                        blue,
                        15,
                        0.03F);
                return;
            }
        }

        RayTraceResult hit = this.rayTrace(world, player, true);
        if (hit == null || hit.typeOfHit != RayTraceResult.Type.BLOCK) {
            return;
        }

        BlockPos pos = hit.getBlockPos();
        Thaumcraft.proxy.blockRunes(
                world,
                pos.getX(),
                pos.getY() + 0.25D,
                pos.getZ(),
                red,
                0.0F,
                blue,
                15,
                0.03F);
    }

    public static TileEntity findLookedAtNodeTile(World world, EntityPlayer player, double range) {
        if (world == null || player == null || range <= 0.0D) {
            return null;
        }

        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F).normalize();
        Vec3d end = eyes.add(look.x * range, look.y * range, look.z * range);
        RayTraceResult blockHit = world.rayTraceBlocks(eyes, end, false, true, false);
        double maxDistance = range;
        if (blockHit != null && blockHit.hitVec != null) {
            maxDistance = Math.min(range, eyes.distanceTo(blockHit.hitVec) + 0.25D);
        }

        BlockPos min = new BlockPos(
                Math.min(eyes.x, end.x) - 1.0D,
                Math.min(eyes.y, end.y) - 1.0D,
                Math.min(eyes.z, end.z) - 1.0D);
        BlockPos max = new BlockPos(
                Math.max(eyes.x, end.x) + 1.0D,
                Math.max(eyes.y, end.y) + 1.0D,
                Math.max(eyes.z, end.z) + 1.0D);

        TileEntity closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.getAllInBoxMutable(min, max)) {
            if (!world.isBlockLoaded(pos)) {
                continue;
            }
            TileEntity tile = world.getTileEntity(pos);
            if (!(tile instanceof INode)) {
                continue;
            }
            AxisAlignedBB scanBox = new AxisAlignedBB(
                    pos.getX() + 0.2D, pos.getY() + 0.2D, pos.getZ() + 0.2D,
                    pos.getX() + 0.8D, pos.getY() + 0.8D, pos.getZ() + 0.8D);
            RayTraceResult intercept = scanBox.calculateIntercept(eyes, end);
            if (intercept == null || intercept.hitVec == null) {
                continue;
            }
            double distance = eyes.distanceTo(intercept.hitVec);
            if (distance > maxDistance || distance >= closestDistance) {
                continue;
            }
            closest = tile;
            closestDistance = distance;
        }
        return closest;
    }

    private static ScanResult createNodeScanResult(INode node, World world, BlockPos pos) {
        String id = node.getId();
        if ((id == null || id.isEmpty()) && world != null && pos != null) {
            id = world.provider.getDimension() + ":" + pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
        }
        return new ScanResult((byte)3, 0, 0, null, "NODE" + id);
    }

    /**
     * Creates a ScanResult for an ItemStack only if it has non-empty aspects.
     * Returns null if the stack is empty or has no registered aspects.
     */
    private static ScanResult toTaggedItemScan(ItemStack target, World world) {
        if (target == null || target.isEmpty()) {
            return null;
        }
        ScanResult result = new ScanResult(
                (byte) 1,
                Item.getIdFromItem(target.getItem()),
                target.getMetadata(),
                null,
                ""
        );
        return ScanManager.getScanAspects(result, world).size() > 0 ? result : null;
    }

    private static void logFallbackDebug(EntityPlayer player, ItemStack heldStack, ScanResult result, IScanEventHandler handler) {
        long now = System.currentTimeMillis();
        if (now - lastFallbackDebugLogMs < DEBUG_LOG_INTERVAL_MS) {
            return;
        }
        lastFallbackDebugLogMs = now;

        String heldName = heldStack.isEmpty() ? "<empty>" : heldStack.getItem().getRegistryName() + ":" + heldStack.getMetadata();
        String target = "type=" + result.type + ",id=" + result.id + ",meta=" + result.meta + ",phenomena=" + result.phenomena;
        if (result.entity != null) {
            target += ",entity=" + result.entity.getName() + "#" + result.entity.getEntityId();
        }
        Thaumcraft.log.info("[ThaumometerDebug] Fallback scanEventhandler {} produced raw target for player {} while holding {} -> {}",
                handler.getClass().getName(),
                player == null ? "<null>" : player.getName(),
                heldName,
                target);
    }
}

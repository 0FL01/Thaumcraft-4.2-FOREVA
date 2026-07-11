package thaumcraft.common.lib.events;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.world.ChunkLoc;
import thaumcraft.common.tiles.TileSensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

public class ServerTickEventsFML {

    private static final Logger LOGGER = LogManager.getLogger(Thaumcraft.MODID);

    // Queued block swap operations (dim -> queue)
    public static Map<Integer, Queue<VirtualSwapper>> swapList = new HashMap<>();

    // Queued chunk regeneration (dim -> chunk list)
    public static HashMap<Integer, ArrayList<ChunkLoc>> chunksToGenerate = new HashMap<>();

    @SubscribeEvent
    public void serverWorldTick(TickEvent.WorldTickEvent event) {
        if (event.side == Side.CLIENT) return;
        if (event.phase != TickEvent.Phase.END) return;

        World world = event.world;

        tickChunkRegeneration(world);
        tickBlockSwap(world);
        if (world instanceof WorldServer) {
            ArrayList<Integer[]> events = TileSensor.noteBlockEvents.get((WorldServer) world);
            if (events != null) {
                events.clear();
            }
        }
    }

    /**
     * Process queued world gen regeneration (used by Eldritch dimension
     * and retrogen features).
     */
    private void tickChunkRegeneration(World world) {
        int dim = world.provider.getDimension();
        ArrayList<ChunkLoc> chunks = chunksToGenerate.get(dim);

        if (chunks != null && !chunks.isEmpty()) {
            int processed = 0;
            for (int a = 0; a < 10 && !chunks.isEmpty(); a++) {
                ChunkLoc loc = chunks.remove(0);
                long worldSeed = world.getSeed();
                Random fmlRandom = new Random(worldSeed);
                long xSeed = fmlRandom.nextLong() >> 3;
                long zSeed = fmlRandom.nextLong() >> 3;
                fmlRandom.setSeed(xSeed * (long) loc.x + zSeed * (long) loc.z ^ worldSeed);

                if (Thaumcraft.instance != null && Thaumcraft.instance.worldGen != null) {
                    Thaumcraft.instance.worldGen.worldGeneration(fmlRandom, loc.x, loc.z, world, false);
                }
                processed++;
            }
            chunksToGenerate.put(dim, chunks);

            if (processed > 0) {
                LOGGER.info("[Thaumcraft] Processed chunk regeneration queue: {} chunks regenerated in dim {}", processed, dim);
            }
        }
    }

    /**
     * Process queued block swap operations (from Portable Hole focus, etc.)
     */
    private void tickBlockSwap(World world) {
        int dim = world.provider.getDimension();
        Queue<VirtualSwapper> queue = swapList.get(dim);

        if (queue == null || queue.isEmpty()) return;

        boolean didSomething = false;
        while (!didSomething) {
            VirtualSwapper vs = queue.poll();
            if (vs == null) {
                didSomething = true;
                break;
            }

            BlockPos pos = new BlockPos(vs.x, vs.y, vs.z);
            IBlockState sourceState = world.getBlockState(pos);
            Block currentBlock = sourceState.getBlock();
            int currentMeta = currentBlock.getMetaFromState(sourceState);

            ItemWandCasting wand = null;
            ItemFocusBasic focus = null;
            ItemStack focusStack = null;

            if (vs.player != null
                    && !vs.player.inventory.getStackInSlot(vs.wand).isEmpty()
                    && vs.player.inventory.getStackInSlot(vs.wand).getItem() instanceof ItemWandCasting) {
                ItemStack wandStack = vs.player.inventory.getStackInSlot(vs.wand);
                wand = (ItemWandCasting) wandStack.getItem();
                focusStack = wand.getFocusItem(wandStack);
                focus = wand.getFocus(wandStack);
            }

            if (!world.isBlockModifiable(vs.player, pos)) continue;
            if (vs.target.isItemEqual(new ItemStack(currentBlock, 1, currentMeta))) continue;
            if (wand == null || focus == null) continue;
            if (!wand.consumeAllVis(vs.player.inventory.getStackInSlot(vs.wand), vs.player,
                    focus.getVisCost(focusStack), false, false)) continue;

            int slot = InventoryUtils.isPlayerCarrying(vs.player, vs.target);
            if (vs.player.capabilities.isCreativeMode) {
                slot = 1;
            }
            if (vs.bSource != currentBlock || vs.mSource != currentMeta || slot < 0) continue;

            Block targetBlock = Block.getBlockFromItem(vs.target.getItem());
            if (targetBlock == Blocks.AIR) continue;
            IBlockState targetState;
            try {
                targetState = targetBlock.getStateFromMeta(vs.target.getItemDamage());
            } catch (RuntimeException ignored) {
                continue;
            }

            int fortune = 0;
            boolean silk = false;
            NonNullList<ItemStack> drops = NonNullList.create();
            if (!vs.player.capabilities.isCreativeMode) {
                fortune = wand.getFocusTreasure(vs.player.inventory.getStackInSlot(vs.wand));
                silk = focus.isUpgradedWith(focusStack, FocusUpgradeType.silktouch);
                if (silk && currentBlock.canSilkHarvest(world, pos, sourceState, vs.player)) {
                    ItemStack silked = BlockUtils.createStackedBlock(currentBlock, currentMeta);
                    if (!silked.isEmpty()) drops.add(silked);
                } else {
                    currentBlock.getDrops(drops, world, pos, sourceState, fortune);
                }
            }

            // Do not charge the player or award drops unless the replacement actually succeeded.
            if (!world.setBlockState(pos, targetState, 3)) continue;

            didSomething = true;

            if (!vs.player.capabilities.isCreativeMode) {
                vs.player.inventory.decrStackSize(slot, 1);

                if (!drops.isEmpty()) {
                    for (ItemStack is : drops) {
                        if (!vs.player.inventory.addItemStackToInventory(is)) {
                            world.spawnEntity(new EntityItem(world,
                                    (double) vs.x + 0.5, (double) vs.y + 0.5, (double) vs.z + 0.5, is));
                        }
                    }
                }

                wand.consumeAllVis(vs.player.inventory.getStackInSlot(vs.wand), vs.player,
                        focus.getVisCost(focusStack), true, false);
            }

            targetBlock.onBlockPlacedBy(world, pos, targetState, (EntityLivingBase) vs.player, vs.target);

            PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockSparkle(vs.x, vs.y, vs.z, 0xC0C0FF),
                    new NetworkRegistry.TargetPoint(world.provider.getDimension(), vs.x, vs.y, vs.z, 32.0));

            world.playEvent(2001, pos, Block.getStateId(sourceState));

            // Chain to adjacent blocks (for Portable Hole area effect)
            if (vs.lifespan > 0) {
                for (int xx = -1; xx <= 1; xx++) {
                    for (int yy = -1; yy <= 1; yy++) {
                        for (int zz = -1; zz <= 1; zz++) {
                            if (xx == 0 && yy == 0 && zz == 0) continue;
                            BlockPos np = new BlockPos(vs.x + xx, vs.y + yy, vs.z + zz);
                            Block adjBlock = world.getBlockState(np).getBlock();
                            int adjMeta = adjBlock.getMetaFromState(world.getBlockState(np));
                            if (adjBlock == vs.bSource && adjMeta == vs.mSource
                                    && BlockUtils.isBlockExposed(world, np.getX(), np.getY(), np.getZ())) {
                                queue.offer(new VirtualSwapper(vs.x + xx, vs.y + yy, vs.z + zz,
                                        vs.bSource, vs.mSource, vs.target,
                                        vs.lifespan - 1, vs.player, vs.wand));
                            }
                        }
                    }
                }
            }
        }
        swapList.put(dim, queue);
    }

    /**
     * Add a block to the swap queue (from Portable Hole focus, etc.)
     */
    public static void addSwapper(World world, int x, int y, int z, Block bs, int ms, ItemStack target,
                                  int life, EntityPlayer player, int wand) {
        int dim = world.provider.getDimension();
        if (target.isEmpty() || Block.getBlockFromItem(target.getItem()) == Blocks.AIR) return;
        // Don't swap air or unbreakable blocks
        BlockPos pos = new BlockPos(x, y, z);
        if (bs == Blocks.AIR || bs.getBlockHardness(world.getBlockState(pos), world, pos) < 0.0f) {
            return;
        }
        // Don't swap if same block type
        if (target.isItemEqual(new ItemStack(bs, 1, ms))) return;

        Queue<VirtualSwapper> queue = swapList.get(dim);
        if (queue == null) {
            queue = new LinkedList<>();
            swapList.put(dim, queue);
        }
        queue.offer(new VirtualSwapper(x, y, z, bs, ms, target, life, player, wand));

        world.playSound(null, pos, TCSounds.WAND, SoundCategory.PLAYERS, 0.25f, 1.0f);
    }

    // ---- Inner classes ----

    /**
     * Stores block data before it is replaced (for warded blocks restoration).
     */
    public static class RestorableWardedBlock {
        public int x;
        public int y;
        public int z;
        public Block bi;
        public int md;
        public NBTTagCompound nbt;

        public RestorableWardedBlock(World world, int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
            net.minecraft.util.math.BlockPos pos = new net.minecraft.util.math.BlockPos(x, y, z);
            this.bi = world.getBlockState(pos).getBlock();
            this.md = bi.getMetaFromState(world.getBlockState(pos));
            TileEntity te = world.getTileEntity(pos);
            if (te != null) {
                this.nbt = new NBTTagCompound();
                te.writeToNBT(this.nbt);
            }
        }
    }

    /**
     * Represents a queued block swap operation.
     */
    public static class VirtualSwapper {
        public int lifespan = 0;
        public int x;
        public int y;
        public int z;
        public Block bSource;
        public int mSource = 0;
        public ItemStack target;
        public int wand = 0;
        public EntityPlayer player;

        public VirtualSwapper(int x, int y, int z, Block bs, int ms, ItemStack t, int life, EntityPlayer p, int wand) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.bSource = bs;
            this.mSource = ms;
            this.target = t;
            this.lifespan = life;
            this.player = p;
            this.wand = wand;
        }
    }
}

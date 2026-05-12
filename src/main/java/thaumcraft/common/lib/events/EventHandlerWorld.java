package thaumcraft.common.lib.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.world.ChunkLoc;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.tiles.TileSensor;

import java.util.ArrayList;
import java.util.List;

public class EventHandlerWorld {

    private static final Logger LOGGER = LogManager.getLogger(Thaumcraft.MODID);

    // ---- World lifecycle ----

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            MazeHandler.loadMaze(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0) {
            MazeHandler.saveMaze(event.getWorld());
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) return;

        VisNetHandler.sources.remove(event.getWorld().provider.getDimension());
        // Phase 8: TileSensor cleanup
        // try {
        //     TileSensor.noteBlockEvents.remove((WorldServer)event.getWorld());
        // } catch (Exception e) {
        //     LOGGER.warn("[Thaumcraft] Error unloading noteblock event handlers.", e);
        // }
    }

    // ---- Chunk data persistence for retrogen ----

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        NBTTagCompound thaumcraftData = new NBTTagCompound();
        event.getData().setTag("Thaumcraft", thaumcraftData);
        thaumcraftData.setBoolean(Config.regenKey, true);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        int dim = event.getWorld().provider.getDimension();
        net.minecraft.world.chunk.Chunk chunk = event.getChunk();
        NBTTagCompound thaumcraftData = event.getData().getCompoundTag("Thaumcraft");

        if (!thaumcraftData.getBoolean(Config.regenKey)
                && (Config.regenAmber || Config.regenAura || Config.regenCinnibar
                || Config.regenInfusedStone || Config.regenStructure || Config.regenTrees)) {
            LOGGER.warn("[Thaumcraft] World gen was never run for chunk at {} in dim {}. Adding to queue for regeneration.",
                    chunk.getPos(), dim);

            ArrayList<ChunkLoc> chunks = ServerTickEventsFML.chunksToGenerate.get(dim);
            if (chunks == null) {
                ServerTickEventsFML.chunksToGenerate.put(dim, new ArrayList<>());
                chunks = ServerTickEventsFML.chunksToGenerate.get(dim);
            }
            if (chunks != null) {
                chunks.add(new ChunkLoc(chunk.x, chunk.z));
                ServerTickEventsFML.chunksToGenerate.put(dim, chunks);
            }
        }
    }

    // ---- Crafting ----

    @SubscribeEvent
    public void onItemCrafted(net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent event) {
        if (event.player.world.isRemote) return;

        // Apply warp on craft
        int warp = ThaumcraftApi.getWarp(event.crafting);
        if (!Config.wuss && warp > 0) {
            Thaumcraft.addStickyWarpToPlayer(event.player, warp);
        }

        // Phase 8: special item handling (alumentum return, arcane bellows return)
        // if (event.crafting.getItem() == ConfigItems.itemResource && event.crafting.getItemDamage() == 13 ...) { ... }
    }

    // ---- Harvest drops ----

    @SubscribeEvent
    public void onHarvestDrops(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player = event.getHarvester();
        if (event.getDrops() == null || event.getDrops().isEmpty() || player == null) return;

        ItemStack held = player.getHeldItemMainhand();
        if (held.isEmpty()) return;

        // Phase 8: Elemental Pickaxe / Primal Crusher / Excavation focus special mining results
        // Future: Utils.findSpecialMiningResult(itemStack, chance, random) replacement
    }

    // ---- Block placement restrictions ----

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onMultiBlockPlace(BlockEvent.MultiPlaceEvent event) {
        if (isNearActiveBoss(event.getWorld(), event.getPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    // ---- Note block events (for TileSensor) ----

    @SubscribeEvent
    public void onNoteBlockPlay(NoteBlockEvent.Play event) {
        if (event.getWorld().isRemote) return;
        // Phase 8: TileSensor note tracking
        // if (!TileSensor.noteBlockEvents.containsKey(event.getWorld())) {
        //     TileSensor.noteBlockEvents.put((WorldServer)event.getWorld(), new ArrayList<>());
        // }
        // TileSensor.noteBlockEvents.get(event.getWorld())
        //         .add(new Integer[]{event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
        //               event.getInstrument().ordinal(), event.getVanillaNoteId()});
    }

    // ---- Bucket filling (custom fluid buckets) ----

    @SubscribeEvent
    public void onFillBucket(FillBucketEvent event) {
        RayTraceResult target = event.getTarget();
        if (target == null || target.typeOfHit != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = target.getBlockPos();
        Block block = event.getWorld().getBlockState(pos).getBlock();
        int meta = block.getMetaFromState(event.getWorld().getBlockState(pos));

        // Phase 8: handle custom fluid bucket fills
        // if (block == ConfigBlocks.blockFluidPure && meta == 0) {
        //     event.getWorld().setBlockToAir(pos);
        //     event.setFilledBucket(new ItemStack(ConfigItems.itemBucketPure));
        //     event.setResult(Event.Result.ALLOW);
        // } else if (block == ConfigBlocks.blockFluidDeath && meta == 3) {
        //     event.getWorld().setBlockToAir(pos);
        //     event.setFilledBucket(new ItemStack(ConfigItems.itemBucketDeath));
        //     event.setResult(Event.Result.ALLOW);
        // }
    }

    // ---- Fuel burning ----

    /**
     * Override getItemBurnTime on Thaumcraft items for fuel.
     * In 1.12.2, IFuelHandler is deprecated. Items override getItemBurnTime() directly.
     * This central handler catches FurnaceFuelBurnTimeEvent as a fallback.
     */
    // @SubscribeEvent
    // public void onFuelBurnTime(net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent event) {
    //     ItemStack stack = event.getItemStack();
    //     if (stack.getItem() == ConfigItems.itemResource && stack.getItemDamage() == 0) {
    //         event.setBurnTime(6400); // Thaumium nugget = 6400 ticks (~320 items)
    //     } else if (stack.getItem() == Item.getItemFromBlock(ConfigBlocks.blockMagicalLog)) {
    //         event.setBurnTime(400); // Magical log = 400 ticks (20 items)
    //     }
    // }

    // ---- Helpers ----

    /**
     * Check if a block position is near an active boss in the Eldritch dimension.
     * Used to prevent block placement during boss fights.
     */
    private boolean isNearActiveBoss(World world, EntityPlayer player, BlockPos pos) {
        if (world.provider.getDimension() != net.minecraftforge.common.DimensionManager.getProviderType(-42).getId()) return false;

        // Replace -42 check with Config.dimensionOuterId if available
        if (player == null || player.capabilities.isCreativeMode) return false;

        List<Entity> bosses = world.getEntitiesWithinAABB(EntityThaumcraftBoss.class,
                player.getEntityBoundingBox().grow(32.0));

        return !bosses.isEmpty();
    }
}

package thaumcraft.common.lib.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedEntities;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedItems;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedPhenomena;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

public class EventHandlerEntity {

    public static final ResourceLocation PLAYER_KNOWLEDGE_KEY = new ResourceLocation("thaumcraft", "player_knowledge");

    // ---- Capability attachment ----

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityPlayer) {
            PlayerKnowledgeProvider provider = new PlayerKnowledgeProvider();
            provider.getInstance().setPlayer((EntityPlayer) event.getObject());
            event.addCapability(PLAYER_KNOWLEDGE_KEY, provider);
        }
    }

    // ---- Sync on join ----

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            syncAllData(player);
        }
    }

    // ---- Clone on death/return from End ----

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        EntityPlayer original = event.getOriginal();
        EntityPlayer clone = event.getEntityPlayer();

        IPlayerKnowledge oldCap = original.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        IPlayerKnowledge newCap = clone.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);

        if (oldCap != null && newCap != null) {
            newCap.deserializeNBT(oldCap.serializeNBT());
            newCap.setPlayer(clone);
        }

        if (!event.isWasDeath()) {
            // Respawn/End return: also sync
            if (!clone.getEntityWorld().isRemote) {
                syncAllData(clone);
            }
        }
    }

    // ---- Sync helper ----

    public static void syncAllData(EntityPlayer player) {
        if (player.getEntityWorld().isRemote) return;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return;

        PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), (net.minecraft.entity.player.EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(knowledge.getResearchComplete()), (net.minecraft.entity.player.EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedEntities(knowledge.getScannedEntities()), (net.minecraft.entity.player.EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedItems(knowledge.getScannedItems()), (net.minecraft.entity.player.EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedPhenomena(knowledge.getScannedPhenomena()), (net.minecraft.entity.player.EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(knowledge.getWarpPerm(), knowledge.getWarpSticky(), knowledge.getWarpTemp()), (net.minecraft.entity.player.EntityPlayerMP) player);
    }

    // ---- Existing handler stubs ----

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
    }

    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
    }

    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
    }

    @SubscribeEvent
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
    }

    @SubscribeEvent
    public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event) {
    }

    @SubscribeEvent
    public void onPlayerSaveToFile(PlayerEvent.SaveToFile event) {
    }

    @SubscribeEvent
    public void onPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
    }
}

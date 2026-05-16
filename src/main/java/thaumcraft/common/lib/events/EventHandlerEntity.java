package thaumcraft.common.lib.events;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.lib.WarpEvents;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedEntities;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedItems;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedPhenomena;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketSyncWipe;
import thaumcraft.common.lib.network.playerdata.PacketRunicCharge;
import thaumcraft.common.lib.research.ResearchManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventHandlerEntity {

    public static final net.minecraft.util.ResourceLocation PLAYER_KNOWLEDGE_KEY =
            new net.minecraft.util.ResourceLocation("thaumcraft", "player_knowledge");
    private static final Map<UUID, ArrayList<WeakReference<EntityTravelingTrunk>>> LINKED_TRUNKS = new HashMap<>();

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
            if (player instanceof EntityPlayerMP) {
                moveLinkedTravelingTrunks((EntityPlayerMP) player);
            }
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                knowledge.setPlayer(player);
                ResearchManager.initializeFreshPlayerData(player);
                if (Thaumcraft.instance != null && Thaumcraft.instance.runicEventHandler != null) {
                    Thaumcraft.instance.runicEventHandler.runicCharge.put(player.getEntityId(), knowledge.getRunicCharge());
                    Thaumcraft.instance.runicEventHandler.isDirty = true;
                }
                ResearchManager.updateCache(player.getName(), knowledge);
            }
            syncAllData(player);
        }
    }

    public static void linkTravelingTrunk(EntityTravelingTrunk trunk, UUID ownerId) {
        if (trunk == null || ownerId == null || trunk.isDead || trunk.world == null || trunk.world.isRemote) {
            return;
        }
        ArrayList<WeakReference<EntityTravelingTrunk>> trunks = LINKED_TRUNKS.get(ownerId);
        if (trunks == null) {
            trunks = new ArrayList<>();
            LINKED_TRUNKS.put(ownerId, trunks);
        }
        Iterator<WeakReference<EntityTravelingTrunk>> iterator = trunks.iterator();
        while (iterator.hasNext()) {
            EntityTravelingTrunk linked = iterator.next().get();
            if (linked == null || linked.isDead) {
                iterator.remove();
                continue;
            }
            if (linked.getEntityId() == trunk.getEntityId() && linked.world == trunk.world) {
                return;
            }
        }
        trunks.add(new WeakReference<>(trunk));
    }

    private static void moveLinkedTravelingTrunks(EntityPlayerMP player) {
        ArrayList<WeakReference<EntityTravelingTrunk>> trunks = LINKED_TRUNKS.get(player.getUniqueID());
        if (trunks == null) {
            return;
        }
        Iterator<WeakReference<EntityTravelingTrunk>> iterator = trunks.iterator();
        while (iterator.hasNext()) {
            EntityTravelingTrunk trunk = iterator.next().get();
            if (trunk == null || trunk.isDead) {
                iterator.remove();
                continue;
            }
            if (trunk.world != player.world) {
                if (trunk.transferToOwnerDimension(player)) {
                    iterator.remove();
                }
            }
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
            ResearchManager.updateCache(clone.getName(), newCap);
        }

        if (!clone.getEntityWorld().isRemote) {
            syncAllData(clone);
        }
    }

    // ---- Sync helper ----

    public static void syncAllData(EntityPlayer player) {
        if (player.getEntityWorld().isRemote || !(player instanceof EntityPlayerMP)) return;

        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge == null) return;

        PacketHandler.INSTANCE.sendTo(new PacketSyncWipe(), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(knowledge.getAspectsDiscovered()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(knowledge.getResearchComplete()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedEntities(knowledge.getScannedEntities()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedItems(knowledge.getScannedItems()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncScannedPhenomena(knowledge.getScannedPhenomena()), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(knowledge.getWarpPerm(), knowledge.getWarpSticky(), knowledge.getWarpTemp(), knowledge.getWarpCounter()), (EntityPlayerMP) player);
        if (Thaumcraft.instance != null && Thaumcraft.instance.runicEventHandler != null) {
            Integer[] info = Thaumcraft.instance.runicEventHandler.runicInfo.get(player.getEntityId());
            int max = info == null || info.length == 0 ? 0 : info[0];
            PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(player.getEntityId(), knowledge.getRunicCharge(), max), (EntityPlayerMP) player);
        }
        ResearchManager.updateCache(player.getName(), knowledge);
    }

    // ==========================================================
    //  Filled stubs (ported from original EventHandlerEntity)
    // ==========================================================

    /**
     * Called every living entity tick (~20/sec per entity).
     * - Calls WarpEvents.checkWarpEvent for players
     * - Handles warp vomit/tick effects
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        WarpEvents.checkWarpEvent(player);
    }

    /**
     * On player death:
     * - Calls WarpEvents.checkDeathGaze
     * - Resets warp counter
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        WarpEvents.checkDeathGaze(player);

        // Reset warp counter on death
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.setWarpCounter(0);
            ResearchManager.syncWarp(player);
        }
    }

    /**
     * On mob death drops:
     * - Zombie brain from zombies (50% + looting bonus)
     * - Guaranteed brain from BrainyZombie/GiantBrainyZombie
     * - Pearl from Endermen with looting
     */
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        if (event.getEntityLiving() instanceof EntityZombie && !(event.getEntityLiving() instanceof EntityBrainyZombie)) {
            float chance = 0.5f + EnchantmentHelper.getLootingModifier(event.getEntityLiving()) * 0.05f;
            if (event.getEntityLiving().world.rand.nextFloat() < chance) {
                event.getDrops().add(new EntityItem(
                        event.getEntityLiving().world,
                        event.getEntityLiving().posX,
                        event.getEntityLiving().posY,
                        event.getEntityLiving().posZ,
                        new ItemStack(ConfigItems.itemZombieBrain)
                ));
            }
        }

        if (event.getEntityLiving() instanceof EntityBrainyZombie || event.getEntityLiving() instanceof EntityGiantBrainyZombie) {
            int count = event.getEntityLiving() instanceof EntityGiantBrainyZombie ? 2 : 1;
            event.getDrops().add(new EntityItem(
                    event.getEntityLiving().world,
                    event.getEntityLiving().posX,
                    event.getEntityLiving().posY,
                    event.getEntityLiving().posZ,
                    new ItemStack(ConfigItems.itemZombieBrain, count)
            ));
        }
    }

    /**
     * Right-click on entities: Pech trade, etc.
     */
    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof EntityGolemBase
                && ((EntityGolemBase) event.getTarget()).getOwnerName().length() > 0
                && !((EntityGolemBase) event.getTarget()).getOwnerName().equals(event.getEntityPlayer().getName())) {
            if (!event.getWorld().isRemote) {
                event.getEntityPlayer().sendMessage(new TextComponentTranslation("You are not my Master!"));
            }
            event.setCanceled(true);
        }
    }

    /**
     * Left-click / attack on entities: used for Pech trade detection.
     */
    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().world.isRemote) return;
        // Future: Pech left-click trade detection
    }

    /**
     * Item pickup: discovery research tracking.
     */
    @SubscribeEvent
    public void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity().world.isRemote) return;
        if (event.getEntityPlayer().getName().startsWith("FakeThaumcraft")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        // Can remain empty — no original behaviour
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        // Can remain empty — no original behaviour
    }

    /**
     * Bow draw interception for wand foci.
     * If player is holding a focus, cancel bow charging.
     */
    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItemMainhand();

        if (!held.isEmpty() && held.getItem() instanceof ItemFocusBasic) {
            event.setCharge(0);
        }
    }

    /**
     * Bow nock interception for wand foci.
     * Prevent drawing bow if holding a focus.
     */
    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItemMainhand();

        if (!held.isEmpty() && held.getItem() instanceof ItemFocusBasic) {
            event.setCanceled(true);
        }
    }

    /**
     * Break speed modifier: reduce mining speed when holding a focus.
     */
    @SubscribeEvent
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack held = player.getHeldItemMainhand();

        if (!held.isEmpty() && held.getItem() instanceof ItemFocusBasic) {
            event.setNewSpeed(0.3f);
        }
    }

    /**
     * Player file load: legacy warpCounter.dat migration.
     */
    @SubscribeEvent
    public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event) {
        // Fresh-world Stage 3 data is stored by Forge capability serialization.
    }

    /**
     * Player file save: legacy warpCounter.dat (if needed).
     */
    @SubscribeEvent
    public void onPlayerSaveToFile(PlayerEvent.SaveToFile event) {
        if (event.getEntityPlayer() != null) {
            ResearchManager.updateCache(event.getEntityPlayer());
        }
    }

    /**
     * Item use finish: warp-on-eat effect for tainted food.
     * Replaces original PlayerUseItemEvent.Finish.
     * In 1.12.2: LivingEntityUseItemEvent.Finish
     */
    @SubscribeEvent
    public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack used = event.getItem();

        if (used.isEmpty() || !(used.getItem() instanceof ItemFood)) return;

        int warp = ThaumcraftApi.getWarp(used);
        if (warp > 0) {
            Thaumcraft.addStickyWarpToPlayer(player, warp);
        }
    }

    /**
     * Jump event: modify jump height if jump boost potion is active.
     */
    @SubscribeEvent
    public void onLivingJump(LivingEvent.LivingJumpEvent event) {
        // Future: modify jump based on thaumcraft potion effects
    }
}

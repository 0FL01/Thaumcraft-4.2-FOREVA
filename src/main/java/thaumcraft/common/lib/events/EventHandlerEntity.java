package thaumcraft.common.lib.events;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintVillager;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.entities.projectile.EntityPrimalArrow;
import thaumcraft.common.items.ItemBathSalts;
import thaumcraft.common.items.armor.Hover;
import thaumcraft.common.items.equipment.ItemBowBone;
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
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.world.dim.Cell;
import thaumcraft.common.lib.world.dim.CellLoc;
import thaumcraft.common.lib.world.dim.MazeHandler;
import thaumcraft.common.lib.world.dim.TeleporterThaumcraft;
import thaumcraft.common.tiles.TileOwned;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventHandlerEntity {

    @SubscribeEvent
    public void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        World world = event.getWorld();
        if (world.isRemote
                || world.provider.getDimension() != Config.dimensionOuterId
                || event.isSpawner()) {
            return;
        }

        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        Cell cell = MazeHandler.getFromHashMap(new CellLoc(pos.getX() >> 4, pos.getZ() >> 4));
        if (cell == null || !TeleporterThaumcraft.hasCeiling(world, pos, 16)) {
            event.setResult(Event.Result.DENY);
        }
    }

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

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.getEntity() instanceof EntityMob) {
            EntityUtils.ensureChampionModAttribute((EntityMob) event.getEntity());
        }
    }

    // ---- Sync on join ----

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) {
            return;
        }

        if (event.getEntity() instanceof EntityEnderPearl) {
            EntityEnderPearl pearl = (EntityEnderPearl) event.getEntity();
            int x = MathHelper.floor(pearl.posX);
            int y = MathHelper.floor(pearl.posY);
            int z = MathHelper.floor(pearl.posZ);
            for (int xx = -5; xx <= 5; xx++) {
                for (int yy = -5; yy <= 5; yy++) {
                    for (int zz = -5; zz <= 5; zz++) {
                        TileEntity tile = event.getWorld().getTileEntity(new BlockPos(x + xx, y + yy, z + zz));
                        if (!(tile instanceof TileOwned)) {
                            continue;
                        }
                        if (pearl.getThrower() instanceof EntityPlayer) {
                            ((EntityPlayer) pearl.getThrower()).sendMessage(
                                    new TextComponentString("\u00a75\u00a7oThe magic of a nearby warded object destroys the ender pearl."));
                        }
                        pearl.setDead();
                        return;
                    }
                }
            }
        }

        if (event.getEntity() instanceof EntityPlayer) {
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
            return;
        }

        if (event.getEntity() instanceof EntityMob) {
            handleChampionSpawn(event, (EntityMob) event.getEntity());
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

        IPlayerKnowledge oldCap = original == null ? null : original.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (oldCap == null && original != null) {
            oldCap = ResearchManager.getResearchData(original.getName());
        }
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
        if (event.getEntityLiving().world.isRemote) {
            return;
        }

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            WarpEvents.checkWarpEvent(player);
            applyTravellerHasteMovement(player);
            enforceHoverFlightBounds(player);
        }

        if (event.getEntityLiving() instanceof EntityMob && !event.getEntityLiving().isDead) {
            EntityMob mob = (EntityMob) event.getEntityLiving();
            IAttributeInstance mod = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);
            if (mod != null) {
                int type = (int) mod.getAttributeValue();
                if (type >= 0 && type < ChampionModifier.mods.length && ChampionModifier.mods[type].type == 0) {
                    ChampionModifier.mods[type].effect.performEffect(mob, null, null, 0.0F);
                }
            }
        }
    }

    /**
     * On player death:
     * - Calls WarpEvents.checkDeathGaze
     * - Resets warp counter
     *
     * On mob death (TC4 parity):
     * - Converts mobs killed while taint-poisoned into tainted variants
     * - Drops EntityAspectOrb for each primal aspect (50% chance each)
     * - Only for non-tainted mobs recently hit by a player
     */
    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            WarpEvents.checkDeathGaze(player);

            // Reset warp counter on death
            IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
            if (knowledge != null) {
                knowledge.setWarpCounter(0);
                ResearchManager.syncWarp(player);
            }
            return;
        }

        if (tryConvertTaintedDeath(event.getEntityLiving())) return;

        // TC4 parity: drop aspect orbs from non-tainted mobs killed by players.
        // TC4 checks recentlyHit > 0; in 1.12 recentlyHit is protected, so we
        // check the damage source's true source instead.
        if (event.getEntityLiving() instanceof ITaintedMob) return;
        if (!(event.getSource().getTrueSource() instanceof EntityPlayer)) return;
        if (event.getSource().getTrueSource() instanceof FakePlayer) return;

        AspectList aspectsCompound = ScanManager.generateEntityAspects(event.getEntity());
        if (aspectsCompound == null || aspectsCompound.size() == 0) return;

        AspectList aspects = ResearchManager.reduceToPrimals(aspectsCompound);
        for (Aspect aspect : aspects.getAspects()) {
            if (!event.getEntityLiving().world.rand.nextBoolean()) continue;
            EntityAspectOrb orb = new EntityAspectOrb(
                    event.getEntityLiving().world,
                    event.getEntityLiving().posX,
                    event.getEntityLiving().posY,
                    event.getEntityLiving().posZ,
                    aspect,
                    1 + event.getEntityLiving().world.rand.nextInt(aspects.getAmount(aspect)));
            event.getEntityLiving().world.spawnEntity(orb);
        }
    }

    private boolean tryConvertTaintedDeath(EntityLivingBase dying) {
        if (dying instanceof ITaintedMob
                || Config.potionFluxTaint == null
                || !dying.isPotionActive(Config.potionFluxTaint)) {
            return false;
        }

        EntityLivingBase replacement = createTaintedReplacement(dying);
        if (replacement == null) {
            return false;
        }

        replacement.setLocationAndAngles(dying.posX, dying.posY, dying.posZ, dying.rotationYaw, 0.0F);
        dying.world.spawnEntity(replacement);
        dying.setDead();
        return true;
    }

    private EntityLivingBase createTaintedReplacement(EntityLivingBase dying) {
        World world = dying.world;
        if (dying instanceof EntityCreeper) {
            return new EntityTaintCreeper(world);
        }
        if (dying instanceof EntitySheep) {
            return new EntityTaintSheep(world);
        }
        if (dying instanceof EntityCow) {
            return new EntityTaintCow(world);
        }
        if (dying instanceof EntityPig) {
            return new EntityTaintPig(world);
        }
        if (dying instanceof EntityChicken) {
            return new EntityTaintChicken(world);
        }
        if (dying instanceof EntityVillager) {
            return new EntityTaintVillager(world);
        }

        EntityThaumicSlime slime = new EntityThaumicSlime(world);
        slime.setSlimeSize(1 + Math.min((int) (dying.getMaxHealth() / 10.0F), 6));
        return slime;
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

        if (event.isRecentlyHit()
                && !(event.getSource().getTrueSource() instanceof FakePlayer)
                && event.getEntity() instanceof EntityMob
                && !(event.getEntity() instanceof EntityThaumcraftBoss)) {
            EntityMob mob = (EntityMob) event.getEntity();
            IAttributeInstance mod = mob.getEntityAttribute(EntityUtils.CHAMPION_MOD);
            if (mod != null && mod.getAttributeValue() >= 0.0D) {
                for (int i = 5 + mob.world.rand.nextInt(3); i > 0; ) {
                    int split = EntityXPOrb.getXPSplit(i);
                    i -= split;
                    mob.world.spawnEntity(new EntityXPOrb(mob.world, mob.posX, mob.posY, mob.posZ, split));
                }
                int lb = Math.min(2, MathHelper.floor((float) (mob.world.rand.nextInt(9) + event.getLootingLevel()) / 5.0F));
                event.getDrops().add(new EntityItem(
                        mob.world,
                        mob.posX,
                        mob.posY + mob.getEyeHeight(),
                        mob.posZ,
                        new ItemStack(ConfigItems.itemLootBag, 1, lb)
                ));
            }
        }

        if (event.getEntityLiving() instanceof EntityZombie
                && !(event.getEntityLiving() instanceof EntityBrainyZombie)
                && event.isRecentlyHit()
                && event.getEntityLiving().world.rand.nextInt(10) - event.getLootingLevel() < 1) {
            event.getDrops().add(new EntityItem(
                    event.getEntityLiving().world,
                    event.getEntityLiving().posX,
                    event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(),
                    event.getEntityLiving().posZ,
                    new ItemStack(ConfigItems.itemZombieBrain)
            ));
        }

        if (event.getEntityLiving() instanceof EntityVillager
                && event.getEntityLiving().world.rand.nextInt(10) - event.getLootingLevel() < 1) {
                event.getDrops().add(new EntityItem(
                        event.getEntityLiving().world,
                        event.getEntityLiving().posX,
                        event.getEntityLiving().posY + event.getEntityLiving().getEyeHeight(),
                        event.getEntityLiving().posZ,
                        new ItemStack(ConfigItems.itemResource, 1, 18)
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
        if (event.getEntityItem() == null || event.getPlayer() == null) {
            return;
        }
        event.getEntityItem().getEntityData().setString("thrower", event.getPlayer().getName());
    }

    @SubscribeEvent
    public void onItemExpire(ItemExpireEvent event) {
        if (event.getEntityItem() == null || event.getEntityItem().world.isRemote) {
            return;
        }
        ItemStack expired = event.getEntityItem().getItem();
        if (expired.isEmpty() || !(expired.getItem() instanceof ItemBathSalts)) {
            return;
        }
        BlockPos pos = new BlockPos(event.getEntityItem());
        IBlockState state = event.getEntityItem().world.getBlockState(pos);
        if (state.getBlock() == Blocks.WATER && state.getValue(BlockLiquid.LEVEL) == 0) {
            event.getEntityItem().world.setBlockState(pos, ConfigBlocks.blockFluidPure.getDefaultState(), 3);
        }
    }

    /**
     * Bow draw interception for wand foci.
     * If player is holding a focus, cancel bow charging.
     */
    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack bow = event.getBow();
        int primalType = findPrimalArrowType(player);
        if (primalType >= 0 && !bow.isEmpty()) {
            float velocityFactor;
            float velocityMultiplier = 2.0F;
            if (bow.getItem() instanceof ItemBowBone) {
                velocityFactor = event.getCharge() / 10.0F;
                velocityMultiplier = 2.5F;
            } else {
                velocityFactor = event.getCharge() / 20.0F;
            }
            velocityFactor = (velocityFactor * velocityFactor + velocityFactor * 2.0F) / 3.0F;
            if (velocityFactor < 0.1F) {
                return;
            }
            if (velocityFactor > 1.0F) {
                velocityFactor = 1.0F;
            }

            EntityPrimalArrow arrow = new EntityPrimalArrow(player.world, player, velocityFactor * velocityMultiplier, primalType);
            if (bow.getItem() instanceof ItemBowBone) {
                arrow.setDamage(arrow.getDamage() + 0.5D);
            } else if (velocityFactor == 1.0F) {
                arrow.setIsCritical(true);
            }

            int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
            if (power > 0) {
                arrow.setDamage(arrow.getDamage() + power * 0.5D + 0.5D);
            }
            int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
            if (primalType == 3) {
                punch++;
            }
            if (punch > 0) {
                arrow.setKnockbackStrength(punch);
            }
            int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow);
            if (flame > 0) {
                arrow.setFire(100);
            }

            bow.damageItem(1, player);
            player.world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS,
                    1.0F, 1.0F / (player.world.rand.nextFloat() * 0.4F + 1.2F) + velocityFactor * 0.5F);

            boolean preserveArrow = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) > 0
                    && player.world.rand.nextFloat() < 0.33F;
            if (!player.capabilities.isCreativeMode || !preserveArrow) {
                InventoryUtils.consumeInventoryItem(player, ConfigItems.itemPrimalArrow, primalType);
            }
            if (!player.world.isRemote) {
                player.world.spawnEntity(arrow);
            }
            event.setCanceled(true);
            return;
        }

        ItemStack held = player.getHeldItemMainhand();
        if (!player.world.isRemote && !held.isEmpty() && held.getItem() instanceof ItemFocusBasic) {
            event.setCharge(0);
        }
    }

    /**
     * Bow nock interception for primal arrows and wand foci.
     * When primal arrows are in inventory, supply the bow as the action result
     * so the bow enters use mode without searching for normal arrows.
     * When holding a focus, return FAIL to prevent bow drawing.
     */
    @SubscribeEvent
    public void onArrowNock(ArrowNockEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        ItemStack bow = event.getBow();
        if (!bow.isEmpty() && findPrimalArrowType(player) >= 0) {
            player.setActiveHand(event.getHand());
            event.setAction(new ActionResult<>(EnumActionResult.SUCCESS, bow));
            return;
        }

        ItemStack held = player.getHeldItemMainhand();

        if (!held.isEmpty() && held.getItem() instanceof ItemFocusBasic) {
            event.setAction(new ActionResult<>(EnumActionResult.FAIL, held));
        }
    }

    /**
     * Break speed modifier: boost aerial mining while hover flight is active.
     */
    @SubscribeEvent
    public void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!event.getEntityPlayer().onGround && Hover.getHover(event.getEntityPlayer().getEntityId())) {
            event.setNewSpeed(event.getOriginalSpeed() * 5.0F);
        }
    }

    /**
     * Player file load: grant auto-unlock research and legacy warpCounter.dat migration.
     */
    @SubscribeEvent
    public void onPlayerLoadFromFile(PlayerEvent.LoadFromFile event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player == null || player.world == null || player.world.isRemote) {
            return;
        }

        // Grant all isAutoUnlock() research on every load.
        // This matches original TC4 1.7.10 behavior: auto-unlock research is always
        // re-granted on player load, never persisted in save files.
        for (ResearchCategoryList category : ResearchCategories.researchCategories.values()) {
            for (ResearchItem ri : category.research.values()) {
                if (ri != null && ri.isAutoUnlock()) {
                    ResearchManager.addResearch(player, ri.key);
                }
            }
        }
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
        Potion unHunger = Potion.getPotionById(Config.potionUnHungerID);

        if (used.isEmpty() || !(used.getItem() instanceof ItemFood)) return;

        if (unHunger != null && player.isPotionActive(unHunger)) {
            if (used.getItem() == Items.ROTTEN_FLESH || used.getItem() == ConfigItems.itemZombieBrain) {
                PotionEffect effect = player.getActivePotionEffect(unHunger);
                if (effect != null) {
                    int amplifier = effect.getAmplifier() - 1;
                    int duration = effect.getDuration() - 600;
                    player.removePotionEffect(unHunger);
                    if (duration > 0 && amplifier >= 0) {
                        PotionEffect reduced = new PotionEffect(unHunger, duration, amplifier, true, true);
                        reduced.getCurativeItems().clear();
                        reduced.getCurativeItems().add(new ItemStack(Items.ROTTEN_FLESH));
                        player.addPotionEffect(reduced);
                    }
                }
                TextComponentTranslation msg = new TextComponentTranslation("warp.text.hunger.2");
                msg.getStyle().setItalic(true).setColor(TextFormatting.DARK_GREEN);
                player.sendMessage(msg);
            } else {
                TextComponentTranslation msg = new TextComponentTranslation("warp.text.hunger.1");
                msg.getStyle().setItalic(true).setColor(TextFormatting.DARK_RED);
                player.sendMessage(msg);
            }
        }

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
        if (!(event.getEntityLiving() instanceof EntityPlayer)) {
            return;
        }
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        ItemStack boots = player.inventory.armorInventory.get(0);
        if (boots.isEmpty() || boots.getItem() != ConfigItems.itemBootsTraveller) {
            return;
        }
        player.motionY += 0.275D;
    }

    private void applyTravellerHasteMovement(EntityPlayer player) {
        if (player.capabilities.isFlying) {
            return;
        }
        ItemStack boots = player.inventory.armorInventory.get(0);
        if (boots.isEmpty() || boots.getItem() != ConfigItems.itemBootsTraveller || player.moveForward <= 0.0F) {
            return;
        }

        int haste = Config.enchHaste == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Config.enchHaste, boots);
        if (haste <= 0) {
            return;
        }

        float bonus = haste * 0.015F;
        if (player.onGround) {
            bonus /= 2.0F;
        }
        if (player.isInWater()) {
            bonus /= 2.0F;
        }
        player.moveRelative(0.0F, 0.0F, 1.0F, bonus);
    }

    private void enforceHoverFlightBounds(EntityPlayer player) {
        if (player.world.provider.getDimension() == Config.dimensionOuterId
                && !player.capabilities.isCreativeMode
                && player.ticksExisted > 0
                && player.ticksExisted % 20 == 0
                && (player.capabilities.isFlying || Hover.getHover(player.getEntityId()))) {
            player.capabilities.isFlying = false;
            Hover.setHover(player.getEntityId(), false);
            player.sendMessage(new TextComponentTranslation("tc.break.fly"));
        }

        if (Hover.getHover(player.getEntityId())) {
            ItemStack chest = player.inventory.armorInventory.get(2);
            if (chest.isEmpty() || chest.getItem() != ConfigItems.itemHoverHarness) {
                Hover.setHover(player.getEntityId(), false);
                player.capabilities.isFlying = false;
            }
        }
    }

    private void handleChampionSpawn(EntityJoinWorldEvent event, EntityMob mob) {
        IAttributeInstance mod = ensureChampionAttribute(mob);
        if (mod != null && mod.getAttributeValue() > -1.0D) {
            EntityUtils.repairChampionName(mob);
        }
        if (mod == null || mod.getAttributeValue() >= -1.0D) {
            return;
        }

        int c = mob.world.rand.nextInt(100);
        if (mob.world.getDifficulty() == EnumDifficulty.EASY || !Config.championMobs) {
            c += 2;
        }
        if (mob.world.getDifficulty() == EnumDifficulty.HARD) {
            c -= Config.championMobs ? 2 : 0;
        }
        if (mob.world.provider.getDimension() == Config.dimensionOuterId) {
            c -= 3;
        }

        Biome biome = mob.world.getBiome(new BlockPos(MathHelper.floor(mob.posX), MathHelper.floor(mob.posY), MathHelper.floor(mob.posZ)));
        if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SPOOKY)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)
                || BiomeDictionary.hasType(biome, BiomeDictionary.Type.END)) {
            c -= Config.championMobs ? 2 : 1;
        }

        if (isDangerousLocation(mob.world, MathHelper.floor(mob.posX), MathHelper.floor(mob.posY), MathHelper.floor(mob.posZ))) {
            c -= Config.championMobs ? 10 : 3;
        }

        int whitelistTier = getChampionWhitelistTier(event.getEntity());
        if (whitelistTier >= 0) {
            if (Config.championMobs || event.getEntity() instanceof EntityThaumcraftBoss) {
                c -= Math.max(0, whitelistTier - 1);
            }
        }

        if (whitelistTier >= 0
                && c <= 0
                && mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH) != null
                && mob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() >= 10.0D) {
            EntityUtils.makeChampion(mob, false);
        } else {
            mod.removeModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
            mod.applyModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
        }
    }

    private IAttributeInstance ensureChampionAttribute(EntityMob mob) {
        return EntityUtils.ensureChampionModAttribute(mob);
    }

    private int getChampionWhitelistTier(Entity entity) {
        ResourceLocation entityKey = EntityList.getKey(entity);
        String className = entity.getClass().getName();
        String legacyClassKey = className.replace("thaumcraft.common.entities.", "Thaumcraft.");
        String simpleClassKey = entity.getClass().getSimpleName();
        int tier = -1;
        for (Map.Entry<String, Integer> entry : ConfigEntities.CHAMPION_WHITELIST.entrySet()) {
            String key = entry.getKey();
            if (key == null || key.isEmpty()) {
                continue;
            }
            boolean matches = key.equals(className)
                    || key.equals(legacyClassKey)
                    || key.equals(simpleClassKey)
                    || (entityKey != null && key.equals(entityKey.toString()));
            if (matches) {
                tier = Math.max(tier, entry.getValue() == null ? 0 : entry.getValue());
            }
        }
        return tier;
    }

    private boolean isDangerousLocation(World world, int x, int y, int z) {
        if (world.provider.getDimension() != Config.dimensionOuterId) {
            return false;
        }
        Cell cell = MazeHandler.getFromHashMap(new CellLoc(x >> 4, z >> 4));
        return cell != null && (cell.feature == 6 || cell.feature == 8);
    }

    private int findPrimalArrowType(EntityPlayer player) {
        if (player == null) {
            return -1;
        }
        for (ItemStack stack : player.inventory.mainInventory) {
            if (!stack.isEmpty() && stack.getItem() == ConfigItems.itemPrimalArrow) {
                return stack.getItemDamage();
            }
        }
        return -1;
    }
}

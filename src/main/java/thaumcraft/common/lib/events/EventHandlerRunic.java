package thaumcraft.common.lib.events;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.api.IRunicArmor;
import thaumcraft.api.IWarpingGear;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.baubles.ItemAmuletRunic;
import thaumcraft.common.items.baubles.ItemGirdleRunic;
import thaumcraft.common.items.baubles.ItemRingRunic;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerKnowledgeProvider;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.playerdata.PacketRunicCharge;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.HashMap;

public class EventHandlerRunic {

    // Per-player runic shielding state (keyed by entity ID)
    public HashMap<Integer, Integer> runicCharge = new HashMap<>();
    private HashMap<Integer, Long> nextCycle = new HashMap<>();
    private HashMap<Integer, Integer> lastCharge = new HashMap<>();
    public HashMap<Integer, Integer[]> runicInfo = new HashMap<>();
    private HashMap<String, Long> upgradeCooldown = new HashMap<>();
    public boolean isDirty = true;
    private int rechargeDelay = 0;

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote) return;
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();

        // Recalculate max charge every 40 ticks or when dirty
        if (this.isDirty || player.ticksExisted % 40 == 0) {
            int max = 0;
            int charged = 0;
            int kinetic = 0;
            int healing = 0;
            int emergency = 0;
            this.isDirty = false;

            // Check armor slots (4 slots)
            for (int a = 0; a < 4; a++) {
                ItemStack stack = player.inventory.armorInventory.get(a);
                if (stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor)) continue;
                int amount = getFinalCharge(stack);
                max += amount;
            }

            // Check baubles (use IBaublesItemHandler)
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            if (baubles != null) {
                for (int a = 0; a < baubles.getSlots(); a++) {
                    ItemStack stack = baubles.getStackInSlot(a);
                    if (stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor)) continue;
                    int amount = getFinalCharge(stack);

                    if (stack.getItem() instanceof ItemRingRunic) {
                        switch (stack.getItemDamage()) {
                            case 2: charged++; break;
                            case 3: healing++; break;
                        }
                    } else if (stack.getItem() instanceof ItemAmuletRunic && stack.getItemDamage() == 1) {
                        emergency++;
                    } else if (stack.getItem() instanceof ItemGirdleRunic && stack.getItemDamage() == 1) {
                        kinetic++;
                    }
                    max += amount;
                }
            }

            if (max > 0) {
                Integer[] info = new Integer[]{max, charged, kinetic, healing, emergency};
                this.runicInfo.put(player.getEntityId(), info);

                if (this.runicCharge.containsKey(player.getEntityId())) {
                    int charge = this.runicCharge.get(player.getEntityId());
                    if (charge > max) {
                        setRunicCharge(player, max, max, true);
                    }
                }
            } else {
                this.runicInfo.remove(player.getEntityId());
                setRunicCharge(player, 0, 0, true);
            }
        }

        // Recharge logic
        if (this.rechargeDelay > 0) {
            this.rechargeDelay--;
        } else if (this.runicInfo.containsKey(player.getEntityId())) {
            if (!this.lastCharge.containsKey(player.getEntityId())) {
                this.lastCharge.put(player.getEntityId(), -1);
            }
            if (!this.runicCharge.containsKey(player.getEntityId())) {
                this.runicCharge.put(player.getEntityId(), 0);
            }
            if (!this.nextCycle.containsKey(player.getEntityId())) {
                this.nextCycle.put(player.getEntityId(), 0L);
            }

            long time = System.currentTimeMillis();
            int charge = this.runicCharge.get(player.getEntityId());
            int maxCharge = this.runicInfo.get(player.getEntityId())[0];

            if (charge > maxCharge) {
                charge = maxCharge;
            } else if (charge < maxCharge
                    && this.nextCycle.get(player.getEntityId()) < time
                    && WandManager.consumeVisFromInventory(player,
                    new AspectList().add(Aspect.AIR, Config.shieldCost).add(Aspect.EARTH, Config.shieldCost))) {
                long interval = Math.max(0, Config.shieldRecharge - this.runicInfo.get(player.getEntityId())[1] * 500L);
                this.nextCycle.put(player.getEntityId(), time + interval);
                setRunicCharge(player, ++charge, maxCharge, false);
            }

            if (this.lastCharge.get(player.getEntityId()) != charge) {
                syncRunicCharge(player, charge, maxCharge);
                this.lastCharge.put(player.getEntityId(), charge);
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        // Fortress armour leech effect (attacker side)
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer leecher = (EntityPlayer) event.getSource().getTrueSource();
            ItemStack helm = leecher.inventory.armorInventory.get(3);
            if (!helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor
                    && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask")
                    && helm.getTagCompound().getInteger("mask") == 2
                    && leecher.world.rand.nextFloat() < event.getAmount() / 12.0f) {
                leecher.heal(1.0f);
            }
        }

        // Player damage absorption (victim side)
        if (event.getEntity() instanceof EntityPlayer) {
            long time = System.currentTimeMillis();
            EntityPlayer player = (EntityPlayer) event.getEntity();

            // Fortress armour thorns effect
            if (event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase) event.getSource().getTrueSource();
                ItemStack helm = player.inventory.armorInventory.get(3);
                if (!helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor
                        && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask")
                        && helm.getTagCompound().getInteger("mask") == 1
                        && player.world.rand.nextFloat() < event.getAmount() / 10.0f) {
                    try {
                        attacker.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.WITHER, 80));
                    } catch (Exception e) {
                        // silent
                    }
                }
            }

            // Skip environmental damage
            if (event.getSource() == DamageSource.IN_FIRE
                    || event.getSource() == DamageSource.ON_FIRE
                    || event.getSource() == DamageSource.LAVA
                    || event.getSource() == DamageSource.DROWN) {
                return;
            }

            // Runic shielding absorption
            if (this.runicInfo.containsKey(player.getEntityId())
                    && this.runicCharge.containsKey(player.getEntityId())
                    && this.runicCharge.get(player.getEntityId()) > 0) {

                int target = -1;
                if (event.getSource().getImmediateSource() != null) {
                    target = event.getSource().getImmediateSource().getEntityId();
                }
                if (event.getSource() == DamageSource.FALL) {
                    target = -2;
                }
                if (event.getSource() == DamageSource.FLY_INTO_WALL) {
                    target = -3;
                }

                // Phase 8: PacketHandler.INSTANCE.sendToAllAround(
                //     new PacketFXShield(player.getEntityId(), target),
                //     new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 64.0));

                int charge = this.runicCharge.get(player.getEntityId());
                Integer[] info = this.runicInfo.get(player.getEntityId());
                int max = info[0];
                int kineticLevel = info[2];
                int healingLevel = info[3];
                int emergencyLevel = info[4];

                if ((float) charge > event.getAmount()) {
                    charge = (int) ((float) charge - event.getAmount());
                    event.setAmount(0.0f);
                } else {
                    event.setAmount(event.getAmount() - (float) charge);
                    charge = 0;
                }

                // Kinetic upgrade (explosion when shield breaks)
                String kineticKey = player.getEntityId() + ":" + 2;
                if (charge <= 0 && kineticLevel > 0
                        && (!this.upgradeCooldown.containsKey(kineticKey) || this.upgradeCooldown.get(kineticKey) < time)) {
                    this.upgradeCooldown.put(kineticKey, time + 20000L);
                    player.world.createExplosion(player, player.posX, player.posY + (double) (player.height / 2.0f), player.posZ,
                            1.5f + (float) kineticLevel * 0.5f, false);
                }

                // Healing upgrade (regeneration when shield breaks)
                String healKey = player.getEntityId() + ":" + 3;
                if (charge <= 0 && healingLevel > 0
                        && (!this.upgradeCooldown.containsKey(healKey) || this.upgradeCooldown.get(healKey) < time)) {
                    this.upgradeCooldown.put(healKey, time + 20000L);
                    try {
                        player.addPotionEffect(new net.minecraft.potion.PotionEffect(net.minecraft.init.MobEffects.REGENERATION, 240, healingLevel));
                    } catch (Exception e) {
                        // silent
                    }
                    // Phase 8: player.world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.RUNICSHIELDEFFECT, ...);
                }

                // Emergency charge (recharge when shield breaks)
                String emergKey = player.getEntityId() + ":" + 4;
                if (charge <= 0 && emergencyLevel > 0
                        && (!this.upgradeCooldown.containsKey(emergKey) || this.upgradeCooldown.get(emergKey) < time)) {
                    this.upgradeCooldown.put(emergKey, time + 60000L);
                    int t = 8 * emergencyLevel;
                    charge = Math.min(max, t);
                    this.isDirty = true;
                    // Phase 8: player.world.playSound(null, player.posX, player.posY, player.posZ, TCSounds.RUNICSHIELDCHARGE, ...);
                }

                if (charge <= 0) {
                    this.rechargeDelay = Config.shieldWait;
                }

                setRunicCharge(player, charge, max, true);
            }
        }

        // Champion mob shielding (non-player entities with champion modifiers)
        if (event.getEntity() instanceof EntityMob) {
            EntityMob mob = (EntityMob) event.getEntity();
            int t = -1;

            // Check if this mob has a champion modifier attribute
            // The port does not have EntityUtils.CHAMPION_MOD attribute,
            // so we use a simplified check: look for IEldritchMob or known champion properties
            if (mob instanceof IEldritchMob) {
                // Eldritch mobs have inherent shielding (type 5 in original, here we hardcode)
                if (mob.getMaxHealth() > 0.0f) {
                    handleChampionShield(event, mob, -1);
                }
            }
        }

        // Champion mob offensive effects (attacker side)
        if (event.getAmount() > 0.0f && event.getSource().getTrueSource() != null
                && event.getEntity() instanceof EntityLivingBase
                && event.getSource().getTrueSource() instanceof EntityMob) {
            // Champion mob attack effects - skip for now (no CHAMPION_MOD attribute)
        }
    }

    /**
     * Apply shielding FX for champion/eldritch mobs.
     */
    private void handleChampionShield(LivingHurtEvent event, EntityMob mob, int dummyTarget) {
        int target = -1;
        if (event.getSource().getImmediateSource() != null) {
            target = event.getSource().getImmediateSource().getEntityId();
        }
        if (event.getSource() == DamageSource.FALL) {
            target = -2;
        }
        if (event.getSource() == DamageSource.FLY_INTO_WALL) {
            target = -3;
        }

        // Phase 8: PacketHandler.INSTANCE.sendToAllAround(
        //     new PacketFXShield(mob.getEntityId(), target),
        //     new NetworkRegistry.TargetPoint(mob.world.provider.getDimension(), mob.posX, mob.posY, mob.posZ, 32.0));

        // Phase 8: mob.world.playSound(null, mob.posX, mob.posY, mob.posZ, TCSounds.RUNICSHIELDEFFECT, ...);
    }

    private void setRunicCharge(EntityPlayer player, int charge, int max, boolean send) {
        int safeCharge = Math.max(0, charge);
        this.runicCharge.put(player.getEntityId(), safeCharge);
        IPlayerKnowledge knowledge = player.getCapability(PlayerKnowledgeProvider.PLAYER_KNOWLEDGE, null);
        if (knowledge != null) {
            knowledge.setRunicCharge(safeCharge);
            ResearchManager.updateCache(player.getName(), knowledge);
        }
        if (send) {
            syncRunicCharge(player, safeCharge, max);
        }
    }

    private void syncRunicCharge(EntityPlayer player, int charge, int max) {
        if (player instanceof EntityPlayerMP) {
            PacketHandler.INSTANCE.sendTo(new PacketRunicCharge(player.getEntityId(), Math.max(0, charge), Math.max(0, max)), (EntityPlayerMP)player);
        }
    }

    // ---- Static utility methods ----

    /**
     * Get the total runic shielding charge for a given item stack.
     */
    public static int getFinalCharge(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor)) {
            return 0;
        }
        IRunicArmor armor = (IRunicArmor) stack.getItem();
        int base = armor.getRunicCharge(stack);
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("RS.HARDEN")) {
            base += stack.getTagCompound().getByte("RS.HARDEN");
        }
        return base;
    }

    /**
     * Get the warp value for a given item stack and player.
     */
    public static int getFinalWarp(ItemStack stack, EntityPlayer player) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IWarpingGear)) {
            return 0;
        }
        IWarpingGear gear = (IWarpingGear) stack.getItem();
        return gear.getWarp(stack, player);
    }

    /**
     * Get the hardening level for a given item stack.
     */
    public static int getHardening(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IRunicArmor)) {
            return 0;
        }
        int base = 0;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("RS.HARDEN")) {
            base += stack.getTagCompound().getByte("RS.HARDEN");
        }
        return base;
    }
}

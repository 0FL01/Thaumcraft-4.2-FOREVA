package thaumcraft.common.lib;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.CommonProxy;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.capabilities.IPlayerKnowledge;
import thaumcraft.common.lib.events.EventHandlerRunic;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;

import java.util.List;

public final class WarpEvents {

    /**
     * Called from EventHandlerEntity.onLivingUpdate() every ~20 ticks.
     * Rolls a random warp event based on warpCounter and total warp.
     */
    public static void checkWarpEvent(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;

        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return;

        int warp = knowledge.getTotalWarp();
        int actualwarp = knowledge.getWarpPerm() + knowledge.getWarpSticky();
        int warpCounter = knowledge.getWarpCounter();
        int r = player.world.rand.nextInt(100);

        if (warpCounter > 0) {
            warp += getWarpFromGear(player);
            if (warp > 0 && (double) r <= Math.sqrt(warpCounter)) {
                warp = Math.min(100, (warp + warp + warpCounter) / 3);
                warpCounter = (int) ((double) warpCounter - Math.max(5.0, Math.sqrt(warpCounter) * 2.0));
                knowledge.setWarpCounter(warpCounter);

                int eff = player.world.rand.nextInt(warp);
                ItemStack helm = player.inventory.armorInventory.get(3);
                if (helm != null && helm.getItem() instanceof ItemFortressArmor
                        && helm.hasTagCompound()
                        && helm.getTagCompound().hasKey("mask")
                        && helm.getTagCompound().getInteger("mask") == 0) {
                    eff -= 2 + player.world.rand.nextInt(4);
                }

                PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((short) 0), (EntityPlayerMP) player);

                if (eff > 0) {
                    if (eff <= 4) {
                        grantResearch(player, 1);
                        player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.3")));
                    } else if (eff > 8) {
                        if (eff <= 12) {
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.11")));
                        } else if (eff <= 16) {
                            applyPotionSafely(player, Config.potionVisExhaust, 5000, Math.min(3, warp / 15), true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.1")));
                        } else if (eff <= 20) {
                            applyPotionSafely(player, Config.potionThaumarhia, Math.min(32000, 10 * warp), 0, true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.15")));
                        } else if (eff <= 24) {
                            PotionEffect pe = new PotionEffect(Config.potionUnnaturalHunger, 5000, Math.min(3, warp / 15), false, true);
                            pe.getCurativeItems().clear();
                            pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
                            pe.addCurativeItem(new ItemStack(ConfigItems.itemZombieBrain));
                            applyEffectSafely(player, pe);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.2")));
                        } else if (eff <= 28) {
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.12")));
                        } else if (eff <= 32) {
                            spawnMist(player, warp, 1);
                        } else if (eff <= 36) {
                            applyPotionSafely(player, Config.potionBlurredVision, Math.min(32000, 10 * warp), 0, true);
                        } else if (eff <= 40) {
                            applyPotionSafely(player, Config.potionSunScorned, 5000, Math.min(3, warp / 15), true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.5")));
                        } else if (eff <= 44) {
                            applyPotionSafely(player, MobEffects.REGENERATION, 1200, Math.min(3, warp / 15), true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.9")));
                        } else if (eff <= 48) {
                            applyPotionSafely(player, Config.potionInfectiousVisExhaust, 6000, Math.min(3, warp / 15), false);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.1")));
                        } else if (eff <= 52) {
                            applyPotionSafely(player, MobEffects.NIGHT_VISION, Math.min(40 * warp, 6000), 0, true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.10")));
                        } else if (eff <= 56) {
                            applyPotionSafely(player, Config.potionDeathGaze, 6000, Math.min(3, warp / 15), true);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.4")));
                        } else if (eff <= 60) {
                            suddenlySpiders(player, warp, false);
                        } else if (eff <= 64) {
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.13")));
                        } else if (eff <= 68) {
                            spawnMist(player, warp, warp / 30);
                        } else if (eff <= 72) {
                            applyPotionSafely(player, MobEffects.BLINDNESS, Math.min(32000, 5 * warp), 0, true);
                        } else if (eff == 76) {
                            if (knowledge.getWarpSticky() > 0) {
                                knowledge.addWarpSticky(-1);
                                syncWarp(player);
                                PacketHandler.INSTANCE.sendTo(new PacketWarpMessage(player, (byte) 1, -1), (EntityPlayerMP) player);
                            }
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.14")));
                        } else if (eff <= 80) {
                            PotionEffect pe = new PotionEffect(Config.potionUnnaturalHunger, 6000, Math.min(3, warp / 15), false, true);
                            pe.getCurativeItems().clear();
                            pe.addCurativeItem(new ItemStack(Items.ROTTEN_FLESH));
                            pe.addCurativeItem(new ItemStack(ConfigItems.itemZombieBrain));
                            applyEffectSafely(player, pe);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.2")));
                        } else if (eff <= 84) {
                            grantResearch(player, warp / 10);
                            player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.3")));
                        } else if (eff > 88) {
                            if (eff <= 92) {
                                suddenlySpiders(player, warp, true);
                            } else {
                                spawnMist(player, warp, warp / 15);
                            }
                        }
                    }
                }

                // Auto-complete research at high warp thresholds
                if (actualwarp > 10 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), "BATHSALTS") && !ThaumcraftApiHelper.isResearchComplete(player.getName(), "@BATHSALTS")) {
                    player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.8")));
                    ResearchManager.addResearch(player, "@BATHSALTS");
                }
                if (actualwarp > 25 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), "ELDRITCHMINOR")) {
                    grantResearch(player, 10);
                    ResearchManager.addResearch(player, "ELDRITCHMINOR");
                }
                if (actualwarp > 50 && !ThaumcraftApiHelper.isResearchComplete(player.getName(), "ELDRITCHMAJOR")) {
                    grantResearch(player, 20);
                    ResearchManager.addResearch(player, "ELDRITCHMAJOR");
                }
            }
        }

        // Decay temporary warp
        knowledge.addWarpTemp(-1);
        syncWarp(player);
    }

    /**
     * Called from EventHandlerEntity.onLivingDeath().
     * If the player has death gaze potion active, nearby entities aggro.
     */
    public static void checkDeathGaze(EntityPlayer player) {
        if (player == null || player.world.isRemote) return;

        PotionEffect pe = player.getActivePotionEffect(Config.potionDeathGaze);
        if (pe == null) return;

        int level = pe.getAmplifier();
        int range = Math.min(8 + level * 3, 24);
        List<Entity> list = player.world.getEntitiesWithinAABBExcludingEntity(player,
                player.getEntityBoundingBox().grow(range, range, range));

        for (Entity entity : list) {
            if (!entity.isEntityAlive() || !(entity instanceof EntityLivingBase)) continue;
            EntityLivingBase living = (EntityLivingBase) entity;
            if (!player.canEntityBeSeen(entity)) continue;
            if (entity == player) continue;
            if (entity instanceof EntityPlayer && !FMLCommonHandler.instance().getMinecraftServerInstance().isPVPEnabled())
                continue;
            if (living.isPotionActive(MobEffects.WITHER)) continue;

            living.setRevengeTarget(player);
            if (entity instanceof EntityCreature) {
                ((EntityCreature) entity).setAttackTarget(player);
            }
            living.addPotionEffect(new PotionEffect(MobEffects.WITHER, 80));
        }
    }

    // ---- Private helpers ----

    private static void spawnMist(EntityPlayer player, int warp, int guardian) {
        PacketHandler.INSTANCE.sendTo(new PacketMiscEvent((short) 1), (EntityPlayerMP) player);
        if (guardian > 0) {
            guardian = Math.min(8, guardian);
            for (int a = 0; a < guardian; a++) {
                spawnGuardian(player);
            }
        }
        player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.6")));
    }

    private static void grantResearch(EntityPlayer player, int times) {
        int amt = 1 + player.world.rand.nextInt(times);
        for (int a = 0; a < amt; a++) {
            Aspect aspect = Aspect.getPrimalAspects().get(player.world.rand.nextInt(6));
            IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
            if (knowledge != null) {
                knowledge.addDiscoveredAspect(aspect.getTag());
                if (knowledge.addAspectPool(aspect, 1)) {
                    PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), (short)1, knowledge.getAspectPoolFor(aspect)), (EntityPlayerMP)player);
                }
                ResearchManager.updateCache(player.getName(), knowledge);
            }
        }
    }

    private static void spawnGuardian(EntityPlayer player) {
        EntityEldritchGuardian eg = new EntityEldritchGuardian(player.world);
        int i = MathHelper.floor(player.posX);
        int j = MathHelper.floor(player.posY);
        int k = MathHelper.floor(player.posZ);

        for (int l = 0; l < 50; l++) {
            int i1 = i + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int j1 = j + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
            int k1 = k + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);

            if (player.world.getBlockState(new net.minecraft.util.math.BlockPos(i1, j1 - 1, k1)).isSideSolid(player.world, new net.minecraft.util.math.BlockPos(i1, j1 - 1, k1), net.minecraft.util.EnumFacing.UP)) {
                eg.setPosition(i1, j1, k1);
                if (player.world.checkNoEntityCollision(eg.getEntityBoundingBox())
                        && player.world.getCollisionBoxes(eg, eg.getEntityBoundingBox()).isEmpty()
                        && !player.world.containsAnyLiquid(eg.getEntityBoundingBox())) {
                    eg.setAttackTarget(player);
                    eg.setRevengeTarget(player);
                    player.world.spawnEntity(eg);
                    break;
                }
            }
        }
    }

    private static void suddenlySpiders(EntityPlayer player, int warp, boolean real) {
        int spawns = Math.min(50, warp);
        for (int a = 0; a < spawns; a++) {
            EntityMindSpider spider = new EntityMindSpider(player.world);
            int i = MathHelper.floor(player.posX);
            int j = MathHelper.floor(player.posY);
            int k = MathHelper.floor(player.posZ);
            boolean success = false;

            for (int l = 0; l < 50; l++) {
                int i1 = i + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int j1 = j + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);
                int k1 = k + MathHelper.getInt(player.world.rand, 7, 24) * MathHelper.getInt(player.world.rand, -1, 1);

            if (player.world.getBlockState(new net.minecraft.util.math.BlockPos(i1, j1 - 1, k1)).isSideSolid(player.world, new net.minecraft.util.math.BlockPos(i1, j1 - 1, k1), net.minecraft.util.EnumFacing.UP)) {
                    spider.setPosition(i1, j1, k1);
                    if (player.world.checkNoEntityCollision(spider.getEntityBoundingBox())
                            && player.world.getCollisionBoxes(spider, spider.getEntityBoundingBox()).isEmpty()
                            && !player.world.containsAnyLiquid(spider.getEntityBoundingBox())) {
                        success = true;
                        break;
                    }
                }
            }
            if (!success) continue;

            spider.setAttackTarget(player);
            spider.setRevengeTarget(player);
            if (!real) {
                spider.setViewer(player.getName());
                spider.setHarmless(true);
            }
            player.world.spawnEntity(spider);
        }
        player.sendMessage(new TextComponentString("\u00a75\u00a7o" + net.minecraft.util.text.translation.I18n.translateToLocal("warp.text.7")));
    }

    /**
     * Sum warp from held item + armor + baubles using EventHandlerRunic.
     */
    public static int getWarpFromGear(EntityPlayer player) {
        int w = EventHandlerRunic.getFinalWarp(player.getHeldItemMainhand(), player);
        for (int a = 0; a < 4; a++) {
            w += EventHandlerRunic.getFinalWarp(player.inventory.armorInventory.get(a), player);
        }
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles != null) {
            for (int a = 0; a < baubles.getSlots(); a++) {
                w += EventHandlerRunic.getFinalWarp(baubles.getStackInSlot(a), player);
            }
        }
        return w;
    }

    // ---- Utility ----

    private static void applyPotionSafely(EntityPlayer player, Potion potion, int duration, int amplifier, boolean showParticles) {
        if (potion == null) return;
        try {
            player.addPotionEffect(new PotionEffect(potion, duration, amplifier, false, showParticles));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void applyEffectSafely(EntityPlayer player, PotionEffect pe) {
        if (pe == null || pe.getPotion() == null) return;
        try {
            player.addPotionEffect(pe);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void syncWarp(EntityPlayer player) {
        if (player.world.isRemote) return;
        IPlayerKnowledge knowledge = CommonProxy.getPlayerKnowledge(player);
        if (knowledge == null) return;
        PacketHandler.INSTANCE.sendTo(
                new PacketSyncWarp(knowledge.getWarpPerm(), knowledge.getWarpSticky(), knowledge.getWarpTemp(), knowledge.getWarpCounter()),
                (EntityPlayerMP) player
        );
        ResearchManager.updateCache(player.getName(), knowledge);
    }
}

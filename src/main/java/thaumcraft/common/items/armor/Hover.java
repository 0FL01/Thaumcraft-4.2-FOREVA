package thaumcraft.common.items.armor;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.baubles.ItemGirdleHover;
import thaumcraft.common.lib.TCSounds;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFlyToServer;
import thaumcraft.common.lib.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Hover mixin for Boots of the Traveler — provides step assist and jump boost.
 */
public class Hover {

    private static final Map<Integer, Boolean> HOVERING = new HashMap<>();
    private static final Map<Integer, Long> TIMING = new HashMap<>();
    private static final Map<Integer, Float> PREV_STEP = new HashMap<>();

    public static void doHover(ItemStack stack, EntityPlayer player, World world, int slot) {
        if (player == null || world == null) {
            return;
        }
        int playerId = player.getEntityId();
        if (!player.capabilities.isFlying && player.moveForward > 0.0F) {
            if (world.isRemote && !player.isSneaking()) {
                if (!PREV_STEP.containsKey(playerId)) {
                    PREV_STEP.put(playerId, player.stepHeight);
                }
                player.stepHeight = 1.0F;
            } else if (world.isRemote) {
                restoreStepHeight(playerId, player);
            }
            if (player.onGround) {
                float bonus = 0.055F;
                if (player.isInWater()) {
                    bonus /= 4.0F;
                }
                player.moveRelative(0.0F, 0.0F, 1.0F, bonus);
            } else {
                player.jumpMovementFactor = getHover(playerId) ? 0.03F : 0.05F;
            }
        } else if (world.isRemote) {
            restoreStepHeight(playerId, player);
        }
        if (player.fallDistance > 0.0F) {
            player.fallDistance = Math.max(0.0F, player.fallDistance - 0.25F);
        }
    }

    public static void resetHover(EntityPlayer player) {
        if (player == null) {
            return;
        }
        restoreStepHeight(player.getEntityId(), player);
    }

    private static void restoreStepHeight(int playerId, EntityPlayer player) {
        Float prevStep = PREV_STEP.remove(playerId);
        if (prevStep != null) {
            player.stepHeight = prevStep;
        } else if (player.stepHeight > 0.5F) {
            player.stepHeight = 0.5F;
        }
    }

    public static void setHover(int playerId, boolean hover) {
        HOVERING.put(playerId, hover);
    }

    public static boolean setHover(EntityPlayer player, ItemStack harness, boolean hover) {
        if (player == null || harness == null || harness.isEmpty()) return false;
        if (hover && !expendCharge(player, harness, false)) {
            hover = false;
        }
        setHover(player.getEntityId(), hover);
        ensureTag(harness).setBoolean("hover", hover);
        return hover;
    }

    public static boolean getHover(int playerId) {
        Boolean hover = HOVERING.get(playerId);
        return hover != null && hover;
    }

    public static boolean getHover(EntityPlayer player) {
        return player != null && getHover(player.getEntityId());
    }

    public static boolean toggleHover(EntityPlayer player, int playerId, ItemStack harness) {
        if (player == null || harness.isEmpty()) return false;
        boolean hover = !getHover(playerId);
        boolean actualHover = setHover(player, harness, hover);
        if (hover && !actualHover) return false;
        if (isLocalClientPlayer(player)) {
            notifyClientHoverChange(player, hover);
        }
        return true;
    }

    public static void handleHoverArmor(EntityPlayer player, ItemStack harness) {
        if (player == null || harness.isEmpty()) return;
        NBTTagCompound tag = ensureTag(harness);
        boolean hover = tag.getBoolean("hover");
        setHover(player.getEntityId(), hover);
        if (hover && !expendCharge(player, harness, true)) {
            setHover(player, harness, false);
            hover = false;
            if (isLocalClientPlayer(player)) {
                notifyClientHoverChange(player, false);
            }
        }

        setHover(player.getEntityId(), hover);
        tag.setBoolean("hover", hover);
        if (!player.capabilities.isCreativeMode) {
            player.capabilities.allowFlying = hover;
            player.capabilities.isFlying = hover;
        }
        if (hover) {
            if (!player.world.isRemote && player instanceof EntityPlayerMP) {
                Utils.resetFloatCounter((EntityPlayerMP) player);
            }
            player.fallDistance = 0.0F;
            applyClientHoverMotion(player, harness);
        } else if (player.fallDistance > 0.0F) {
            player.fallDistance *= 0.75F;
        }
        if (!player.world.isRemote && player instanceof EntityPlayerMP) {
            ((EntityPlayerMP) player).sendPlayerAbilities();
        }
    }

    private static NBTTagCompound ensureTag(ItemStack harness) {
        if (!harness.hasTagCompound()) harness.setTagCompound(new NBTTagCompound());
        return harness.getTagCompound();
    }

    public static boolean expendCharge(EntityPlayer player, ItemStack harness, boolean doit) {
        if (!harness.hasTagCompound() || !harness.getTagCompound().hasKey("jar")) return false;
        ItemStack jar = new ItemStack(harness.getTagCompound().getCompoundTag("jar"));
        if (jar.isEmpty() || !(jar.getItem() instanceof BlockJarItem)) return false;
        BlockJarItem container = (BlockJarItem) jar.getItem();
        AspectList aspects = container.getAspects(jar);
        if (aspects == null || aspects.getAmount(Aspect.ENERGY) <= 0) return false;

        int charge = harness.getTagCompound().getShort("charge") & 65535;
        int threshold = Math.max(1, Math.round(360.0F * getEfficiency(player)));
        if (!doit) return true;
        if (charge < threshold) {
            harness.getTagCompound().setShort("charge", (short) (charge + 1));
            return true;
        }

        charge = 0;
        aspects.remove(Aspect.ENERGY, 1);
        container.setAspects(jar, aspects);
        NBTTagCompound jarTag = new NBTTagCompound();
        jar.writeToNBT(jarTag);
        harness.getTagCompound().setTag("jar", jarTag);
        if (aspects.getAmount(Aspect.ENERGY) <= 0) {
            harness.getTagCompound().setShort("charge", (short) charge);
            return false;
        }
        harness.getTagCompound().setShort("charge", (short) charge);
        return true;
    }

    private static void applyClientHoverMotion(EntityPlayer player, ItemStack harness) {
        if (!isLocalClientPlayer(player)) return;
        long currentTime = System.currentTimeMillis();
        Long time = TIMING.get(player.getEntityId());
        if (time == null || time < currentTime) {
            TIMING.put(player.getEntityId(), currentTime + 1200L);
            player.world.playSound(player.posX, player.posY, player.posZ, TCSounds.JACOBS,
                    SoundCategory.MASTER, 0.05F, 1.0F + player.world.rand.nextFloat() * 0.05F, false);
        }
        float motionModifier = getHoverMotionModifier(player, harness);
        player.motionX *= motionModifier;
        player.motionZ *= motionModifier;
    }

    private static boolean isLocalClientPlayer(EntityPlayer player) {
        return player.world.isRemote && Thaumcraft.proxy != null
                && Thaumcraft.proxy.getClientPlayer() == player;
    }

    private static void notifyClientHoverChange(EntityPlayer player, boolean hover) {
        PacketHandler.INSTANCE.sendToServer(new PacketFlyToServer(player, hover));
        player.world.playSound(player.posX, player.posY, player.posZ,
                hover ? TCSounds.HHON : TCSounds.HHOFF,
                SoundCategory.MASTER, 0.33F, 1.0F, false);
    }

    private static float getHoverMotionModifier(EntityPlayer player, ItemStack harness) {
        int haste = Config.enchHaste == null ? 0 : EnchantmentHelper.getEnchantmentLevel(Config.enchHaste, harness);
        float modifier = 0.7F + 0.075F * (float) haste;
        if (hasHoverGirdle(player)) {
            modifier += 0.21F;
        }
        return Math.min(1.0F, modifier);
    }

    private static float getEfficiency(EntityPlayer player) {
        if (player == null) return 1.0F;
        return hasHoverGirdle(player) ? 0.8F : 1.0F;
    }

    private static boolean hasHoverGirdle(EntityPlayer player) {
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles == null) return false;
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack stack = baubles.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemGirdleHover) {
                return true;
            }
        }
        return false;
    }
}

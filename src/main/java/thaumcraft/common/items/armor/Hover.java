package thaumcraft.common.items.armor;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.blocks.BlockJarItem;
import thaumcraft.common.items.baubles.ItemGirdleHover;

import java.util.HashMap;
import java.util.Map;

/**
 * Hover mixin for Boots of the Traveler — provides step assist and jump boost.
 */
public class Hover {

    private static final Map<Integer, Boolean> HOVERING = new HashMap<>();

    public static void doHover(ItemStack stack, EntityPlayer player, World world, int slot) {
        if (player.isSneaking()) return;
        // Provides step assist (auto step-up) and slight jump boost
        if (player.onGround && !player.isInWater()) {
            player.stepHeight = 1.0f;
        }
    }

    public static void resetHover(EntityPlayer player) {
        player.stepHeight = 0.5f;
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
        return setHover(player, harness, !getHover(playerId));
    }

    public static void handleHoverArmor(EntityPlayer player, ItemStack harness) {
        if (player == null || harness.isEmpty()) return;
        NBTTagCompound tag = ensureTag(harness);
        if (!HOVERING.containsKey(player.getEntityId())) {
            setHover(player.getEntityId(), tag.getBoolean("hover"));
        }
        boolean hover = getHover(player);
        if (hover && !expendCharge(player, harness, true)) {
            hover = false;
        }

        setHover(player.getEntityId(), hover);
        tag.setBoolean("hover", hover);
        player.capabilities.allowFlying = hover || player.capabilities.isCreativeMode;
        if (!hover && !player.capabilities.isCreativeMode) {
            player.capabilities.isFlying = false;
        }
        if (hover) {
            player.fallDistance = 0.0F;
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
        if (player != null && player.capabilities.isCreativeMode) return true;
        if (!harness.hasTagCompound() || !harness.getTagCompound().hasKey("jar")) return false;
        ItemStack jar = new ItemStack(harness.getTagCompound().getCompoundTag("jar"));
        if (jar.isEmpty() || !(jar.getItem() instanceof BlockJarItem)) return false;
        BlockJarItem container = (BlockJarItem) jar.getItem();
        AspectList aspects = container.getAspects(jar);
        if (aspects == null || aspects.getAmount(Aspect.ENERGY) <= 0) return false;

        int charge = harness.getTagCompound().getShort("charge") & 65535;
        int threshold = Math.max(1, Math.round(360.0F * getEfficiency(player)));
        if (!doit) return true;
        charge++;
        if (charge >= threshold) {
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
        }
        harness.getTagCompound().setShort("charge", (short) charge);
        return true;
    }

    private static float getEfficiency(EntityPlayer player) {
        if (player == null) return 1.0F;
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        if (baubles == null) return 1.0F;
        for (int slot = 0; slot < baubles.getSlots(); slot++) {
            ItemStack stack = baubles.getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemGirdleHover) {
                return 0.8F;
            }
        }
        return 1.0F;
    }
}

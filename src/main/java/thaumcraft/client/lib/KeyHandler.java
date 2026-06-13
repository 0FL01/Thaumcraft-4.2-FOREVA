package thaumcraft.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.items.armor.Hover;
import thaumcraft.common.items.armor.ItemHoverHarness;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;

@SideOnly(Side.CLIENT)
public class KeyHandler {
    public final KeyBinding keyF = new KeyBinding("Change Wand Focus",
            KeyConflictContext.IN_GAME, Keyboard.KEY_F, "key.categories.misc");
    public final KeyBinding keyH = new KeyBinding("Activate Hover Harness",
            KeyConflictContext.IN_GAME, Keyboard.KEY_H, "key.categories.misc");
    public final KeyBinding keyG = new KeyBinding("Wand Focus Selector",
            KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories.misc");

    public static boolean radialActive = false;
    public static boolean radialLock = false;
    public static long lastPressF = 0L;
    public static long lastPressH = 0L;
    public static long lastPressG = 0L;

    private boolean keyPressedF = false;
    private boolean keyPressedH = false;
    private boolean keyPressedG = false;

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(this.keyF);
        ClientRegistry.registerKeyBinding(this.keyH);
        ClientRegistry.registerKeyBinding(this.keyG);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.inGameHasFocus) {
            releaseAllKeys();
            return;
        }

        EntityPlayer player = minecraft.player;
        handleFocusKey(player);
        handleHoverKey(player);
        handleMiscKey(player);
    }

    private void handleFocusKey(EntityPlayer player) {
        if (this.keyF.isKeyDown()) {
            boolean firstPress = !this.keyPressedF;
            if (firstPress) {
                lastPressF = System.currentTimeMillis();
                radialLock = false;
            }
            if (player != null) {
                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting && !ItemWandCasting.isSceptre(held)) {
                    if (player.isSneaking()) {
                        if (firstPress) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(player, "REMOVE"));
                        }
                    } else if (!radialLock) {
                        radialActive = true;
                    }
                } else if (!held.isEmpty() && held.getItem() instanceof ItemGolemBell && firstPress) {
                    PacketHandler.INSTANCE.sendToServer(new PacketItemKeyToServer(player, 0));
                }
            }
            this.keyPressedF = true;
        } else {
            radialActive = false;
            if (this.keyPressedF) {
                lastPressF = System.currentTimeMillis();
            }
            this.keyPressedF = false;
        }
    }

    private void handleHoverKey(EntityPlayer player) {
        if (this.keyH.isKeyDown()) {
            if (player != null && !this.keyPressedH) {
                lastPressH = System.currentTimeMillis();
                ItemStack harness = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
                if (!harness.isEmpty() && harness.getItem() instanceof ItemHoverHarness) {
                    Hover.toggleHover(player, player.getEntityId(), harness);
                }
            }
            this.keyPressedH = true;
        } else {
            if (this.keyPressedH) {
                lastPressH = System.currentTimeMillis();
            }
            this.keyPressedH = false;
        }
    }

    private void handleMiscKey(EntityPlayer player) {
        if (this.keyG.isKeyDown()) {
            boolean firstPress = !this.keyPressedG;
            if (firstPress) {
                lastPressG = System.currentTimeMillis();
                radialLock = false;
            }
            if (player != null) {
                ItemStack held = player.getHeldItemMainhand();
                if (!held.isEmpty() && held.getItem() instanceof ItemWandCasting && !ItemWandCasting.isSceptre(held)) {
                    if (player.isSneaking()) {
                        if (firstPress) {
                            PacketHandler.INSTANCE.sendToServer(new PacketFocusChangeToServer(player, "REMOVE"));
                        }
                    } else if (!radialLock) {
                        radialActive = true;
                    }
                }
            }
            this.keyPressedG = true;
        } else {
            radialActive = false;
            if (this.keyPressedG) {
                lastPressG = System.currentTimeMillis();
            }
            this.keyPressedG = false;
        }
    }

    private void releaseAllKeys() {
        long now = System.currentTimeMillis();
        if (this.keyPressedF) {
            lastPressF = now;
        }
        if (this.keyPressedH) {
            lastPressH = now;
        }
        if (this.keyPressedG) {
            lastPressG = now;
        }
        radialActive = false;
        this.keyPressedF = false;
        this.keyPressedH = false;
        this.keyPressedG = false;
    }
}

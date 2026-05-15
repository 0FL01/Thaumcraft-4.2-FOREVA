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
import thaumcraft.common.items.armor.Hover;
import thaumcraft.common.items.armor.ItemHoverHarness;

@SideOnly(Side.CLIENT)
public class KeyHandler {
    public final KeyBinding keyH = new KeyBinding("Activate Hover Harness",
            KeyConflictContext.IN_GAME, Keyboard.KEY_H, "key.categories.misc");
    public static long lastPressH = 0L;
    private boolean keyPressedH = false;

    public KeyHandler() {
        ClientRegistry.registerKeyBinding(this.keyH);
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.inGameHasFocus) {
            if (this.keyPressedH) {
                lastPressH = System.currentTimeMillis();
            }
            this.keyPressedH = false;
            return;
        }

        if (this.keyH.isKeyDown()) {
            EntityPlayer player = minecraft.player;
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
}

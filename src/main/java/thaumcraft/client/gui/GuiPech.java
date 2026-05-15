package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.common.entities.ContainerPech;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.TCSounds;

public class GuiPech extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_pech.png");

    private final EntityPech pech;

    public GuiPech(EntityPlayer player, World world, EntityPech pech) {
        super(new ContainerPech(player.inventory, world, pech));
        this.xSize = 175;
        this.ySize = 232;
        this.pech = pech;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        if (canTrade()) {
            this.drawTexturedModalRect(this.guiLeft + 67, this.guiTop + 24, 176, 0, 25, 25);
        }
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!isMouseIn(mouseX, mouseY, 67, 24, 25, 25)) return;
        if (!canTrade()) return;

        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
        playButton();
    }

    private boolean canTrade() {
        Slot inputSlot = this.inventorySlots.getSlot(0);
        ItemStack input = inputSlot.getStack();
        if (input.isEmpty() || !this.pech.isValued(input)) return false;
        for (int i = 1; i <= 4; i++) {
            if (this.inventorySlots.getSlot(i).getHasStack()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY < height;
    }

    private void playButton() {
        if (this.mc.player == null || this.mc.player.world == null) return;
        float pitch = 0.95F + this.mc.player.world.rand.nextFloat() * 0.1F;
        this.mc.player.playSound(TCSounds.PECH_DICE, 0.5F, pitch);
    }
}

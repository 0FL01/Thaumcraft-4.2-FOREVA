package thaumcraft.client.gui;

import java.io.IOException;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.common.entities.golems.ContainerTravelingTrunk;
import thaumcraft.common.entities.golems.EntityTravelingTrunk;

public class GuiTravelingTrunk extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/guitrunkbase.png");

    private final EntityPlayer player;
    private final EntityTravelingTrunk trunk;

    public GuiTravelingTrunk(EntityPlayer player, EntityTravelingTrunk trunk) {
        super(new ContainerTravelingTrunk(player.inventory, player.world, trunk));
        this.player = player;
        this.trunk = trunk;
        this.ySize = 200;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        Entity owner = this.trunk.getOwner();
        String ownerName = owner instanceof EntityPlayer ? owner.getName() : this.player.getName();
        String title = ownerName + I18n.translateToLocal("entity.trunk.guiname");
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        this.fontRenderer.drawString(title, 16, 8, 12624112);
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        if (this.trunk.isDead && this.mc.player != null) {
            this.mc.player.closeScreen();
            return;
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        GlStateManager.enableBlend();
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        int health = Math.round(this.trunk.getHealth() / this.trunk.getMaxHealth() * 39.0F);
        health = MathHelper.clamp(health, 0, 39);
        this.drawTexturedModalRect(this.guiLeft + 134, this.guiTop + 2, 176, 16, health, 6);

        if (this.trunk.getUpgrade() == 1) {
            this.drawTexturedModalRect(this.guiLeft + 80, this.guiTop, 206, 0, this.xSize, 27);
        }

        if (this.trunk.getStay()) {
            this.drawTexturedModalRect(this.guiLeft + 112, this.guiTop, 176, 0, 10, 10);
        }
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (!isMouseIn(mouseX, mouseY, 112, 0, 10, 10)) return;

        playToggleSound(this.trunk.getStay());
        this.player.sendMessage(new TextComponentTranslation(
                this.trunk.getStay() ? "entity.trunk.move" : "entity.trunk.stay"));
        this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
    }

    @Override
    public void onGuiClosed() {
        this.trunk.setOpen(false);
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private boolean isMouseIn(int mouseX, int mouseY, int x, int y, int width, int height) {
        int relX = mouseX - (this.guiLeft + x);
        int relY = mouseY - (this.guiTop + y);
        return relX >= 0 && relY >= 0 && relX < width && relY <= height;
    }

    private void playToggleSound(boolean currentlyStay) {
        SoundEvent click = SoundEvents.UI_BUTTON_CLICK;
        this.player.playSound(click, 0.3F, 0.6F + (currentlyStay ? 0.0F : 0.2F));
    }
}

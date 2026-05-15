package thaumcraft.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.items.equipment.ItemElementalPickaxe;
import thaumcraft.common.items.wands.foci.FocusExcavation;
import thaumcraft.common.tiles.TileArcaneBore;

public class GuiArcaneBore extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("thaumcraft", "textures/gui/gui_arcanebore.png");

    private final TileArcaneBore bore;

    public GuiArcaneBore(InventoryPlayer playerInventory, TileArcaneBore bore) {
        super(new ContainerArcaneBore(playerInventory, bore));
        this.bore = bore;
        this.xSize = 176;
        this.ySize = 141;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

        ItemStack pickaxe = this.bore.getStackInSlot(1);
        if (!pickaxe.isEmpty() && pickaxe.isItemStackDamageable()
                && pickaxe.getItemDamage() + 1 >= pickaxe.getMaxDamage()) {
            this.drawTexturedModalRect(this.guiLeft + 74, this.guiTop + 18, 184, 0, 16, 16);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.guiLeft + 112), (float) (this.guiTop + 8), 505.0F);
        GlStateManager.scale(0.5F, 0.5F, 0.0F);
        this.fontRenderer.drawString("Width:" + (1 + (this.bore.area + this.bore.maxRadius) * 2), 0, 0, 0xFFFFFF);
        this.fontRenderer.drawString("Speed: +" + this.bore.speed, 0, 10, 0xFFFFFF);
        this.fontRenderer.drawString("Other properties:", 0, 24, 0xFFFFFF);

        int offset = 0;
        if (hasNativeClusters(pickaxe)) {
            this.fontRenderer.drawString("Native Clusters", 4, 34 + offset, 0xC0C0C0);
            offset += 9;
        }
        if (this.bore.fortune > 0) {
            this.fontRenderer.drawString("Fortune" + this.bore.fortune, 4, 34 + offset, 0xEECACA);
            offset += 9;
        }
        if (hasSilkTouch(pickaxe)) {
            this.fontRenderer.drawString("Silk Touch", 4, 34 + offset, 0x8080FF);
        }
        GlStateManager.popMatrix();
    }

    private boolean hasNativeClusters(ItemStack pickaxe) {
        if (!pickaxe.isEmpty() && pickaxe.getItem() instanceof ItemElementalPickaxe) {
            return true;
        }
        ItemStack focus = this.bore.getStackInSlot(0);
        return !focus.isEmpty()
                && focus.getItem() instanceof ItemFocusBasic
                && ((ItemFocusBasic) focus.getItem()).isUpgradedWith(focus, FocusExcavation.dowsing);
    }

    private boolean hasSilkTouch(ItemStack pickaxe) {
        if (!pickaxe.isEmpty() && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, pickaxe) > 0) {
            return true;
        }
        ItemStack focus = this.bore.getStackInSlot(0);
        return !focus.isEmpty()
                && focus.getItem() instanceof ItemFocusBasic
                && ((ItemFocusBasic) focus.getItem()).isUpgradedWith(focus, FocusUpgradeType.silktouch);
    }
}

package thaumcraft.api.wands;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;

public interface IWandFocus {
    public int getFocusColor();

    public TextureAtlasSprite getFocusDepthLayerIcon();

    public TextureAtlasSprite getOrnament();

    public WandFocusAnimation getAnimation();

    public AspectList getVisCost();

    public boolean isVisCostPerTick();

    public ItemStack onFocusRightClick(ItemStack var1, World var2, EntityPlayer var3, RayTraceResult var4);

    public void onUsingFocusTick(ItemStack var1, EntityPlayer var2, int var3);

    public void onPlayerStoppedUsingFocus(ItemStack var1, World var2, EntityPlayer var3, int var4);

    public String getSortingHelper(ItemStack var1);

    public boolean onFocusBlockStartBreak(ItemStack var1, int var2, int var3, int var4, EntityPlayer var5);

    public boolean acceptsEnchant(int var1);

    public static enum WandFocusAnimation {
        WAVE,
        CHARGE;

    }
}


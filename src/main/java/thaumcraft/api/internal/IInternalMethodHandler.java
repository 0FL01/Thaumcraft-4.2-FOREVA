package thaumcraft.api.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public interface IInternalMethodHandler {
    public void generateVisEffect(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8);

    public boolean isResearchComplete(String var1, String var2);

    public ItemStack getStackInRowAndColumn(Object var1, int var2, int var3);

    public AspectList getObjectAspects(ItemStack var1);

    public AspectList getBonusObjectTags(ItemStack var1, AspectList var2);

    public AspectList generateTags(Item var1, int var2);

    public boolean consumeVisFromWand(ItemStack var1, EntityPlayer var2, AspectList var3, boolean var4, boolean var5);

    public boolean consumeVisFromWandCrafting(ItemStack var1, EntityPlayer var2, AspectList var3, boolean var4);

    public boolean consumeVisFromInventory(EntityPlayer var1, AspectList var2);

    public void addWarpToPlayer(EntityPlayer var1, int var2, boolean var3);

    public void addStickyWarpToPlayer(EntityPlayer var1, int var2);

    public boolean hasDiscoveredAspect(String var1, Aspect var2);

    public AspectList getDiscoveredAspects(String var1);
}


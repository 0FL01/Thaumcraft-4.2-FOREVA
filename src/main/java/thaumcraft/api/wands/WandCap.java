package thaumcraft.api.wands;

import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

public class WandCap {
    private String tag;
    private int craftCost;
    float baseCostModifier;
    List<Aspect> specialCostModifierAspects;
    float specialCostModifier;
    ResourceLocation texture;
    ItemStack item;
    public static LinkedHashMap<String, WandCap> caps = new LinkedHashMap();

    public WandCap(String tag, float discount, ItemStack item, int craftCost) {
        this.setTag(tag);
        this.baseCostModifier = discount;
        this.specialCostModifierAspects = null;
        this.texture = new ResourceLocation("thaumcraft", "textures/models/wand_cap_" + this.getTag() + ".png");
        this.item = item;
        this.setCraftCost(craftCost);
        caps.put(tag, this);
    }

    public WandCap(String tag, float discount, List<Aspect> specialAspects, float discountSpecial, ItemStack item, int craftCost) {
        this.setTag(tag);
        this.baseCostModifier = discount;
        this.specialCostModifierAspects = specialAspects;
        this.specialCostModifier = discountSpecial;
        this.texture = new ResourceLocation("thaumcraft", "textures/models/wand_cap_" + this.getTag() + ".png");
        this.item = item;
        this.setCraftCost(craftCost);
        caps.put(tag, this);
    }

    public float getBaseCostModifier() {
        return this.baseCostModifier;
    }

    public List<Aspect> getSpecialCostModifierAspects() {
        return this.specialCostModifierAspects;
    }

    public float getSpecialCostModifier() {
        return this.specialCostModifier;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getCraftCost() {
        return this.craftCost;
    }

    public void setCraftCost(int craftCost) {
        this.craftCost = craftCost;
    }

    public String getResearch() {
        return "CAP_" + this.getTag();
    }
}


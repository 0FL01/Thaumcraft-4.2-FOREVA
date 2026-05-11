package thaumcraft.api.wands;

import java.util.LinkedHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.wands.IWandRodOnUpdate;

public class WandRod {
    private String tag;
    private int craftCost;
    int capacity;
    protected ResourceLocation texture;
    ItemStack item;
    IWandRodOnUpdate onUpdate;
    boolean glow;
    public static LinkedHashMap<String, WandRod> rods = new LinkedHashMap();

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, ResourceLocation texture) {
        this.setTag(tag);
        this.capacity = capacity;
        this.texture = texture;
        this.item = item;
        this.setCraftCost(craftCost);
        rods.put(tag, this);
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, IWandRodOnUpdate onUpdate, ResourceLocation texture) {
        this.setTag(tag);
        this.capacity = capacity;
        this.texture = texture;
        this.item = item;
        this.setCraftCost(craftCost);
        rods.put(tag, this);
        this.onUpdate = onUpdate;
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost) {
        this.setTag(tag);
        this.capacity = capacity;
        this.texture = new ResourceLocation("thaumcraft", "textures/models/wand_rod_" + this.getTag() + ".png");
        this.item = item;
        this.setCraftCost(craftCost);
        rods.put(tag, this);
    }

    public WandRod(String tag, int capacity, ItemStack item, int craftCost, IWandRodOnUpdate onUpdate) {
        this.setTag(tag);
        this.capacity = capacity;
        this.texture = new ResourceLocation("thaumcraft", "textures/models/wand_rod_" + this.getTag() + ".png");
        this.item = item;
        this.setCraftCost(craftCost);
        rods.put(tag, this);
        this.onUpdate = onUpdate;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
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

    public IWandRodOnUpdate getOnUpdate() {
        return this.onUpdate;
    }

    public void setOnUpdate(IWandRodOnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }

    public boolean isGlowing() {
        return this.glow;
    }

    public void setGlowing(boolean hasGlow) {
        this.glow = hasGlow;
    }

    public String getResearch() {
        return "ROD_" + this.getTag();
    }
}


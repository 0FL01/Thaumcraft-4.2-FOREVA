package thaumcraft.api.research;

import java.util.Arrays;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

/** TC6 research data projected into the canonical TC4 research registry. */
public class ResearchEntry {

    String key;
    String category;
    String name;
    String[] parents;
    String[] siblings;
    int displayColumn;
    int displayRow;
    Object[] icons;
    EnumResearchMeta[] meta;
    ItemStack[] rewardItem;
    ResearchStage.Knowledge[] rewardKnow;
    private ResearchStage[] stages;
    private ResearchAddendum[] addenda;

    public enum EnumResearchMeta {
        ROUND,
        SPIKY,
        REVERSE,
        HIDDEN,
        AUTOUNLOCK,
        HEX
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return I18n.translateToLocal(name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getParents() {
        return parents;
    }

    public String[] getParentsClean() {
        if (parents == null) {
            return null;
        }
        return Arrays.stream(parents)
                .filter(parent -> parent != null && !parent.startsWith("!"))
                .toArray(String[]::new);
    }

    public String[] getParentsStripped() {
        if (parents == null) {
            return null;
        }
        return Arrays.stream(parents)
                .map(parent -> parent == null ? null : parent.replaceFirst("^[!~]", "").split("@")[0])
                .toArray(String[]::new);
    }

    protected final void setParentsValue(String[] parents) {
        this.parents = parents;
    }

    public String[] getSiblings() {
        return siblings;
    }

    protected final void setSiblingsValue(String[] siblings) {
        this.siblings = siblings;
    }

    public int getDisplayColumn() {
        return displayColumn;
    }

    public void setDisplayColumn(int displayColumn) {
        this.displayColumn = displayColumn;
    }

    public int getDisplayRow() {
        return displayRow;
    }

    public void setDisplayRow(int displayRow) {
        this.displayRow = displayRow;
    }

    public Object[] getIcons() {
        return icons;
    }

    public void setIcons(Object[] icons) {
        this.icons = icons;
    }

    public EnumResearchMeta[] getMeta() {
        return meta;
    }

    public boolean hasMeta(EnumResearchMeta type) {
        return meta != null && Arrays.asList(meta).contains(type);
    }

    public void setMeta(EnumResearchMeta[] meta) {
        this.meta = meta;
    }

    public ResearchStage[] getStages() {
        return stages;
    }

    public void setStages(ResearchStage[] stages) {
        this.stages = stages;
    }

    public ItemStack[] getRewardItem() {
        return rewardItem;
    }

    public void setRewardItem(ItemStack[] rewardItem) {
        this.rewardItem = rewardItem;
    }

    public ResearchStage.Knowledge[] getRewardKnow() {
        return rewardKnow;
    }

    public void setRewardKnow(ResearchStage.Knowledge[] rewardKnow) {
        this.rewardKnow = rewardKnow;
    }

    public ResearchAddendum[] getAddenda() {
        return addenda;
    }

    public void setAddenda(ResearchAddendum[] addenda) {
        this.addenda = addenda;
    }
}

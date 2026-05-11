package thaumcraft.api.research;

import java.util.function.BiConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchPage;

public class ResearchItem {
    public final String key;
    public final String category;
    public final AspectList tags;
    public String[] parents = null;
    public String[] parentsHidden = null;
    public String[] siblings = null;
    public final int displayColumn;
    public final int displayRow;
    public final ItemStack icon_item;
    public final ResourceLocation icon_resource;
    private int complexity;
    private boolean isSpecial;
    private boolean isSecondary;
    private boolean isRound;
    private boolean isStub;
    private boolean isVirtual;
    private boolean isConcealed;
    private boolean isHidden;
    private boolean isLost;
    private boolean isAutoUnlock;
    private ItemStack[] itemTriggers;
    private String[] entityTriggers;
    private Aspect[] aspectTriggers;
    private ResearchPage[] pages = null;

    /**
     * Callback fired when this research is completed by a player.
     * Parameters: (EntityPlayer player, String researchKey)
     */
    public BiConsumer<EntityPlayer, String> onResearchComplete = null;

    public ResearchItem(String key, String category) {
        this.key = key;
        this.category = category;
        this.tags = new AspectList();
        this.icon_resource = null;
        this.icon_item = null;
        this.displayColumn = 0;
        this.displayRow = 0;
        this.setVirtual();
    }

    public ResearchItem(String key, String category, AspectList tags, int col, int row, int complex, ResourceLocation icon) {
        this.key = key;
        this.category = category;
        this.tags = tags;
        this.icon_resource = icon;
        this.icon_item = null;
        this.displayColumn = col;
        this.displayRow = row;
        this.complexity = complex;
        if (this.complexity < 1) {
            this.complexity = 1;
        }
        if (this.complexity > 3) {
            this.complexity = 3;
        }
    }

    public ResearchItem(String key, String category, AspectList tags, int col, int row, int complex, ItemStack icon) {
        this.key = key;
        this.category = category;
        this.tags = tags;
        this.icon_item = icon;
        this.icon_resource = null;
        this.displayColumn = col;
        this.displayRow = row;
        this.complexity = complex;
        if (this.complexity < 1) {
            this.complexity = 1;
        }
        if (this.complexity > 3) {
            this.complexity = 3;
        }
    }

    public ResearchItem setSpecial() {
        this.isSpecial = true;
        return this;
    }

    public ResearchItem setStub() {
        this.isStub = true;
        return this;
    }

    public ResearchItem setLost() {
        this.isLost = true;
        return this;
    }

    public ResearchItem setConcealed() {
        this.isConcealed = true;
        return this;
    }

    public ResearchItem setHidden() {
        this.isHidden = true;
        return this;
    }

    public ResearchItem setVirtual() {
        this.isVirtual = true;
        return this;
    }

    public ResearchItem setParents(String ... par) {
        this.parents = par;
        return this;
    }

    public ResearchItem setParentsHidden(String ... par) {
        this.parentsHidden = par;
        return this;
    }

    public ResearchItem setSiblings(String ... sib) {
        this.siblings = sib;
        return this;
    }

    public ResearchItem setPages(ResearchPage ... par) {
        this.pages = par;
        return this;
    }

    public ResearchPage[] getPages() {
        return this.pages;
    }

    public ResearchItem setItemTriggers(ItemStack ... par) {
        this.itemTriggers = par;
        return this;
    }

    public ResearchItem setEntityTriggers(String ... par) {
        this.entityTriggers = par;
        return this;
    }

    public ResearchItem setAspectTriggers(Aspect ... par) {
        this.aspectTriggers = par;
        return this;
    }

    public ItemStack[] getItemTriggers() {
        return this.itemTriggers;
    }

    public String[] getEntityTriggers() {
        return this.entityTriggers;
    }

    public Aspect[] getAspectTriggers() {
        return this.aspectTriggers;
    }

    public ResearchItem registerResearchItem() {
        ResearchCategories.addResearch(this);
        return this;
    }

    public String getName() {
        return I18n.translateToLocal((String)("tc.research_name." + this.key));
    }

    public String getText() {
        return I18n.translateToLocal((String)("tc.research_text." + this.key));
    }

    public boolean isSpecial() {
        return this.isSpecial;
    }

    public boolean isStub() {
        return this.isStub;
    }

    public boolean isLost() {
        return this.isLost;
    }

    public boolean isConcealed() {
        return this.isConcealed;
    }

    public boolean isHidden() {
        return this.isHidden;
    }

    public boolean isVirtual() {
        return this.isVirtual;
    }

    public boolean isAutoUnlock() {
        return this.isAutoUnlock;
    }

    public ResearchItem setAutoUnlock() {
        this.isAutoUnlock = true;
        return this;
    }

    public boolean isRound() {
        return this.isRound;
    }

    public ResearchItem setRound() {
        this.isRound = true;
        return this;
    }

    public boolean isSecondary() {
        return this.isSecondary;
    }

    public ResearchItem setSecondary() {
        this.isSecondary = true;
        return this;
    }

    public int getComplexity() {
        return this.complexity;
    }

    public ResearchItem setComplexity(int complexity) {
        this.complexity = complexity;
        return this;
    }

    public Aspect getResearchPrimaryTag() {
        Aspect aspect = null;
        int highest = 0;
        if (this.tags != null) {
            for (Aspect tag : this.tags.getAspects()) {
                if (this.tags.getAmount(tag) <= highest) continue;
                aspect = tag;
                highest = this.tags.getAmount(tag);
            }
        }
        return aspect;
    }
}


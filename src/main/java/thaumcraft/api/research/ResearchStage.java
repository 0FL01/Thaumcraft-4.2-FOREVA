package thaumcraft.api.research;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;

public class ResearchStage {

    private String text;
    private ResourceLocation[] recipes;
    private Object[] obtain;
    private Object[] craft;
    private int[] craftReference;
    private Knowledge[] know;
    private String[] research;
    private String[] researchIcon;
    private int warp;

    public static class Knowledge {
        public EnumKnowledgeType type;
        public ResearchCategory category;
        public int amount;

        public Knowledge(EnumKnowledgeType type, ResearchCategory category, int amount) {
            this.type = type;
            this.category = category;
            this.amount = amount;
        }
    }

    public String getText() {
        return text;
    }

    public String getTextLocalized() {
        return I18n.translateToLocal(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public ResourceLocation[] getRecipes() {
        return recipes;
    }

    public void setRecipes(ResourceLocation[] recipes) {
        this.recipes = recipes;
    }

    public Object[] getObtain() {
        return obtain;
    }

    public void setObtain(Object[] obtain) {
        this.obtain = obtain;
    }

    public Object[] getCraft() {
        return craft;
    }

    public void setCraft(Object[] craft) {
        this.craft = craft;
    }

    public int[] getCraftReference() {
        return craftReference;
    }

    public void setCraftReference(int[] craftReference) {
        this.craftReference = craftReference;
    }

    public Knowledge[] getKnow() {
        return know;
    }

    public void setKnow(Knowledge[] know) {
        this.know = know;
    }

    public String[] getResearch() {
        return research;
    }

    public void setResearch(String[] research) {
        this.research = research;
    }

    public String[] getResearchIcon() {
        return researchIcon;
    }

    public void setResearchIcon(String[] researchIcon) {
        this.researchIcon = researchIcon;
    }

    public int getWarp() {
        return warp;
    }

    public void setWarp(int warp) {
        this.warp = warp;
    }
}

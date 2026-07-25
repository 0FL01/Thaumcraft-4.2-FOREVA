package thaumcraft.api.research;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class ResearchAddendum {
    private String text;
    private ResourceLocation[] recipes;
    private String[] research;

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

    public String[] getResearch() {
        return research;
    }

    public void setResearch(String[] research) {
        this.research = research;
    }
}

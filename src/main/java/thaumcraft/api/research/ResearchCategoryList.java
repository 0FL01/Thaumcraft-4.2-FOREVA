package thaumcraft.api.research;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchItem;

public class ResearchCategoryList {
    public int minDisplayColumn;
    public int minDisplayRow;
    public int maxDisplayColumn;
    public int maxDisplayRow;
    public ResourceLocation icon;
    public ResourceLocation background;
    public Map<String, ResearchItem> research = new HashMap<String, ResearchItem>();

    public ResearchCategoryList(ResourceLocation icon, ResourceLocation background) {
        this.icon = icon;
        this.background = background;
    }
}


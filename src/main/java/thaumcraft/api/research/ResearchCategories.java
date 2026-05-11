package thaumcraft.api.research;

import net.minecraftforge.fml.common.FMLLog;
import java.util.Collection;
import java.util.LinkedHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import org.apache.logging.log4j.Level;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;

public class ResearchCategories {
    public static LinkedHashMap<String, ResearchCategoryList> researchCategories = new LinkedHashMap();

    public static ResearchCategoryList getResearchList(String key) {
        return researchCategories.get(key);
    }

    public static String getCategoryName(String key) {
        return I18n.translateToLocal((String)("tc.research_category." + key));
    }

    public static ResearchItem getResearch(String key) {
        Collection<ResearchCategoryList> rc = researchCategories.values();
        for (ResearchCategoryList cat : rc) {
            Collection<ResearchItem> rl = cat.research.values();
            for (ResearchItem ri : rl) {
                if (!ri.key.equals(key)) continue;
                return ri;
            }
        }
        return null;
    }

    public static void registerCategory(String key, ResourceLocation icon, ResourceLocation background) {
        if (ResearchCategories.getResearchList(key) == null) {
            ResearchCategoryList rl = new ResearchCategoryList(icon, background);
            researchCategories.put(key, rl);
        }
    }

    public static void addResearch(ResearchItem ri) {
        ResearchCategoryList rl = ResearchCategories.getResearchList(ri.category);
        if (rl != null && !rl.research.containsKey(ri.key)) {
            if (!ri.isVirtual()) {
                for (ResearchItem rr : rl.research.values()) {
                    if (rr.displayColumn != ri.displayColumn || rr.displayRow != ri.displayRow) continue;
                    FMLLog.log((Level)Level.FATAL, (String)("[Thaumcraft] Research [" + ri.getName() + "] not added as it overlaps with existing research [" + rr.getName() + "]"), (Object[])new Object[0]);
                    return;
                }
            }
            rl.research.put(ri.key, ri);
            if (ri.displayColumn < rl.minDisplayColumn) {
                rl.minDisplayColumn = ri.displayColumn;
            }
            if (ri.displayRow < rl.minDisplayRow) {
                rl.minDisplayRow = ri.displayRow;
            }
            if (ri.displayColumn > rl.maxDisplayColumn) {
                rl.maxDisplayColumn = ri.displayColumn;
            }
            if (ri.displayRow > rl.maxDisplayRow) {
                rl.maxDisplayRow = ri.displayRow;
            }
        }
    }
}


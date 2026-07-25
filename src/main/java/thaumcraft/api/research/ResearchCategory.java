package thaumcraft.api.research;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/** TC6 category data backed by the canonical TC4 category map. */
public class ResearchCategory extends ResearchCategoryList {

    public ResourceLocation background2;
    public String researchKey;
    public String key;
    public AspectList formula;

    public ResearchCategory(String key, String researchKey, AspectList formula,
                            ResourceLocation icon, ResourceLocation background) {
        this(key, researchKey, formula, icon, background, null);
    }

    public ResearchCategory(String key, String researchKey, AspectList formula,
                            ResourceLocation icon, ResourceLocation background,
                            ResourceLocation background2) {
        super(icon, background);
        this.key = key;
        this.researchKey = researchKey;
        this.formula = formula;
        this.background2 = background2;
    }

    public int applyFormula(AspectList input) {
        return applyFormula(input, 1.0D);
    }

    public int applyFormula(AspectList input, double multiplier) {
        if (formula == null) {
            return 0;
        }
        double total = 0.0D;
        for (Aspect aspect : formula.getAspects()) {
            total += multiplier * multiplier * input.getAmount(aspect)
                    * formula.getAmount(aspect) / 10.0D;
        }
        return (int) Math.ceil(total > 0.0D ? Math.sqrt(total) : total);
    }
}

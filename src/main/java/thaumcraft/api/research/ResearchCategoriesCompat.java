package thaumcraft.api.research;

public class ResearchCategoriesCompat {

    public static ResearchEntry getResearch(String key) {
        return ResearchCategories.getResearch(key);
    }
}

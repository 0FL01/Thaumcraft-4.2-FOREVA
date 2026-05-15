package thaumcraft.common.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigResearch {
    public static final Map<String, Object> recipes = new HashMap<>();

    public static void init() {
        // Stage 9 research/category/page registration remains in progress.
        recipes.clear();
    }
}

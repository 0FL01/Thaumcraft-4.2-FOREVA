package thaumcraft.api.research;

import net.minecraft.util.ResourceLocation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ResearchCategoryAddonCompatibilityTest {

    private LinkedHashMap<String, ResearchCategoryList> oldCategories;

    @Before
    public void saveCategories() {
        oldCategories = new LinkedHashMap<>(ResearchCategories.researchCategories);
        ResearchCategories.researchCategories.clear();
    }

    @After
    public void restoreCategories() {
        ResearchCategories.researchCategories.clear();
        ResearchCategories.researchCategories.putAll(oldCategories);
    }

    @Test
    public void legacyCategoryRegistrationExposesTc6CategoryData() {
        ResourceLocation icon = new ResourceLocation("thaumcraft", "textures/test/icon.png");
        ResourceLocation background = new ResourceLocation("thaumcraft", "textures/test/background.png");

        ResearchCategories.registerCategory("BASICS", icon, background);

        ResearchCategory category = ResearchCategories.getResearchCategory("BASICS");
        assertNotNull(category);
        assertEquals("BASICS", category.key);
        assertEquals(icon, category.icon);
        assertEquals(background, category.background);
    }

    @Test
    public void builtInCategoriesArePublishedDuringInitialization() throws IOException {
        String source = new String(Files.readAllBytes(
                Paths.get("src/main/java/thaumcraft/common/Thaumcraft.java")), StandardCharsets.UTF_8);
        int init = source.indexOf("public void init(FMLInitializationEvent event)");
        int postInit = source.indexOf("public void postInit(FMLPostInitializationEvent event)");
        int categories = source.indexOf("ConfigResearch.initCategories();", init);

        assertTrue("TC6 addons must see built-in research categories during init",
                init >= 0 && categories > init && categories < postInit);
    }
}

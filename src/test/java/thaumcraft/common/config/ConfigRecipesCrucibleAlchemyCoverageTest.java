package thaumcraft.common.config;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ConfigRecipesCrucibleAlchemyCoverageTest {

    @Test
    public void configRecipesContainsExtendedCrucibleAlchemyBaseline() throws IOException {
        String source = ConfigRecipesSourceReader.readMergedSource();

        assertTrue("Missing balanced shard registration baseline", source.contains("BalancedShard_\" + a"));
        assertTrue("Missing AltGunpowder crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltGunpowder\""));
        assertTrue("Missing AltSlime crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltSlime\""));
        assertTrue("Missing AltClay crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltClay\""));
        assertTrue("Missing AltGlowstone crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltGlowstone\""));
        assertTrue("Missing AltInk crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltInk\""));
        assertTrue("Missing AltWeb crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltWeb\""));
        assertTrue("Missing AltMossyCobble crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltMossyCobble\""));
        assertTrue("Missing AltIce crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltIce\""));
        assertTrue("Missing AltCrackedBrick crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltCrackedBrick\""));
        assertTrue("Missing AltBonemeal crucible baseline", source.contains("ConfigResearch.recipes.put(\"AltBonemeal\""));

        assertTrue("Missing PureIron crucible baseline", source.contains("ConfigResearch.recipes.put(\"PureIron\""));
        assertTrue("Missing PureGold crucible baseline", source.contains("ConfigResearch.recipes.put(\"PureGold\""));
        assertTrue("Missing TransIron crucible baseline", source.contains("ConfigResearch.recipes.put(\"TransIron\""));
        assertTrue("Missing TransGold crucible baseline", source.contains("ConfigResearch.recipes.put(\"TransGold\""));

        assertTrue("Missing EtherealBloom crucible baseline", source.contains("ConfigResearch.recipes.put(\"EtherealBloom\""));
        assertTrue("Missing LiquidDeath crucible baseline", source.contains("ConfigResearch.recipes.put(\"LiquidDeath\""));
        assertTrue("Missing BottleTaint crucible baseline", source.contains("ConfigResearch.recipes.put(\"BottleTaint\""));
        assertTrue("Missing CoreGather crucible baseline", source.contains("ConfigResearch.recipes.put(\"CoreGather\""));
        assertTrue("Missing BathSalts crucible baseline", source.contains("ConfigResearch.recipes.put(\"BathSalts\""));
        assertTrue("Missing SaneSoap crucible baseline", source.contains("ConfigResearch.recipes.put(\"SaneSoap\""));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

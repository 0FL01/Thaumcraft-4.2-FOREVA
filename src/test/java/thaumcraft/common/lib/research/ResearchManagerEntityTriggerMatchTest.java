package thaumcraft.common.lib.research;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ResearchManagerEntityTriggerMatchTest {

    @Test
    public void matchesLegacyThaumcraftTriggerToNamespacedEntityKey() {
        assertTrue(ResearchManager.entityTriggerMatches("Thaumcraft.Firebat", "thaumcraft:firebat"));
        assertTrue(ResearchManager.entityTriggerMatches("Thaumcraft.PrimalOrb", "thaumcraft:primalorb"));
    }

    @Test
    public void matchesVanillaShortTriggerToNamespacedEntityKey() {
        assertTrue(ResearchManager.entityTriggerMatches("Enderman", "minecraft:enderman"));
        assertTrue(ResearchManager.entityTriggerMatches("Creeper", "minecraft:creeper"));
    }

    @Test
    public void rejectsDifferentEntitiesAndInvalidInputs() {
        assertFalse(ResearchManager.entityTriggerMatches("Thaumcraft.Firebat", "thaumcraft:brainyzombie"));
        assertFalse(ResearchManager.entityTriggerMatches("Enderman", "minecraft:zombie"));
        assertFalse(ResearchManager.entityTriggerMatches(null, "minecraft:enderman"));
        assertFalse(ResearchManager.entityTriggerMatches("Enderman", null));
        assertFalse(ResearchManager.entityTriggerMatches(" ", "minecraft:enderman"));
        assertFalse(ResearchManager.entityTriggerMatches("Enderman", " "));
    }
}

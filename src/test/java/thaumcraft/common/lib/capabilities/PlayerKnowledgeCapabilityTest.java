package thaumcraft.common.lib.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PlayerKnowledgeCapabilityTest {

    @Test
    public void nbtRoundTripPreservesStage3State() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.setInitializedAspects(true);
        knowledge.setAspectPool(Aspect.AIR, 17);
        knowledge.addDiscoveredAspect(Aspect.MAGIC.getTag());
        knowledge.setAspectPool(Aspect.MAGIC, 3);
        knowledge.scanItem("@123");
        knowledge.scanEntity("@456");
        knowledge.scanPhenomena("@NODE0:1:2:3");
        knowledge.addResearch("FIRSTSTEPS");
        knowledge.setWarpPerm(5);
        knowledge.setWarpSticky(2);
        knowledge.setWarpTemp(1);
        knowledge.setWarpCounter(9);
        knowledge.setRunicCharge(4);

        NBTTagCompound nbt = knowledge.serializeNBT();
        PlayerKnowledgeCapability copy = new PlayerKnowledgeCapability();
        copy.deserializeNBT(nbt);

        assertTrue(copy.hasInitializedAspects());
        assertEquals(17, copy.getAspectPoolFor(Aspect.AIR));
        assertTrue(copy.hasDiscoveredAspect(Aspect.MAGIC));
        assertEquals(3, copy.getAspectPoolFor(Aspect.MAGIC));
        assertTrue(copy.hasScannedItem("@123"));
        assertTrue(copy.hasScannedEntity("@456"));
        assertTrue(copy.hasScannedPhenomena("@NODE0:1:2:3"));
        assertTrue(copy.isResearchComplete("FIRSTSTEPS"));
        assertEquals(5, copy.getWarpPerm());
        assertEquals(2, copy.getWarpSticky());
        assertEquals(1, copy.getWarpTemp());
        assertEquals(9, copy.getWarpCounter());
        assertFalse(nbt.hasKey("runicCharge"));
        assertEquals(0, copy.getRunicCharge());
    }

    @Test
    public void deserializeReplacesPriorSets() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.scanItem("OLD");
        knowledge.scanEntity("OLD");
        knowledge.scanPhenomena("OLD");
        knowledge.addResearch("OLD");
        knowledge.setAspectPool(Aspect.AIR, 10);

        PlayerKnowledgeCapability incoming = new PlayerKnowledgeCapability();
        incoming.scanItem("NEW");
        incoming.scanEntity("NEW");
        incoming.scanPhenomena("NEW");
        incoming.addResearch("NEW");
        incoming.setAspectPool(Aspect.FIRE, 7);

        knowledge.deserializeNBT(incoming.serializeNBT());

        assertFalse(knowledge.hasScannedItem("OLD"));
        assertFalse(knowledge.hasScannedEntity("OLD"));
        assertFalse(knowledge.hasScannedPhenomena("OLD"));
        assertFalse(knowledge.isResearchComplete("OLD"));
        assertEquals(0, knowledge.getAspectPoolFor(Aspect.AIR));
        assertTrue(knowledge.hasScannedItem("NEW"));
        assertTrue(knowledge.hasScannedEntity("NEW"));
        assertTrue(knowledge.hasScannedPhenomena("NEW"));
        assertTrue(knowledge.isResearchComplete("NEW"));
        assertEquals(7, knowledge.getAspectPoolFor(Aspect.FIRE));
    }

    @Test
    public void hashScanKeyReplacesAtKeyWithHashKey() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.scanItem("@123");
        knowledge.scanItem("#123");
        assertFalse(knowledge.hasScannedItem("@123"));
        assertTrue(knowledge.hasScannedItem("#123"));
    }

    @Test
    public void warpTotalsAndRunicClampButCounterKeepsSignedValue() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.setWarpPerm(-1);
        knowledge.setWarpSticky(-2);
        knowledge.setWarpTemp(-3);
        knowledge.setWarpCounter(-4);
        knowledge.setRunicCharge(-5);

        assertEquals(0, knowledge.getWarpPerm());
        assertEquals(0, knowledge.getWarpSticky());
        assertEquals(0, knowledge.getWarpTemp());
        assertEquals(-4, knowledge.getWarpCounter());
        assertEquals(0, knowledge.getRunicCharge());
    }

    @Test
    public void signedAspectPoolsKeepTc4MutationAndPersistenceSemantics() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        assertTrue(knowledge.setAspectPool(Aspect.AIR, -3));
        assertEquals(-3, knowledge.getAspectPoolFor(Aspect.AIR));
        assertTrue(knowledge.addAspectPool(Aspect.AIR, 5));
        assertEquals(2, knowledge.getAspectPoolFor(Aspect.AIR));

        assertTrue(knowledge.addAspectPool(Aspect.AIR, -5));
        assertEquals("TC4 reports overdraw success without changing the pool",
                2, knowledge.getAspectPoolFor(Aspect.AIR));
        assertTrue(knowledge.addAspectPool(Aspect.AIR, -2));
        assertEquals(0, knowledge.getAspectPoolFor(Aspect.AIR));
        assertFalse(knowledge.addAspectPool(Aspect.AIR, -1));

        AspectList incoming = new AspectList();
        incoming.aspects.put(Aspect.FIRE, -7);
        knowledge.setAspectsDiscovered(incoming);
        knowledge.setWarpCounter(-9);

        PlayerKnowledgeCapability copy = new PlayerKnowledgeCapability();
        copy.deserializeNBT(knowledge.serializeNBT());
        assertEquals(-7, copy.getAspectPoolFor(Aspect.FIRE));
        assertEquals(-9, copy.getWarpCounter());
        assertFalse(copy.addAspectPool(Aspect.FIRE, -1));
        assertEquals(-7, copy.getAspectPoolFor(Aspect.FIRE));
    }

    @Test
    public void legacyRunicChargeIsIgnoredOnLoad() {
        NBTTagCompound legacy = new NBTTagCompound();
        legacy.setInteger("runicCharge", 12);
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.setRunicCharge(3);

        knowledge.deserializeNBT(legacy);

        assertEquals(0, knowledge.getRunicCharge());
    }

    @Test
    public void researchSerializationFiltersUnknownAutoUnlockAndRedundantClues() {
        Map<String, ResearchCategoryList> oldCategories = new LinkedHashMap<>(ResearchCategories.researchCategories);
        try {
            ResearchCategories.researchCategories.clear();
            ResearchCategories.registerCategory("TEST", new ResourceLocation("thaumcraft", "textures/test/icon.png"),
                    new ResourceLocation("thaumcraft", "textures/test/background.png"));
            new ResearchItem("VALID", "TEST", new AspectList().add(Aspect.AIR, 1), 0, 0, 1,
                    new ResourceLocation("thaumcraft", "textures/test/research.png")).registerResearchItem();
            new ResearchItem("CLUE", "TEST", new AspectList().add(Aspect.FIRE, 1), 1, 0, 1,
                    new ResourceLocation("thaumcraft", "textures/test/clue.png")).setHidden().registerResearchItem();
            new ResearchItem("AUTO", "TEST", new AspectList().add(Aspect.ORDER, 1), 2, 0, 1,
                    new ResourceLocation("thaumcraft", "textures/test/auto.png")).setAutoUnlock().registerResearchItem();

            PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
            knowledge.addResearch("VALID");
            knowledge.addResearch("@VALID");
            knowledge.addResearch("@CLUE");
            knowledge.addResearch("AUTO");
            knowledge.addResearch("STALE");

            PlayerKnowledgeCapability copy = new PlayerKnowledgeCapability();
            copy.deserializeNBT(knowledge.serializeNBT());

            assertTrue(copy.isResearchComplete("VALID"));
            assertTrue(copy.isResearchComplete("@CLUE"));
            assertFalse(copy.isResearchComplete("@VALID"));
            assertFalse(copy.isResearchComplete("AUTO"));
            assertFalse(copy.isResearchComplete("STALE"));
        } finally {
            ResearchCategories.researchCategories.clear();
            ResearchCategories.researchCategories.putAll(oldCategories);
        }
    }
}

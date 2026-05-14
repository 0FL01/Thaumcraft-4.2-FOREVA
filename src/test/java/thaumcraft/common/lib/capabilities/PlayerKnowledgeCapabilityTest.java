package thaumcraft.common.lib.capabilities;

import net.minecraft.nbt.NBTTagCompound;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;

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
        assertEquals(4, copy.getRunicCharge());
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
    public void negativeWarpAndRunicValuesClampToZero() {
        PlayerKnowledgeCapability knowledge = new PlayerKnowledgeCapability();
        knowledge.setWarpPerm(-1);
        knowledge.setWarpSticky(-2);
        knowledge.setWarpTemp(-3);
        knowledge.setWarpCounter(-4);
        knowledge.setRunicCharge(-5);

        assertEquals(0, knowledge.getWarpPerm());
        assertEquals(0, knowledge.getWarpSticky());
        assertEquals(0, knowledge.getWarpTemp());
        assertEquals(0, knowledge.getWarpCounter());
        assertEquals(0, knowledge.getRunicCharge());
    }
}

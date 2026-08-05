package thaumcraft.common.blocks;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BlockAiryNodeHarvestParityTest {

    @Test
    public void nodeEssenceDropCountKeepsTc4235InclusiveUpperBound() {
        assertEquals(0, BlockAiry.getNodeEssenceDropCount(4));
        assertEquals(1, BlockAiry.getNodeEssenceDropCount(5));
        assertEquals(1, BlockAiry.getNodeEssenceDropCount(9));
        assertEquals(2, BlockAiry.getNodeEssenceDropCount(10));
        assertEquals(2, BlockAiry.getNodeEssenceDropCount(19));
        assertEquals(3, BlockAiry.getNodeEssenceDropCount(20));
    }
}

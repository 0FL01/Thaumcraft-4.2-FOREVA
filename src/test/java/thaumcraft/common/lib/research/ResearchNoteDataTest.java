package thaumcraft.common.lib.research;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.HexUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ResearchNoteDataTest {

    @Test
    public void noteDataRoundTripsOriginalNbtKeys() {
        NBTTagCompound tag = new NBTTagCompound();
        ResearchNoteData data = new ResearchNoteData();
        data.key = "ALCHEMY";
        data.color = 0x123456;
        data.complete = true;
        data.copies = 2;
        HexUtils.Hex hex = new HexUtils.Hex(-1, 2);
        data.hexes.put(hex.toString(), hex);
        data.hexEntries.put(hex.toString(), new ResearchManager.HexEntry(Aspect.AIR, 1));

        ResearchManager.writeNoteData(tag, data);

        assertEquals("ALCHEMY", tag.getString("key"));
        assertEquals(0x123456, tag.getInteger("color"));
        assertTrue(tag.getBoolean("complete"));
        assertEquals(2, tag.getInteger("copies"));
        NBTTagList hexGrid = tag.getTagList("hexgrid", Constants.NBT.TAG_COMPOUND);
        assertEquals(1, hexGrid.tagCount());
        assertEquals(-1, hexGrid.getCompoundTagAt(0).getByte("hexq"));
        assertEquals(2, hexGrid.getCompoundTagAt(0).getByte("hexr"));
        assertEquals(1, hexGrid.getCompoundTagAt(0).getByte("type"));
        assertEquals(Aspect.AIR.getTag(), hexGrid.getCompoundTagAt(0).getString("aspect"));

        ResearchNoteData copy = ResearchManager.readNoteData(tag);
        assertEquals("ALCHEMY", copy.key);
        assertEquals(0x123456, copy.color);
        assertTrue(copy.complete);
        assertEquals(2, copy.copies);
        assertEquals(1, copy.hexEntries.size());
        assertEquals(Aspect.AIR, copy.hexEntries.get(hex.toString()).aspect);
        assertEquals(1, copy.hexEntries.get(hex.toString()).type);
    }

    @Test
    public void readNoteDataRejectsNullNbt() {
        assertNull(ResearchManager.readNoteData(null));
    }
}

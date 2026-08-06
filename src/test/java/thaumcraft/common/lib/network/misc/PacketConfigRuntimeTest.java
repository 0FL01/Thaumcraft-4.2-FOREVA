package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import thaumcraft.common.config.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PacketConfigRuntimeTest {
    private final boolean[] workingBooleans = new boolean[5];
    private final boolean[] clientBooleans = new boolean[5];
    private int workingDifficulty;
    private int workingCap;
    private int clientDifficulty;
    private int clientCap;

    @Before
    public void rememberConfig() {
        this.workingBooleans[0] = Config.allowCheatSheet;
        this.workingBooleans[1] = Config.wardedStone;
        this.workingBooleans[2] = Config.allowMirrors;
        this.workingBooleans[3] = Config.hardNode;
        this.workingBooleans[4] = Config.wuss;
        this.clientBooleans[0] = Config.CallowCheatSheet;
        this.clientBooleans[1] = Config.CwardedStone;
        this.clientBooleans[2] = Config.CallowMirrors;
        this.clientBooleans[3] = Config.ChardNode;
        this.clientBooleans[4] = Config.Cwuss;
        this.workingDifficulty = Config.researchDifficulty;
        this.workingCap = Config.aspectTotalCap;
        this.clientDifficulty = Config.CresearchDifficulty;
        this.clientCap = Config.CaspectTotalCap;
    }

    @After
    public void restoreConfig() {
        setWorking(this.workingBooleans, this.workingDifficulty, this.workingCap);
        Config.CallowCheatSheet = this.clientBooleans[0];
        Config.CwardedStone = this.clientBooleans[1];
        Config.CallowMirrors = this.clientBooleans[2];
        Config.ChardNode = this.clientBooleans[3];
        Config.Cwuss = this.clientBooleans[4];
        Config.CresearchDifficulty = this.clientDifficulty;
        Config.CaspectTotalCap = this.clientCap;
    }

    @Test
    public void packetKeepsReferenceTenByteWireOrderAndSignedDifficulty() {
        setWorking(new boolean[]{true, false, true, false, true}, -7, 0x12345678);
        ByteBuf buffer = Unpooled.buffer();

        new PacketConfig().toBytes(buffer);

        assertEquals(10, buffer.readableBytes());
        assertTrue(buffer.readBoolean());
        assertFalse(buffer.readBoolean());
        assertTrue(buffer.readBoolean());
        assertFalse(buffer.readBoolean());
        assertTrue(buffer.readBoolean());
        assertEquals(-7, buffer.readByte());
        assertEquals(0x12345678, buffer.readInt());
        assertEquals(0, buffer.readableBytes());
    }

    @Test
    public void decodedPacketAppliesOnlyWorkingClientFields() {
        Config.CallowCheatSheet = false;
        Config.CwardedStone = true;
        Config.CallowMirrors = false;
        Config.ChardNode = true;
        Config.Cwuss = false;
        Config.CresearchDifficulty = 31;
        Config.CaspectTotalCap = 47;

        setWorking(new boolean[]{true, false, true, false, true}, -12, 321);
        ByteBuf buffer = Unpooled.buffer();
        new PacketConfig().toBytes(buffer);
        PacketConfig decoded = new PacketConfig();
        decoded.fromBytes(buffer);
        setWorking(new boolean[]{false, true, false, true, false}, 0, 0);

        decoded.applyConfig();

        assertWorking(new boolean[]{true, false, true, false, true}, -12, 321);
        assertFalse(Config.CallowCheatSheet);
        assertTrue(Config.CwardedStone);
        assertFalse(Config.CallowMirrors);
        assertTrue(Config.ChardNode);
        assertFalse(Config.Cwuss);
        assertEquals(31, Config.CresearchDifficulty);
        assertEquals(47, Config.CaspectTotalCap);
    }

    private static void setWorking(boolean[] values, int difficulty, int cap) {
        Config.allowCheatSheet = values[0];
        Config.wardedStone = values[1];
        Config.allowMirrors = values[2];
        Config.hardNode = values[3];
        Config.wuss = values[4];
        Config.researchDifficulty = difficulty;
        Config.aspectTotalCap = cap;
    }

    private static void assertWorking(boolean[] values, int difficulty, int cap) {
        assertEquals(values[0], Config.allowCheatSheet);
        assertEquals(values[1], Config.wardedStone);
        assertEquals(values[2], Config.allowMirrors);
        assertEquals(values[3], Config.hardNode);
        assertEquals(values[4], Config.wuss);
        assertEquals(difficulty, Config.researchDifficulty);
        assertEquals(cap, Config.aspectTotalCap);
    }
}

package thaumcraft.common.lib.network.fx;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.util.math.BlockPos;
import org.junit.Test;
import thaumcraft.common.lib.network.PacketBase;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacketFXSerializationTest {

    @Test
    public void zapPacketRoundTripsPayload() {
        PacketFXZap source = new PacketFXZap(31, 62);
        PacketFXZap target = new PacketFXZap();
        roundTrip(source, target);

        assertEquals(31, (int) getField(target, "source"));
        assertEquals(62, (int) getField(target, "target"));
    }

    @Test
    public void wispZapPacketRoundTripsPayload() {
        PacketFXWispZap source = new PacketFXWispZap(14, 28);
        PacketFXWispZap target = new PacketFXWispZap();
        roundTrip(source, target);

        assertEquals(14, (int) getField(target, "source"));
        assertEquals(28, (int) getField(target, "target"));
    }

    @Test
    public void sonicPacketRoundTripsPayload() {
        PacketFXSonic source = new PacketFXSonic(77);
        PacketFXSonic target = new PacketFXSonic();
        roundTrip(source, target);

        assertEquals(77, (int) getField(target, "source"));
    }

    @Test
    public void shieldPacketRoundTripsPayload() {
        PacketFXShield source = new PacketFXShield(99, -2);
        PacketFXShield target = new PacketFXShield();
        roundTrip(source, target);

        assertEquals(99, (int) getField(target, "source"));
        assertEquals(-2, (int) getField(target, "target"));
    }

    @Test
    public void blockSparklePacketRoundTripsPayload() {
        PacketFXBlockSparkle source = new PacketFXBlockSparkle(8, 20, -3, 0xC0C0FF);
        PacketFXBlockSparkle target = new PacketFXBlockSparkle();
        roundTrip(source, target);

        assertEquals(8, (int) getField(target, "x"));
        assertEquals(20, (int) getField(target, "y"));
        assertEquals(-3, (int) getField(target, "z"));
        assertEquals(0xC0C0FF, (int) getField(target, "color"));
    }

    @Test
    public void blockArcPacketRoundTripsPayload() {
        PacketFXBlockArc source = new PacketFXBlockArc(13, 64, -7, 101);
        PacketFXBlockArc target = new PacketFXBlockArc();
        roundTrip(source, target);

        assertEquals(13, (int) getField(target, "x"));
        assertEquals(64, (int) getField(target, "y"));
        assertEquals(-7, (int) getField(target, "z"));
        assertEquals(101, (int) getField(target, "entityId"));
    }

    @Test
    public void blockZapPacketRoundTripsPayload() {
        PacketFXBlockZap source = new PacketFXBlockZap(1.25F, -4.5F, 11.0F, 6.75F, 9.5F, -3.25F);
        PacketFXBlockZap target = new PacketFXBlockZap();
        roundTrip(source, target);

        assertEquals(1.25F, (float) getField(target, "x"), 0.0001F);
        assertEquals(-4.5F, (float) getField(target, "y"), 0.0001F);
        assertEquals(11.0F, (float) getField(target, "z"), 0.0001F);
        assertEquals(6.75F, (float) getField(target, "dx"), 0.0001F);
        assertEquals(9.5F, (float) getField(target, "dy"), 0.0001F);
        assertEquals(-3.25F, (float) getField(target, "dz"), 0.0001F);
    }

    @Test
    public void essentiaSourcePacketRoundTripsPayload() {
        PacketFXEssentiaSource source = new PacketFXEssentiaSource(12, -7, 44, (byte) -3, (byte) 6, (byte) 1, 0xAA5500);
        PacketFXEssentiaSource target = new PacketFXEssentiaSource();
        roundTrip(source, target);

        assertEquals(12, (int) getField(target, "x"));
        assertEquals(-7, (int) getField(target, "y"));
        assertEquals(44, (int) getField(target, "z"));
        assertEquals((byte) -3, (byte) getField(target, "dx"));
        assertEquals((byte) 6, (byte) getField(target, "dy"));
        assertEquals((byte) 1, (byte) getField(target, "dz"));
        assertEquals(0xAA5500, (int) getField(target, "color"));
    }

    @Test
    public void infusionSourcePacketRoundTripsPayload() {
        PacketFXInfusionSource source = new PacketFXInfusionSource(-5, 27, 3, (byte) 4, (byte) -2, (byte) 0, 12345);
        PacketFXInfusionSource target = new PacketFXInfusionSource();
        roundTrip(source, target);

        assertEquals(-5, (int) getField(target, "x"));
        assertEquals(27, (int) getField(target, "y"));
        assertEquals(3, (int) getField(target, "z"));
        assertEquals((byte) 4, (byte) getField(target, "dx"));
        assertEquals((byte) -2, (byte) getField(target, "dy"));
        assertEquals((byte) 0, (byte) getField(target, "dz"));
        assertEquals(12345, (int) getField(target, "color"));
    }

    @Test
    public void blockBubblePacketRoundTripsPayload() {
        PacketFXBlockBubble source = new PacketFXBlockBubble(10, -3, 77, 0x3366CC);
        PacketFXBlockBubble target = new PacketFXBlockBubble();
        roundTrip(source, target);

        assertEquals(10, (int) getField(target, "x"));
        assertEquals(-3, (int) getField(target, "y"));
        assertEquals(77, (int) getField(target, "z"));
        assertEquals(0x3366CC, (int) getField(target, "color"));
    }

    @Test
    public void blockDigPacketRoundTripsPayload() {
        PacketFXBlockDig source = new PacketFXBlockDig(1, 2, 3, (byte) 4, (byte) 5, (byte) -6, 98, 7);
        PacketFXBlockDig target = new PacketFXBlockDig();
        roundTrip(source, target);

        assertEquals(1, (int) getField(target, "x"));
        assertEquals(2, (int) getField(target, "y"));
        assertEquals(3, (int) getField(target, "z"));
        assertEquals(98, (int) getField(target, "bi"));
        assertEquals(7, (int) getField(target, "md"));
        assertEquals((byte) 4, (byte) getField(target, "dx"));
        assertEquals((byte) 5, (byte) getField(target, "dy"));
        assertEquals((byte) -6, (byte) getField(target, "dz"));
    }

    @Test
    public void beamPulsePacketRoundTripsPayload() {
        PacketFXBeamPulse source = new PacketFXBeamPulse(31, 62, 0xAA22CC);
        PacketFXBeamPulse target = new PacketFXBeamPulse();
        roundTrip(source, target);

        assertEquals(31, (int) getField(target, "source"));
        assertEquals(62, (int) getField(target, "target"));
        assertEquals(0xAA22CC, (int) getField(target, "color"));
    }

    @Test
    public void beamPulseGolemBossPacketRoundTripsPayload() {
        PacketFXBeamPulseGolemBoss source = new PacketFXBeamPulseGolemBoss(7, 9);
        PacketFXBeamPulseGolemBoss target = new PacketFXBeamPulseGolemBoss();
        roundTrip(source, target);

        assertEquals(7, (int) getField(target, "source"));
        assertEquals(9, (int) getField(target, "target"));
    }

    @Test
    public void visDrainPacketRoundTripsPayload() {
        PacketFXVisDrain source = new PacketFXVisDrain(new BlockPos(1, 2, 3), new BlockPos(-4, 5, -6), 0x55AAFF);
        PacketFXVisDrain target = new PacketFXVisDrain();
        roundTrip(source, target);

        assertEquals(new BlockPos(1, 2, 3), getField(target, "from"));
        assertEquals(new BlockPos(-4, 5, -6), getField(target, "to"));
        assertEquals(0x55AAFF, (int) getField(target, "color"));
    }

    private static void roundTrip(PacketBase source, PacketBase target) {
        ByteBuf buffer = Unpooled.buffer();
        source.toBytes(buffer);
        target.fromBytes(buffer);
        assertTrue(buffer.readableBytes() == 0);
    }

    private static Object getField(Object target, String name) {
        try {
            Field declared = target.getClass().getDeclaredField(name);
            declared.setAccessible(true);
            return declared.get(target);
        } catch (Exception e) {
            throw new AssertionError("Unable to read field " + name + " on " + target.getClass().getSimpleName(), e);
        }
    }
}

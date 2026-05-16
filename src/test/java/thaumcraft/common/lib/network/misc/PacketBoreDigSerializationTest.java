package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import thaumcraft.common.lib.network.PacketBase;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacketBoreDigSerializationTest {

    @Test
    public void boreDigPacketRoundTripsPayload() {
        PacketBoreDig source = new PacketBoreDig(17, -2, 91, 0x12AB34);
        PacketBoreDig target = new PacketBoreDig();
        roundTrip(source, target);

        assertEquals(17, (int) getField(target, "x"));
        assertEquals(-2, (int) getField(target, "y"));
        assertEquals(91, (int) getField(target, "z"));
        assertEquals(0x12AB34, (int) getField(target, "digloc"));
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

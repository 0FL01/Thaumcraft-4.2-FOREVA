package thaumcraft.common.lib.network.misc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import thaumcraft.common.lib.network.PacketBase;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PacketMiscWarpSerializationTest {

    @Test
    public void miscEventPacketRoundTripsPayload() {
        PacketMiscEvent source = new PacketMiscEvent((short) 2);
        PacketMiscEvent target = new PacketMiscEvent();
        roundTrip(source, target);

        assertEquals((short) 2, (short) getField(target, "type"));
    }

    @Test
    public void warpMessagePacketRoundTripsPayload() {
        PacketWarpMessage source = new PacketWarpMessage((byte) 1, -3);
        PacketWarpMessage target = new PacketWarpMessage();
        roundTrip(source, target);

        assertEquals((byte) 1, (byte) getField(target, "type"));
        assertEquals(-3, (int) getField(target, "data"));
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

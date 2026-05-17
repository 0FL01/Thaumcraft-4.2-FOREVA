package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerDataPacketSerializationTest {

    @Test
    public void syncAspectsRoundTripsPoolAmounts() {
        AspectList aspects = new AspectList().add(Aspect.AIR, 12).add(Aspect.MAGIC, 4);
        PacketSyncAspects decoded = new PacketSyncAspects();
        roundTrip(new PacketSyncAspects(aspects), decoded);

        assertEquals(12, decoded.getAspects().getAmount(Aspect.AIR));
        assertEquals(4, decoded.getAspects().getAmount(Aspect.MAGIC));
    }

    @Test
    public void syncResearchAndScansRoundTripExactSets() {
        assertEquals(set("A", "B"), roundTripResearch(set("A", "B")).getResearch());
        assertEquals(set("@1", "#2"), roundTripItems(set("@1", "#2")).getScannedItems());
        assertEquals(set("@E"), roundTripEntities(set("@E")).getScannedEntities());
        assertEquals(set("@NODE0:1:2:3"), roundTripPhenomena(set("@NODE0:1:2:3")).getScannedPhenomena());
    }

    @Test
    public void warpSyncIncludesCounter() {
        PacketSyncWarp decoded = new PacketSyncWarp();
        roundTrip(new PacketSyncWarp(1, 2, 3, 4), decoded);

        assertEquals(1, decoded.getWarpPerm());
        assertEquals(2, decoded.getWarpSticky());
        assertEquals(3, decoded.getWarpTemp());
        assertEquals(4, decoded.getWarpCounter());
    }

    @Test
    public void aspectAndRunicMutationPacketsRoundTrip() {
        PacketAspectPool pool = new PacketAspectPool();
        roundTrip(new PacketAspectPool(Aspect.FIRE.getTag(), (short)2, 9), pool);
        assertEquals(Aspect.FIRE.getTag(), pool.getAspectTag());
        assertEquals(2, pool.getAmount());
        assertEquals(9, pool.getTotal());

        PacketAspectDiscovery discovery = new PacketAspectDiscovery();
        roundTrip(new PacketAspectDiscovery(Aspect.WATER.getTag()), discovery);
        assertEquals(Aspect.WATER.getTag(), discovery.getAspectTag());

        PacketRunicCharge runic = new PacketRunicCharge();
        roundTrip(new PacketRunicCharge(11, 3, 8), runic);
        assertEquals(11, runic.getEntityId());
        assertEquals(3, runic.getCharge());
        assertEquals(8, runic.getMax());

        ByteBuf runicWire = Unpooled.buffer();
        new PacketRunicCharge(11, 3, 8).toBytes(runicWire);
        assertEquals(8, runicWire.readableBytes());
    }

    @Test
    public void aspectPlacePacketRoundTripsNullAndNonNullAspect() {
        PacketAspectPlaceToServer withAspect = new PacketAspectPlaceToServer();
        setField(withAspect, "dim", 7);
        setField(withAspect, "playerid", 11);
        setField(withAspect, "x", 1);
        setField(withAspect, "y", 2);
        setField(withAspect, "z", 3);
        setField(withAspect, "aspect", Aspect.ORDER);
        setField(withAspect, "q", (byte)4);
        setField(withAspect, "r", (byte)-5);

        PacketAspectPlaceToServer decodedWithAspect = new PacketAspectPlaceToServer();
        roundTrip(withAspect, decodedWithAspect);
        assertEquals(7, (int) getField(decodedWithAspect, "dim"));
        assertEquals(11, (int) getField(decodedWithAspect, "playerid"));
        assertEquals(1, (int) getField(decodedWithAspect, "x"));
        assertEquals(2, (int) getField(decodedWithAspect, "y"));
        assertEquals(3, (int) getField(decodedWithAspect, "z"));
        assertEquals(Aspect.ORDER, getField(decodedWithAspect, "aspect"));
        assertEquals((byte)4, (byte) getField(decodedWithAspect, "q"));
        assertEquals((byte)-5, (byte) getField(decodedWithAspect, "r"));

        PacketAspectPlaceToServer nullAspect = new PacketAspectPlaceToServer();
        setField(nullAspect, "dim", 9);
        setField(nullAspect, "playerid", 13);
        setField(nullAspect, "x", -10);
        setField(nullAspect, "y", 64);
        setField(nullAspect, "z", 25);
        setField(nullAspect, "aspect", null);
        setField(nullAspect, "q", (byte)0);
        setField(nullAspect, "r", (byte)0);

        PacketAspectPlaceToServer decodedNullAspect = new PacketAspectPlaceToServer();
        roundTrip(nullAspect, decodedNullAspect);
        assertEquals(9, (int) getField(decodedNullAspect, "dim"));
        assertEquals(13, (int) getField(decodedNullAspect, "playerid"));
        assertEquals(-10, (int) getField(decodedNullAspect, "x"));
        assertEquals(64, (int) getField(decodedNullAspect, "y"));
        assertEquals(25, (int) getField(decodedNullAspect, "z"));
        assertEquals(null, getField(decodedNullAspect, "aspect"));
        assertEquals((byte)0, (byte) getField(decodedNullAspect, "q"));
        assertEquals((byte)0, (byte) getField(decodedNullAspect, "r"));
    }

    @Test
    public void aspectCombinationPacketRoundTripsCombinationFlags() {
        PacketAspectCombinationToServer source = new PacketAspectCombinationToServer();
        setField(source, "dim", 2);
        setField(source, "playerid", 42);
        setField(source, "x", 100);
        setField(source, "y", 45);
        setField(source, "z", -12);
        setField(source, "aspect1", Aspect.AIR);
        setField(source, "aspect2", Aspect.FIRE);
        setField(source, "ab1", true);
        setField(source, "ab2", false);

        PacketAspectCombinationToServer decoded = new PacketAspectCombinationToServer();
        roundTrip(source, decoded);
        assertEquals(2, (int) getField(decoded, "dim"));
        assertEquals(42, (int) getField(decoded, "playerid"));
        assertEquals(100, (int) getField(decoded, "x"));
        assertEquals(45, (int) getField(decoded, "y"));
        assertEquals(-12, (int) getField(decoded, "z"));
        assertEquals(Aspect.AIR, getField(decoded, "aspect1"));
        assertEquals(Aspect.FIRE, getField(decoded, "aspect2"));
        assertEquals(true, (boolean) getField(decoded, "ab1"));
        assertEquals(false, (boolean) getField(decoded, "ab2"));
    }

    @Test
    public void playerCompletePacketRoundTripsResearchSelectionPayload() {
        PacketPlayerCompleteToServer source = new PacketPlayerCompleteToServer();
        setField(source, "key", "FOCUSPRIMAL");
        setField(source, "dim", -1);
        setField(source, "username", "tester");
        setField(source, "type", (byte)1);

        PacketPlayerCompleteToServer decoded = new PacketPlayerCompleteToServer();
        roundTrip(source, decoded);
        assertEquals("FOCUSPRIMAL", getField(decoded, "key"));
        assertEquals(-1, (int) getField(decoded, "dim"));
        assertEquals("tester", getField(decoded, "username"));
        assertEquals((byte)1, (byte) getField(decoded, "type"));
    }

    private void roundTrip(thaumcraft.common.lib.network.PacketBase source, thaumcraft.common.lib.network.PacketBase target) {
        ByteBuf buffer = Unpooled.buffer();
        source.toBytes(buffer);
        target.fromBytes(buffer);
        assertTrue(buffer.readableBytes() == 0);
    }

    private PacketSyncResearch roundTripResearch(Set<String> input) {
        PacketSyncResearch packet = new PacketSyncResearch();
        roundTrip(new PacketSyncResearch(input), packet);
        return packet;
    }

    private PacketSyncScannedItems roundTripItems(Set<String> input) {
        PacketSyncScannedItems packet = new PacketSyncScannedItems();
        roundTrip(new PacketSyncScannedItems(input), packet);
        return packet;
    }

    private PacketSyncScannedEntities roundTripEntities(Set<String> input) {
        PacketSyncScannedEntities packet = new PacketSyncScannedEntities();
        roundTrip(new PacketSyncScannedEntities(input), packet);
        return packet;
    }

    private PacketSyncScannedPhenomena roundTripPhenomena(Set<String> input) {
        PacketSyncScannedPhenomena packet = new PacketSyncScannedPhenomena();
        roundTrip(new PacketSyncScannedPhenomena(input), packet);
        return packet;
    }

    private static Set<String> set(String... values) {
        return new HashSet<>(Arrays.asList(values));
    }

    private static void setField(Object target, String field, Object value) {
        try {
            Field declared = target.getClass().getDeclaredField(field);
            declared.setAccessible(true);
            declared.set(target, value);
        } catch (Exception e) {
            throw new AssertionError("Unable to set field " + field + " on " + target.getClass().getSimpleName(), e);
        }
    }

    private static Object getField(Object target, String field) {
        try {
            Field declared = target.getClass().getDeclaredField(field);
            declared.setAccessible(true);
            return declared.get(target);
        } catch (Exception e) {
            throw new AssertionError("Unable to read field " + field + " on " + target.getClass().getSimpleName(), e);
        }
    }
}

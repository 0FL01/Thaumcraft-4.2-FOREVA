package thaumcraft.common.lib.network.playerdata;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

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
}

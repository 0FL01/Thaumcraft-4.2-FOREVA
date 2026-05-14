package thaumcraft.common.lib.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import thaumcraft.common.lib.network.fx.PacketFXBeamPulse;
import thaumcraft.common.lib.network.fx.PacketFXBeamPulseGolemBoss;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXBlockBubble;
import thaumcraft.common.lib.network.fx.PacketFXBlockDig;
import thaumcraft.common.lib.network.fx.PacketFXBlockSparkle;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.network.fx.PacketFXEssentiaSource;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.network.fx.PacketFXSonic;
import thaumcraft.common.lib.network.fx.PacketFXVisDrain;
import thaumcraft.common.lib.network.fx.PacketFXWispZap;
import thaumcraft.common.lib.network.fx.PacketFXZap;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;
import thaumcraft.common.lib.network.misc.PacketBoreDig;
import thaumcraft.common.lib.network.misc.PacketConfig;
import thaumcraft.common.lib.network.misc.PacketFlyToServer;
import thaumcraft.common.lib.network.misc.PacketFocusChangeToServer;
import thaumcraft.common.lib.network.misc.PacketItemKeyToServer;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.network.misc.PacketNote;
import thaumcraft.common.lib.network.playerdata.PacketAspectCombinationToServer;
import thaumcraft.common.lib.network.playerdata.PacketAspectDiscovery;
import thaumcraft.common.lib.network.playerdata.PacketAspectPlaceToServer;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;
import thaumcraft.common.lib.network.playerdata.PacketResearchComplete;
import thaumcraft.common.lib.network.playerdata.PacketRunicCharge;
import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedEntities;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedItems;
import thaumcraft.common.lib.network.playerdata.PacketSyncScannedPhenomena;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;
import thaumcraft.common.lib.network.playerdata.PacketSyncWipe;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;

public class PacketHandler {
    public static final String CHANNEL = "thaumcraft";
    public static final int REFERENCE_PACKET_COUNT = 39;
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

    // Single dispatch handler that calls onMessage() on each packet
    private static final IMessageHandler<PacketBase, IMessage> DISPATCH_HANDLER =
        (message, ctx) -> message.onMessage(ctx);

    public static void init() {
        int idx = 0;
        register(PacketBiomeChange.class, idx++, Side.CLIENT);
        register(PacketConfig.class, idx++, Side.CLIENT);
        register(PacketMiscEvent.class, idx++, Side.CLIENT);
        register(PacketSyncWipe.class, idx++, Side.CLIENT);
        register(PacketSyncAspects.class, idx++, Side.CLIENT);
        register(PacketSyncResearch.class, idx++, Side.CLIENT);
        register(PacketSyncScannedItems.class, idx++, Side.CLIENT);
        register(PacketSyncScannedEntities.class, idx++, Side.CLIENT);
        register(PacketSyncScannedPhenomena.class, idx++, Side.CLIENT);
        register(PacketResearchComplete.class, idx++, Side.CLIENT);
        register(PacketAspectPool.class, idx++, Side.CLIENT);
        register(PacketAspectDiscovery.class, idx++, Side.CLIENT);
        register(PacketScannedToServer.class, idx++, Side.SERVER);
        register(PacketAspectCombinationToServer.class, idx++, Side.SERVER);
        register(PacketPlayerCompleteToServer.class, idx++, Side.SERVER);
        register(PacketAspectPlaceToServer.class, idx++, Side.SERVER);
        register(PacketRunicCharge.class, idx++, Side.CLIENT);
        register(PacketBoreDig.class, idx++, Side.CLIENT);
        register(PacketNote.class, idx++, Side.CLIENT);
        register(PacketSyncWarp.class, idx++, Side.CLIENT);
        register(PacketWarpMessage.class, idx++, Side.CLIENT);
        register(PacketNote.class, idx++, Side.SERVER);
        register(PacketItemKeyToServer.class, idx++, Side.SERVER);
        register(PacketFocusChangeToServer.class, idx++, Side.SERVER);
        register(PacketFlyToServer.class, idx++, Side.SERVER);
        register(PacketFXBlockBubble.class, idx++, Side.CLIENT);
        register(PacketFXBlockDig.class, idx++, Side.CLIENT);
        register(PacketFXBlockSparkle.class, idx++, Side.CLIENT);
        register(PacketFXBlockArc.class, idx++, Side.CLIENT);
        register(PacketFXBlockZap.class, idx++, Side.CLIENT);
        register(PacketFXEssentiaSource.class, idx++, Side.CLIENT);
        register(PacketFXInfusionSource.class, idx++, Side.CLIENT);
        register(PacketFXShield.class, idx++, Side.CLIENT);
        register(PacketFXSonic.class, idx++, Side.CLIENT);
        register(PacketFXWispZap.class, idx++, Side.CLIENT);
        register(PacketFXZap.class, idx++, Side.CLIENT);
        register(PacketFXVisDrain.class, idx++, Side.CLIENT);
        register(PacketFXBeamPulse.class, idx++, Side.CLIENT);
        register(PacketFXBeamPulseGolemBoss.class, idx++, Side.CLIENT);
        if (idx != REFERENCE_PACKET_COUNT) {
            throw new IllegalStateException("Thaumcraft packet discriminator table changed: expected " + REFERENCE_PACKET_COUNT + " entries, got " + idx);
        }
    }

    @SuppressWarnings("unchecked")
    private static void register(Class<? extends PacketBase> clazz, int discriminator, Side side) {
        INSTANCE.registerMessage(DISPATCH_HANDLER, (Class) clazz, discriminator, side);
    }
}

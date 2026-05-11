package thaumcraft.common.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Generic handler for all stub packet types.
 * Real packet handling will be implemented in Phase 7.
 */
public class PacketStubHandler implements IMessageHandler<PacketBase, IMessage> {

    @Override
    public IMessage onMessage(PacketBase message, MessageContext ctx) {
        return null;
    }
}

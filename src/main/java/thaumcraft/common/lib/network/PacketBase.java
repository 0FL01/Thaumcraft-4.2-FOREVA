package thaumcraft.common.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Base class for all packet stubs. Each packet type extends this
 * with an empty no-arg constructor required by SimpleNetworkWrapper.
 * Real implementations will be filled in during Phase 7 (networking).
 */
public class PacketBase implements IMessage {

    public PacketBase() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }
}

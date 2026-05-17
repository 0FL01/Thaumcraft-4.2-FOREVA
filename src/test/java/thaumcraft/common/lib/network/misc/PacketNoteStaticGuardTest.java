package thaumcraft.common.lib.network.misc;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class PacketNoteStaticGuardTest {

    @Test
    public void packetNoteShouldKeepReferencePayloadAndConstructors() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketNote.java");

        assertTrue(source.contains("private int x;"));
        assertTrue(source.contains("private int y;"));
        assertTrue(source.contains("private int z;"));
        assertTrue(source.contains("private int dim;"));
        assertTrue(source.contains("private byte note;"));
        assertTrue(source.contains("public PacketNote(int x, int y, int z, int dim)"));
        assertTrue(source.contains("this(x, y, z, dim, (byte) -1);"));
        assertTrue(source.contains("public PacketNote(int x, int y, int z, int dim, byte note)"));
    }

    @Test
    public void packetNoteShouldKeepClientAndServerSyncBranches() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketNote.java");

        assertTrue(source.contains("if (ctx.side == Side.CLIENT)"));
        assertTrue(source.contains("if (this.note == -1)"));
        assertTrue(source.contains("DimensionManager.getWorld(this.dim);"));
        assertTrue(source.contains("tile instanceof TileEntityNote"));
        assertTrue(source.contains("tile instanceof TileSensor"));
        assertTrue(source.contains("PacketHandler.INSTANCE.sendToAllAround("));
        assertTrue(source.contains("new NetworkRegistry.TargetPoint(this.dim, this.x, this.y, this.z, 8.0)"));
    }

    @Test
    public void packetNoteShouldKeepFiveFieldSerializationContract() throws IOException {
        String source = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketNote.java");

        assertTrue(source.contains("buffer.writeInt(this.x);"));
        assertTrue(source.contains("buffer.writeInt(this.y);"));
        assertTrue(source.contains("buffer.writeInt(this.z);"));
        assertTrue(source.contains("buffer.writeInt(this.dim);"));
        assertTrue(source.contains("buffer.writeByte(this.note);"));
        assertTrue(source.contains("this.x = buffer.readInt();"));
        assertTrue(source.contains("this.y = buffer.readInt();"));
        assertTrue(source.contains("this.z = buffer.readInt();"));
        assertTrue(source.contains("this.dim = buffer.readInt();"));
        assertTrue(source.contains("this.note = buffer.readByte();"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

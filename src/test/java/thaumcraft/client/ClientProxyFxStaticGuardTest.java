package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyFxStaticGuardTest {

    @Test
    public void clientProxyFxMethodsAreImplemented() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("ClientProxy blockSparkle must spawn client particles",
                source.contains("public void blockSparkle(") && source.contains("world.spawnParticle(EnumParticleTypes.REDSTONE"));
        assertTrue("ClientProxy beam must contain non-noop beam particle path",
                source.contains("public void beam(") && source.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("ClientProxy bolt must contain non-noop bolt particle path",
                source.contains("public void bolt(") && source.contains("speed * 2"));
        assertTrue("ClientProxy must override burst for direct entity FX call sites",
                source.contains("public void burst(") && source.contains("EnumParticleTypes.EXPLOSION_NORMAL"));
        assertTrue("ClientProxy must override wispFX3 for wisp ambient FX",
                source.contains("public void wispFX3(") && source.contains("hasTarget"));
        assertTrue("ClientProxy must override sparkle for firebat/lifter visuals",
                source.contains("public void sparkle(") && source.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("ClientProxy must override particleCount using client particle settings",
                source.contains("public int particleCount(") && source.contains("mc.gameSettings.particleSetting"));
    }

    @Test
    public void fxPacketsKeepClientSchedulingHandlers() throws IOException {
        String visDrain = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java");
        String blockArc = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockArc.java");
        String blockSparkle = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.java");
        String shield = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXShield.java");
        String sonic = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXSonic.java");
        String serverTick = readFile("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String runic = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");
        String eldritchGuardian = readFile("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");

        assertTrue("PacketFXVisDrain must schedule client task and call proxy beam",
                visDrain.contains("Minecraft.getMinecraft().addScheduledTask") && visDrain.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXBlockArc must schedule client task and call proxy bolt",
                blockArc.contains("Minecraft.getMinecraft().addScheduledTask") && blockArc.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXBlockSparkle must schedule client task and call proxy blockSparkle",
                blockSparkle.contains("Minecraft.getMinecraft().addScheduledTask") && blockSparkle.contains("Thaumcraft.proxy.blockSparkle("));
        assertTrue("PacketFXShield must schedule client task and route through proxy burst/bolt",
                shield.contains("Minecraft.getMinecraft().addScheduledTask")
                        && shield.contains("Thaumcraft.proxy.burst(")
                        && shield.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXSonic must schedule client task and route through proxy burst",
                sonic.contains("Minecraft.getMinecraft().addScheduledTask")
                        && sonic.contains("Thaumcraft.proxy.burst("));
        assertTrue("Server block-swap path must send PacketFXBlockSparkle around replaced block",
                serverTick.contains("new PacketFXBlockSparkle(vs.x, vs.y, vs.z, 0xC0C0FF)"));
        assertTrue("Runic shielding paths must send PacketFXShield for player and champion shield reactions",
                runic.contains("new PacketFXShield(player.getEntityId(), target)")
                        && runic.contains("new PacketFXShield(mob.getEntityId(), target)"));
        assertTrue("Eldritch guardian sonic attack path must send PacketFXSonic",
                eldritchGuardian.contains("new PacketFXSonic(this.getEntityId())"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

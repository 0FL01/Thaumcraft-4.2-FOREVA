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
        String blockZap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockZap.java");
        String blockDig = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockDig.java");
        String blockBubble = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockBubble.java");
        String essentiaSource = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXEssentiaSource.java");
        String infusionSource = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.java");
        String blockSparkle = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.java");
        String shield = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXShield.java");
        String sonic = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXSonic.java");
        String wispZap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXWispZap.java");
        String zap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXZap.java");
        String serverTick = readFile("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String runic = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");
        String eldritchGuardian = readFile("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");
        String wisp = readFile("src/main/java/thaumcraft/common/entities/monster/EntityWisp.java");
        String focusShock = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");
        String eldritchTrap = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchTrap.java");
        String infusionMatrix = readFile("src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java");
        String essentiaHandler = readFile("src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java");

        assertTrue("PacketFXVisDrain must schedule client task and call proxy beam",
                visDrain.contains("Minecraft.getMinecraft().addScheduledTask") && visDrain.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXBlockArc must schedule client task and call proxy bolt",
                blockArc.contains("Minecraft.getMinecraft().addScheduledTask") && blockArc.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXBlockZap must schedule client task and route through proxy bolt",
                blockZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockZap.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXBlockDig must schedule client task and emit dig particles",
                blockDig.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockDig.contains("EnumParticleTypes.BLOCK_CRACK"));
        assertTrue("PacketFXBlockBubble must schedule client task and emit bubble particles",
                blockBubble.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockBubble.contains("EnumParticleTypes.WATER_BUBBLE"));
        assertTrue("PacketFXEssentiaSource must schedule client task and route through proxy beam",
                essentiaSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && essentiaSource.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXInfusionSource must schedule client task and route through proxy beam",
                infusionSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && infusionSource.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXBlockSparkle must schedule client task and call proxy blockSparkle",
                blockSparkle.contains("Minecraft.getMinecraft().addScheduledTask") && blockSparkle.contains("Thaumcraft.proxy.blockSparkle("));
        assertTrue("PacketFXShield must schedule client task and route through proxy burst/bolt",
                shield.contains("Minecraft.getMinecraft().addScheduledTask")
                        && shield.contains("Thaumcraft.proxy.burst(")
                        && shield.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXSonic must schedule client task and route through proxy burst",
                sonic.contains("Minecraft.getMinecraft().addScheduledTask")
                        && sonic.contains("Thaumcraft.proxy.burst("));
        assertTrue("PacketFXWispZap must schedule client task and route through proxy bolt",
                wispZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && wispZap.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXZap must schedule client task and route through proxy bolt",
                zap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && zap.contains("Thaumcraft.proxy.bolt("));
        assertTrue("Server block-swap path must send PacketFXBlockSparkle around replaced block",
                serverTick.contains("new PacketFXBlockSparkle(vs.x, vs.y, vs.z, 0xC0C0FF)"));
        assertTrue("Runic shielding paths must send PacketFXShield for player and champion shield reactions",
                runic.contains("new PacketFXShield(player.getEntityId(), target)")
                        && runic.contains("new PacketFXShield(mob.getEntityId(), target)"));
        assertTrue("Eldritch guardian sonic attack path must send PacketFXSonic",
                eldritchGuardian.contains("new PacketFXSonic(this.getEntityId())"));
        assertTrue("Wisp ranged attack path must send PacketFXWispZap",
                wisp.contains("new PacketFXWispZap(this.getEntityId(), this.targetedEntity.getEntityId())"));
        assertTrue("FocusShock chain lightning path must send PacketFXZap",
                focusShock.contains("new PacketFXZap(center.getEntityId(), closest.getEntityId())"));
        assertTrue("TileEldritchTrap damage path must send PacketFXBlockZap",
                eldritchTrap.contains("new PacketFXBlockZap("));
        assertTrue("TileInfusionMatrix instability zap path must send PacketFXBlockZap",
                infusionMatrix.contains("new PacketFXBlockZap("));
        assertTrue("TileInfusionMatrix infusion paths must send PacketFXInfusionSource",
                infusionMatrix.contains("new PacketFXInfusionSource("));
        assertTrue("Essentia drain path must send PacketFXEssentiaSource",
                essentiaHandler.contains("new PacketFXEssentiaSource("));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

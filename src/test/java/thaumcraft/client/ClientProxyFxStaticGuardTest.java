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
        String particleEngine = readFile("src/main/java/thaumcraft/client/fx/ParticleEngine.java");

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
        assertTrue("ClientProxy must override wispFXEG for eldritch guardian trail FX",
                source.contains("public void wispFXEG(") && source.contains("target.height * 0.22f"));
        assertTrue("ClientProxy must override taintLandFX for falling taint landing FX",
                source.contains("public void taintLandFX(") && source.contains("entity.getEntityBoundingBox()"));
        assertTrue("ClientProxy must override slimeJumpFX for infested champion fallback",
                source.contains("public void slimeJumpFX(") && source.contains("sparkle(x, y, z, 0.7f, 0xAA22FF"));
        assertTrue("ClientProxy must override drawGenericParticles for champion modifier fallback",
                source.contains("public void drawGenericParticles(") && source.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("ClientProxy must override sparkle for firebat/lifter visuals",
                source.contains("public void sparkle(") && source.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("ClientProxy must override particleCount using client particle settings",
                source.contains("public int particleCount(") && source.contains("mc.gameSettings.particleSetting"));
        assertTrue("ClientProxy must override crucibleFroth and crucibleFrothDown",
                source.contains("public void crucibleFroth(")
                        && source.contains("public void crucibleFrothDown("));
        assertTrue("ClientProxy must override crucibleBubble with colored bubble path",
                source.contains("public void crucibleBubble(")
                        && source.contains("EnumParticleTypes.WATER_BUBBLE"));
        assertTrue("ClientProxy must override crucibleBoilSound and crucibleBoil",
                source.contains("public void crucibleBoilSound(")
                        && source.contains("public void crucibleBoil(")
                        && source.contains("TCSounds.BUBBLE"));
        assertTrue("ParticleEngine must keep queued particle intake + tick drain + effectRenderer dispatch baseline",
                particleEngine.contains("public static void addEffect(World world, Particle particle)")
                        && particleEngine.contains("pendingParticles")
                        && particleEngine.contains("event.phase != TickEvent.Phase.END")
                        && particleEngine.contains("mc.effectRenderer.addEffect(queued.particle)")
                        && particleEngine.contains("MAX_PARTICLE_ADDITIONS_PER_TICK"));
        assertTrue("ParticleEngine must keep render-world bookkeeping baseline",
                particleEngine.contains("lastRenderWorldTime")
                        && particleEngine.contains("lastRenderPartialTicks")
                        && particleEngine.contains("event.getPartialTicks()"));
    }

    @Test
    public void fxPacketsKeepClientSchedulingHandlers() throws IOException {
        String visDrain = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXVisDrain.java");
        String blockArc = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockArc.java");
        String blockZap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockZap.java");
        String blockDig = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockDig.java");
        String blockBubble = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockBubble.java");
        String beamPulse = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBeamPulse.java");
        String beamPulseGolemBoss = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBeamPulseGolemBoss.java");
        String essentiaSource = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXEssentiaSource.java");
        String infusionSource = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXInfusionSource.java");
        String blockSparkle = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXBlockSparkle.java");
        String shield = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXShield.java");
        String sonic = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXSonic.java");
        String wispZap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXWispZap.java");
        String zap = readFile("src/main/java/thaumcraft/common/lib/network/fx/PacketFXZap.java");
        String miscEvent = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketMiscEvent.java");
        String warpMessage = readFile("src/main/java/thaumcraft/common/lib/network/playerdata/PacketWarpMessage.java");
        String serverTick = readFile("src/main/java/thaumcraft/common/lib/events/ServerTickEventsFML.java");
        String runic = readFile("src/main/java/thaumcraft/common/lib/events/EventHandlerRunic.java");
        String eldritchGuardian = readFile("src/main/java/thaumcraft/common/entities/monster/EntityEldritchGuardian.java");
        String wisp = readFile("src/main/java/thaumcraft/common/entities/monster/EntityWisp.java");
        String focusShock = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");
        String eldritchTrap = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchTrap.java");
        String infusionMatrix = readFile("src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java");
        String essentiaHandler = readFile("src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java");
        String packetBoreDig = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketBoreDig.java");
        String arcaneBore = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");
        String warpEvents = readFile("src/main/java/thaumcraft/common/lib/WarpEvents.java");
        String fallingTaint = readFile("src/main/java/thaumcraft/common/entities/EntityFallingTaint.java");

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
        assertTrue("PacketFXBeamPulse must schedule client task and route through proxy beam",
                beamPulse.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulse.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXBeamPulseGolemBoss must schedule client task and route through proxy beam",
                beamPulseGolemBoss.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulseGolemBoss.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXEssentiaSource must schedule client task and route through proxy beam",
                essentiaSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && essentiaSource.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketFXInfusionSource must schedule client task and route through proxy beam",
                infusionSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && infusionSource.contains("Thaumcraft.proxy.beam("));
        assertTrue("PacketMiscEvent must schedule client task and update warp vignette/fog markers",
                miscEvent.contains("Minecraft.getMinecraft().addScheduledTask")
                        && miscEvent.contains("ClientTickEventsFML.warpVignette")
                        && miscEvent.contains("RenderEventHandler.fogFiddled"));
        assertTrue("PacketWarpMessage must schedule client task and show warp notifications",
                warpMessage.contains("Minecraft.getMinecraft().addScheduledTask")
                        && warpMessage.contains("TextComponentTranslation"));
        assertTrue("PacketBoreDig must schedule client task and route to TileArcaneBore.getDigEvent",
                packetBoreDig.contains("Minecraft.getMinecraft().addScheduledTask")
                        && packetBoreDig.contains("getDigEvent"));
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
        assertTrue("Server block-swap path must keep silk-harvest and fortune drop routing",
                serverTick.contains("currentBlock.canSilkHarvest(world, pos, state, vs.player)")
                        && serverTick.contains("currentBlock.getDrops(drops, world, pos, state, fortune)"));
        assertTrue("Swapper queue add path must play wand sound cue",
                serverTick.contains("world.playSound(player, x, y, z, TCSounds.WAND, SoundCategory.PLAYERS, 0.25f, 1.0f)"));
        assertTrue("Runic shielding paths must send PacketFXShield for player and champion shield reactions",
                runic.contains("new PacketFXShield(player.getEntityId(), target)")
                        && runic.contains("new PacketFXShield(mob.getEntityId(), target)"));
        assertTrue("Runic shielding paths must play runic shield effect/charge sounds",
                runic.contains("TCSounds.RUNICSHIELDEFFECT")
                        && runic.contains("TCSounds.RUNICSHIELDCHARGE"));
        assertTrue("Eldritch guardian sonic attack path must send PacketFXSonic",
                eldritchGuardian.contains("new PacketFXSonic(this.getEntityId())"));
        assertTrue("Eldritch guardian periodic fog pulse must send PacketMiscEvent short mist signal",
                eldritchGuardian.contains("new PacketMiscEvent((short) 2)"));
        assertTrue("Eldritch guardian client update must emit wispFXEG trail",
                eldritchGuardian.contains("Thaumcraft.proxy.wispFXEG("));
        assertTrue("Wisp ranged attack path must send PacketFXWispZap",
                wisp.contains("new PacketFXWispZap(this.getEntityId(), this.targetedEntity.getEntityId())"));
        assertTrue("EntityFallingTaint client update must emit taintLandFX landing loop",
                fallingTaint.contains("Thaumcraft.proxy.taintLandFX(this);"));
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
        assertTrue("WarpEvents must send PacketMiscEvent/PacketWarpMessage in active warp paths",
                warpEvents.contains("new PacketMiscEvent((short) 0)")
                        && warpEvents.contains("new PacketMiscEvent((short) 1)")
                        && warpEvents.contains("new PacketWarpMessage(player, (byte) 1, -1)"));
        assertTrue("TileArcaneBore must send PacketBoreDig and process client dig FX path",
                arcaneBore.contains("new PacketBoreDig(")
                        && arcaneBore.contains("public void getDigEvent(")
                        && arcaneBore.contains("playClientDigFx("));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

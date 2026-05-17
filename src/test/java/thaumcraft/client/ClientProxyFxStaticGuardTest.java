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
        String commonProxy = readFile("src/main/java/thaumcraft/common/CommonProxy.java");
        String renderHandler = readFile("src/main/java/thaumcraft/client/lib/RenderEventHandler.java");
        String particleEngine = readFile("src/main/java/thaumcraft/client/fx/ParticleEngine.java");
        String sonicFx = readFile("src/main/java/thaumcraft/client/fx/other/FXSonic.java");
        String shieldRunesFx = readFile("src/main/java/thaumcraft/client/fx/other/FXShieldRunes.java");

        assertTrue("ClientProxy blockSparkle must spawn client particles",
                source.contains("public void blockSparkle(") && source.contains("world.spawnParticle(EnumParticleTypes.REDSTONE"));
        assertTrue("ClientProxy beam must contain non-noop beam particle path",
                source.contains("public void beam(") && source.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("ClientProxy bolt must contain non-noop bolt particle path",
                source.contains("public void bolt(") && source.contains("speed * 2"));
        assertTrue("ClientProxy must override burst for direct entity FX call sites",
                source.contains("public void burst(") && source.contains("EnumParticleTypes.EXPLOSION_NORMAL"));
        assertTrue("ClientProxy must expose dedicated sonicFX path through ParticleEngine",
                source.contains("public void sonicFX(")
                        && source.contains("new FXSonic("));
        assertTrue("ClientProxy must expose dedicated shieldRunesFX path through ParticleEngine",
                source.contains("public void shieldRunesFX(")
                        && source.contains("new FXShieldRunes("));
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
        assertTrue("CommonProxy and ClientProxy must keep startScan proxy surface",
                commonProxy.contains("public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius)")
                        && source.contains("public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius)")
                        && source.contains("RenderEventHandler.startScan(entity, pos, expireAtMs, radius);"));
        assertTrue("CommonProxy must keep no-op side-safe stubs for dedicated sonic/shield rune fx",
                commonProxy.contains("public void sonicFX(World world, Entity source, int age)")
                        && commonProxy.contains("public void shieldRunesFX(World world, Entity source, int age, float yaw, float pitch)"));
        assertTrue("RenderEventHandler must keep scan-state baseline and startScan hook",
                renderHandler.contains("public static int scanEntityId = -1;")
                        && renderHandler.contains("public static BlockPos scanPos = BlockPos.ORIGIN;")
                        && renderHandler.contains("public static void startScan(Entity entity, BlockPos pos, long expireAtMs, int range)")
                        && renderHandler.contains("scanExpireAtMs"));
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
        assertTrue("Dedicated FXSonic particle must keep target-following emission baseline",
                sonicFx.contains("class FXSonic extends Particle")
                        && sonicFx.contains("this.target")
                        && sonicFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXShieldRunes particle must keep target-following emission baseline",
                shieldRunesFx.contains("class FXShieldRunes extends Particle")
                        && shieldRunesFx.contains("this.target")
                        && shieldRunesFx.contains("EnumParticleTypes.CRIT_MAGIC"));
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
        String eldritchWarden = readFile("src/main/java/thaumcraft/common/entities/monster/boss/EntityEldritchWarden.java");
        String wisp = readFile("src/main/java/thaumcraft/common/entities/monster/EntityWisp.java");
        String focusShock = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusShock.java");
        String focusWarding = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusWarding.java");
        String focusPortableHole = readFile("src/main/java/thaumcraft/common/items/wands/foci/FocusPortableHole.java");
        String eldritchTrap = readFile("src/main/java/thaumcraft/common/tiles/TileEldritchTrap.java");
        String infusionMatrix = readFile("src/main/java/thaumcraft/common/tiles/TileInfusionMatrix.java");
        String essentiaHandler = readFile("src/main/java/thaumcraft/common/lib/events/EssentiaHandler.java");
        String packetBoreDig = readFile("src/main/java/thaumcraft/common/lib/network/misc/PacketBoreDig.java");
        String arcaneBore = readFile("src/main/java/thaumcraft/common/tiles/TileArcaneBore.java");
        String warpEvents = readFile("src/main/java/thaumcraft/common/lib/WarpEvents.java");
        String fallingTaint = readFile("src/main/java/thaumcraft/common/entities/EntityFallingTaint.java");

        assertTrue("PacketFXVisDrain must schedule client task and route through dedicated FXBeam",
                visDrain.contains("Minecraft.getMinecraft().addScheduledTask")
                        && visDrain.contains("new FXBeam(")
                        && visDrain.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXBlockArc must schedule client task and route through dedicated FXArc",
                blockArc.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockArc.contains("new FXArc(")
                        && blockArc.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXBlockZap must schedule client task and route through proxy bolt",
                blockZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockZap.contains("Thaumcraft.proxy.bolt("));
        assertTrue("PacketFXBlockDig must schedule client task and emit dig particles",
                blockDig.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockDig.contains("EnumParticleTypes.BLOCK_CRACK"));
        assertTrue("PacketFXBlockBubble must schedule client task and emit bubble particles",
                blockBubble.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockBubble.contains("EnumParticleTypes.WATER_BUBBLE"));
        assertTrue("PacketFXBeamPulse must schedule client task and route through dedicated FXBeam",
                beamPulse.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulse.contains("new FXBeam(")
                        && beamPulse.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXBeamPulseGolemBoss must schedule client task and route through dedicated FXBeamGolemBoss",
                beamPulseGolemBoss.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulseGolemBoss.contains("new FXBeamGolemBoss(")
                        && beamPulseGolemBoss.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXEssentiaSource must schedule client task and route through dedicated FXBeam",
                essentiaSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && essentiaSource.contains("new FXBeam(")
                        && essentiaSource.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXInfusionSource must schedule client task and route through dedicated FXBeam",
                infusionSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && infusionSource.contains("new FXBeam(")
                        && infusionSource.contains("ParticleEngine.addEffect("));
        assertTrue("PacketMiscEvent must schedule client task via proxy boundary and update warp vignette/fog markers",
                miscEvent.contains("Thaumcraft.proxy.scheduleClientTask(")
                        && miscEvent.contains("ClientTickEventsFML.warpVignette")
                        && miscEvent.contains("RenderEventHandler.fogFiddled"));
        assertTrue("PacketWarpMessage must schedule client task via proxy boundary and show warp notifications",
                warpMessage.contains("Thaumcraft.proxy.scheduleClientTask(")
                        && warpMessage.contains("TextComponentTranslation"));
        assertTrue("PacketBoreDig must schedule client task via proxy boundary and route to TileArcaneBore.getDigEvent",
                packetBoreDig.contains("Thaumcraft.proxy.scheduleClientTask(")
                        && packetBoreDig.contains("getDigEvent"));
        assertTrue("PacketFXBlockSparkle must schedule client task and call proxy blockSparkle",
                blockSparkle.contains("Minecraft.getMinecraft().addScheduledTask") && blockSparkle.contains("Thaumcraft.proxy.blockSparkle("));
        assertTrue("PacketFXShield must schedule client task and route through dedicated shield/bolt FX",
                shield.contains("Minecraft.getMinecraft().addScheduledTask")
                        && shield.contains("Thaumcraft.proxy.shieldRunesFX(")
                        && shield.contains("new FXShieldRunes(")
                        && shield.contains("new FXLightningBolt(")
                        && shield.contains("Thaumcraft.proxy.burst(")
                        && shield.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXSonic must schedule client task and route through dedicated sonic FX",
                sonic.contains("Minecraft.getMinecraft().addScheduledTask")
                        && sonic.contains("new FXSonic(")
                        && sonic.contains("ParticleEngine.addEffect(")
                        && sonic.contains("Thaumcraft.proxy.sonicFX(")
                        && sonic.contains("Thaumcraft.proxy.burst("));
        assertTrue("PacketFXWispZap must schedule client task and route through dedicated lightning bolt FX",
                wispZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && wispZap.contains("new FXLightningBolt(")
                        && wispZap.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXZap must schedule client task and route through dedicated lightning bolt FX",
                zap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && zap.contains("new FXLightningBolt(")
                        && zap.contains("ParticleEngine.addEffect("));
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
        assertTrue("Eldritch warden client update must emit wispFXEG trail",
                eldritchWarden.contains("Thaumcraft.proxy.wispFXEG("));
        assertTrue("Wisp ranged attack path must send PacketFXWispZap",
                wisp.contains("new PacketFXWispZap(this.getEntityId(), this.targetedEntity.getEntityId())"));
        assertTrue("EntityFallingTaint client update must emit taintLandFX landing loop",
                fallingTaint.contains("Thaumcraft.proxy.taintLandFX(this);"));
        assertTrue("FocusShock chain lightning path must send PacketFXZap",
                focusShock.contains("new PacketFXZap(center.getEntityId(), closest.getEntityId())"));
        assertTrue("FocusWarding ward/unward paths must send PacketFXBlockSparkle around touched blocks",
                focusWarding.contains("new PacketFXBlockSparkle(c.x, c.y, c.z, 0xFC9A00)")
                        && focusWarding.contains("PacketHandler.INSTANCE.sendToAllAround(")
                        && focusWarding.contains("new NetworkRegistry.TargetPoint(world.provider.getDimension(), c.x, c.y, c.z, 32.0D)"));
        assertTrue("FocusPortableHole.createHole must send PacketFXBlockSparkle cue",
                focusPortableHole.contains("new PacketFXBlockSparkle(x, y, z, 0x400040)")
                        && focusPortableHole.contains("PacketHandler.INSTANCE.sendToAllAround(")
                        && focusPortableHole.contains("new NetworkRegistry.TargetPoint(world.provider.getDimension(), x, y, z, 32.0D)"));
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

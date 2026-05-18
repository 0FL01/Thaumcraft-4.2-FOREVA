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
        String blockWardFx = readFile("src/main/java/thaumcraft/client/fx/other/FXBlockWard.java");
        String boreFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBoreParticles.java");
        String boreSparkleFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBoreSparkle.java");
        String breakingFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBreaking.java");
        String burstFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBurst.java");
        String bubbleFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBubble.java");
        String bubbleAltFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBubbleAlt.java");
        String blockRunesFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXBlockRunes.java");
        String essentiaTrailFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXEssentiaTrail.java");
        String genericFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXGeneric.java");
        String smokeDriftFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXSmokeDrift.java");
        String sparkleFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXSparkle.java");
        String swarmFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXSwarm.java");
        String ventFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXVent.java");
        String visSparkleFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXVisSparkle.java");
        String wispArcingFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXWispArcing.java");
        String wispFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXWisp.java");
        String wispEgFx = readFile("src/main/java/thaumcraft/client/fx/particles/FXWispEG.java");
        String sonicFx = readFile("src/main/java/thaumcraft/client/fx/other/FXSonic.java");
        String shieldRunesFx = readFile("src/main/java/thaumcraft/client/fx/other/FXShieldRunes.java");
        String beamWandFx = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamWand.java");
        String beamBoreFx = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamBore.java");
        String beamPowerFx = readFile("src/main/java/thaumcraft/client/fx/beams/FXBeamPower.java");

        assertTrue("ClientProxy blockSparkle must route generic colors through dedicated FXVisSparkle",
                source.contains("public void blockSparkle(")
                        && source.contains("new FXVisSparkle(world, x, y, z, red, green, blue, amount)"));
        assertTrue("ClientProxy blockSparkle must preserve random-color sentinel routing for -9999",
                source.contains("color == -9999")
                        && source.contains("new FXVisSparkle(world, x, y, z, 0.0f, 0.0f, 0.0f, amount, true)"));
        assertTrue("ClientProxy blockSparkle must route ward/portable-hole colors through dedicated FXBlockWard",
                source.contains("color == 0xFC9A00 || color == 0x400040")
                        && source.contains("new FXBlockWard(world, x, y, z, color, amount)"));
        assertTrue("ClientProxy beam must contain dedicated FXBeam particle path",
                source.contains("public void beam(") && source.contains("new FXBeam("));
        assertTrue("ClientProxy bolt must contain non-noop bolt particle path",
                source.contains("public void bolt(") && source.contains("speed * 2"));
        assertTrue("ClientProxy must override burst for direct entity FX call sites",
                source.contains("public void burst(") && source.contains("new FXBurst("));
        assertTrue("ClientProxy must expose dedicated sonicFX path through ParticleEngine",
                source.contains("public void sonicFX(")
                        && source.contains("new FXSonic("));
        assertTrue("ClientProxy must expose dedicated shieldRunesFX path through ParticleEngine",
                source.contains("public void shieldRunesFX(")
                        && source.contains("new FXShieldRunes("));
        assertTrue("ClientProxy must expose dedicated zapFX path through FXLightningBolt",
                source.contains("public void zapFX(")
                        && source.contains("bolt.setType(2);")
                        && source.contains("bolt.setWidth(0.125F);")
                        && source.contains("bolt.finalizeBolt();"));
        assertTrue("ClientProxy must override wispFX3 for wisp ambient FX",
                source.contains("public void wispFX3(") && source.contains("new FXWisp("));
        assertTrue("ClientProxy must override wispFXEG for eldritch guardian trail FX",
                source.contains("public void wispFXEG(") && source.contains("new FXWispEG("));
        assertTrue("ClientProxy must override taintLandFX for falling taint landing FX",
                source.contains("public void taintLandFX(")
                        && source.contains("entity.getEntityBoundingBox()")
                        && source.contains("new FXBreaking(")
                        && source.contains("Items.SNOWBALL"));
        assertTrue("ClientProxy must override slimeJumpFX for infested champion fallback",
                source.contains("public void slimeJumpFX(")
                        && source.contains("new FXBreaking(")
                        && source.contains("fx.setRBGColorF(0.7F, 0.0F, 1.0F)"));
        assertTrue("ClientProxy must expose dedicated swarmParticleFX/splooshFX routes for taint swarm-family visuals",
                source.contains("public Object swarmParticleFX(")
                        && source.contains("new FXSwarm(")
                        && source.contains("public void splooshFX(Entity entity)")
                        && source.contains("new FXBreaking(")
                        && source.contains("Items.SNOWBALL"));
        assertTrue("ClientProxy must override drawGenericParticles for champion modifier fallback",
                source.contains("public void drawGenericParticles(") && source.contains("new FXGeneric("));
        assertTrue("ClientProxy must override drawVentParticles for thaumatorium vent routing",
                source.contains("public void drawVentParticles(") && source.contains("new FXVent("));
        assertTrue("CommonProxy/ClientProxy must keep extended beam helpers for wand/bore/power routes",
                commonProxy.contains("public Object beamCont(")
                        && commonProxy.contains("public Object beamBore(")
                        && commonProxy.contains("public Object beamPower(")
                        && source.contains("public Object beamCont(")
                        && source.contains("new FXBeamWand(")
                        && source.contains("public Object beamBore(")
                        && source.contains("new FXBeamBore(")
                        && source.contains("public Object beamPower(")
                        && source.contains("new FXBeamPower("));
        assertTrue("CommonProxy/ClientProxy must keep extended node/source/trail/rune/arc FX surfaces",
                commonProxy.contains("public void sourceStreamFX(")
                        && commonProxy.contains("public void essentiaTrailFx(")
                        && commonProxy.contains("public void blockRunes(")
                        && commonProxy.contains("public void arcLightning(")
                        && commonProxy.contains("public void bolt(World world, Entity sourceEntity, Entity targetedEntity)")
                        && commonProxy.contains("public void nodeBolt(World world, float x, float y, float z, Entity target)")
                        && commonProxy.contains("public void nodeBolt(World world, float x, float y, float z, float tx, float ty, float tz)")
                        && source.contains("public void sourceStreamFX(")
                        && source.contains("new FXWispArcing(")
                        && source.contains("public void essentiaTrailFx(")
                        && source.contains("new FXEssentiaTrail(")
                        && source.contains("public void blockRunes(")
                        && source.contains("new FXBlockRunes(")
                        && source.contains("public void arcLightning(")
                        && source.contains("new FXArc(")
                        && source.contains("public void bolt(World world, Entity sourceEntity, Entity targetedEntity)")
                        && source.contains("public void nodeBolt(World world, float x, float y, float z, Entity target)")
                        && source.contains("public void nodeBolt(World world, float x, float y, float z, float tx, float ty, float tz)"));
        assertTrue("CommonProxy/ClientProxy must keep infusion helper surfaces",
                commonProxy.contains("public void drawInfusionParticles1(")
                        && commonProxy.contains("public void drawInfusionParticles2(")
                        && commonProxy.contains("public void drawInfusionParticles3(")
                        && commonProxy.contains("public void drawInfusionParticles4(")
                        && source.contains("public void drawInfusionParticles1(")
                        && source.contains("public void drawInfusionParticles2(")
                        && source.contains("public void drawInfusionParticles3(")
                        && source.contains("public void drawInfusionParticles4("));
        assertTrue("ClientProxy infusion particle helper should use dedicated FXBoreSparkle for phases 3/4",
                source.contains("new FXBoreSparkle(world, x, y, z, tx + 0.5D, ty - 0.5D, tz + 0.5D)"));
        assertTrue("CommonProxy and ClientProxy must keep boreDigFx proxy surface with dedicated client FX routing",
                commonProxy.contains("public void boreDigFx(World world,")
                        && source.contains("public void boreDigFx(World world,")
                        && source.contains("new FXBoreParticles("));
        assertTrue("ClientProxy must override sparkle for firebat/lifter visuals",
                source.contains("public void sparkle(") && source.contains("new FXSparkle("));
        assertTrue("ClientProxy must override particleCount using client particle settings",
                source.contains("public int particleCount(") && source.contains("mc.gameSettings.particleSetting"));
        assertTrue("ClientProxy must override crucibleFroth and crucibleFrothDown",
                source.contains("public void crucibleFroth(")
                        && source.contains("public void crucibleFrothDown(")
                        && source.contains("new FXSmokeDrift("));
        assertTrue("ClientProxy must override crucibleBubble with colored bubble path",
                source.contains("public void crucibleBubble(")
                        && source.contains("new FXBubbleAlt(")
                        && source.contains("ParticleEngine.addEffect(world, bubble)"));
        assertTrue("ClientProxy must override crucibleBoilSound and crucibleBoil",
                source.contains("public void crucibleBoilSound(")
                        && source.contains("public void crucibleBoil(")
                        && source.contains("TCSounds.BUBBLE")
                        && source.contains("new FXBubble(")
                        && source.contains("bubble.setBubbleSpeed(0.003D * type)"));
        assertTrue("CommonProxy and ClientProxy must keep startScan proxy surface",
                commonProxy.contains("public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius)")
                        && source.contains("public void startScan(Entity entity, BlockPos pos, long expireAtMs, int radius)")
                        && source.contains("RenderEventHandler.startScan(entity, pos, expireAtMs, radius);"));
        assertTrue("CommonProxy must keep no-op side-safe stubs for dedicated sonic/shield rune/zap fx",
                commonProxy.contains("public void sonicFX(World world, Entity source, int age)")
                        && commonProxy.contains("public void shieldRunesFX(World world, Entity source, int age, float yaw, float pitch)")
                        && commonProxy.contains("public void zapFX(World world, Entity source, Entity target)"));
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
        assertTrue("Dedicated FXBlockWard particle must keep block-centered ward emission baseline",
                blockWardFx.contains("class FXBlockWard extends Particle")
                        && blockWardFx.contains("EnumFacing.random(this.rand)")
                        && blockWardFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXBoreParticles must keep target-driven bore dig emission baseline",
                boreFx.contains("class FXBoreParticles extends Particle")
                        && boreFx.contains("targetX")
                        && boreFx.contains("EnumParticleTypes.BLOCK_CRACK")
                        && boreFx.contains("EnumParticleTypes.ITEM_CRACK"));
        assertTrue("Dedicated FXBoreSparkle particle must keep target-chasing sparkle baseline",
                boreSparkleFx.contains("class FXBoreSparkle extends Particle")
                        && boreSparkleFx.contains("targetX")
                        && boreSparkleFx.contains("EnumParticleTypes.REDSTONE")
                        && boreSparkleFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXBreaking particle must keep item-crack + tint baseline",
                breakingFx.contains("class FXBreaking extends Particle")
                        && breakingFx.contains("setParticleMaxAge(int particleMaxAge)")
                        && breakingFx.contains("EnumParticleTypes.ITEM_CRACK")
                        && breakingFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXBurst particle must keep burst emission baseline",
                burstFx.contains("class FXBurst extends Particle")
                        && burstFx.contains("EnumParticleTypes.EXPLOSION_NORMAL"));
        assertTrue("Dedicated FXBubble particle must keep froth controls and water-bubble emission baseline",
                bubbleFx.contains("class FXBubble extends Particle")
                        && bubbleFx.contains("setFroth()")
                        && bubbleFx.contains("setFroth2()")
                        && bubbleFx.contains("setBubbleSpeed(double bubbleSpeed)")
                        && bubbleFx.contains("EnumParticleTypes.WATER_BUBBLE"));
        assertTrue("Dedicated FXBubbleAlt particle must keep colored crucible bubble emission baseline",
                bubbleAltFx.contains("class FXBubbleAlt extends Particle")
                        && bubbleAltFx.contains("setRGB(float r, float g, float b)")
                        && bubbleAltFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXBlockRunes particle must keep rune-around-block emission baseline",
                blockRunesFx.contains("class FXBlockRunes extends Particle")
                        && blockRunesFx.contains("EnumFacing.random(this.rand)")
                        && blockRunesFx.contains("setGravity(float gravity)")
                        && blockRunesFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXEssentiaTrail particle must keep colorized target-chasing trail baseline",
                essentiaTrailFx.contains("class FXEssentiaTrail extends Particle")
                        && essentiaTrailFx.contains("targetX")
                        && essentiaTrailFx.contains("new Color(")
                        && essentiaTrailFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXGeneric particle must keep configurable generic particle baseline",
                genericFx.contains("class FXGeneric extends Particle")
                        && genericFx.contains("loop")
                        && genericFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXSmokeDrift particle must keep smoke drift emission baseline",
                smokeDriftFx.contains("class FXSmokeDrift extends Particle")
                        && smokeDriftFx.contains("EnumParticleTypes.SMOKE_NORMAL")
                        && smokeDriftFx.contains("this.motionY *= 0.92D"));
        assertTrue("Dedicated FXSparkle particle must keep colored sparkle emission baseline",
                sparkleFx.contains("class FXSparkle extends Particle")
                        && sparkleFx.contains("EnumParticleTypes.REDSTONE")
                        && sparkleFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXSwarm particle must keep target-tracking swarm baseline",
                swarmFx.contains("class FXSwarm extends Particle")
                        && swarmFx.contains("private final Entity target;")
                        && swarmFx.contains("steerTowardsTarget()")
                        && swarmFx.contains("EnumParticleTypes.REDSTONE")
                        && swarmFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXVent particle must keep vent trail emission baseline",
                ventFx.contains("class FXVent extends Particle")
                        && ventFx.contains("EnumParticleTypes.REDSTONE")
                        && ventFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXVisSparkle particle must keep block-centered sparkle emission baseline",
                visSparkleFx.contains("class FXVisSparkle extends Particle")
                        && visSparkleFx.contains("baseX")
                        && visSparkleFx.contains("EnumParticleTypes.REDSTONE")
                        && visSparkleFx.contains("randomizeColor")
                        && visSparkleFx.contains("0.33f + this.rand.nextFloat() * 0.67f"));
        assertTrue("Dedicated FXWisp particle must keep target-aware wisp trail baseline",
                wispFx.contains("class FXWisp extends Particle")
                        && wispFx.contains("hasTarget")
                        && wispFx.contains("EnumParticleTypes.REDSTONE"));
        assertTrue("Dedicated FXWispArcing particle must keep source-target arcing trail baseline",
                wispArcingFx.contains("class FXWispArcing extends Particle")
                        && wispArcingFx.contains("targetX")
                        && wispArcingFx.contains("EnumParticleTypes.CRIT_MAGIC"));
        assertTrue("Dedicated FXWispEG particle must keep target-following elder trail baseline",
                wispEgFx.contains("class FXWispEG extends Particle")
                        && wispEgFx.contains("this.target")
                        && wispEgFx.contains("target.height * 0.22F"));
        assertTrue("Dedicated FXBeamWand/FXBeamBore/FXBeamPower classes must keep extended beam control surface",
                beamWandFx.contains("class FXBeamWand extends FXBeam")
                        && beamWandFx.contains("updateBeam(double tx, double ty, double tz)")
                        && beamBoreFx.contains("class FXBeamBore extends FXBeam")
                        && beamBoreFx.contains("public int impact;")
                        && beamPowerFx.contains("class FXBeamPower extends FXBeam")
                        && beamPowerFx.contains("setPulse(boolean pulse, float red, float green, float blue)"));
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
        String taintSwarm = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSwarm.java");
        String taintSpore = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSpore.java");
        String taintSporeSwarmer = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSporeSwarmer.java");
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

        assertTrue("PacketFXVisDrain must schedule client task and route through FXVisSparkle trail",
                visDrain.contains("Minecraft.getMinecraft().addScheduledTask")
                        && visDrain.contains("new FXVisSparkle(")
                        && visDrain.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXBlockArc must schedule client task and route through proxy arcLightning",
                blockArc.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockArc.contains("Thaumcraft.proxy.arcLightning(")
                        && blockArc.contains("EntityCultistPortal"));
        assertTrue("PacketFXBlockZap must schedule client task and route through proxy nodeBolt",
                blockZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockZap.contains("Thaumcraft.proxy.nodeBolt("));
        assertTrue("PacketFXBlockDig must schedule client task and route dig particles through proxy boreDigFx",
                blockDig.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockDig.contains("Thaumcraft.proxy.boreDigFx("));
        assertTrue("PacketFXBlockBubble must schedule client task and route through proxy crucibleBubble surface",
                blockBubble.contains("Minecraft.getMinecraft().addScheduledTask")
                        && blockBubble.contains("Thaumcraft.proxy.crucibleBubble("));
        assertTrue("PacketFXBeamPulse must schedule client task and route through dedicated FXBeam",
                beamPulse.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulse.contains("new FXBeam(")
                        && beamPulse.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXBeamPulseGolemBoss must schedule client task and route through dedicated FXBeamGolemBoss",
                beamPulseGolemBoss.contains("Minecraft.getMinecraft().addScheduledTask")
                        && beamPulseGolemBoss.contains("new FXBeamGolemBoss(")
                        && beamPulseGolemBoss.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXEssentiaSource must schedule client task and update EssentiaHandler sourceFX queue",
                essentiaSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && essentiaSource.contains("EssentiaHandler.sourceFX")
                        && essentiaSource.contains("new EssentiaHandler.EssentiaSourceFX("));
        assertTrue("PacketFXInfusionSource must schedule client task and update TileInfusionMatrix sourceFX state",
                infusionSource.contains("Minecraft.getMinecraft().addScheduledTask")
                        && infusionSource.contains("matrix.sourceFX")
                        && infusionSource.contains("new TileInfusionMatrix.SourceFX("));
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
        assertTrue("PacketFXShield must schedule client task and route shield rune triggers through proxy boundary",
                shield.contains("Minecraft.getMinecraft().addScheduledTask")
                        && shield.contains("Thaumcraft.proxy.shieldRunesFX(")
                        && !shield.contains("new FXShieldRunes(")
                        && !shield.contains("ParticleEngine.addEffect(")
                        && !shield.contains("new FXLightningBolt(")
                        && !shield.contains("Thaumcraft.proxy.burst("));
        assertTrue("PacketFXSonic must schedule client task and route sonic trigger through proxy boundary",
                sonic.contains("Minecraft.getMinecraft().addScheduledTask")
                        && sonic.contains("Thaumcraft.proxy.sonicFX(")
                        && !sonic.contains("new FXSonic(")
                        && !sonic.contains("ParticleEngine.addEffect(")
                        && !sonic.contains("Thaumcraft.proxy.burst("));
        assertTrue("PacketFXWispZap must schedule client task and route through single proxy bolt path",
                wispZap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && wispZap.contains("Thaumcraft.proxy.bolt(mc.world, sourceEntity, targetEntity)")
                        && !wispZap.contains("new FXLightningBolt(")
                        && !wispZap.contains("ParticleEngine.addEffect("));
        assertTrue("PacketFXZap must schedule client task and route zap trigger through proxy boundary",
                zap.contains("Minecraft.getMinecraft().addScheduledTask")
                        && zap.contains("Thaumcraft.proxy.zapFX(mc.world, sourceEntity, targetEntity);")
                        && !zap.contains("new FXLightningBolt(")
                        && !zap.contains("bolt.finalizeBolt();")
                        && !zap.contains("ParticleEngine.addEffect(")
                        && !zap.contains("Thaumcraft.proxy.bolt(")
                        && !zap.contains("playEvent(2005"));
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
        assertTrue("Taint swarm-family client loops must route through proxy swarmParticleFX/splooshFX",
                taintSwarm.contains("Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.22F, 15.0F, 0.08F)")
                        && taintSpore.contains("Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F)")
                        && taintSpore.contains("Thaumcraft.proxy.splooshFX(this);")
                        && taintSporeSwarmer.contains("Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F)")
                        && taintSporeSwarmer.contains("Thaumcraft.proxy.splooshFX(this);"));
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

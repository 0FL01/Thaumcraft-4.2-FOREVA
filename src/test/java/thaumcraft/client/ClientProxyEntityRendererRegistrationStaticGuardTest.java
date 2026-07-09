package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientProxyEntityRendererRegistrationStaticGuardTest {

    @Test
    public void entityRendererBootstrapStaysWired() throws IOException {
        String source = readFile("src/main/java/thaumcraft/client/ClientProxy.java");
        String noopRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderNoop.java");

        assertTrue("ClientProxy must keep setupEntityRenderers entry-point",
                source.contains("private void setupEntityRenderers()"));
        assertTrue("ClientProxy must keep dedicated special-item renderer registrations for item-like entities",
                source.contains("registerEntityRenderer(EntitySpecialItem.class, RenderSpecialItem::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPermanentItem.class, RenderSpecialItem::new, registered);")
                        && source.contains("registerEntityRenderer(EntityFollowingItem.class, RenderFollowingItem::new, registered);")
                        && source.contains("registerEntityRenderer(EntityItemGrate.class, RenderSpecialItem::new, registered);"));
        assertTrue("ClientProxy must keep non-noop projectile baselines plus dedicated aspect-orb renderer",
                source.contains("registerEntityRenderer(EntityDart.class, RenderDart::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPrimalArrow.class, RenderPrimalArrow::new, registered);")
                        && source.contains("registerEntityRenderer(EntityBottleTaint.class,")
                        && source.contains("registerEntityRenderer(EntityAlumentum.class, RenderAlumentum::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPrimalOrb.class, RenderPrimalOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityFrostShard.class, RenderFrostShard::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPechBlast.class, RenderPechBlast::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchOrb.class, RenderEldritchOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityGolemOrb.class, RenderElectricOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityShockOrb.class, RenderElectricOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityExplosiveOrb.class, RenderExplosiveOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEmber.class, RenderEmber::new, registered);")
                        && source.contains("registerEntityRenderer(EntityGolemBobber.class, RenderGolemBobber::new, registered);")
                        && source.contains("registerEntityRenderer(EntityAspectOrb.class, RenderAspectOrb::new, registered);")
                        && source.contains("registerEntityRenderer(EntityFallingTaint.class, RenderFallingTaint::new, registered);"));
        assertTrue("ClientProxy must keep vanilla mob fallback renderer registrations for compatible zombie/spider groups",
                source.contains("registerEntityRenderer(EntityBrainyZombie.class, RenderBrainyZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityGiantBrainyZombie.class, RenderBrainyZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityInhabitedZombie.class, RenderInhabitedZombie::new, registered);")
                        && source.contains("registerEntityRenderer(EntityMindSpider.class, RenderMindSpider::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSpider.class, RenderTaintSpider::new, registered);"));
        assertTrue("ClientProxy must keep dedicated taint animal-like renderer registrations",
                source.contains("registerEntityRenderer(EntityTaintChicken.class, RenderTaintChicken::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintCow.class, RenderTaintCow::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintPig.class, RenderTaintPig::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSheep.class, RenderTaintSheep::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintVillager.class, RenderTaintVillager::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintCreeper.class, RenderTaintCreeper::new, registered);"));
        assertTrue("ClientProxy must keep fallback RenderFallbackBiped registrations for cultist entities",
                source.contains("registerEntityRenderer(EntityCultistKnight.class, manager -> new RenderCultist<>(manager, 0.5F), registered);")
                        && source.contains("registerEntityRenderer(EntityCultistCleric.class, manager -> new RenderCultist<>(manager, 0.5F), registered);")
                        && source.contains("registerEntityRenderer(EntityCultistLeader.class, manager -> new RenderCultist<>(manager, 0.6F), registered);"));
        assertTrue("ClientProxy must keep extended fallback registrations for the remaining Stage 8-d monster baseline",
                source.contains("registerEntityRenderer(EntityFireBat.class, RenderFireBat::new, registered);")
                        && source.contains("registerEntityRenderer(EntityWisp.class, RenderWisp::new, registered);")
                        && source.contains("registerEntityRenderer(EntityWatcher.class, RenderWatcher::new, registered);")
                        && source.contains("registerEntityRenderer(EntityPech.class, RenderPech::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchGuardian.class, RenderEldritchGuardian::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchWarden.class, RenderEldritchWarden::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchGolem.class, RenderEldritchGolem::new, registered);")
                        && source.contains("registerEntityRenderer(EntityEldritchCrab.class, RenderEldritchCrab::new, registered);")
                        && source.contains("registerEntityRenderer(EntityThaumicSlime.class, RenderThaumicSlime::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSpore.class, RenderTaintSpore::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSporeSwarmer.class, RenderTaintSporeSwarmer::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintSwarm.class, RenderTaintSwarm::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacle.class, manager -> new RenderTaintacle<>(manager, 0.6F, 10), registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacleSmall.class, manager -> new RenderTaintacle<>(manager, 0.2F, 6), registered);")
                        && source.contains("registerEntityRenderer(EntityTaintacleGiant.class, manager -> new RenderTaintacle<>(manager, 1.0F, 14), registered);"));
        assertTrue("ClientProxy must keep fallback registrations for remaining special entities",
                source.contains("registerEntityRenderer(EntityGolemBase.class, RenderGolemBase::new, registered);")
                        && source.contains("registerEntityRenderer(EntityTravelingTrunk.class, RenderTravelingTrunk::new, registered);")
                        && source.contains("registerEntityRenderer(EntityCultistPortal.class, RenderCultistPortal::new, registered);"));
        assertTrue("RenderFallbackLiving must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackLiving.java").contains("extends RenderLiving<T>"));
        assertTrue("ClientProxy should not retain obsolete RenderTaintTextureLiving references",
                !source.contains("RenderTaintTextureLiving"));
        assertTrue("RenderFallbackBiped must exist as a non-noop typed texture renderer",
                readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallbackBiped.java").contains("extends RenderBiped<T>"));
        String pechRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderPech.java");
        String pechModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelPech.java");
        assertTrue("RenderPech must provide dedicated pech-model, held-item layer, and texture routing baseline",
                pechRenderer.contains("extends RenderLiving<EntityPech>")
                        && pechRenderer.contains("new ModelPech()")
                        && pechRenderer.contains("PechHeldItemLayer")
                        && pechRenderer.contains("renderItemSide(")
                        && pechRenderer.contains("textures/models/pech_forage.png")
                        && pechRenderer.contains("textures/models/pech_thaum.png")
                        && pechRenderer.contains("textures/models/pech_stalker.png")
                        && pechRenderer.contains("entity.getPechType()")
                        && pechModel.contains("class ModelPech")
                        && pechModel.contains("mumble")
                        && pechModel.contains("rightArm")
                        && pechModel.contains("upperPack"));
        String fireBatRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFireBat.java");
        String fireBatModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelFireBat.java");
        assertTrue("RenderFireBat must provide dedicated firebat model plus vampire texture, scaling, and hanging transform baselines",
                fireBatRenderer.contains("extends RenderLiving<EntityFireBat>")
                        && fireBatRenderer.contains("new ModelFireBat()")
                        && fireBatRenderer.contains("textures/models/firebat.png")
                        && fireBatRenderer.contains("textures/models/vampirebat.png")
                        && fireBatRenderer.contains("entity.getIsVampire()")
                        && fireBatRenderer.contains("entity.getIsDevil()")
                        && fireBatRenderer.contains("entity.getIsBatHanging()")
                        && fireBatModel.contains("class ModelFireBat")
                        && fireBatModel.contains("getBatSize()")
                        && fireBatModel.contains("batOuterRightWing")
                        && fireBatModel.contains("getIsBatHanging()"));
        String wispRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderWisp.java");
        assertTrue("RenderWisp must provide dedicated billboard core and halo baseline",
                wispRenderer.contains("extends Render<EntityWisp>")
                        && wispRenderer.contains("textures/misc/wisp.png")
                        && wispRenderer.contains("textures/misc/particles.png")
                        && wispRenderer.contains("Aspect.getAspect(entity.getWispType())")
                        && wispRenderer.contains("GlStateManager.disableLighting()")
                        && wispRenderer.contains("GlStateManager.disableCull()")
                        && wispRenderer.contains("renderCore(entity, red, green, blue)")
                        && wispRenderer.contains("renderHalo(entity, partialTicks)")
                        && wispRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, FULL_BRIGHT, FULL_BRIGHT)")
                        && wispRenderer.contains("this.renderManager.options.thirdPersonView == 2 ? -1.0F : 1.0F")
                        && wispRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && wispRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR"));
        String watcherRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderWatcher.java");
        String watcherModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelWatcher.java");
        assertTrue("RenderWatcher must provide dedicated watcher model, beam texture, frustum check, and target-beam baseline",
                watcherRenderer.contains("extends RenderLiving<EntityWatcher>")
                        && watcherRenderer.contains("new ModelWatcher()")
                        && watcherRenderer.contains("textures/models/watcher.png")
                        && watcherRenderer.contains("textures/models/watcher_beam.png")
                        && watcherRenderer.contains("shouldRender(EntityWatcher livingEntity, ICamera camera, double camX, double camY, double camZ)")
                        && watcherRenderer.contains("entity.getGazeProgress(partialTicks)")
                        && watcherRenderer.contains("camera.isBoundingBoxInFrustum")
                        && watcherRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR")
                        && watcherModel.contains("class ModelWatcher extends ModelBase")
                        && watcherModel.contains("watcherSpines = new ModelRenderer[12]")
                        && watcherModel.contains("watcher.hasTargetedEntity()")
                        && watcherModel.contains("watcher.getTailAngle(partialTicks)")
                        && watcherModel.contains("watcher.getFinAngle(partialTicks)"));
        String aspectOrbRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderAspectOrb.java");
        assertTrue("RenderAspectOrb must provide particle-texture billboard with aspect tint and blend-factor mapping baseline",
                aspectOrbRenderer.contains("extends Render<EntityAspectOrb>")
                        && aspectOrbRenderer.contains("textures/misc/particles.png")
                        && aspectOrbRenderer.contains("mapDestBlendFactor")
                        && aspectOrbRenderer.contains("orb.getAspect().getBlend()")
                        && aspectOrbRenderer.contains("orb.orbMaxAge - orb.orbAge")
                        && aspectOrbRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR"));
        String specialItemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderSpecialItem.java");
        String followingItemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFollowingItem.java");
        assertTrue("RenderSpecialItem and RenderFollowingItem must provide dedicated special-item render baselines",
                specialItemRenderer.contains("extends Render<EntityItem>")
                        && specialItemRenderer.contains("renderBurst(")
                        && specialItemRenderer.contains("RenderEntityItem")
                        && specialItemRenderer.contains("Random(245L)")
                        && specialItemRenderer.contains("GL11.GL_TRIANGLE_FAN")
                        && specialItemRenderer.contains("255, 0, 255, 0")
                        && followingItemRenderer.contains("extends Render<EntityItem>")
                        && followingItemRenderer.contains("RenderEntityItem")
                        && followingItemRenderer.contains("!entity.getItem().isEmpty()")
                        && followingItemRenderer.contains("this.itemRenderer.doRender(entity, x, y, z, entityYaw, partialTicks)"));
        String electricOrbRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderElectricOrb.java");
        assertTrue("RenderElectricOrb must provide shared golem/shock orb particle billboard baseline",
                electricOrbRenderer.contains("extends Render<Entity>")
                        && electricOrbRenderer.contains("textures/misc/particles.png")
                        && electricOrbRenderer.contains("entity instanceof EntityGolemOrb")
                        && electricOrbRenderer.contains("((EntityGolemOrb) entity).red")
                        && electricOrbRenderer.contains("MathHelper.sin(entity.ticksExisted / 5.0F)")
                        && electricOrbRenderer.contains("DefaultVertexFormats.POSITION_TEX"));
        String explosiveOrbRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderExplosiveOrb.java");
        assertTrue("RenderExplosiveOrb must provide dedicated particles2 billboard baseline",
                explosiveOrbRenderer.contains("extends Render<Entity>")
                        && explosiveOrbRenderer.contains("textures/misc/particles2.png")
                        && explosiveOrbRenderer.contains("entity.ticksExisted % 4")
                        && explosiveOrbRenderer.contains("GlStateManager.scale(0.7F, 0.7F, 0.7F)")
                        && explosiveOrbRenderer.contains("DefaultVertexFormats.POSITION_TEX"));
        String emberRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEmber.java");
        assertTrue("RenderEmber must provide duration-driven particles billboard baseline",
                emberRenderer.contains("extends Render<EntityEmber>")
                        && emberRenderer.contains("textures/misc/particles.png")
                        && emberRenderer.contains("entity.duration")
                        && emberRenderer.contains("8.0F * ((float) entity.ticksExisted / (float) entity.duration)")
                        && emberRenderer.contains("0.25F + lifeFraction")
                        && emberRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR"));
        String eldritchOrbRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchOrb.java");
        assertTrue("RenderEldritchOrb must provide spike-burst plus billboard particle baseline",
                eldritchOrbRenderer.contains("extends Render<Entity>")
                        && eldritchOrbRenderer.contains("textures/misc/particles.png")
                        && eldritchOrbRenderer.contains("renderSpikeBurst(")
                        && eldritchOrbRenderer.contains("renderBillboard(")
                        && eldritchOrbRenderer.contains("BlockCustomOreItem.colors[5]")
                        && eldritchOrbRenderer.contains("entity.ticksExisted % 13")
                        && eldritchOrbRenderer.contains("DefaultVertexFormats.POSITION_COLOR"));
        String primalOrbRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderPrimalOrb.java");
        assertTrue("RenderPrimalOrb must provide primal-color spike-burst plus billboard baseline",
                primalOrbRenderer.contains("extends Render<Entity>")
                        && primalOrbRenderer.contains("textures/misc/particles.png")
                        && primalOrbRenderer.contains("renderPrimalSpikes(")
                        && primalOrbRenderer.contains("renderPrimalBillboard(")
                        && primalOrbRenderer.contains("BlockCustomOreItem.colors[i / 2 + 1]")
                        && primalOrbRenderer.contains("entity.ticksExisted % 13")
                        && primalOrbRenderer.contains("GlStateManager.scale(0.5F, 0.5F, 0.5F)"));
        String pechBlastRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderPechBlast.java");
        assertTrue("RenderPechBlast must provide intentional no-op renderer baseline",
                pechBlastRenderer.contains("extends Render<EntityPechBlast>")
                        && pechBlastRenderer.contains("intentional no-op draw path")
                        && pechBlastRenderer.contains("return null;"));
        String alumentumRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderAlumentum.java");
        assertTrue("RenderAlumentum must provide intentional no-op renderer baseline",
                alumentumRenderer.contains("extends Render<EntityAlumentum>")
                        && alumentumRenderer.contains("intentional no-op draw path")
                        && alumentumRenderer.contains("return null;"));
        String dartRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderDart.java");
        assertTrue("RenderDart must provide dedicated arrow renderer baseline",
                dartRenderer.contains("extends RenderArrow<EntityDart>")
                        && dartRenderer.contains("new ResourceLocation(\"textures/entity/arrow.png\")"));
        String primalArrowRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderPrimalArrow.java");
        assertTrue("RenderPrimalArrow must provide dedicated typed arrow renderer baseline",
                primalArrowRenderer.contains("extends RenderArrow<EntityPrimalArrow>")
                        && primalArrowRenderer.contains("new ResourceLocation(\"textures/entity/arrow.png\")")
                        && primalArrowRenderer.contains("entity.getArrowType()")
                        && primalArrowRenderer.contains("BlockCustomOreItem.colors")
                        && primalArrowRenderer.contains("GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F)"));
        String golemBobberRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderGolemBobber.java");
        assertTrue("RenderGolemBobber must provide particles-atlas bobber quad plus fisher tether baseline",
                golemBobberRenderer.contains("extends Render<EntityGolemBobber>")
                        && golemBobberRenderer.contains("new ResourceLocation(\"textures/particle/particles.png\")")
                        && golemBobberRenderer.contains("if (entity.fisher != null)")
                        && golemBobberRenderer.contains("fisher.rightArm / 3.0F")
                        && golemBobberRenderer.contains("buffer.begin(3, DefaultVertexFormats.POSITION_COLOR)")
                        && golemBobberRenderer.contains("GlStateManager.disableTexture2D()")
                        && golemBobberRenderer.contains("GlStateManager.enableTexture2D()"));
        String fallingTaintRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFallingTaint.java");
        assertTrue("RenderFallingTaint must provide block-model atlas render baseline",
                fallingTaintRenderer.contains("extends Render<EntityFallingTaint>")
                        && fallingTaintRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && fallingTaintRenderer.contains("block.getStateFromMeta(entity.metadata)")
                        && fallingTaintRenderer.contains("world.getBlockState(blockPos)")
                        && fallingTaintRenderer.contains("EnumBlockRenderType.MODEL")
                        && fallingTaintRenderer.contains("dispatcher.getBlockModelRenderer().renderModel(")
                        && fallingTaintRenderer.contains("buffer.setTranslation(-blockPos.getX() - 0.5D")
                        && fallingTaintRenderer.contains("GlStateManager.disableLighting()")
                        && fallingTaintRenderer.contains("GlStateManager.enableLighting()"));
        String frostShardRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderFrostShard.java");
        assertTrue("RenderFrostShard must provide the dedicated baked frost-shard model path with a visible fallback",
                frostShardRenderer.contains("extends Render<EntityFrostShard>")
                        && frostShardRenderer.contains("new ResourceLocation(\"thaumcraft\", \"textures/blocks/frostshard.png\")")
                        && frostShardRenderer.contains("new Random(entity.getEntityId())")
                        && frostShardRenderer.contains("entity.getDamage() * 0.1F")
                        && frostShardRenderer.contains("GlStateManager.enableBlend()")
                        && frostShardRenderer.contains("ClientModelRegistry.getFrostShardModel()")
                        && frostShardRenderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && frostShardRenderer.contains("renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F)")
                        && frostShardRenderer.contains("renderCrossQuads()")
                        && frostShardRenderer.contains("buffer.begin(7, DefaultVertexFormats.POSITION_TEX)"));
        String eldritchGuardianRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchGuardian.java");
        String eldritchGuardianModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelEldritchGuardian.java");
        assertTrue("RenderEldritchGuardian must provide dedicated guardian texture baseline",
                eldritchGuardianRenderer.contains("extends RenderLiving<EntityEldritchGuardian>")
                        && eldritchGuardianRenderer.contains("new ModelEldritchGuardian()")
                        && eldritchGuardianRenderer.contains("textures/models/eldritch_guardian.png")
                        && eldritchGuardianRenderer.contains("getDistanceFadeAlpha(")
                        && eldritchGuardianModel.contains("class ModelEldritchGuardian")
                        && eldritchGuardianModel.contains("HoodEye")
                        && eldritchGuardianModel.contains("OpenGlHelper.setLightmapTextureCoords"));
        String eldritchWardenRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchWarden.java");
        assertTrue("RenderEldritchWarden must provide dedicated warden texture and scale baseline",
                eldritchWardenRenderer.contains("extends RenderLiving<EntityEldritchWarden>")
                        && eldritchWardenRenderer.contains("new ModelEldritchGuardian()")
                        && eldritchWardenRenderer.contains("textures/models/eldritch_warden.png")
                        && eldritchWardenRenderer.contains("entity.getSpawnTimer()")
                        && eldritchWardenRenderer.contains("GlStateManager.scale(1.5F, 1.5F, 1.5F)"));
        String eldritchGolemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchGolem.java");
        String eldritchGolemModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelEldritchGolem.java");
        assertTrue("RenderEldritchGolem must provide dedicated golem texture and scale baseline",
                eldritchGolemRenderer.contains("extends RenderLiving<EntityEldritchGolem>")
                        && eldritchGolemRenderer.contains("new ModelEldritchGolem()")
                        && eldritchGolemRenderer.contains("textures/models/eldritch_golem.png")
                        && eldritchGolemRenderer.contains("GlStateManager.scale(2.15F, 2.15F, 2.15F)")
                        && eldritchGolemModel.contains("class ModelEldritchGolem")
                        && eldritchGolemModel.contains("Frontcloth0")
                        && eldritchGolemModel.contains("getAttackTimer()"));
        String eldritchCrabRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderEldritchCrab.java");
        String eldritchCrabModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelEldritchCrab.java");
        assertTrue("RenderEldritchCrab must provide crab texture and overlay layer baseline",
                eldritchCrabRenderer.contains("extends RenderLiving<EntityEldritchCrab>")
                        && eldritchCrabRenderer.contains("new ModelEldritchCrab()")
                        && eldritchCrabRenderer.contains("textures/models/crab.png")
                        && eldritchCrabRenderer.contains("textures/models/craboverlay.png")
                        && eldritchCrabRenderer.contains("class CrabOverlayLayer")
                        && eldritchCrabRenderer.contains("this.addLayer(new CrabOverlayLayer())")
                        && eldritchCrabModel.contains("class ModelEldritchCrab")
                        && eldritchCrabModel.contains("hasHelm()")
                        && eldritchCrabModel.contains("rightClawEnd")
                        && eldritchCrabModel.contains("tailHelm"));
        String taintSporeRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSpore.java");
        String taintSporeModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSpore.java");
        assertTrue("RenderTaintSpore must provide dedicated spore model and size-driven negative-scale baseline",
                taintSporeRenderer.contains("extends RenderLiving<EntityTaintSpore>")
                        && taintSporeRenderer.contains("new ModelTaintSpore()")
                        && taintSporeRenderer.contains("entity.displaySize")
                        && taintSporeRenderer.contains("entity.getSporeSize()")
                        && taintSporeRenderer.contains("baseScale = -0.12F")
                        && taintSporeModel.contains("class ModelTaintSpore")
                        && taintSporeModel.contains("hurtTime > 0")
                        && taintSporeModel.contains("GL11.glEnable(GL11.GL_BLEND)"));
        String taintSporeSwarmerRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSporeSwarmer.java");
        String taintSporeSwarmerModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSporeSwarmer.java");
        assertTrue("RenderTaintSporeSwarmer must provide dedicated swarmer model with fullbright inner shell baseline",
                taintSporeSwarmerRenderer.contains("extends RenderLiving<EntityTaintSporeSwarmer>")
                        && taintSporeSwarmerRenderer.contains("new ModelTaintSporeSwarmer()")
                        && taintSporeSwarmerModel.contains("class ModelTaintSporeSwarmer")
                        && taintSporeSwarmerModel.contains("displaySize")
                        && taintSporeSwarmerModel.contains("0xF000F0")
                        && taintSporeSwarmerModel.contains("outerCube")
                        && taintSporeSwarmerModel.contains("hurtTime > 0"));
        String cultistPortalRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderCultistPortal.java");
        assertTrue("RenderCultistPortal must provide dedicated portal texture baseline",
                cultistPortalRenderer.contains("extends Render<EntityCultistPortal>")
                        && cultistPortalRenderer.contains("textures/misc/cultist_portal.png")
                        && cultistPortalRenderer.contains("renderPortal(")
                        && cultistPortalRenderer.contains("portal.hurtTime")
                        && cultistPortalRenderer.contains("portal.pulse")
                        && cultistPortalRenderer.contains("this.renderManager.playerViewY")
                        && cultistPortalRenderer.contains("this.renderManager.playerViewX")
                        && cultistPortalRenderer.contains("DefaultVertexFormats.POSITION_TEX_COLOR"));
        String thaumicSlimeRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderThaumicSlime.java");
        assertTrue("RenderThaumicSlime must provide texture, scale, and gel layer baselines",
                thaumicSlimeRenderer.contains("extends RenderLiving<EntityThaumicSlime>")
                        && thaumicSlimeRenderer.contains("textures/models/tslime.png")
                        && thaumicSlimeRenderer.contains("entity.getSlimeSize()")
                        && thaumicSlimeRenderer.contains("entity.field_70812_c")
                        && thaumicSlimeRenderer.contains("entity.field_70811_b")
                        && thaumicSlimeRenderer.contains("class SlimeGelLayer")
                        && thaumicSlimeRenderer.contains("this.addLayer(new SlimeGelLayer())"));
        String taintSwarmRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSwarm.java");
        assertTrue("RenderTaintSwarm must stay as dedicated noop baseline renderer",
                taintSwarmRenderer.contains("extends RenderNoop<EntityTaintSwarm>"));
        String taintacleRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintacle.java");
        String taintacleModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintacle.java");
        String taintacleModelRenderer = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelRendererTaintacle.java");
        assertTrue("RenderTaintacle must provide dedicated tentacle model, length-based routing, and giant-scale baseline",
                taintacleRenderer.contains("extends RenderLiving<T>")
                        && taintacleRenderer.contains("textures/models/taintacle.png")
                        && taintacleRenderer.contains("new ModelTaintacle(length)")
                        && taintacleRenderer.contains("entity instanceof EntityTaintacleGiant")
                        && taintacleRenderer.contains("GlStateManager.scale(1.33F, 1.33F, 1.33F)")
                        && taintacleModel.contains("class ModelTaintacle")
                        && taintacleModel.contains("tentacle.render(scale, 0.88F)")
                        && taintacleModel.contains("getAgitationState()")
                        && taintacleModel.contains("flailIntensity")
                        && taintacleModelRenderer.contains("class ModelRendererTaintacle extends ModelRenderer")
                        && taintacleModelRenderer.contains("OpenGlHelper.setLightmapTextureCoords")
                        && taintacleModelRenderer.contains("childModels"));
        String brainyZombieRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderBrainyZombie.java");
        assertTrue("RenderBrainyZombie must provide dedicated brainy texture plus giant-anger scale baseline",
                brainyZombieRenderer.contains("extends RenderZombie")
                        && brainyZombieRenderer.contains("textures/models/bzombie.png")
                        && brainyZombieRenderer.contains("entity instanceof EntityGiantBrainyZombie")
                        && brainyZombieRenderer.contains("((EntityGiantBrainyZombie) entity).getAnger()")
                        && brainyZombieRenderer.contains("GlStateManager.scale(scale, scale, scale)"));
        String inhabitedZombieRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderInhabitedZombie.java");
        assertTrue("RenderInhabitedZombie must provide dedicated czombie texture baseline",
                inhabitedZombieRenderer.contains("extends RenderZombie")
                        && inhabitedZombieRenderer.contains("textures/models/czombie.png"));
        String mindSpiderRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderMindSpider.java");
        assertTrue("RenderMindSpider must provide taint spider texture and eyes layer baseline",
                mindSpiderRenderer.contains("extends RenderLiving<EntityMindSpider>")
                        && mindSpiderRenderer.contains("textures/models/taint_spider.png")
                        && mindSpiderRenderer.contains("textures/models/taint_spider_eyes.png")
                        && mindSpiderRenderer.contains("this.addLayer(new SpiderEyesLayer())")
                        && mindSpiderRenderer.contains("entity.spiderScaleAmount()")
                        && mindSpiderRenderer.contains("getDeathMaxRotation(EntityMindSpider entity)")
                        && mindSpiderRenderer.contains("return 180.0F;")
                        && mindSpiderRenderer.contains("Math.min(0.1F, entity.ticksExisted / 100.0F)")
                        && mindSpiderRenderer.contains("GlStateManager.alphaFunc(516, 0.003921569F)")
                        && mindSpiderRenderer.contains("GlStateManager.depthMask(false)")
                        && mindSpiderRenderer.contains("int i = 61680;")
                        && mindSpiderRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && mindSpiderRenderer.contains("entity.getViewer()")
                        && mindSpiderRenderer.contains("Minecraft.getMinecraft().player.getName()"));
        String taintSpiderRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSpider.java");
        assertTrue("RenderTaintSpider must provide taint spider texture, eyes, and y-scale baseline",
                taintSpiderRenderer.contains("extends RenderLiving<EntityTaintSpider>")
                        && taintSpiderRenderer.contains("textures/models/taint_spider.png")
                        && taintSpiderRenderer.contains("textures/models/taint_spider_eyes.png")
                        && taintSpiderRenderer.contains("this.addLayer(new SpiderEyesLayer())")
                        && taintSpiderRenderer.contains("entity.spiderScaleAmount()")
                        && taintSpiderRenderer.contains("getDeathMaxRotation(EntityTaintSpider entity)")
                        && taintSpiderRenderer.contains("return 180.0F;")
                        && taintSpiderRenderer.contains("int i = 61680;")
                        && taintSpiderRenderer.contains("OpenGlHelper.setLightmapTextureCoords(")
                        && taintSpiderRenderer.contains("scale * 1.25F"));
        String taintCreeperRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintCreeper.java");
        assertTrue("RenderTaintCreeper must provide creeper texture plus flash scale/color multiplier baseline",
                taintCreeperRenderer.contains("extends RenderLiving<EntityTaintCreeper>")
                        && taintCreeperRenderer.contains("textures/models/creeper.png")
                        && taintCreeperRenderer.contains("new ResourceLocation(\"thaumcraft\", \"textures/entity/creeper/creeper_armor.png\")")
                        && taintCreeperRenderer.contains("getCreeperFlashIntensity")
                        && taintCreeperRenderer.contains("preRenderCallback")
                        && taintCreeperRenderer.contains("getColorMultiplier")
                        && taintCreeperRenderer.contains("class CreeperArmorLayer")
                        && taintCreeperRenderer.contains("this.addLayer(new CreeperArmorLayer(this))")
                        && taintCreeperRenderer.contains("entity.getPowered()"));
        String taintVillagerRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintVillager.java");
        assertTrue("RenderTaintVillager must provide villager texture and pre-render scale baseline",
                taintVillagerRenderer.contains("extends RenderLiving<EntityTaintVillager>")
                        && taintVillagerRenderer.contains("textures/models/villager.png")
                        && taintVillagerRenderer.contains("GlStateManager.scale(scale, scale, scale)")
                        && taintVillagerRenderer.contains("0.9375F"));
        String taintChickenRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintChicken.java");
        assertTrue("RenderTaintChicken must provide chicken texture and wing rotation baseline",
                taintChickenRenderer.contains("extends RenderLiving<EntityTaintChicken>")
                        && taintChickenRenderer.contains("textures/models/chicken.png")
                        && taintChickenRenderer.contains("handleRotationFloat")
                        && taintChickenRenderer.contains("field_756_e")
                        && taintChickenRenderer.contains("field_752_b")
                        && taintChickenRenderer.contains("destPos")
                        && taintChickenRenderer.contains("MathHelper.sin"));
        String taintCowRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintCow.java");
        assertTrue("RenderTaintCow must provide cow texture baseline",
                taintCowRenderer.contains("extends RenderLiving<EntityTaintCow>")
                        && taintCowRenderer.contains("textures/models/cow.png"));
        String taintPigRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintPig.java");
        assertTrue("RenderTaintPig must provide pig texture baseline",
                taintPigRenderer.contains("extends RenderLiving<EntityTaintPig>")
                        && taintPigRenderer.contains("textures/models/pig.png"));
        String taintSheepRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTaintSheep.java");
        assertTrue("RenderTaintSheep must provide sheep texture and fur-layer baseline",
                taintSheepRenderer.contains("extends RenderLiving<EntityTaintSheep>")
                        && taintSheepRenderer.contains("textures/models/sheep.png")
                        && taintSheepRenderer.contains("textures/models/sheep_fur.png")
                        && taintSheepRenderer.contains("class SheepFurLayer")
                        && taintSheepRenderer.contains("this.addLayer(new SheepFurLayer(this))")
                        && taintSheepRenderer.contains("entity.getSheared()")
                        && taintSheepRenderer.contains("ModelTaintSheep1")
                        && taintSheepRenderer.contains("ModelTaintSheep2"));
        String taintSheepModel1 = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSheep1.java");
        assertTrue("ModelTaintSheep1 must avoid vanilla EntitySheep cast and use taint sheep head animation hooks",
                taintSheepModel1.contains("extends ModelQuadruped")
                        && taintSheepModel1.contains("instanceof EntityTaintSheep")
                        && taintSheepModel1.contains("getHeadRotationPointY")
                        && taintSheepModel1.contains("getHeadRotationAngleX"));
        String taintSheepModel2 = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelTaintSheep2.java");
        assertTrue("ModelTaintSheep2 must avoid vanilla EntitySheep cast and use taint sheep head animation hooks",
                taintSheepModel2.contains("extends ModelQuadruped")
                        && taintSheepModel2.contains("instanceof EntityTaintSheep")
                        && taintSheepModel2.contains("getHeadRotationPointY")
                        && taintSheepModel2.contains("getHeadRotationAngleX"));
        String cultistRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderCultist.java");
        assertTrue("RenderCultist must provide shared cultist texture plus ritualist beam and leader-scale baselines",
                cultistRenderer.contains("extends RenderBiped<T>")
                        && cultistRenderer.contains("new LayerBipedArmor(this)")
                        && cultistRenderer.contains("textures/models/cultist.png")
                        && cultistRenderer.contains("textures/misc/wispy.png")
                        && cultistRenderer.contains("entity instanceof EntityCultistCleric")
                        && cultistRenderer.contains("getIsRitualist()")
                        && cultistRenderer.contains("drawFloatyLine(")
                        && cultistRenderer.contains("entity instanceof EntityCultistLeader")
                        && cultistRenderer.contains("GlStateManager.scale(1.25F, 1.25F, 1.25F)")
                        && cultistRenderer.contains("Config.golemLinkQuality"));
        String trunkRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderTravelingTrunk.java");
        assertTrue("RenderTravelingTrunk must provide anger-based texture routing baseline",
                trunkRenderer.contains("extends RenderLiving<EntityTravelingTrunk>")
                        && trunkRenderer.contains("textures/models/trunk.png")
                        && trunkRenderer.contains("textures/models/trunkangry.png")
                        && trunkRenderer.contains("entity.getAnger() > 0"));
        String golemRenderer = readFile("src/main/java/thaumcraft/client/renderers/entity/RenderGolemBase.java");
        assertTrue("RenderGolemBase must provide golem-type texture routing baseline",
                golemRenderer.contains("extends RenderLiving<EntityGolemBase>")
                        && golemRenderer.contains("new ModelGolem(false)")
                        && golemRenderer.contains("new GolemAccessoriesLayer(this)")
                        && golemRenderer.contains("new GolemDamageLayer(this)")
                        && golemRenderer.contains("new GolemHeldItemLayer(this)")
                        && golemRenderer.contains("ModelGolemAccessories")
                        && golemRenderer.contains("golem_damage.png")
                        && golemRenderer.contains("golem_decoration.png")
                        && golemRenderer.contains("entity.getGolemType()")
                        && golemRenderer.contains("golem_straw.png")
                        && golemRenderer.contains("golem_wood.png")
                        && golemRenderer.contains("golem_tallow.png")
                        && golemRenderer.contains("golem_clay.png")
                        && golemRenderer.contains("golem_flesh.png")
                        && golemRenderer.contains("golem_stone.png")
                        && golemRenderer.contains("golem_iron.png")
                        && golemRenderer.contains("golem_thaumium.png"));
        String golemModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelGolem.java");
        assertTrue("ModelGolem must provide dedicated golem body, animation, and damage-pass baseline",
                golemModel.contains("class ModelGolem")
                        && golemModel.contains("public final ModelRenderer golemHead;")
                        && golemModel.contains("public final ModelRenderer golemBody;")
                        && golemModel.contains("public final ModelRenderer golemRightArm;")
                        && golemModel.contains("public int pass = 0;")
                        && golemModel.contains("entity instanceof EntityGolemBase")
                        && golemModel.contains("golem.healing > 0")
                        && golemModel.contains("golem.getActionTimer()")
                        && golemModel.contains("golem.getCarryLimit()"));
        String golemAccessoriesModel = readFile("src/main/java/thaumcraft/client/renderers/models/entities/ModelGolemAccessories.java");
        assertTrue("ModelGolemAccessories must provide dedicated decoration and advanced-head baseline",
                golemAccessoriesModel.contains("class ModelGolemAccessories")
                        && golemAccessoriesModel.contains("golemHeadFez")
                        && golemAccessoriesModel.contains("golemHeadGlasses")
                        && golemAccessoriesModel.contains("golemHeadJar")
                        && golemAccessoriesModel.contains("golemEvilHead")
                        && golemAccessoriesModel.contains("golem.getGolemDecoration()")
                        && golemAccessoriesModel.contains("golem.advanced"));
        String golemEntity = readFile("src/main/java/thaumcraft/common/entities/golems/EntityGolemBase.java");
        assertTrue("EntityGolemBase must expose renderer-facing action and health-percentage accessors",
                golemEntity.contains("public float getHealthPercentage()")
                        && golemEntity.contains("public int getActionTimer()"));
        String travelingTrunkEntity = readFile("src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java");
        assertTrue("EntityTravelingTrunk must expose anger accessor for renderer texture routing",
                travelingTrunkEntity.contains("public int getAnger()"));
        String taintCreeperEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintCreeper.java");
        assertTrue("EntityTaintCreeper must expose flash and powered accessors for renderer timing/layer paths",
                taintCreeperEntity.contains("public float getCreeperFlashIntensity(float partialTicks)"));
        assertTrue("EntityTaintCreeper must expose powered accessor for armor-layer rendering",
                taintCreeperEntity.contains("public boolean getPowered()"));
        assertTrue("EntityTaintCreeper must keep underwater/no-despawn baseline contracts",
                taintCreeperEntity.contains("public boolean canBreatheUnderwater()")
                        && taintCreeperEntity.contains("protected boolean canDespawn()")
                        && taintCreeperEntity.contains("public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn)")
                        && taintCreeperEntity.contains("public int getMaxFallHeight()")
                        && taintCreeperEntity.contains("return 3 + (int)(this.getHealth() - 1.0F);")
                        && taintCreeperEntity.contains("return true;")
                        && taintCreeperEntity.contains("return false;"));
        assertTrue("EntityTaintCreeper must keep reference-shaped NBT persistence contracts",
                taintCreeperEntity.contains("nbt.setBoolean(\"powered\"")
                        && taintCreeperEntity.contains("nbt.setShort(\"Fuse\"")
                        && taintCreeperEntity.contains("nbt.setByte(\"ExplosionRadius\"")
                        && taintCreeperEntity.contains("nbt.hasKey(\"Fuse\", 99)")
                        && taintCreeperEntity.contains("nbt.hasKey(\"ExplosionRadius\", 99)")
                        && taintCreeperEntity.contains("nbt.hasKey(\"powered\", 1)")
                        && taintCreeperEntity.contains("nbt.getBoolean(\"powered\")"));
        assertTrue("EntityTaintCreeper must keep reference-shaped fuse sound and state-driven ignite progression",
                taintCreeperEntity.contains("SoundEvents.ENTITY_CREEPER_PRIMED")
                        && taintCreeperEntity.contains("this.timeSinceIgnited += state;")
                        && taintCreeperEntity.contains("if (this.timeSinceIgnited < 0)")
                        && taintCreeperEntity.contains("createExplosion(this, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 1.5F, false)")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.particleCount(100)")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.taintsplosionFX(this)"));
        assertTrue("EntityTaintCreeper must keep early client sploosh FX baseline",
                taintCreeperEntity.contains("public void onLivingUpdate()")
                        && taintCreeperEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.particleCount(10)")
                        && taintCreeperEntity.contains("Thaumcraft.proxy.splooshFX(this)"));
        assertTrue("EntityTaintCreeper must keep fall-accelerated fuse baseline",
                taintCreeperEntity.contains("public void fall(float distance, float damageMultiplier)")
                        && taintCreeperEntity.contains("distance * 1.5F")
                        && taintCreeperEntity.contains("this.timeSinceIgnited > this.fuseTime - 5"));
        assertTrue("EntityTaintCreeper must keep post-explosion taint-poison splash baseline for nearby non-tainted living entities",
                taintCreeperEntity.contains("getEntitiesWithinAABB(EntityLivingBase.class")
                        && taintCreeperEntity.contains("instanceof thaumcraft.api.entities.ITaintedMob")
                        && taintCreeperEntity.contains("Config.potionFluxTaint")
                        && taintCreeperEntity.contains("entity.addPotionEffect(new PotionEffect"));
        assertTrue("EntityTaintCreeper must keep post-explosion taint biome/fibre spread baseline",
                taintCreeperEntity.contains("for (int i = 0; i < 10; i++)")
                        && taintCreeperEntity.contains("Utils.setBiomeAt(this.world, x, z, thaumcraft.common.lib.world.ThaumcraftWorldGenerator.biomeTaint)")
                        && taintCreeperEntity.contains("ConfigBlocks.blockTaintFibres.getDefaultState()")
                        && taintCreeperEntity.contains("this.world.isSideSolid(below, EnumFacing.UP, false)"));
        String taintSheepEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSheep.java");
        assertTrue("EntityTaintSheep must keep sheared-state data/NBT/shearing contracts for fur-layer renderer parity",
                taintSheepEntity.contains("DataParameter<Byte>")
                        && taintSheepEntity.contains("SHEEP_FLAGS")
                        && taintSheepEntity.contains("AIConvertGrass")
                        && taintSheepEntity.contains("convertGrassAI")
                        && taintSheepEntity.contains("tasks.addTask(2, this.convertGrassAI)")
                        && taintSheepEntity.contains("targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true))")
                        && taintSheepEntity.contains("targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityVillager.class, false))")
                        && taintSheepEntity.contains("protected void updateAITasks()")
                        && taintSheepEntity.contains("this.sheepTimer = this.convertGrassAI.getConvertTimer();")
                        && taintSheepEntity.contains("sheepTimer")
                        && taintSheepEntity.contains("entityInit()")
                        && taintSheepEntity.contains("handleStatusUpdate(byte id)")
                        && taintSheepEntity.contains("getHeadRotationPointY(float partialTicks)")
                        && taintSheepEntity.contains("getHeadRotationAngleX(float partialTicks)")
                        && taintSheepEntity.contains("public boolean getSheared()")
                        && taintSheepEntity.contains("public void setSheared(boolean sheared)")
                        && taintSheepEntity.contains("setBaseValue(20.0D)")
                        && taintSheepEntity.contains("setBaseValue(3.0)")
                        && taintSheepEntity.contains("setBaseValue(0.25D)")
                        && taintSheepEntity.contains("public boolean canBreatheUnderwater()")
                        && taintSheepEntity.contains("protected boolean canDespawn()")
                        && taintSheepEntity.contains("public int getTotalArmorValue()")
                        && taintSheepEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintSheepEntity.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintSheepEntity.contains("SoundEvents.ENTITY_SHEEP_AMBIENT")
                        && taintSheepEntity.contains("this.world.rand.nextInt(3) == 0")
                        && taintSheepEntity.contains("return !this.getSheared();")
                        && taintSheepEntity.contains("this.setSheared(true);")
                        && taintSheepEntity.contains("Blocks.WOOL")
                        && taintSheepEntity.contains("\"Sheared\""));
        String taintVillagerEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintVillager.java");
        assertTrue("EntityTaintVillager must keep reference-shaped AI/village/attribute/fx/drop baseline contracts",
                taintVillagerEntity.contains("this(world, 0);")
                        && taintVillagerEntity.contains("private int randomTickDivider;")
                        && taintVillagerEntity.contains("PathNavigateGround")
                        && taintVillagerEntity.contains("setBreakDoors(true)")
                        && taintVillagerEntity.contains("setCanSwim(true)")
                        && taintVillagerEntity.contains("new EntityAIMoveIndoors(this)")
                        && taintVillagerEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintVillagerEntity.contains("new EntityAIMoveThroughVillage(this, 1.0D, false)")
                        && taintVillagerEntity.contains("new EntityAIWatchClosest2(this, EntityVillager.class, 5.0F, 0.02F)")
                        && taintVillagerEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintVillagerEntity.contains("setBaseValue(30.0D)")
                        && taintVillagerEntity.contains("setBaseValue(4.0D)")
                        && taintVillagerEntity.contains("setBaseValue(0.3D)")
                        && taintVillagerEntity.contains("public boolean canBreatheUnderwater()")
                        && taintVillagerEntity.contains("protected boolean canDespawn()")
                        && taintVillagerEntity.contains("this.world.getVillageCollection().addToVillagerPositionList(pos)")
                        && taintVillagerEntity.contains("this.villageObj = this.world.getVillageCollection().getNearestVillage(pos, 32)")
                        && taintVillagerEntity.contains("this.villageObj.addOrRenewAgressor(livingBase)")
                        && taintVillagerEntity.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_AMBIENT")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_HURT")
                        && taintVillagerEntity.contains("SoundEvents.ENTITY_VILLAGER_DEATH")
                        && taintVillagerEntity.contains("this.world.rand.nextInt(2) == 0")
                        && taintVillagerEntity.contains("this.world.rand.nextInt(13) < 1 + looting")
                        && taintVillagerEntity.contains("itemResource, 1, 18"));
        String taintSwarmEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSwarm.java");
        assertTrue("EntityTaintSwarm must keep reference-shaped summoned/flight/attack/NBT/drop baseline contracts",
                taintSwarmEntity.contains("private static final byte FLAG_SUMMONED = 2")
                        && taintSwarmEntity.contains("DataParameter<Byte> FLAGS")
                        && taintSwarmEntity.contains("private BlockPos currentFlightTarget;")
                        && taintSwarmEntity.contains("public int damBonus = 0;")
                        && taintSwarmEntity.contains("this.setSize(2.0F, 2.0F);")
                        && taintSwarmEntity.contains("setBaseValue(30.0D)")
                        && taintSwarmEntity.contains("setBaseValue(2.0D + this.damBonus)")
                        && taintSwarmEntity.contains("public boolean getIsSummoned()")
                        && taintSwarmEntity.contains("public void setIsSummoned(boolean summoned)")
                        && taintSwarmEntity.contains("return 15728880;")
                        && taintSwarmEntity.contains("return 1.0F;")
                        && taintSwarmEntity.contains("this.motionY *= 0.6000000238418579D;")
                        && taintSwarmEntity.contains("public final ArrayList<Object> swarm = new ArrayList<>();")
                        && taintSwarmEntity.contains("Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))")
                        && taintSwarmEntity.contains("this.swarm.add(Thaumcraft.proxy.swarmParticleFX")
                        && taintSwarmEntity.contains("Thaumcraft.proxy.particleCount(25)")
                        && taintSwarmEntity.contains("this.attackEntityFrom(DamageSource.STARVE, 5.0F);")
                        && taintSwarmEntity.contains("this.world.getClosestPlayerToEntity(this, 12.0D)")
                        && taintSwarmEntity.contains("this.world.getBiome(pos) == ThaumcraftWorldGenerator.biomeTaint")
                        && taintSwarmEntity.contains("target.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 100, 0));")
                        && taintSwarmEntity.contains("EntityUtils.setRecentlyHit(target, 100);")
                        && taintSwarmEntity.contains("nbt.setByte(\"Flags\"")
                        && taintSwarmEntity.contains("nbt.setByte(\"damBonus\"")
                        && taintSwarmEntity.contains("nbt.getByte(\"Flags\")")
                        && taintSwarmEntity.contains("nbt.getByte(\"damBonus\")")
                        && taintSwarmEntity.contains("this.world.rand.nextBoolean()")
                        && taintSwarmEntity.contains("itemResource, 1, 11"));
        String taintSporeEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSpore.java");
        assertTrue("EntityTaintSpore must keep reference-shaped size/fullbright/swarm-particle/burst baseline contracts",
                taintSporeEntity.contains("public final ArrayList<Object> swarm = new ArrayList<>();")
                        && taintSporeEntity.contains("this.setSporeSize(2);")
                        && taintSporeEntity.contains("this.dataManager.register(SPORE_SIZE, 1);")
                        && taintSporeEntity.contains("return 0.0D;")
                        && taintSporeEntity.contains("return distance < 4096.0D;")
                        && taintSporeEntity.contains("return 15728880;")
                        && taintSporeEntity.contains("return 1.0F;")
                        && taintSporeEntity.contains("this.world.getBiome(this.getPosition()) != ThaumcraftWorldGenerator.biomeTaint")
                        && taintSporeEntity.contains("this.attackEntityFrom(DamageSource.DROWN, 1.0f);")
                        && taintSporeEntity.contains("Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))")
                        && taintSporeEntity.contains("this.swarm.size() < this.getSporeSize() / 3")
                        && taintSporeEntity.contains("this.swarm.add(thaumcraft.common.Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F));")
                        && taintSporeEntity.contains("this.sploosh(50);")
                        && taintSporeEntity.contains("protected void sploosh(int amount)")
                        && taintSporeEntity.contains("Thaumcraft.proxy.splooshFX(this);")
                        && taintSporeEntity.contains("nbt.setInteger(\"Size\", this.getSporeSize() - 1)")
                        && taintSporeEntity.contains("this.setSporeSize(nbt.getInteger(\"Size\") + 1)")
                        && taintSporeEntity.contains("this.world.rand.nextBoolean()")
                        && taintSporeEntity.contains("itemResource, 1, 11")
                        && taintSporeEntity.contains("itemResource, 1, 12"));
        String taintSporeSwarmerEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSporeSwarmer.java");
        assertTrue("EntityTaintSporeSwarmer must keep reference-shaped spawn-counter/swarm-burst behavior contracts",
                taintSporeSwarmerEntity.contains("private int spawnCounter;")
                        && taintSporeSwarmerEntity.contains("this.spawnCounter = 500;")
                        && taintSporeSwarmerEntity.contains("this.setSporeSize(10);")
                        && taintSporeSwarmerEntity.contains("this.setSize(1.0F, 1.0F);")
                        && taintSporeSwarmerEntity.contains("setBaseValue(75.0D)")
                        && taintSporeSwarmerEntity.contains("setBaseValue(1.0D)")
                        && taintSporeSwarmerEntity.contains("this.sploosh(10);")
                        && taintSporeSwarmerEntity.contains("this.pushOutOfBlocks(this.posX, this.posY, this.posZ);")
                        && taintSporeSwarmerEntity.contains("this.world.getClosestPlayerToEntity(this, 16.0D)")
                        && taintSporeSwarmerEntity.contains("this.swarmBurst(1);")
                        && taintSporeSwarmerEntity.contains("private final ArrayList<Object> swarm = new ArrayList<>();")
                        && taintSporeSwarmerEntity.contains("Thaumcraft.proxy.isParticleAlive(this.swarm.get(i))")
                        && taintSporeSwarmerEntity.contains("this.swarm.add(thaumcraft.common.Thaumcraft.proxy.swarmParticleFX(this.world, this, 0.1F, 10.0F, 0.0F));")
                        && taintSporeSwarmerEntity.contains("this.world.setEntityState(this, (byte) 6);")
                        && taintSporeSwarmerEntity.contains("if (id == 6)")
                        && taintSporeSwarmerEntity.contains("this.spawnCounter = 500;")
                        && taintSporeSwarmerEntity.contains("this.sploosh(25);")
                        && taintSporeSwarmerEntity.contains("Thaumcraft.proxy.splooshFX(this);")
                        && taintSporeSwarmerEntity.contains("return thaumcraft.common.lib.TCSounds.ROOTS;")
                        && taintSporeSwarmerEntity.contains("for (int i = 0; i <= 1; i++)")
                        && taintSporeSwarmerEntity.contains("itemResource, 1, 11")
                        && taintSporeSwarmerEntity.contains("itemResource, 1, 12"));
        String taintacleSmallEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintacleSmall.java");
        assertTrue("EntityTaintacleSmall must keep reference-shaped lifetime/attribute/no-drop contracts",
                taintacleSmallEntity.contains("private int lifetime = 200;")
                        && taintacleSmallEntity.contains("this.setSize(0.22F, 1.0F);")
                        && taintacleSmallEntity.contains("this.experienceValue = 0;")
                        && taintacleSmallEntity.contains("setBaseValue(8.0D)")
                        && taintacleSmallEntity.contains("setBaseValue(2.0D)")
                        && taintacleSmallEntity.contains("if (--this.lifetime <= 0)")
                        && taintacleSmallEntity.contains("this.attackEntityFrom(DamageSource.STARVE, 10.0F);")
                        && taintacleSmallEntity.contains("return false;")
                        && taintacleSmallEntity.contains("return Item.getItemById(0);")
                        && taintacleSmallEntity.contains("protected void dropFewItems(boolean wasRecentlyHit, int looting) {"));
        String taintacleEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintacle.java");
        assertTrue("EntityTaintacle must keep reference-shaped spawn/target/combat/tentacle-spawn baseline contracts",
                taintacleEntity.contains("this.setSize(0.66f, 3.0f);")
                        && taintacleEntity.contains("this.experienceValue = 10;")
                        && taintacleEntity.contains("setBaseValue(50.0)")
                        && taintacleEntity.contains("setBaseValue(7.0)")
                        && taintacleEntity.contains("new AxisAlignedBB(this.posX, this.posY, this.posZ, this.posX, this.posY, this.posZ).grow(24.0D, 8.0D, 24.0D)")
                        && taintacleEntity.contains("ConfigBlocks.blockTaintFibres")
                        && taintacleEntity.contains("ConfigBlocks.blockTaint")
                        && taintacleEntity.contains("this.isValidTaintacleSpawnGround(pos) || this.isValidTaintacleSpawnGround(pos.down())")
                        && taintacleEntity.contains("private boolean isTaintBiome(BlockPos pos)")
                        && taintacleEntity.contains("return 0.25D;")
                        && taintacleEntity.contains("return false;")
                        && taintacleEntity.contains("this.attackTentacle(this.getAttackTarget(), dist);")
                        && taintacleEntity.contains("this.setAttackTarget(this.findNearestTarget());")
                        && taintacleEntity.contains("Thaumcraft.proxy.tentacleAriseFX(this);")
                        && taintacleEntity.contains("protected void attackTentacle(Entity entity, float distance)")
                        && taintacleEntity.contains("DamageSourceThaumcraft.causeTentacleDamage(this)")
                        && taintacleEntity.contains("EnchantmentHelper.getModifierForCreature")
                        && taintacleEntity.contains("EnchantmentHelper.getKnockbackModifier(this)")
                        && taintacleEntity.contains("EnchantmentHelper.getFireAspectModifier(this)")
                        && taintacleEntity.contains("EnchantmentHelper.applyThornEnchantments")
                        && taintacleEntity.contains("EnchantmentHelper.applyArthropodEnchantments")
                        && taintacleEntity.contains("protected void spawnTentacles(Entity entity)")
                        && taintacleEntity.contains("new EntityTaintacleSmall(this.world)")
                        && taintacleEntity.contains("Utils.setBiomeAt(this.world, x, z, ThaumcraftWorldGenerator.biomeTaint)")
                        && taintacleEntity.contains("this.spawnTentacles(source.getTrueSource());")
                        && taintacleEntity.contains("public boolean getAgitationState()")
                        && taintacleEntity.contains("protected float updateRotation(float current, float intended, float maxChange)")
                        && taintacleEntity.contains("return 1.3F - this.height / 10.0F;")
                        && taintacleEntity.contains("return this.height / 8.0f;"));
        String taintacleGiantEntity = readFile("src/main/java/thaumcraft/common/entities/monster/boss/EntityTaintacleGiant.java");
        assertTrue("EntityTaintacleGiant must keep damageable underwater/enrage survivability contracts",
                taintacleGiantEntity.contains("setBaseValue(125.0)")
                        && taintacleGiantEntity.contains("setBaseValue(9.0)")
                        && taintacleGiantEntity.contains("public boolean getCanSpawnHere() { return false; }")
                        && taintacleGiantEntity.contains("public boolean canDespawn() { return false; }")
                        && taintacleGiantEntity.contains("public boolean canBreatheUnderwater() { return true; }")
                        && taintacleGiantEntity.contains("protected int decreaseAirSupply(int air) { return air; }")
                        && taintacleGiantEntity.contains("if (!this.world.isRemote && amount > 35.0f)")
                        && !taintacleGiantEntity.contains("isEntityInvulnerable(DamageSource source) { return true; }"));
        String giantBrainyZombieEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityGiantBrainyZombie.java");
        assertTrue("EntityGiantBrainyZombie must keep reference-shaped anger/scale/attribute/drop contracts",
                giantBrainyZombieEntity.contains("DataParameter<Float> ANGER")
                        && giantBrainyZombieEntity.contains("this.experienceValue = 15;")
                        && giantBrainyZombieEntity.contains("1.2F + this.getAnger()")
                        && giantBrainyZombieEntity.contains("this.tasks.addTask(2, new EntityAILeapAtTarget(this, 0.4F));")
                        && giantBrainyZombieEntity.contains("this.dataManager.register(ANGER, 1.0F);")
                        && giantBrainyZombieEntity.contains("public float getAnger()")
                        && giantBrainyZombieEntity.contains("this.setAnger(this.getAnger() - 0.002F);")
                        && giantBrainyZombieEntity.contains("this.setSize(0.6F * scale, 1.8F * scale);")
                        && giantBrainyZombieEntity.contains("setBaseValue(7.0D + (double) ((this.getAnger() - 1.0F) * 5.0F))")
                        && giantBrainyZombieEntity.contains("Math.min(2.0F, this.getAnger() + 0.1F)")
                        && giantBrainyZombieEntity.contains("setBaseValue(60.0D)")
                        && giantBrainyZombieEntity.contains("setBaseValue(7.0D)")
                        && giantBrainyZombieEntity.contains("for (int i = 0; i < 6; i++)")
                        && giantBrainyZombieEntity.contains("this.dropItem(net.minecraft.init.Items.ROTTEN_FLESH, 2);")
                        && giantBrainyZombieEntity.contains("ConfigItems.itemZombieBrain")
                        && giantBrainyZombieEntity.contains("nbt.setFloat(\"Anger\", this.getAnger());"));
        String brainyZombieEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityBrainyZombie.java");
        assertTrue("EntityBrainyZombie must keep reference-shaped target/reinforcement/drop contracts",
                brainyZombieEntity.contains("this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));")
                        && brainyZombieEntity.contains("setBaseValue(25.0D)")
                        && brainyZombieEntity.contains("setBaseValue(5.0D)")
                        && brainyZombieEntity.contains("EntityZombie.SPAWN_REINFORCEMENTS_CHANCE")
                        && brainyZombieEntity.contains("setBaseValue(0.0D)")
                        && brainyZombieEntity.contains("for (int i = 0; i < 3; ++i)")
                        && brainyZombieEntity.contains("Items.ROTTEN_FLESH")
                        && brainyZombieEntity.contains("ConfigItems.itemZombieBrain"));
        String inhabitedZombieEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityInhabitedZombie.java");
        assertTrue("EntityInhabitedZombie must keep reference-shaped armor/crab-spawn/no-drop contracts",
                inhabitedZombieEntity.contains("this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));")
                        && inhabitedZombieEntity.contains("this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityCultist.class, true));")
                        && inhabitedZombieEntity.contains("setBaseValue(30.0)")
                        && inhabitedZombieEntity.contains("setBaseValue(5.0)")
                        && inhabitedZombieEntity.contains("EntityZombie.SPAWN_REINFORCEMENTS_CHANCE")
                        && inhabitedZombieEntity.contains("public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingData)")
                        && inhabitedZombieEntity.contains("this.world.getDifficulty() == EnumDifficulty.HARD ? 0.9F : 0.6F")
                        && inhabitedZombieEntity.contains("EntityEquipmentSlot.HEAD")
                        && inhabitedZombieEntity.contains("EntityEquipmentSlot.CHEST")
                        && inhabitedZombieEntity.contains("EntityEquipmentSlot.LEGS")
                        && inhabitedZombieEntity.contains("ConfigItems.itemHelmetCultistPlate")
                        && inhabitedZombieEntity.contains("ConfigItems.itemChestCultistPlate")
                        && inhabitedZombieEntity.contains("ConfigItems.itemLegsCultistPlate")
                        && inhabitedZombieEntity.contains("protected Item getDropItem()")
                        && inhabitedZombieEntity.contains("return Item.getItemById(0);")
                        && inhabitedZombieEntity.contains("EntityEldritchCrab crab = new EntityEldritchCrab(this.world);")
                        && inhabitedZombieEntity.contains("crab.setHelm(true);")
                        && inhabitedZombieEntity.contains("this.canDropLoot()")
                        && inhabitedZombieEntity.contains("public void onDeath(DamageSource cause)")
                        && inhabitedZombieEntity.contains("protected SoundEvent getAmbientSound()")
                        && inhabitedZombieEntity.contains("TCSounds.CRABTALK")
                        && inhabitedZombieEntity.contains("SoundEvents.ENTITY_HOSTILE_HURT"));
        String mindSpiderEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityMindSpider.java");
        assertTrue("EntityMindSpider must expose viewer accessor and synced harmless/viewer data contracts",
                mindSpiderEntity.contains("public String getViewer()")
                        && mindSpiderEntity.contains("private static final DataParameter<Byte> HARMLESS")
                        && mindSpiderEntity.contains("private static final DataParameter<String> VIEWER")
                        && mindSpiderEntity.contains("this.dataManager.register(HARMLESS, (byte) 0)")
                        && mindSpiderEntity.contains("this.dataManager.register(VIEWER, \"\")")
                        && mindSpiderEntity.contains("SharedMonsterAttributes.ATTACK_DAMAGE")
                        && !mindSpiderEntity.contains("SharedMonsterAttributes.MOVEMENT_SPEED")
                        && mindSpiderEntity.contains("public boolean isHarmless()")
                        && mindSpiderEntity.contains("public float spiderScaleAmount()")
                        && mindSpiderEntity.contains("public double getYOffset()")
                        && mindSpiderEntity.contains("public boolean canBeCollidedWith()")
                        && mindSpiderEntity.contains("protected boolean canTriggerWalking()")
                        && mindSpiderEntity.contains("public boolean attackEntityAsMob(")
                        && mindSpiderEntity.contains("return super.attackEntityAsMob(entityIn);")
                        && mindSpiderEntity.contains("this.dataManager.set(HARMLESS, harmless ? (byte) 1 : (byte) 0)")
                        && mindSpiderEntity.contains("nbt.setByte(\"harmless\"")
                        && mindSpiderEntity.contains("nbt.setString(\"viewer\""));
        String taintSpiderEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintSpider.java");
        assertTrue("EntityTaintSpider must keep reference-shaped scale, size, attributes, and loot-drop contracts",
                taintSpiderEntity.contains("public float spiderScaleAmount()")
                        && taintSpiderEntity.contains("return 0.4F;")
                        && taintSpiderEntity.contains("this.setSize(0.4F, 0.3F);")
                        && taintSpiderEntity.contains("this.experienceValue = 2;")
                        && taintSpiderEntity.contains("setBaseValue(5.0D)")
                        && taintSpiderEntity.contains("setBaseValue(2.0D)")
                        && taintSpiderEntity.contains("public double getYOffset()")
                        && taintSpiderEntity.contains("return 0.1D;")
                        && taintSpiderEntity.contains("this.world.rand.nextInt(6) == 0"));
        String taintChickenEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintChicken.java");
        assertTrue("EntityTaintChicken must keep reference-shaped AI/fall/sound baseline contracts",
                taintChickenEntity.contains("this.tasks.addTask(0, new EntityAISwimming(this));")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintChickenEntity.contains("new EntityAILeapAtTarget(this, 0.3F)")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintChickenEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, true)")
                        && taintChickenEntity.contains("new EntityAIWander(this, 1.0D)")
                        && taintChickenEntity.contains("new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F)")
                        && taintChickenEntity.contains("new EntityAILookIdle(this)")
                        && taintChickenEntity.contains("new EntityAIHurtByTarget(this, false)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintChickenEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintChickenEntity.contains("public boolean canBreatheUnderwater()")
                        && taintChickenEntity.contains("protected boolean canDespawn()")
                        && taintChickenEntity.contains("public void fall(float distance, float damageMultiplier)")
                        && taintChickenEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintChickenEntity.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintChickenEntity.contains("SoundEvents.ENTITY_CHICKEN_HURT"));
        String taintCowEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintCow.java");
        assertTrue("EntityTaintCow must keep reference-shaped AI/attribute/sound baseline contracts",
                taintCowEntity.contains("this.setSize(0.9F, 1.3F);")
                        && taintCowEntity.contains("PathNavigateGround")
                        && taintCowEntity.contains("setCanSwim(true)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintCowEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, false)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintCowEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintCowEntity.contains("setBaseValue(40.0D)")
                        && taintCowEntity.contains("setBaseValue(6.0D)")
                        && taintCowEntity.contains("setBaseValue(0.27D)")
                        && taintCowEntity.contains("public boolean canBreatheUnderwater()")
                        && taintCowEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintCowEntity.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintCowEntity.contains("SoundEvents.ENTITY_COW_HURT"));
        String taintPigEntity = readFile("src/main/java/thaumcraft/common/entities/monster/EntityTaintPig.java");
        assertTrue("EntityTaintPig must keep reference-shaped AI/attribute/sound/drop baseline contracts",
                taintPigEntity.contains("this.setSize(0.9F, 0.9F);")
                        && taintPigEntity.contains("PathNavigateGround")
                        && taintPigEntity.contains("setCanSwim(true)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityPlayer.class, 1.0D, false)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityVillager.class, 1.0D, true)")
                        && taintPigEntity.contains("new AIAttackOnCollide(this, EntityAnimal.class, 1.0D, false)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityPlayer.class, true)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityVillager.class, false)")
                        && taintPigEntity.contains("new EntityAINearestAttackableTarget(this, EntityAnimal.class, false)")
                        && taintPigEntity.contains("setBaseValue(20.0D)")
                        && taintPigEntity.contains("setBaseValue(4.0D)")
                        && taintPigEntity.contains("setBaseValue(0.275D)")
                        && taintPigEntity.contains("public boolean canBreatheUnderwater()")
                        && taintPigEntity.contains("protected boolean canDespawn()")
                        && taintPigEntity.contains("public int getMaxSpawnedInChunk()")
                        && taintPigEntity.contains("this.world.isRemote && this.ticksExisted < 5")
                        && taintPigEntity.contains("Thaumcraft.proxy.splooshFX(this)")
                        && taintPigEntity.contains("SoundEvents.ENTITY_PIG_AMBIENT")
                        && taintPigEntity.contains("this.world.rand.nextInt(3) == 0"));
        assertTrue("ClientProxy must iterate ConfigEntities.ENTITIES for renderer registration coverage",
                source.contains("for (net.minecraftforge.fml.common.registry.EntityEntry entry : ConfigEntities.ENTITIES)"));
        assertTrue("ClientProxy must keep fallback RenderNoop registrations for remaining entities",
                source.contains("if (registered.contains(entityClass))")
                        && source.contains("RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNoop::new);"));
        assertTrue("RenderNoop must stay as a side-safe doRender baseline",
                noopRenderer.contains("extends Render<T>")
                        && noopRenderer.contains("public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)"));
    }

    private static String readFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

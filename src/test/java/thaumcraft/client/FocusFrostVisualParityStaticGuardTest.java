package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FocusFrostVisualParityStaticGuardTest {

    @Test
    public void frostShardShouldKeepReferenceMeshAndSmoothedFlightOrientation() throws IOException {
        String focus = read("src/main/java/thaumcraft/common/items/wands/foci/FocusFrost.java");
        String shard = read("src/main/java/thaumcraft/common/entities/projectile/EntityFrostShard.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/entity/RenderFrostShard.java");
        String modelRegistry = read("src/main/java/thaumcraft/client/ClientModelRegistry.java");

        assertTrue("FocusFrost must keep the TC 4.2.3.5 focus color",
                focus.contains("return 0x4F69CC;"));

        assertTrue("EntityFrostShard must retain EntityThrowable's wrapped and smoothed rotation update",
                shard.contains("super.onUpdate();"));
        assertFalse("EntityFrostShard must not overwrite the smoothed yaw with an instantaneous motion angle",
                shard.contains("this.rotationYaw ="));
        assertFalse("EntityFrostShard must not overwrite the smoothed pitch with an instantaneous motion angle",
                shard.contains("this.rotationPitch ="));

        assertTrue("EntityFrostShard must spawn ahead of the caster instead of inside their head collision box",
                shard.contains("shooter.posX - MathHelper.sin(yaw) * 0.8D")
                        && shard.contains("shooter.posY + shooter.getEyeHeight() - 0.1D")
                        && shard.contains("shooter.posZ + MathHelper.cos(yaw) * 0.8D"));
        assertTrue("EntityFrostShard must sync and briefly ignore its thrower on the client",
                shard.contains("private int throwerId = -1;")
                        && shard.contains("this.ticksExisted <= THROWER_IMPACT_GRACE_TICKS && this.isThrower(result.entityHit)")
                        && shard.contains("buf.writeInt(thrower != null ? thrower.getEntityId() : this.throwerId);")
                        && shard.contains("this.throwerId = buf.readInt();"));

        assertTrue("ClientModelRegistry must stitch and bake the original orb OBJ with the frost shard texture",
                modelRegistry.contains("new ResourceLocation(\"thaumcraft\", \"textures/models/orb.obj\")")
                        && modelRegistry.contains("event.getMap().registerSprite(FROST_SHARD_SPRITE);")
                        && modelRegistry.contains("OBJLoader.INSTANCE.loadModel(FROST_SHARD_OBJ)")
                        && modelRegistry.contains("\"#OBJModel.Default.Texture.Name\", FROST_SHARD_SPRITE.toString()")
                        && modelRegistry.contains("DefaultVertexFormats.ITEM")
                        && modelRegistry.contains("bakeFrostShardModel(event);"));

        assertTrue("RenderFrostShard must render the baked orb while preserving the reference rotation and seeded scale",
                renderer.contains("ClientModelRegistry.getFrostShardModel()")
                        && renderer.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && renderer.contains("renderModelBrightnessColor(model, 1.0F, 1.0F, 1.0F, 1.0F)")
                        && renderer.contains("new Random(entity.getEntityId())")
                        && renderer.contains("entity.getDamage() * 0.1F")
                        && renderer.contains("entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks")
                        && renderer.contains("entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks"));

        assertArrayEquals("Runtime orb.obj must remain identical to the TC4 source asset",
                Files.readAllBytes(Paths.get("thaumcraft_src/assets/thaumcraft/textures/models/orb.obj")),
                Files.readAllBytes(Paths.get("src/main/resources/assets/thaumcraft/textures/models/orb.obj")));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

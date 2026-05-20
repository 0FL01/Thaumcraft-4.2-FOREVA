package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class TravelingTrunkRendererFidelityStaticGuardTest {

    @Test
    public void travelingTrunkBundleStaysReferenceShaped() throws IOException {
        String entity = read("src/main/java/thaumcraft/common/entities/golems/EntityTravelingTrunk.java");
        String renderer = read("src/main/java/thaumcraft/client/renderers/entity/RenderTravelingTrunk.java");
        String model = read("src/main/java/thaumcraft/client/renderers/models/entities/ModelTrunk.java");

        assertTrue("EntityTravelingTrunk must keep synced open/stay/upgrade/rows/anger data surface",
                entity.contains("DataParameter<java.lang.Boolean> OPEN")
                        && entity.contains("DataParameter<java.lang.Boolean> STAY")
                        && entity.contains("DataParameter<java.lang.Integer> UPGRADE")
                        && entity.contains("DataParameter<java.lang.Integer> ROWS")
                        && entity.contains("DataParameter<java.lang.Integer> ANGER")
                        && entity.contains("public float lidrot;")
                        && entity.contains("public float field_768_a;")
                        && entity.contains("public float field_767_b;")
                        && entity.contains("this.dataManager.register(OPEN, false);")
                        && entity.contains("this.dataManager.register(UPGRADE, -1);")
                        && entity.contains("this.dataManager.register(ROWS, 3);")
                        && entity.contains("this.dataManager.register(ANGER, 0);"));
        assertTrue("EntityTravelingTrunk must keep synced accessors and inventory row sizing baseline",
                entity.contains("public boolean isOpen()")
                        && entity.contains("return this.dataManager.get(OPEN);")
                        && entity.contains("return this.dataManager.get(STAY);")
                        && entity.contains("return this.dataManager.get(UPGRADE);")
                        && entity.contains("return this.dataManager.get(ROWS);")
                        && entity.contains("return this.dataManager.get(ANGER);")
                        && entity.contains("this.setRows(this.getUpgrade() == 1 ? 4 : 3);")
                        && entity.contains("this.inventory.setSlotCount(this.getRows() * 9);"));
        assertTrue("EntityTravelingTrunk must keep lid and squish animation state updates",
                entity.contains("this.field_767_b += (this.field_768_a - this.field_767_b) * 0.5F;")
                        && entity.contains("if (this.world.isRemote)")
                        && entity.contains("this.lidrot += 0.035F;")
                        && entity.contains("this.field_768_a = -0.5F;")
                        && entity.contains("this.field_768_a = 1.0F;")
                        && entity.contains("this.field_768_a = 0.35F;")
                        && entity.contains("this.lidrot = 0.15F;"));
        assertTrue("RenderTravelingTrunk must keep dedicated trunk model and squish/lid transform path",
                renderer.contains("new ModelTrunk()")
                        && renderer.contains("private final ModelTrunk trunkModel;")
                        && renderer.contains("this.adjustTrunk(entity, partialTickTime);")
                        && renderer.contains("entity.field_767_b + (entity.field_768_a - entity.field_767_b) * partialTickTime")
                        && renderer.contains("entity.getUpgrade() == 1")
                        && renderer.contains("this.trunkModel.chestLid.rotateAngleX")
                        && renderer.contains("entity.lidrot"));
        assertTrue("ModelTrunk must keep chest shell plus upgrade icon atlas render path",
                model.contains("class ModelTrunk extends ModelBase")
                        && model.contains("public final ModelRenderer chestLid;")
                        && model.contains("public final ModelRenderer chestBelow;")
                        && model.contains("public final ModelRenderer chestKnob;")
                        && model.contains("this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;")
                        && model.contains("ConfigItems.itemGolemUpgrade")
                        && model.contains("getParticleIcon(ConfigItems.itemGolemUpgrade, upgrade)")
                        && model.contains("TextureMap.LOCATION_BLOCKS_TEXTURE")
                        && model.contains("buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);"));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

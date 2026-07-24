package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class CrucibleRendererFidelityStaticGuardTest {

    @Test
    public void tileCrucibleRendererUsesWaterAtlasUvAndCombinedLight() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileCrucibleRenderer.java");
        String tileSource = read("src/main/java/thaumcraft/common/tiles/TileCrucible.java");
        String blockSource = read("src/main/java/thaumcraft/common/blocks/BlockMetalDevice.java");
        String blockModel = read("src/main/resources/assets/thaumcraft/models/block/blockmetaldevice_0.json");
        int fluidFormat = source.indexOf("private static final VertexFormat FLUID_VERTEX_FORMAT");
        int positionElement = source.indexOf(".addElement(DefaultVertexFormats.POSITION_3F)", fluidFormat);
        int textureElement = source.indexOf(".addElement(DefaultVertexFormats.TEX_2F)", positionElement);
        int colorElement = source.indexOf(".addElement(DefaultVertexFormats.COLOR_4UB)", textureElement);
        int lightmapElement = source.indexOf(".addElement(DefaultVertexFormats.TEX_2S)", colorElement);
        int normalElement = source.indexOf(".addElement(DefaultVertexFormats.NORMAL_3B)", lightmapElement);
        int paddingElement = source.indexOf(".addElement(DefaultVertexFormats.PADDING_1B)", normalElement);
        int vertex = source.indexOf("buffer.pos(x, y, 0.0D)");
        int vertexTexture = source.indexOf(".tex(u, v)", vertex);
        int vertexColor = source.indexOf(".color(r, g, b, a)", vertexTexture);
        int vertexLightmap = source.indexOf(".lightmap(lightU, lightV)", vertexColor);
        int vertexNormal = source.indexOf(".normal(0.0F, 0.0F, 1.0F)", vertexLightmap);

        assertTrue("Crucible renderer should resolve water atlas sprite for the surface quad",
                source.contains("Blocks.WATER.getDefaultState()"));
        assertTrue("Crucible renderer should use world combined light at tile position",
                source.contains("getCombinedLight(tile.getPos(), 0)"));
        assertTrue("Crucible renderer should use the donor vertex layout instead of writing normals into BLOCK positions",
                fluidFormat >= 0
                        && positionElement > fluidFormat
                        && textureElement > positionElement
                        && colorElement > textureElement
                        && lightmapElement > colorElement
                        && normalElement > lightmapElement
                        && paddingElement > normalElement
                        && source.contains("buffer.begin(GL11.GL_QUADS, FLUID_VERTEX_FORMAT);"));
        assertTrue("Crucible renderer should submit data in the same order as its vertex format",
                vertex >= 0
                        && vertexTexture > vertex
                        && vertexColor > vertexTexture
                        && vertexLightmap > vertexColor
                        && vertexNormal > vertexLightmap
                        && source.contains("int lightU = (packedLight >> 16) & 0xFFFF;")
                        && source.contains("int lightV = packedLight & 0xFFFF;")
                        && !source.contains("buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);"));
        assertTrue("Crucible renderer should map quad UVs from water sprite bounds",
                source.contains("water.getMinU()")
                        && source.contains("water.getMaxU()")
                        && source.contains("water.getMinV()")
                        && source.contains("water.getMaxV()"));
        assertTrue("Crucible fluid quad should cover a full block like the original UtilsFX.renderQuadFromIcon scale 1.0 path",
                source.contains("drawFluidSurface(0.5F"));
        assertTrue("Low aspect counts should tint translucent 1.12 water sprites without immediate RGB or alpha drops",
                source.contains("float recolor = raw;")
                        && source.contains("float a = 1.0F - raw / 2.0F;")
                        && !source.contains("0.5F + raw / 2.0F")
                        && !source.contains("float a = 1.0F - recolor / 2.0F;"));
        assertTrue("Crucible surface should require actual water like TC4",
                source.contains("if (!tile.hasWater())")
                        && tileSource.contains("public boolean hasWater()"));
        assertTrue("Crucible renderer should preserve an incoming enabled blend state",
                source.contains("boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);")
                        && source.contains("if (!blendEnabled)"));
        assertTrue("Crucible block model should use original full-block alpha shell scale, not a shrunken basin mesh",
                blockModel.contains("\"ambientocclusion\": false")
                        && blockModel.contains("\"particle\": \"thaumcraft:blocks/crucible3\"")
                        && blockModel.contains("\"top\": \"thaumcraft:blocks/crucible1\"")
                        && blockModel.contains("\"bottom\": \"thaumcraft:blocks/crucible2\"")
                        && blockModel.contains("\"outer\": \"thaumcraft:blocks/crucible3\"")
                        && blockModel.contains("\"inner\": \"thaumcraft:blocks/crucible5\"")
                        && blockModel.contains("\"inner_bottom\": \"thaumcraft:blocks/crucible6\"")
                        && blockModel.contains("\"from\": [0, 0, 0]")
                        && blockModel.contains("\"to\": [16, 16, 16]")
                        && blockModel.contains("\"from\": [2, 0, 0]")
                        && blockModel.contains("\"to\": [2, 16, 16]")
                        && blockModel.contains("\"from\": [0, 4, 0]")
                        && blockModel.contains("\"to\": [16, 4, 16]")
                        && !blockModel.contains("\"from\": [1, 0, 1]")
                        && !blockModel.contains("\"to\": [15, 11, 3]")
                        && !blockModel.contains("\"parent\": \"block/cube_all\""));
        assertTrue("Crucible alpha-mask textures require the metal device block to render in CUTOUT",
                blockSource.contains("public BlockRenderLayer getRenderLayer()")
                        && blockSource.contains("return BlockRenderLayer.CUTOUT;"));
    }

    @Test
    public void tileCrucibleRendererKeepsRequiredFluidMixinTarget() throws IOException {
        String source = read("src/main/java/thaumcraft/client/renderers/tile/TileCrucibleRenderer.java");
        int renderFluid = source.indexOf("public void renderFluid(TileCrucible tile, double x, double y, double z)");
        int textureLookup = source.indexOf(".getTexture(Blocks.WATER.getDefaultState())", renderFluid);

        assertTrue("Aqua Acrobatics requires render() to delegate to the TC6-style renderFluid target",
                source.contains("renderFluid(tile, x, y, z);"));
        assertTrue("The required renderFluid mixin target must own the water texture lookup redirected by Aqua Acrobatics",
                renderFluid >= 0 && textureLookup > renderFluid);
    }

    @Test
    public void crucibleBubbleFxKeepsOriginalCustomParticleShape() throws IOException {
        String bubble = read("src/main/java/thaumcraft/client/fx/particles/FXBubble.java");
        String proxy = read("src/main/java/thaumcraft/client/ClientProxy.java");

        assertTrue("Crucible bubbles should support the TC6 particle strip indices 64-66 and frothDown index 73",
                bubble.contains("setParticle(int particle)")
                        && bubble.contains("setFinalParticles(int start, int count)")
                        && proxy.contains("setParticle(64)")
                        && proxy.contains("setFinalParticles(65, 2)")
                        && proxy.contains("setParticle(73)"));
        assertTrue("Crucible bubbles should render their own fullbright colored billboard instead of delegating to vanilla particle visuals",
                bubble.contains("float u0 = (float) (this.particle % 16) / 16.0F;")
                        && bubble.contains("Particle.interpPosX")
                        && bubble.contains("PARTICLE_TEXTURE = new ResourceLocation(\"thaumcraft\", \"textures/misc/particles.png\")")
                        && bubble.contains("Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLE_TEXTURE)")
                        && bubble.contains("DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP")
                        && bubble.contains("customBuffer.pos(")
                        && bubble.contains(".color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha)")
                        && bubble.contains(".lightmap(lightU, lightV)")
                        && bubble.contains("return 0xF000F0;")
                        && bubble.contains("return 3;")
                        && !bubble.contains("super.renderParticle(buffer")
                        && !bubble.contains("setParticleTextureIndex("));
        assertTrue("Crucible proxy should use TC6-style gravity, random drift, alpha, and fixed boil burst count",
                proxy.contains("setRandomMovementScale(0.001D, 0.001D, 0.001D)")
                        && proxy.contains("setRandomMovementScale(0.002D, 0.002D, 0.002D)")
                        && proxy.contains("setGravity(0.1F)")
                        && proxy.contains("setGravity(0.05F)")
                        && proxy.contains("setGravity(-0.001F)")
                        && proxy.contains("setGravity(-0.025F * type)")
                        && proxy.contains("setAlpha(0.8F)")
                        && proxy.contains("for (int i = 0; i < 2; i++)"));
    }

    @Test
    public void crucibleDrawEffectsKeepsTc4BoilTriggers() throws IOException {
        String crucible = read("src/main/java/thaumcraft/common/tiles/TileCrucible.java");

        assertTrue("Heated water must spawn continuous surface froth",
                crucible.contains("if (this.heat > 150)")
                        && crucible.contains("Thaumcraft.proxy.crucibleFroth("));
        assertTrue("Aspected water must keep occasional colored bubble spawns",
                crucible.contains("this.world.rand.nextInt(6) == 0")
                        && crucible.contains("this.aspects.size() > 0")
                        && crucible.contains("Thaumcraft.proxy.crucibleBubble("));
        assertTrue("Overfilled crucibles must keep side froth overflow",
                crucible.contains("this.tagAmount() > this.maxTags")
                        && crucible.contains("Thaumcraft.proxy.crucibleFrothDown("));
        assertTrue("Crucible block event 2 must keep the burst boil path",
                crucible.contains("if (id == 2)")
                        && crucible.contains("Thaumcraft.proxy.crucibleBoilSound(")
                        && crucible.contains("Thaumcraft.proxy.crucibleBoil("));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

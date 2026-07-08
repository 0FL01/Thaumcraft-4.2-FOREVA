package thaumcraft.client;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class AiryNodeRoutingContractTest {

    @Test
    public void airyNodeFamilyShouldUseTesrWorldRoutingAndRestoreReferenceBlockContracts() throws IOException {
        String blockAiry = read("src/main/java/thaumcraft/common/blocks/BlockAiry.java");
        String config = read("src/main/java/thaumcraft/common/config/Config.java");
        String materialAiry = read("src/main/java/thaumcraft/common/config/MaterialAiry.java");
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String tileRenderer = read("src/main/java/thaumcraft/client/renderers/tile/TileNodeRenderer.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemNodeRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockairy.json");

        assertTrue("BlockAiry should use the TC4-style airy material instead of vanilla Material.AIR so Forge 1.12 World.isAirBlock does not treat aura nodes as air",
                blockAiry.contains("super(Config.airyMaterial);")
                        && config.contains("airyMaterial = new MaterialAiry(MapColor.AIR);")
                        && materialAiry.contains("extends Material")
                        && materialAiry.contains("this.setReplaceable();")
                        && materialAiry.contains("this.setImmovableMobility();")
                        && materialAiry.contains("public boolean isSolid()")
                        && materialAiry.contains("return false;")
                        && materialAiry.contains("public boolean blocksMovement()"));

        assertTrue("BlockAiry should route node/energized-node through TESR and keep warding aura invisible like TC4",
                blockAiry.contains("return meta == 0 || meta == 4 || meta == 5 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;"));

        assertTrue("BlockAiry should restore the original airy small-bounds, no-side-solid, meta-specific collision, and nitor-only item-drop contract",
                blockAiry.contains("private static final AxisAlignedBB AIRY_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);")
                        && blockAiry.contains("if (meta == 10 || meta == 11) return 100.0f;")
                        && blockAiry.contains("if (meta == 12) return -1.0f;")
                        && blockAiry.contains("if (meta == 0 || meta == 5) return 200.0f;")
                        && blockAiry.contains("if (meta == 10 || meta == 11) return 50.0f;")
                        && blockAiry.contains("if (meta == 12) return Float.MAX_VALUE;")
                        && blockAiry.contains("return meta == 2 || meta == 3 || meta == 4;")
                        && blockAiry.contains("return meta == 2 || meta == 3;")
                        && blockAiry.contains("return meta == 3 || meta == 4 || meta == 10 || meta == 11 || meta == 12 ? ZERO_AABB : AIRY_AABB;")
                        && blockAiry.contains("if (meta == 0 || meta == 2 || meta == 3 || meta == 4 || meta == 5 || meta == 10 || meta == 11 || meta == 12) {")
                        && blockAiry.contains("if (meta == 4 && entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {")
                        && blockAiry.contains("if (meta == 12) {")
                        && blockAiry.contains("private static final AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);")
                        && blockAiry.contains("return meta == 4 || meta == 12 ? super.getSelectedBoundingBox(state, world, pos) : ZERO_AABB.offset(pos);")
                        && blockAiry.contains("return false;")
                        && blockAiry.contains("return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null ? ConfigItems.itemResource : Items.AIR;")
                        && blockAiry.contains("return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null")
                        && blockAiry.contains("public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)")
                        && blockAiry.contains("stack.getMetadata() == 0 && placer instanceof EntityPlayer")
                        && blockAiry.contains("ThaumcraftWorldGenerator.createRandomNodeAt(worldIn, pos, worldIn.rand, false, false, false);")
                        && blockAiry.contains("if (this.getMetaFromState(state) == 0 && !worldIn.isRemote && te instanceof INode && ConfigItems.itemWispEssence != null) {")
                        && blockAiry.contains("((ItemWispEssence) itemstack.getItem()).setAspects(itemstack, new AspectList().add(aspect, 2));"));

        assertTrue("ClientProxy should explicitly register every airy metadata and override node metas onto the builtin/entity item stub while still installing the dedicated node item renderer",
                clientProxy.contains("Item airyItem = Item.getItemFromBlock(ConfigBlocks.blockAiry);")
                        && clientProxy.contains("for (int meta = 0; meta <= 12; meta++) {")
                        && clientProxy.contains("registerBlockItemModel(airyItem, meta, \"type=\" + meta);")
                        && clientProxy.contains("registerBuiltinItemModel(airyItem, 0, \"blockairy\");")
                        && clientProxy.contains("registerBuiltinItemModel(airyItem, 5, \"blockairy\");")
                        && clientProxy.contains("airyItem.setTileEntityItemStackRenderer(new ItemNodeRenderer());"));

        assertTrue("TileNodeRenderer should sample the TC4 32x32 nodes.png sheet, not compress node strips into an 8-row grid",
                tileRenderer.contains("public static final ResourceLocation NODES_TEXTURE")
                        && tileRenderer.contains("new ResourceLocation(\"thaumcraft\", \"textures/misc/nodes.png\")")
                        && tileRenderer.contains("float v0 = strip / (float) frames;")
                        && tileRenderer.contains("float v1 = (strip + 1) / (float) frames;")
                        && tileRenderer.contains("renderFacingStrip(renderX, renderY, renderZ, angle, centerScale, alpha, FRAMES, strip, frame, 0xFFFFFF);")
                        && tileRenderer.contains("private static boolean isHoldingThaumometer(EntityPlayer player)")
                        && tileRenderer.contains("player.getHeldItemOffhand()")
                        && tileRenderer.contains("OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 220.0F, 220.0F);")
                        && tileRenderer.contains("renderFacingStrip(renderX, renderY, renderZ, 0.0F, 0.5F, 0.1F, FRAMES, 1, frame, 0xFFFFFF);")
                        && !tileRenderer.contains("STRIPS = 8"));

        assertTrue("ItemNodeRenderer and the airy item-model stub must keep the dedicated inventory node sprite path for metas 0 and 5",
                itemRenderer.contains("if (meta != 0 && meta != 5)")
                        && itemRenderer.contains("Minecraft.getMinecraft().getTextureManager().bindTexture(TileNodeRenderer.NODES_TEXTURE);")
                        && itemRenderer.contains("drawAnimatedQuadStrip(scale, alpha, frames, strip, frame, 0xFFFFFF);")
                        && itemRenderer.contains("float v0 = strip / (float) frames;")
                        && !itemRenderer.contains("TileNodeRenderer.renderNode(")
                        && itemRenderer.contains("GlStateManager.scale(2.0D, 2.0D, 2.0D);")
                        && itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

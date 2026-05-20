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
        String clientProxy = read("src/main/java/thaumcraft/client/ClientProxy.java");
        String itemRenderer = read("src/main/java/thaumcraft/client/renderers/item/ItemNodeRenderer.java");
        String itemModel = read("src/main/resources/assets/thaumcraft/models/item/blockairy.json");

        assertTrue("BlockAiry should route node and energized-node world rendering through TESR while leaving the rest of the airy family on baked models for now",
                blockAiry.contains("return meta == 0 || meta == 5 ? EnumBlockRenderType.INVISIBLE : EnumBlockRenderType.MODEL;"));

        assertTrue("BlockAiry should restore the original airy small-bounds, no-side-solid, meta-specific collision, and nitor-only item-drop contract",
                blockAiry.contains("private static final AxisAlignedBB AIRY_AABB = new AxisAlignedBB(0.3D, 0.3D, 0.3D, 0.7D, 0.7D, 0.7D);")
                        && blockAiry.contains("if (meta == 10 || meta == 11) return 100.0f;")
                        && blockAiry.contains("if (meta == 12) return -1.0f;")
                        && blockAiry.contains("if (meta == 0 || meta == 5) return 200.0f;")
                        && blockAiry.contains("if (meta == 10 || meta == 11) return 50.0f;")
                        && blockAiry.contains("if (meta == 12) return Float.MAX_VALUE;")
                        && blockAiry.contains("return meta == 2 || meta == 3 || meta == 4;")
                        && blockAiry.contains("return meta == 2 || meta == 3;")
                        && blockAiry.contains("return meta == 3 || meta == 4 || meta == 10 || meta == 11 || meta == 12 ? NULL_AABB : AIRY_AABB;")
                        && blockAiry.contains("if (meta == 0 || meta == 2 || meta == 3 || meta == 4 || meta == 5 || meta == 10 || meta == 11 || meta == 12) {")
                        && blockAiry.contains("if (meta == 4 && entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer)) {")
                        && blockAiry.contains("if (meta == 12) {")
                        && blockAiry.contains("return false;")
                        && blockAiry.contains("return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null ? ConfigItems.itemResource : Items.AIR;")
                        && blockAiry.contains("return this.getMetaFromState(state) == 1 && ConfigItems.itemResource != null")
                        && blockAiry.contains("if (this.getMetaFromState(state) == 0 && !worldIn.isRemote && te instanceof INode && ConfigItems.itemWispEssence != null) {")
                        && blockAiry.contains("((ItemWispEssence) itemstack.getItem()).setAspects(itemstack, new AspectList().add(aspect, 2));"));

        assertTrue("ClientProxy should explicitly register every airy metadata and override node metas onto the builtin/entity item stub while still installing the dedicated node item renderer",
                clientProxy.contains("Item airyItem = Item.getItemFromBlock(ConfigBlocks.blockAiry);")
                        && clientProxy.contains("for (int meta = 0; meta <= 12; meta++) {")
                        && clientProxy.contains("registerBlockItemModel(airyItem, meta, \"type=\" + meta);")
                        && clientProxy.contains("registerBuiltinItemModel(airyItem, 0, \"blockairy\");")
                        && clientProxy.contains("registerBuiltinItemModel(airyItem, 5, \"blockairy\");")
                        && clientProxy.contains("airyItem.setTileEntityItemStackRenderer(new ItemNodeRenderer());"));

        assertTrue("ItemNodeRenderer and the airy item-model stub must keep the dedicated inventory node render path for metas 0 and 5",
                itemRenderer.contains("if (meta != 0 && meta != 5)")
                        && itemRenderer.contains("TileNodeRenderer.renderNode(")
                        && itemRenderer.contains("GlStateManager.scale(2.0D, 2.0D, 2.0D);")
                        && itemModel.contains("\"parent\": \"builtin/entity\""));
    }

    private static String read(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }
}

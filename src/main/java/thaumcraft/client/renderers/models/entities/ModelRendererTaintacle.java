package thaumcraft.client.renderers.models.entities;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class ModelRendererTaintacle extends ModelRenderer {

    private boolean compiledTaintacle;
    private int displayListTaintacle;

    public ModelRendererTaintacle(ModelBase model) {
        super(model);
    }

    public ModelRendererTaintacle(ModelBase model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    public void render(float scale, float childScale) {
        if (isHidden || !showModel) {
            return;
        }
        if (!compiledTaintacle) {
            compileDisplayListTaintacle(scale);
        }

        GlStateManager.translate(offsetX, offsetY, offsetZ);

        if (rotateAngleX == 0.0F && rotateAngleY == 0.0F && rotateAngleZ == 0.0F) {
            if (rotationPointX == 0.0F && rotationPointY == 0.0F && rotationPointZ == 0.0F) {
                applyFullbrightIfLeaf();
                GlStateManager.callList(displayListTaintacle);
                renderChildrenScaled(scale, childScale);
            } else {
                GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
                applyFullbrightIfLeaf();
                GlStateManager.callList(displayListTaintacle);
                renderChildrenScaled(scale, childScale);
                GlStateManager.translate(-rotationPointX * scale, -rotationPointY * scale, -rotationPointZ * scale);
            }
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
            if (rotateAngleZ != 0.0F) {
                GlStateManager.rotate(rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }
            if (rotateAngleY != 0.0F) {
                GlStateManager.rotate(rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }
            if (rotateAngleX != 0.0F) {
                GlStateManager.rotate(rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }
            applyFullbrightIfLeaf();
            GlStateManager.callList(displayListTaintacle);
            renderChildrenScaled(scale, childScale);
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(-offsetX, -offsetY, -offsetZ);
    }

    private void renderChildrenScaled(float scale, float childScale) {
        if (childModels == null) {
            return;
        }
        for (ModelRenderer child : childModels) {
            if (!(child instanceof ModelRendererTaintacle)) {
                continue;
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(childScale, childScale, childScale);
            ((ModelRendererTaintacle) child).render(scale, childScale);
            GlStateManager.popMatrix();
        }
    }

    private void applyFullbrightIfLeaf() {
        if (childModels == null) {
            int packed = 0xF000F0;
            int lightU = packed % 65536;
            int lightV = packed / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightU, lightV);
        }
    }

    private void compileDisplayListTaintacle(float scale) {
        displayListTaintacle = GLAllocation.generateDisplayLists(1);
        GlStateManager.glNewList(displayListTaintacle, GL11.GL_COMPILE);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        for (ModelBox cube : cubeList) {
            cube.render(buffer, scale);
        }
        GlStateManager.glEndList();
        compiledTaintacle = true;
    }
}

package thaumcraft.codechicken.lib.lighting;

import thaumcraft.codechicken.lib.lighting.PlanarLightModel;
import thaumcraft.codechicken.lib.render.CCRenderState;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;

public class LightModel
implements CCRenderState.IVertexOperation {
    public static final int operationIndex = CCRenderState.registerOperation();
    public static LightModel standardLightModel = new LightModel().setAmbient(new Vector3(0.4, 0.4, 0.4)).addLight(new Light(new Vector3(0.2, 1.0, -0.7)).setDiffuse(new Vector3(0.6, 0.6, 0.6))).addLight(new Light(new Vector3(-0.2, 1.0, 0.7)).setDiffuse(new Vector3(0.6, 0.6, 0.6)));
    private Vector3 ambient = new Vector3();
    private Light[] lights = new Light[8];
    private int lightCount;

    public LightModel addLight(Light light) {
        this.lights[this.lightCount++] = light;
        return this;
    }

    public LightModel setAmbient(Vector3 vec) {
        this.ambient.set(vec);
        return this;
    }

    public int apply(int colour, Vector3 normal) {
        Vector3 n_colour = this.ambient.copy();
        for (int l = 0; l < this.lightCount; ++l) {
            Light light = this.lights[l];
            double n_l = light.position.dotProduct(normal);
            double f = n_l > 0.0 ? 1.0 : 0.0;
            n_colour.x += light.ambient.x + f * light.diffuse.x * n_l;
            n_colour.y += light.ambient.y + f * light.diffuse.y * n_l;
            n_colour.z += light.ambient.z + f * light.diffuse.z * n_l;
        }
        if (n_colour.x > 1.0) {
            n_colour.x = 1.0;
        }
        if (n_colour.y > 1.0) {
            n_colour.y = 1.0;
        }
        if (n_colour.z > 1.0) {
            n_colour.z = 1.0;
        }
        n_colour.multiply((double)(colour >>> 24) / 255.0, (double)(colour >> 16 & 0xFF) / 255.0, (double)(colour >> 8 & 0xFF) / 255.0);
        return (int)(n_colour.x * 255.0) << 24 | (int)(n_colour.y * 255.0) << 16 | (int)(n_colour.z * 255.0) << 8 | colour & 0xFF;
    }

    @Override
    public boolean load() {
        CCRenderState.pipeline.addDependency(CCRenderState.normalAttrib);
        CCRenderState.pipeline.addDependency(CCRenderState.colourAttrib);
        return true;
    }

    @Override
    public void operate() {
        CCRenderState.setColour(this.apply(CCRenderState.colour, CCRenderState.normal));
    }

    @Override
    public int operationID() {
        return operationIndex;
    }

    public PlanarLightModel reducePlanar() {
        int[] colours = new int[6];
        for (int i = 0; i < 6; ++i) {
            colours[i] = this.apply(-1, Rotation.axes[i]);
        }
        return new PlanarLightModel(colours);
    }

    public static class Light {
        public Vector3 ambient = new Vector3();
        public Vector3 diffuse = new Vector3();
        public Vector3 position;

        public Light(Vector3 pos) {
            this.position = pos.copy().normalize();
        }

        public Light setDiffuse(Vector3 vec) {
            this.diffuse.set(vec);
            return this;
        }

        public Light setAmbient(Vector3 vec) {
            this.ambient.set(vec);
            return this;
        }
    }
}


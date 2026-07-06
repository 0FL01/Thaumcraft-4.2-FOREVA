package thaumcraft.common.world.aura;

/**
 * Small TC6 aura chunk facade used by addon compatibility shims.
 */
public class AuraChunk {

    private short base;
    private float vis;
    private float flux;

    public AuraChunk() {
        this((short) 100, 100.0F, 0.0F);
    }

    public AuraChunk(short base, float vis, float flux) {
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }

    public short getBase() {
        return base;
    }

    public void setBase(short base) {
        this.base = base;
    }

    public float getVis() {
        return vis;
    }

    public void setVis(float vis) {
        this.vis = Math.max(0.0F, vis);
    }

    public float getFlux() {
        return flux;
    }

    public void setFlux(float flux) {
        this.flux = Math.max(0.0F, flux);
    }
}

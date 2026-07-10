package thaumcraft.client.renderers.models;

public class ModelFluxScrubber {
    private final ModelEldritchCap obeliskCap = new ModelEldritchCap();

    public void renderCap(float scale) {
        obeliskCap.renderCapGroup();
    }

    public void renderTip(float scale) {
        obeliskCap.renderTipGroup();
    }
}

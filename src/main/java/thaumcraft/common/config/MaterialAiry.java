package thaumcraft.common.config;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialAiry extends Material {

    public MaterialAiry(MapColor color) {
        super(color);
        this.setReplaceable();
        this.setImmovableMobility();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean blocksMovement() {
        return false;
    }
}

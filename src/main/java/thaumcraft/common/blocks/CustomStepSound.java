package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class CustomStepSound
extends SoundType {

    private final String soundName;

    public CustomStepSound(String name, float volumeIn, float pitchIn) {
        super(volumeIn, pitchIn,
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundEvents.BLOCK_STONE_STEP,
                SoundEvents.BLOCK_GLASS_PLACE,
                SoundEvents.BLOCK_GLASS_HIT,
                SoundEvents.BLOCK_GLASS_FALL);
        this.soundName = name;
    }

    public String getSoundName() {
        return soundName;
    }
}

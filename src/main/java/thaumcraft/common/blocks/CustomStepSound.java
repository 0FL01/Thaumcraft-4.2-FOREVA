package thaumcraft.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;
import thaumcraft.common.lib.TCSounds;

public class CustomStepSound
extends SoundType {

    private final String soundName;

    public CustomStepSound(String name, float volumeIn, float pitchIn) {
        super(volumeIn, pitchIn,
                resolveBreakSound(name),
                SoundEvents.BLOCK_STONE_STEP,
                SoundEvents.BLOCK_GLASS_PLACE,
                SoundEvents.BLOCK_GLASS_HIT,
                SoundEvents.BLOCK_GLASS_FALL);
        this.soundName = name;
    }

    public String getSoundName() {
        return soundName;
    }

    private static SoundEvent resolveBreakSound(String name) {
        if ("crystal".equals(name)) {
            return TCSounds.CRYSTAL;
        }
        if ("jar".equals(name)) {
            return TCSounds.JAR;
        }
        if ("urnbreak".equals(name)) {
            return TCSounds.URNBREAK;
        }
        return SoundEvents.BLOCK_GLASS_BREAK;
    }
}

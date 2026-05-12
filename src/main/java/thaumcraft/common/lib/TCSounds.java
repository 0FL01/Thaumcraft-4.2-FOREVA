package thaumcraft.common.lib;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.Thaumcraft;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Thaumcraft.MODID)
public final class TCSounds {

    private static final List<SoundEvent> ALL = new ArrayList<>();

    public static final SoundEvent HEARTBEAT        = sound("heartbeat");
    public static final SoundEvent SPILL            = sound("spill");
    public static final SoundEvent BUBBLE           = sound("bubble");
    public static final SoundEvent ALEMBICKNOCK     = sound("alembicknock");
    public static final SoundEvent CREAK            = sound("creak");
    public static final SoundEvent SQUEEK           = sound("squeek");
    public static final SoundEvent GOLEMIRONSHOOT   = sound("golemironshoot");
    public static final SoundEvent JAR              = sound("jar");
    public static final SoundEvent SWARM            = sound("swarm");
    public static final SoundEvent SWARMATTACK      = sound("swarmattack");
    public static final SoundEvent FLY              = sound("fly");
    public static final SoundEvent KEY              = sound("key");
    public static final SoundEvent DOORFAIL         = sound("doorfail");
    public static final SoundEvent CAMERATICKS      = sound("cameraticks");
    public static final SoundEvent CAMERACLACK      = sound("cameraclack");
    public static final SoundEvent PUMP             = sound("pump");
    public static final SoundEvent PAGE             = sound("page");
    public static final SoundEvent LEARN            = sound("learn");
    public static final SoundEvent WRITE            = sound("write");
    public static final SoundEvent ERASE            = sound("erase");
    public static final SoundEvent BRAIN            = sound("brain");
    public static final SoundEvent CRYSTAL          = sound("crystal");
    public static final SoundEvent WISPDEAD         = sound("wispdead");
    public static final SoundEvent WISPLIVE         = sound("wisplive");
    public static final SoundEvent WAND             = sound("wand");
    public static final SoundEvent WANDFAIL         = sound("wandfail");
    public static final SoundEvent RUMBLE           = sound("rumble");
    public static final SoundEvent ICE              = sound("ice");
    public static final SoundEvent JACOBS           = sound("jacobs");
    public static final SoundEvent HHOFF            = sound("hhoff");
    public static final SoundEvent HHON             = sound("hhon");
    public static final SoundEvent PECH_IDLE        = sound("pech_idle");
    public static final SoundEvent PECH_TRADE       = sound("pech_trade");
    public static final SoundEvent PECH_DICE        = sound("pech_dice");
    public static final SoundEvent PECH_HIT         = sound("pech_hit");
    public static final SoundEvent PECH_DEATH       = sound("pech_death");
    public static final SoundEvent PECH_CHARGE      = sound("pech_charge");
    public static final SoundEvent SHOCK            = sound("shock");
    public static final SoundEvent FIRELOOP         = sound("fireloop");
    public static final SoundEvent ZAP              = sound("zap");
    public static final SoundEvent CRAFTFAIL        = sound("craftfail");
    public static final SoundEvent CRAFTSTART       = sound("craftstart");
    public static final SoundEvent RUNICSHIELDEFFECT  = sound("runicShieldEffect");
    public static final SoundEvent RUNICSHIELDCHARGE  = sound("runicShieldCharge");
    public static final SoundEvent SWING            = sound("swing");
    public static final SoundEvent WIND             = sound("wind");
    public static final SoundEvent TOOL             = sound("tool");
    public static final SoundEvent GORE             = sound("gore");
    public static final SoundEvent ROOTS            = sound("roots");
    public static final SoundEvent TENTACLE         = sound("tentacle");
    public static final SoundEvent UPGRADE          = sound("upgrade");
    public static final SoundEvent WHISPERS         = sound("whispers");
    public static final SoundEvent MONOLITH         = sound("monolith");
    public static final SoundEvent INFUSER          = sound("infuser");
    public static final SoundEvent INFUSERSTART     = sound("infuserstart");
    public static final SoundEvent EGIDLE           = sound("egidle");
    public static final SoundEvent EGATTACK         = sound("egattack");
    public static final SoundEvent EGDEATH          = sound("egdeath");
    public static final SoundEvent EGSCREECH        = sound("egscreech");
    public static final SoundEvent CRABCLAW         = sound("crabclaw");
    public static final SoundEvent CRABDEATH        = sound("crabdeath");
    public static final SoundEvent CRABTALK         = sound("crabtalk");
    public static final SoundEvent CHANT            = sound("chant");
    public static final SoundEvent COINS            = sound("coins");
    public static final SoundEvent URNBREAK         = sound("urnbreak");
    public static final SoundEvent EVILPORTAL       = sound("evilportal");

    private static SoundEvent sound(String path) {
        ResourceLocation id = new ResourceLocation(Thaumcraft.MODID, path);
        SoundEvent event = new SoundEvent(id);
        event.setRegistryName(id);
        ALL.add(event);
        return event;
    }

    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(ALL.toArray(new SoundEvent[0]));
    }

    private TCSounds() {}
}

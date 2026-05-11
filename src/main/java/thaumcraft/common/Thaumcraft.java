package thaumcraft.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(
    modid = Thaumcraft.MODID,
    name = Thaumcraft.NAME,
    version = Thaumcraft.VERSION,
    dependencies = "required-after:forge@[14.23.5.2847,);required-after:baubles@[1.12-1.5.2,)",
    guiFactory = "thaumcraft.client.gui.GuiFactory"
)
public class Thaumcraft {

    public static final String MODID = "thaumcraft";
    public static final String NAME = "Thaumcraft";
    public static final String VERSION = "@VERSION@";

    public static final Logger log = LogManager.getLogger("THAUMCRAFT");

    @Mod.Instance(MODID)
    public static Thaumcraft instance;

    @SidedProxy(
        clientSide = "thaumcraft.client.ClientProxy",
        serverSide = "thaumcraft.common.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        log.info("Thaumcraft {} initializing", VERSION);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        log.info("Thaumcraft init");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        log.info("Thaumcraft postInit");
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        log.info("Thaumcraft serverLoad");
    }
}

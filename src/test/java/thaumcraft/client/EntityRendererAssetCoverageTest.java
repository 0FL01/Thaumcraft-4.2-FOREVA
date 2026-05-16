package thaumcraft.client;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class EntityRendererAssetCoverageTest {

    @Test
    public void stage8dEntityAssetBaselineExists() {
        String[] required = new String[]{
                "textures/misc/cultist_portal.png",
                "textures/misc/wisp.png",
                "textures/misc/wispy.png",
                "textures/models/bucket.obj",
                "textures/models/orb.obj",
                "textures/models/bzombie.png",
                "textures/models/bzombievil.png",
                "textures/models/crab.png",
                "textures/models/craboverlay.png",
                "textures/models/cultist.png",
                "textures/models/eldritch_golem.png",
                "textures/models/eldritch_guardian.png",
                "textures/models/eldritch_warden.png",
                "textures/models/firebat.png",
                "textures/models/golem_clay.png",
                "textures/models/golem_damage.png",
                "textures/models/golem_decoration.png",
                "textures/models/golem_flesh.png",
                "textures/models/golem_iron.png",
                "textures/models/golem_stone.png",
                "textures/models/golem_straw.png",
                "textures/models/golem_tallow.png",
                "textures/models/golem_thaumium.png",
                "textures/models/golem_wood.png",
                "textures/models/pech_forage.png",
                "textures/models/pech_stalker.png",
                "textures/models/pech_thaum.png",
                "textures/models/taint_spider.png",
                "textures/models/taint_spider_eyes.png",
                "textures/models/taint_spore.png",
                "textures/models/taintacle.png",
                "textures/models/trunk.png",
                "textures/models/trunkangry.png",
                "textures/models/tslime.png",
                "textures/models/vampirebat.png",
                "textures/models/villager.png"
        };

        Path base = Paths.get("src/main/resources/assets/thaumcraft");
        for (String relative : required) {
            Path path = base.resolve(relative);
            assertTrue("Missing Stage 8-d renderer asset: " + relative, Files.exists(path));
        }
    }
}

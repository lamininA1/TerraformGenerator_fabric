package org.terraform.scratch;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerraformGeneratorScratch implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(TerraformGeneratorScratch.class);
    @Override
    public void onInitialize() {
        LOGGER.info("TerraformGenerator scratch mod initializing");
    }
}

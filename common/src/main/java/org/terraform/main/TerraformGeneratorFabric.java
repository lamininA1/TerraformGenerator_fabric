package org.terraform.main;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class TerraformGeneratorFabric implements ModInitializer {

    private final TerraformGeneratorPlugin plugin = new TerraformGeneratorPlugin();

    @Override
    public void onInitialize() {
        System.out.println("TerraformGenerator fabric mod initializing");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> plugin.initialize());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> plugin.shutdown());
    }
}

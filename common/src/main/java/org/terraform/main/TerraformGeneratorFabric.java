package org.terraform.main;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.level.ServerLevel;

public class TerraformGeneratorFabric implements ModInitializer {

    private final TerraformGeneratorPlugin plugin = new TerraformGeneratorPlugin();

    @Override
    public void onInitialize() {
        System.out.println("TerraformGenerator fabric mod initializing");

        ServerLifecycleEvents.SERVER_STARTED.register(server -> plugin.initialize());
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> plugin.shutdown());

        ServerWorldEvents.LOAD.register((ServerLevel world) -> plugin.onFabricWorldLoad(world));
        ServerWorldEvents.UNLOAD.register((ServerLevel world) -> plugin.onFabricWorldUnload(world));
    }
}

package org.terraform.main;

import net.fabricmc.api.ModInitializer;

public class TerraformGeneratorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Basic Fabric initialization
        System.out.println("TerraformGenerator fabric mod initializing");
        // Reuse existing plugin initialization logic where possible
        try {
            TerraformGeneratorPlugin plugin = new TerraformGeneratorPlugin();
            plugin.onEnable();
        } catch (Exception e) {
            System.err.println("Failed to run plugin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package org.terraform.v1_18_R2;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.terraform.coregen.BlockDataFixerAbstract;
import org.terraform.coregen.fabric.FabricNMSInjectorAbstract;
import org.terraform.coregen.populatordata.PopulatorDataAbstract;
import org.terraform.coregen.populatordata.PopulatorDataICAAbstract;
import org.terraform.data.TerraformWorld;

/**
 * Placeholder Fabric injector for v1_18_R2.
 */
public class FabricNMSInjector extends FabricNMSInjectorAbstract {
    @Override
    public void startupTasks() {
        CustomBiomeHandler.init();
    }

    @Override
    public @Nullable BlockDataFixerAbstract getBlockDataFixer() {
        return new BlockDataFixer();
    }

    @Override
    public boolean attemptInject(ServerLevel world) {
        TerraformWorld tw = TerraformWorld.get(world.dimension().location().toString(), world.getSeed());
        tw.minY = -64;
        tw.maxY = 320;
        // TODO implement actual Fabric chunk generator injection
        return false;
    }

    @Override
    public PopulatorDataICAAbstract getICAData(ServerLevel world) {
        return null;
    }

    @Override
    public @Nullable PopulatorDataICAAbstract getICAData(PopulatorDataAbstract data) {
        return null;
    }

    @Override
    public void storeBee(ServerLevel world, BlockPos hivePos) {
        // TODO implement storing bees on Fabric
    }
}

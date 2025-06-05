package org.terraform.coregen.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.terraform.coregen.BlockDataFixerAbstract;
import org.terraform.coregen.populatordata.PopulatorDataAbstract;
import org.terraform.coregen.populatordata.PopulatorDataICAAbstract;

/**
 * Base injector used when running as a Fabric mod. Methods
 * use Mojang mappings rather than Bukkit APIs.
 */
public abstract class FabricNMSInjectorAbstract {
    public void startupTasks() {}

    @Nullable
    public BlockDataFixerAbstract getBlockDataFixer() { return null; }

    /**
     * Attempt to hook the chunk generator for the provided world.
     * @return true if the injection succeeded.
     */
    public abstract boolean attemptInject(ServerLevel world);

    /**
     * Obtain ICA data for the given world or chunk.
     */
    public abstract PopulatorDataICAAbstract getICAData(ServerLevel world);

    public abstract @Nullable PopulatorDataICAAbstract getICAData(PopulatorDataAbstract data);

    /**
     * Store a bee in the hive located at the given position.
     */
    public abstract void storeBee(ServerLevel world, BlockPos hivePos);

    public int getMinY() { return 0; }
    public int getMaxY() { return 256; }
}

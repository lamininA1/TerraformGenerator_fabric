package org.terraform.main;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.terraform.biome.BiomeBank;
import org.terraform.coregen.ChunkCache;
import org.terraform.coregen.HeightMap;
import org.terraform.coregen.NMSInjectorAbstract;
import org.terraform.coregen.fabric.FabricNMSInjectorAbstract;
import org.terraform.coregen.TerraformPopulator;
import org.terraform.coregen.bukkit.TerraformGenerator;
import org.terraform.coregen.populatordata.PopulatorDataPostGen;
import org.terraform.data.SimpleChunkLocation;
import org.terraform.data.TerraformWorld;
import org.terraform.main.config.TConfig;
import org.terraform.reflection.Post14PrivateFieldHandler;
import org.terraform.reflection.Pre14PrivateFieldHandler;
import org.terraform.reflection.PrivateFieldHandler;
import org.terraform.schematic.SchematicListener;
import org.terraform.structure.StructureRegistry;
import org.terraform.utils.BlockUtils;
import org.terraform.utils.GenUtils;
import org.terraform.utils.datastructs.ConcurrentLRUCache;
import org.terraform.utils.noise.NoiseCacheHandler;
import org.terraform.utils.version.Version;
import org.terraform.watchdog.TfgWatchdogSuppressant;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class TerraformGeneratorPlugin {

    public static final Set<String> INJECTED_WORLDS = new HashSet<>();
    public static final @NotNull PrivateFieldHandler privateFieldHandler;
    public static TLogger logger;

    //Injector "can" be null, but the plugin can be assumed to be completely broken
    // in that case. Just crash.
    public static @NotNull NMSInjectorAbstract injector;
    public static TfgWatchdogSuppressant watchdogSuppressant;
    private static TerraformGeneratorPlugin instance;

    static {
        PrivateFieldHandler handler;
        try {
            Field.class.getDeclaredField("modifiers");
            handler = new Pre14PrivateFieldHandler();
        }
        catch (NoSuchFieldException | SecurityException ex) {
            handler = new Post14PrivateFieldHandler();
        }
        privateFieldHandler = handler;
    }

    private LanguageManager lang;
    private final File dataFolder = new File("./terraformgenerator");

    public static TerraformGeneratorPlugin get() {
        return instance;
    }

    public void initialize() {
        GenUtils.initGenUtils();
        BlockUtils.initBlockUtils();
        instance = this;

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        try {
            TConfig.init(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            System.err.println("Failed to load config.yml: " + e.getMessage());
            return;
        }

        logger = new TLogger();
        lang = new LanguageManager(this, TConfig.c);

        // Initiate the height map flat radius value
        HeightMap.spawnFlatRadiusSquared = TConfig.c.HEIGHT_MAP_SPAWN_FLAT_RADIUS;
        if (HeightMap.spawnFlatRadiusSquared > 0) {
            HeightMap.spawnFlatRadiusSquared *= HeightMap.spawnFlatRadiusSquared;
        }

        BiomeBank.initSinglesConfig(); // Initiates single biome modes.

        // Initialize chunk cache based on config size
        TerraformGenerator.CHUNK_CACHE = new ConcurrentLRUCache<>(
                "CHUNK_CACHE",
                TConfig.c.DEVSTUFF_CHUNKCACHE_SIZE,
                (key)->{
                    return new ChunkCache(key.tw(),key.x(),key.z());
                });

        // Initialize biome query cache based on config size
        GenUtils.biomeQueryCache = new ConcurrentLRUCache<>(
                "biomeQueryCache",
                TConfig.c.DEVSTUFF_CHUNKBIOMES_SIZE,
                (key) -> {
                    EnumSet<BiomeBank> banks = EnumSet.noneOf(BiomeBank.class);
                    int gridX = key.chunkX * 16;
                    int gridZ = key.chunkZ * 16;
                    for (int x = gridX; x < gridX + 16; x++) {
                        for (int z = gridZ; z < gridZ + 16; z++) {
                            BiomeBank bank = key.tw.getBiomeBank(x, z);
                            if (!banks.contains(bank)) {
                                banks.add(bank);
                            }
                        }
                    }
                    return banks;
                }
        );

        LangOpt.init(this);
        watchdogSuppressant = new TfgWatchdogSuppressant();

        TerraformGenerator.updateSeaLevelFromConfig();

        String version = Version.getVersionPackage();
        logger.stdout("Detected version: " + version + ", number: " + Version.DOUBLE);
        try {
            injector = Version.getInjector();
            if (injector != null) {
                injector.startupTasks();
            }else throw new ClassNotFoundException(); //injector no longer throws on no version mapping.
        }
        catch (ClassNotFoundException e) {
            TerraformGeneratorPlugin.logger.stackTrace(e);
            logger.stdout("&cNo support for this version has been made yet!");
        }
        catch (InstantiationException |
               IllegalAccessException |
               IllegalArgumentException |
               InvocationTargetException |
               NoSuchMethodException |
               SecurityException e) {
            TerraformGeneratorPlugin.logger.stackTrace(e);
            logger.stdout("&cSomething went wrong initiating the injector!");
        }

        if (TConfig.c.MISC_SAPLING_CUSTOM_TREES_ENABLED) {
            // TODO register custom sapling handling using Fabric events
        }

        StructureRegistry.init();
    }


    public void shutdown() {
        // This is already done in NativeGeneratorPatcherPopulator World Unload Event.
        // NativeGeneratorPatcherPopulator.flushChanges();
    }


    // Bukkit world events have been removed. Equivalent hooks are
    // provided by Fabric lifecycle events (see onFabricWorldLoad/Unload).

    // Fabric lifecycle hooks
    public void onFabricWorldLoad(ServerLevel world) {
        if (injector instanceof org.terraform.coregen.fabric.FabricNMSInjectorAbstract fabricInjector) {
            TerraformWorld tw = TerraformWorld.get(world.dimension().location().toString(), world.getSeed());
            if (fabricInjector.attemptInject(world)) {
                INJECTED_WORLDS.add(tw.getName());
                tw.minY = fabricInjector.getMinY();
                tw.maxY = fabricInjector.getMaxY();
                logger.stdout("Fabric injection success for " + tw.getName());
            } else {
                logger.stdout("Fabric injection failed for " + tw.getName());
            }
        }
    }

    public void onFabricWorldUnload(ServerLevel world) {
        String name = world.dimension().location().toString();
        if(INJECTED_WORLDS.contains(name)) {
            logger.stdout("Flushing noise cache for world " + name);
            NoiseCacheHandler.flushNoiseCaches(TerraformWorld.get(name, world.getSeed()));
        }
    }


    // Default world generator is handled by Fabric datapack/JSON settings.

    public LanguageManager getLang() {
        // TODO Auto-generated method stub
        return lang;
    }

    public File getDataFolder() {
        return dataFolder;
    }

}

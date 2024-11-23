package net.abhinav.ultrathunderstorm;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class UltraThunderStorm extends JavaPlugin {

    private boolean stormActive = false; // Tracks if a manual storm is active
    private final Random random = new Random();

    @Override
    public void onEnable() {
        getLogger().info("Ultra Thunder Storm Plugin Enabled!");

        // Save default config
        saveDefaultConfig();

        // Register command
        this.getCommand("ultrastorm").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("ultrastorm.command")) {
                sender.sendMessage("You don't have permission to use this command!");
                return true;
            }

            World world = Bukkit.getWorlds().get(0); // Default to the first world
            if (!stormActive) {
                stormActive = true;
                sender.sendMessage("§6Intense Thunder Storm has begun! Hold tight!");
                startThunderstorm(world);
            } else {
                stormActive = false;
                sender.sendMessage("§2Intense Thunder Storm has ended!");
            }
            return true;
        });

        // Listen for weather changes
        Bukkit.getPluginManager().registerEvents(new ThunderstormListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Ultra Thunder Storm Plugin Disabled!");
    }

    public void startThunderstorm(World world) {
        int particleIntensity = getConfig().getBoolean("particles") ? 10 : 0; // Adjust based on config
        boolean largeFires = getConfig().getBoolean("large_fire");

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!stormActive && !world.isThundering()) {
                    cancel();
                    return;
                }

                // Generate up to 100 lightning strikes per second
                for (int i = 0; i < 100; i++) {
                    Location randomLocation = getRandomLocation(world);
                    LightningStrike lightning = world.strikeLightning(randomLocation);

                    // Add particles if enabled
                    if (particleIntensity > 0) {
                        world.spawnParticle(Particle.EXPLOSION, randomLocation, particleIntensity, 1, 1, 1, 0.1);
                    }

                    // Chance for bigger fires
                    if (largeFires && random.nextInt(10) < 2) { // 20% chance
                        createBigFire(randomLocation);
                    }
                }
            }
        }.runTaskTimer(this, 0, 1); // Schedule every tick (20 times per second)
    }

    private Location getRandomLocation(World world) {
        int x = random.nextInt(2000) - 1000; // Random x within a 1000 block radius
        int z = random.nextInt(2000) - 1000; // Random z within a 1000 block radius
        int y = world.getHighestBlockYAt(x, z) + 1; // Get the surface level
        return new Location(world, x, y, z);
    }

    private void createBigFire(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // Create a large fire zone
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location fireLocation = location.clone().add(x, 0, z);
                if (world.getBlockAt(fireLocation).isEmpty()) {
                    world.getBlockAt(fireLocation).setType(org.bukkit.Material.FIRE);
                }
            }
        }
    }

    public boolean shouldTriggerIntenseStorm() {
        if (!getConfig().getBoolean("enabled")) return false;
        int chance = getConfig().getInt("chance");
        return random.nextInt(100) < chance;
    }
}

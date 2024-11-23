package net.abhinav.ultrathunderstorm;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ThunderstormListener implements Listener {

    private final UltraThunderStorm plugin;

    public ThunderstormListener(UltraThunderStorm plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!event.toWeatherState() || !event.getWorld().isThundering()) return;

        // Check if this thunderstorm should trigger intense mode
        if (plugin.shouldTriggerIntenseStorm()) {
            World world = event.getWorld();
            Bukkit.broadcastMessage("§6Intense Thunder Storm has begun! Hold tight!");
            plugin.startThunderstorm(world);
        }
    }
}

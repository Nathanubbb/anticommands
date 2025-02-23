package it.R_Developing.AntiCommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class AntiCommands extends JavaPlugin implements Listener, CommandExecutor {
    private FileConfiguration config;

    public static void main(String[] args) {}

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigData();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().substring(1).toLowerCase();

        if(event.getPlayer().isOp() || event.getPlayer().hasPermission("anticommands.admin") || event.getPlayer().hasPermission("anticommands.bypass")) {
            return;
        }

        List<String> blockedCommands = config.getStringList("commands");

        if(config.getBoolean("sense")) {
            if(!blockedCommands.contains(command)) {
                String response = config.getString("not-permitted");
                if(response != null) {
                    event.getPlayer().sendMessage(response);
                    event.setCancelled(true);
                }
            }
        } else {
            if(blockedCommands.contains(command)) {
                String response = config.getString("not-permitted");
                if(response != null) {
                    event.getPlayer().sendMessage(response);
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if(!player.hasPermission("anticommands.admin")) {
                player.sendMessage("You don't have permission to reload the config.");
                return false;
            }

            reloadConfigData();
            player.sendMessage("Configuration reloaded successfully.");
            return true;
        }
        return false;
    }

    public void reloadConfigData() {
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
    }
}

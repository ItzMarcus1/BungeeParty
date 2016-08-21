package me.itzmarcus.bungeeparty;

import me.itzmarcus.bungeeparty.Commands.MainCommand;
import me.itzmarcus.bungeeparty.MySQL.ConnectionHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by marcus on 18-08-2016.
 */
public class Core extends Plugin {

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder().getPath(), "database.yml");
        if(!file.exists()) {
            try {
                file.createNewFile();
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
                config.set("ip", "localhost");
                config.set("database", "BungeeParty");
                config.set("user", "username");
                config.set("password", "password");
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
                ProxyServer.getInstance().getConsole().sendMessage("Created a new config file.");
            } catch (IOException e) {
                ProxyServer.getInstance().getConsole().sendMessage("Could not create the config file.");
            }
        } else {
            ProxyServer.getInstance().getConsole().sendMessage("Found the correct config file.");
        }
        getProxy().getPluginManager().registerCommand(this, new MainCommand(this));
    }
}

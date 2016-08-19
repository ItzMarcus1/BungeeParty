package me.itzmarcus.bungeeparty;

import me.itzmarcus.bungeeparty.Commands.MainCommand;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by marcus on 18-08-2016.
 */
public class Core extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerCommand(this, new MainCommand());
    }
}

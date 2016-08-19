package me.itzmarcus.bungeeparty.Commands;

import me.itzmarcus.bungeeparty.MySQL.ConnectionHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by marcus on 18-08-2016.
 */
public class MainCommand extends Command {

    public MainCommand() {
        super("party");
    }

    ConnectionHandler c = new ConnectionHandler();

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(args.length == 0) {
            p.sendMessage("§6----------------------------------------------------");
            p.sendMessage("");
            p.sendMessage("§8» §e/party invite <player> §8- §aInvite a player to your party.");
            p.sendMessage("§8» §e/party kick <player> §8- §aKick a player from your party.");
            p.sendMessage("§8» §e/party disband §8- §aDisband your party.");
            p.sendMessage("");
            p.sendMessage("§6----------------------------------------------------");
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("disband")) {
                c.createTeam(p.getName());
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("invite")) {
                ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if(invitedPlayer == null) {
                    p.sendMessage("§cThis player is currently not online.");
                    return;
                }

                // TODO: Auto create the party if the main player does not have a party, then invite the invited player.
            } else if(args[0].equalsIgnoreCase("kick")) {
                String kickedPlayer = args[1];

                // TODO: Check if the player and the main player are in the same party. Then kick the player.
            }
        }
    }
}

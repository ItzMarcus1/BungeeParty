package me.itzmarcus.bungeeparty.Commands;

import me.itzmarcus.bungeeparty.Core;
import me.itzmarcus.bungeeparty.MySQL.ConnectionHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by marcus on 18-08-2016.
 */
public class MainCommand extends Command {

    Core plugin;
    public MainCommand(Core instance) {
        super("party");
        plugin = instance;
    }

    ConnectionHandler c = new ConnectionHandler(plugin);

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(args.length == 0) {
            p.sendMessage("§6----------------------------------------------------");
            p.sendMessage("");
            p.sendMessage("§8» §e/party invite <player> §8- §aInvite a player to your party.");
            p.sendMessage("§8» §e/party kick <player> §8- §aKick a player from your party.");
            p.sendMessage("§8» §e/party accept <player> §8- §aAccept a Party invite.");
            p.sendMessage("§8» §e/party disband §8- §aDisband your party.");
            p.sendMessage("");
            p.sendMessage("§6----------------------------------------------------");
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("disband")) {
                if(c.isPartyLeader(p.getName())) {
                    c.disbandParty(p.getName());
                } else {
                    p.sendMessage("§cYou are not the leader of any party.");
                }
            } else {
                p.sendMessage("§6----------------------------------------------------");
                p.sendMessage("");
                p.sendMessage("§8» §e/party invite <player> §8- §aInvite a player to your party.");
                p.sendMessage("§8» §e/party kick <player> §8- §aKick a player from your party.");
                p.sendMessage("§8» §e/party accept <player> §8- §aAccept a Party invite.");
                p.sendMessage("§8» §e/party disband §8- §aDisband your party.");
                p.sendMessage("");
                p.sendMessage("§6----------------------------------------------------");
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("invite")) {
                ProxiedPlayer invitedPlayer = ProxyServer.getInstance().getPlayer(args[1]);
                if(invitedPlayer == null) {
                    p.sendMessage("§cThis player is currently not online.");
                    return;
                }
                c.invitePlayer(p.getName(), invitedPlayer.getName());
            } else if(args[0].equalsIgnoreCase("kick")) {
                String kickedPlayer = args[1];
                p.sendMessage("§c§lThis command is still undergoing testing.");
            } else if(args[0].equalsIgnoreCase("accept")) {
                String leaderPlayer = args[1];
                if(!c.hasPendingRequest(p.getName())) {
                    p.sendMessage("§cYou don't have any pending requests.");
                } else {
                    c.createParty(leaderPlayer, p.getName());
                }
            }
        }
    }
}

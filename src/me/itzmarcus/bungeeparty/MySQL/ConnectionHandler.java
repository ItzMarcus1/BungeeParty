package me.itzmarcus.bungeeparty.MySQL;

import me.itzmarcus.bungeeparty.Core;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marcus on 18-08-2016.
 */
public class ConnectionHandler {

    Core plugin;
    public ConnectionHandler(Core instance) {
        plugin = instance;
    }

    public static Connection connection;
    private String connect;
    private String host;
    private String port;
    private String database;
    private String table;
    private String user;
    private String password;

    public HashMap<String, String> invitations = new HashMap<>();
    public int maxPartyMembers = 4;

    public void openConnection() {
        try{
            File file = new File(plugin.getDataFolder().getPath(), "database.yml");
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            host = config.getString("ip");
            port = "3306";
            database = config.getString("database");
            user = config.getString("user");
            password = config.getString("password");
            connect = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&" + "user=" + user + "&password=" + password;
            connection = DriverManager.getConnection(connect);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try{
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isPartyLeader(String leaderName) {
        openConnection();
        try{
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `party` WHERE leader=?;");
            statement.setString(1, leaderName);
            ResultSet result = statement.executeQuery();

            boolean value = result.next();
            result.close();
            statement.close();
            return value;
        }catch(Exception e){
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean isPartyMember(String name){
        openConnection();
        try{

            for(int i = 1; i <= 3; i++){
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `party` WHERE member_"+i+"=?;");
                statement.setString(1, name);

                ResultSet result = statement.executeQuery();

                if(result.next()){
                    result.close();
                    return true;
                }
                result.close();
                statement.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            closeConnection();
        }

        return false;
    }

    public int getTotalPartyMembersCount(String leader) {
        openConnection();
        try {
            ArrayList<String> members = new ArrayList<>();
            for(int i = 1; i <= maxPartyMembers; i++){
                PreparedStatement statement = connection.prepareStatement("SELECT member_"+i+" FROM `party` WHERE leader=?;");
                statement.setString(1, leader);
                ResultSet result = statement.executeQuery();

                if(result.next()){
                    String value = result.getString("member_"+i);
                    if(value != null){
                        if(!value.equalsIgnoreCase("null")){
                            if(!members.contains(value)){
                                members.add(value);
                            }
                        }
                    }
                }
            }
            return members.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void disbandParty(String player) {
        openConnection();
        try {
            ProxyServer.getInstance().getPlayer(player).sendMessage("§aYou have disbanded your party.");
            if(invitations.containsKey(player)) {
                invitations.remove(player);
            }
            PreparedStatement sql = connection.prepareStatement("DELETE FROM `party` WHERE leader=?");
            sql.setString(1, player);
            sql.executeUpdate();

            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void removeMember(String leader, String member) {
        openConnection();
        try {
            // TODO
        } catch (Exception e) {
            e.printStackTrace();
            ProxyServer.getInstance().getPlayer(leader).sendMessage("§c§lSomething went wrong. Contact an administrator.");
        } finally {
            closeConnection();
        }
    }

    public void addMember(String leader, String member) {
        openConnection();
        try {
            int memberCount = getTotalPartyMembersCount(leader);
            if(memberCount == 0) {
                PreparedStatement sql = connection.prepareStatement("UPDATE `party` SET `member_1`=? WHERE `leader`=?");
                sql.setString(1, member);
                sql.setString(2, leader);

                sql.executeUpdate();

                sql.close();
            } else if(memberCount == 1) {
                PreparedStatement sql = connection.prepareStatement("UPDATE `party` SET `member_2`=? WHERE `leader`=?");
                sql.setString(1, member);
                sql.setString(2, leader);

                sql.executeUpdate();

                sql.close();
            } else if(memberCount == 2) {
                PreparedStatement sql = connection.prepareStatement("UPDATE `party` SET `member_3`=? WHERE `leader`=?");
                sql.setString(1, member);
                sql.setString(2, leader);

                sql.executeUpdate();

                sql.close();
            } else if(memberCount == 3) {
                PreparedStatement sql = connection.prepareStatement("UPDATE `party` SET `member_4`=? WHERE `leader`=?");
                sql.setString(1, member);
                sql.setString(2, leader);

                sql.executeUpdate();

                sql.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void invitePlayer(String inviter, String invited) {
        if(inviter.equals(invited)) {
            ProxyServer.getInstance().getPlayer(inviter).sendMessage("§cYou cannot invite yourself to a party!");
            return;
        }
        if(getTotalPartyMembersCount(inviter) >= 4) {
            ProxyServer.getInstance().getPlayer(inviter).sendMessage("§cYour party is currently full.");
            return;
        }
        if(invitations.containsKey(inviter) && invitations.get(inviter).equalsIgnoreCase(invited)) {
            ProxyServer.getInstance().getPlayer(inviter).sendMessage("§cYou have already invited this player.");
        } else {
            invitations.put(inviter, invited);
            invitations.put(invited, inviter);

            ProxyServer.getInstance().getPlayer(inviter).sendMessage("§aYou have invited " + invited + " to your party.");
            ProxyServer.getInstance().getPlayer(invited).sendMessage("§aYou have been invited to " + inviter + "'s party. Do /party accept " + inviter + ".");
        }
    }

    public void acceptInvite(String player) {
        if(hasPendingRequest(player)) {
            addMember(getPendingRequest(player), player);
            ProxyServer.getInstance().getPlayer(getPendingRequest(player)).sendMessage("§a" + player + " has joined your party.");
            ProxyServer.getInstance().getPlayer(player).sendMessage("§aYou have joined " + getPendingRequest(player) + "'s party.");

            invitations.remove(player);
            invitations.remove(getPendingRequest(player));
        }
    }

    public String getPendingRequest(String player) {
        return invitations.get(player);
    }

    public boolean hasPendingRequest(String player) {
        if(invitations.containsKey(player)) {
            return true;
        } else {
            return false;
        }
    }

    public void createParty(String leader, String invitedPlayer) {
        if(!isPartyLeader(leader) && !isPartyMember(leader)) {
            openConnection();
            try {
                PreparedStatement sql = connection.prepareStatement("INSERT INTO `party` values(?,?,?,?,?)");
                sql.setString(1, leader);
                sql.setString(2, invitedPlayer);
                sql.setString(3, "null");
                sql.setString(4, "null");
                sql.setString(5, "null");
                sql.execute();
                sql.close();

                ProxyServer.getInstance().getPlayer(getPendingRequest(leader)).sendMessage("§a" + leader + " has joined your party.");
                ProxyServer.getInstance().getPlayer(invitedPlayer).sendMessage("§aYou have joined " + invitedPlayer + "'s party.");

                invitations.remove(leader);
                invitations.remove(invitedPlayer);
            } catch (Exception e) {
                e.printStackTrace();
                ProxyServer.getInstance().getPlayer(leader).sendMessage("§c§lSomething went wrong. Contact an administrator.");
            } finally {
                closeConnection();
            }
        } else {
            ProxyServer.getInstance().getPlayer(leader).sendMessage("§cYou are the owner/member of another party.");
        }
    }

}

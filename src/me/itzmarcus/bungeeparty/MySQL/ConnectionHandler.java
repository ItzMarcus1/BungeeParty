package me.itzmarcus.bungeeparty.MySQL;

import net.md_5.bungee.api.ProxyServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by marcus on 18-08-2016.
 */
public class ConnectionHandler {

    public static Connection connection;
    private String connect;
    private String host;
    private String port;
    private String database;
    private String table;
    private String user;
    private String password;

    public void openConnection() {
        try{
            host = "94.23.12.84";
            port = "3306";
            database = "BungeeParty";
            user = "ItzMarcus";
            password = "marcus2001";
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

    public boolean playerHasParty(String player) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `data` WHERE leader=?");
            sql.setString(1, player);
            ResultSet set = sql.executeQuery();
            boolean status = set.next();

            sql.close();
            set.close();

            ProxyServer.getInstance().getPlayer(player).sendMessage("§cYou are the owner of another party.");
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            ProxyServer.getInstance().getPlayer(player).sendMessage("§c§lSomething went wrong. Contact an administrator.");
            return false;
        } finally {
            closeConnection();
        }
    }

    public boolean playerHasPartyMemberID(String player, String id) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `data` WHERE member_" + id + "=?");
            sql.setString(1, player);
            ResultSet set = sql.executeQuery();
            boolean status = set.next();

            sql.close();
            set.close();

            ProxyServer.getInstance().getPlayer(player).sendMessage("§cYou already have a party.");
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            ProxyServer.getInstance().getPlayer(player).sendMessage("§c§lSomething went wrong. Contact an administrator.");
            return false;
        } finally {
            closeConnection();
        }
    }

    public String getMember(String player, String id) {
        openConnection();
        try {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM `data` WHERE leader=?");
            sql.setString(1, player);
            ResultSet set = sql.executeQuery();
            String name = String.valueOf(set.next());

            sql.close();
            set.close();

            return name;
        } catch (Exception e) {
            e.printStackTrace();
            return "none";
        } finally {
            closeConnection();
        }
    }

    public int getTotalMembers(String leader) {
            int memberCount = 0;
            int id = 1;
            for(int i = 0; i < 4; i++) {
                if(!getMember(leader, String.valueOf(id)).equalsIgnoreCase("none")) {
                    memberCount++;
                }
                id++;
            }
        return memberCount;
    }

    public void createParty(String leader) {
        if(!playerHasParty(leader) && !playerHasPartyMemberID(leader, "1") && !playerHasPartyMemberID(leader, "2") && !playerHasPartyMemberID(leader, "3") && !playerHasPartyMemberID(leader, "4")) {
            openConnection();
            try {
                PreparedStatement sql = connection.prepareStatement("INSERT INTO `data` values(?,?,?,?,?)");
                sql.setString(1, leader);
                sql.setString(2, "test");
                sql.setString(3, "marcus");
                sql.setString(4, "none");
                sql.setString(5, "none");
                sql.execute();
                sql.close();

                ProxyServer.getInstance().getPlayer(leader).sendMessage("§aYou have created a party.");
            } catch (Exception e) {
                e.printStackTrace();
                ProxyServer.getInstance().getPlayer(leader).sendMessage("§c§lSomething went wrong. Contact an administrator.");
            } finally {
                closeConnection();
            }
        }
    }

}

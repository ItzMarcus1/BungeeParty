package me.itzmarcus.bungeeparty.MySQL;

import java.sql.Connection;
import java.sql.DriverManager;

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
            host = "ip-address";
            port = "3306";
            database = "name";
            user = "name";
            password = "password";
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

}

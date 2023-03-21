package mechanic;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Database {
    private Connection connection = null;
    private final String host = "jdbc:mySQL://localhost:3306/drivedb";
    private final String admin = "root";
    private final String password = "root123";

    public Database() {

    }

    public String[] getFileList(String username) {
        String[] fileList = null;
        try {
            String query = "SELECT filename from owning WHERE username=\"" + username + "\"";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ArrayList<String> tmp = new ArrayList<String>();
            while (resultSet.next()) {
                tmp.add(resultSet.getString("filename"));
            }
            if (tmp.size() > 0) {
                fileList = tmp.toArray(new String[tmp.size()]);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public void addFile(String user, String filename, long fileSize) {
        try {
            String query = "INSERT INTO files(filename, filesize)" +
                    " VALUES(\"" + filename + "\"," + fileSize + ")";
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            if (result > 0) {
                updateRelation(user, filename);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRelation(String username, String filename) {
        try {
            String query = "INSERT INTO owning(username, filename)" +
                    " VALUES(\"" + username +"\",\"" + filename + "\")";
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(host, admin, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.connection;
    }

    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

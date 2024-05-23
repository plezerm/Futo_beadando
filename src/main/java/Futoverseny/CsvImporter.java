package Futoverseny;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CsvImporter {

    public static String jdbcUrl = "jdbc:h2:~/IdeaProjects/database";
    public static String jdbcUsername = "sa";
    public static String jdbcPassword = "";
    public static Charset ekezet = Charset.forName("windows-1252");

    public static void main() {
    }

    public static void letsLoadIt() {
        String projectDir = System.getProperty("user.dir");
        projectDir += "/src/main/resources/csv/";
        //a versenyek és a futók internetes táblázatokból származnak,
        //az eredmények pedig véletlengenerátorral készültek
        String runnerPath = projectDir+"runners.csv";
        String compPath = projectDir+"competitions.csv";
        String resPath = projectDir+"results.csv";

        CsvImporter firstFile = new CsvImporter();
        firstFile.readRunnersFromFile(runnerPath);
        firstFile.readCompsFromFile(compPath);
        firstFile.readResultsFromFile(resPath);
    }

    public void readRunnersFromFile (String csvFile) {

        //ha létezik a tábla az adatbázisban, false jön vissza, hogy ne kerüljenek be duplán az adatok
        if (CreateTable("runnerdb")) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
                 BufferedReader br = new BufferedReader(new FileReader(csvFile,ekezet))) {

                String line;
                String sql = "INSERT INTO runnerdb (name, age, gender) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    String name = data[0];
                    int age = Integer.parseInt(data[1]);
                    int gender = Integer.parseInt(data[2]);
                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, age);
                    preparedStatement.setInt(3, gender);
                    preparedStatement.executeUpdate();
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void readCompsFromFile (String csvFile) {

        if (CreateTable("competitiondb")) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
                 BufferedReader br = new BufferedReader(new FileReader(csvFile,ekezet))) {

                String line;
                String sql = "INSERT INTO competitiondb (name, length) VALUES (?, ? )";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    String name = data[0];
                    int length = Integer.parseInt(data[1]);

                    preparedStatement.setString(1, name);
                    preparedStatement.setInt(2, length);
                    preparedStatement.executeUpdate();
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void readResultsFromFile (String csvFile) {

        if (CreateTable("resultdb")) {
            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
                 BufferedReader br = new BufferedReader(new FileReader(csvFile,ekezet))) {

                String line;
                String sql = "INSERT INTO resultdb (runner, competition, time) VALUES (?, ?, ? )";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    int runner = Integer.parseInt(data[0]);
                    int competition = Integer.parseInt(data[1]);
                    long time = Long.parseLong(data[2]);

                    preparedStatement.setInt(1, runner);
                    preparedStatement.setInt(2, competition);
                    preparedStatement.setLong(3, time);
                    preparedStatement.executeUpdate();
                }

            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    boolean CreateTable (String dataBaseName){

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
             Statement statement = connection.createStatement()) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, dataBaseName.toUpperCase(), new String[] {"TABLE"});
            if (!resultSet.next()) {
                String sql = "";
                switch (dataBaseName) {
                    case "runnerdb" -> sql = "CREATE TABLE runnerdb ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "name VARCHAR(255),"
                            + "age INT,"
                            + "gender INT)";
                    case "competitiondb" -> sql = "CREATE TABLE competitiondb ("
                            + "id INT AUTO_INCREMENT PRIMARY KEY,"
                            + "competitionName VARCHAR(255),"
                            + "length INT)";
                    case "resultdb" -> sql = "CREATE TABLE resultdb ("
                            + "runner INT, competition INT, time LONG,"
                            + "FOREIGN KEY (runner) REFERENCES runnerdb(id),"
                            + "FOREIGN KEY (competition) REFERENCES competitiondb(id))";
                }
                statement.executeUpdate(sql);
                return true;
            } else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}

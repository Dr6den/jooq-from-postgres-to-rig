package com.dvsts.migration;

import org.jooq.DSLContext;
import org.jooq.Record;
import static org.jooq.impl.DSL.field;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.Field;
import org.jooq.Table;
//import org.jooq.example.db.postgres.tables.Cdc;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;
import org.jooq.impl.SQLDataType;



/**
 *
 * @author andrew
 */
public class MigrationRunner {
    public static void main(String[] args0) {
        String dbFromDriver = System.getProperty("db.from.driver");
        String dbFromUrl = System.getProperty("db.from.url");
        String dbFromUsername = System.getProperty("db.from.username");
        String dbFromPassword = System.getProperty("db.from.password");
        String dbFromSchema = System.getProperty("db.from.schema");

        String dbToDriver = System.getProperty("db.to.driver");
        String dbToUrl = System.getProperty("db.to.url");
        String dbToUsername = System.getProperty("db.to.username");
        String dbToPassword = System.getProperty("db.to.password");
        String dbToSchema = System.getProperty("db.to.schema");

        if (dbFromDriver == null) {
            Properties prop = new Properties();
            InputStream input = null;

            try {
                input = new FileInputStream("./config.properties");
                prop.load(input);

                dbFromDriver = prop.getProperty("db.from.driver");
                dbFromUrl = prop.getProperty("db.from.url");
                dbFromUsername = prop.getProperty("db.from.username");
                dbFromPassword = prop.getProperty("db.from.password");
                dbFromSchema = prop.getProperty("db.from.schema");

                dbToDriver = prop.getProperty("db.to.driver");
                dbToUrl = prop.getProperty("db.to.url");
                dbToUsername = prop.getProperty("db.to.username");
                dbToPassword = prop.getProperty("db.to.password");
                dbToSchema = prop.getProperty("db.to.schema");
            } catch (IOException ex) {System.out.println("please put config.properties file to the same folder where jar lies..."+ex.getMessage());
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

            String cdc = "cdc";
            String alreadyFetched = "already_fetched";
            String activeDirectoryUser = "active_directory_user";
            String eventAcknowledge = "event_acknowledge";
            String ciscoCmsCall = "cisco_cms_call";
            String ciscoCmsCallLeg = "cisco_cms_call_leg";
            String ciscoCdrCall = "cisco_cdr_call";
            String ciscoCdrCallLeg = "cisco_cdr_call_leg";
            String ciscoCmsCallParticipantDetails = "cisco_cms_call_participant_details";
            String failedBatches = "failed_batches";
            String lateInserts = "late_inserts";
            String lateInsertsValues = "late_inserts_values";

            String cdcTableData = "";
            String alreadyFetchedCsv = "";
            String activeDirectoryUserCsv = "";
            String eventAcknowledgeCsv = "";
            String ciscoCmsCallCsv = "";
            String ciscoCmsCallLegCsv = "";
            String ciscoCdrCallCsv = "";
            String ciscoCdrCallLegCsv = "";
            String ciscoCmsCallParticipantDetailsCsv = "";
            String failedBatchesCsv = "";
            String lateInsertsCsv = "";
            String lateInsertsValuesCsv = "";

        try {
            Class.forName(dbFromDriver).newInstance();
            try (Connection connection = DriverManager.getConnection(dbFromUrl, dbFromUsername, dbFromPassword)) {
                DSLContext source = DSL.using(connection, SQLDialect.POSTGRES);

                cdcTableData = getCsvDataFromSinglePostgresTable(cdc, connection);
                alreadyFetchedCsv = getCsvDataFromSinglePostgresTable(alreadyFetched, connection);
                activeDirectoryUserCsv = getCsvDataFromSinglePostgresTable(activeDirectoryUser, connection);
                eventAcknowledgeCsv = getCsvDataFromSinglePostgresTable(eventAcknowledge, connection);
                ciscoCmsCallCsv = getCsvDataFromSinglePostgresTable(ciscoCmsCall, connection);
                ciscoCmsCallLegCsv = getCsvDataFromSinglePostgresTable(ciscoCmsCallLeg, connection);
                ciscoCdrCallCsv = getCsvDataFromSinglePostgresTable(ciscoCdrCall, connection);
                ciscoCdrCallLegCsv = getCsvDataFromSinglePostgresTable(ciscoCdrCallLeg, connection);
                ciscoCmsCallParticipantDetailsCsv = getCsvDataFromSinglePostgresTable(ciscoCmsCallParticipantDetails, connection);
                failedBatchesCsv = getCsvDataFromSinglePostgresTable(failedBatches, connection);
                lateInsertsCsv = getCsvDataFromSinglePostgresTable(lateInserts, connection);
                lateInsertsValuesCsv = getCsvDataFromSinglePostgresTable(lateInsertsValues, connection);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            Class.forName(dbToDriver).newInstance();
            try (Connection derbyconnection = DriverManager.getConnection(dbToUrl, dbToUsername, dbToPassword)) {
                DSLContext target = DSL.using(derbyconnection, SQLDialect.DERBY);
                target.setSchema(dbToSchema).execute();

                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, cdc, cdcTableData);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, alreadyFetched, alreadyFetchedCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, activeDirectoryUser, activeDirectoryUserCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, eventAcknowledge, eventAcknowledgeCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, ciscoCmsCall, ciscoCmsCallCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, ciscoCmsCallLeg, ciscoCmsCallLegCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, ciscoCdrCall, ciscoCdrCallCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, ciscoCdrCallLeg, ciscoCdrCallLegCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, ciscoCmsCallParticipantDetails, ciscoCmsCallParticipantDetailsCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, failedBatches, failedBatchesCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, lateInserts, lateInsertsCsv);
                deleteAldAndInsertNewDataToTheTable(target, derbyconnection, lateInsertsValues, lateInsertsValuesCsv);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(MigrationRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void deleteAldAndInsertNewDataToTheTable(DSLContext target, Connection connection, String nameOfTable, String csvData) throws IOException {
        target.deleteFrom(table(name(nameOfTable))).execute();
        target.loadInto(table(name(nameOfTable))).loadCSV(csvData).fields(getFields(connection, nameOfTable)).execute();
    }

    private static String getCsvDataFromSinglePostgresTable(String tableName, Connection conn) {
        DSLContext source = DSL.using(conn, SQLDialect.POSTGRES);
        return source.select().from(tableName).fetch().formatCSV();
    }
   
    private static List<Field<String>> getFields(Connection conn, String tableName) {
        DSLContext source = DSL.using(conn, SQLDialect.DERBY);
        Field[] fields = source.select().from("\""+tableName+"\"").fetch().fields();
        return Arrays.asList(fields);
    }
}

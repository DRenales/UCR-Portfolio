package SparkWorks;

//Apache Spark Includes
import org.apache.spark.sql.*;

//Java Includes
import java.io.IOException;
import java.util.Scanner;

public class SparkMainApp {
    /*
        Function: clearScreen
        Author: Dominic Renales
        Editors:
        Input: None
        Output: None
        Summary: Read the function name...like damn son...
    */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void greeting()
     * Purpose  -> Method to print a greeting to the console menu.
     *             Purely aesthetic.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static void greeting(){
        System.out.println(
                "\n\n**************************************************\n" +
                        "             COVID-19 Data Analysis                   \n" +
                        "**************************************************\n");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> void mainMenu(SparkSession sparkSession)
     * Purpose  -> Method to initialize the main menu.
     *             Allows the user to choose which dataset to run queries on.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static void mainMenu(SparkSession sparkSession) throws Exception {
        Scanner input = new Scanner(System.in);
        int choice;

        clearScreen();
        greeting();
        System.out.println("Please choose a dataset you would like to run\n" +
                "0. EXIT\n" +
                "1. USA\n" +
                "2. GLOBAL\n");

        while((choice = input.nextInt()) != 0) {
            switch (choice) {
                case 1:
                    System.out.println("\nAccessing USA Database...\n");
                    System.out.println("Please wait while the data is pre-processed...\n\n");
                    queryUSA(sparkSession);
                    break;
                case 2:
                    System.out.println("\nAccessing Global Database...\n");
                    System.out.println("Please wait while the data is pre-processed...\n\n");
                    queryGlobal(sparkSession);
                    break;
                default:
                    System.out.println("Invalid Input");
                    mainMenu(sparkSession);
            }

            clearScreen();
            greeting();
            System.out.println("Please choose a dataset you would like to run\n" +
                    "0. EXIT\n" +
                    "1. USA\n" +
                    "2. GLOBAL\n");
        }
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> void mainMenu(SparkSession sparkSession)
     * Purpose  -> Method to permit queries to run on the USA.csv dataset.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static void queryUSA(SparkSession sparkSession) throws Exception {
        USA_Queries db = new USA_Queries("hdfs://localhost:9000/COVID19/USA.csv", sparkSession);
        Scanner input = new Scanner(System.in);
        int choice;

        clearScreen();
        greeting();
        System.out.println("0. None, Go Back\n" +
                "1. Number of Case Outcomes by State\n" +
                "2. Number of Total Tests Administered\n" +
                "3. Number of Specified Tests by Date Range\n" +
                "4. Total Results Reported Filtered by State and Quarter of the Year\n" +
                "5. Top 'K' Results Reported by State\n" +
                "6. Total Number of Cases by Date Range\n" +
                "7. Total Number of New Cases by Date Range\n" +
                "8. List Total Quarterly New Results Reported by Case Outcome\n" +
                "9. List Total Quarterly Positive Cases by State\n" +
                "10. COVID-19 Recent Statistics by Specific State\n");

        while((choice = input.nextInt()) != 0) {
            switch (choice) {
                case 1: db.getNumOfSpecifiedOutcomesByState(); break;
                case 2: db.getNumOfTestsAdministeredByState(); break;
                case 3: db.getTotalNumOfSpecifiedCasesByDateRange(); break;
                case 4: db.getNumOfSpecifiedOutcomesByQuarterOfYear(); break;
                case 5: db.topKResultsReportedByState(); break;
                case 6: db.getTotalNumOfCasesByDateRange(); break;
                case 7: db.getTotalNumOfNewCasesByDateRange(); break;
                case 8: db.listTotalQuarterlyReportsByCase(); break;
                case 9: db.listTotalQuarterlyReportsByState(); break;
                case 10: db.recentEvents(); break;
                default: System.out.println("Invalid Input");
            }
            greeting();
            System.out.println("0. None, Go Back\n" +
                    "1. Number of Case Outcomes by State\n" +
                    "2. Number of Total Tests Administered\n" +
                    "3. Number of Specified Tests by Date Range\n" +
                    "4. Total Results Reported Filtered by State and Quarter of the Year\n" +
                    "5. Top 'K' Results Reported by State\n" +
                    "6. Total Number of Cases by Date Range\n" +
                    "7. Total Number of New Cases by Date Range\n" +
                    "8. List Total Quarterly New Results Reported by Case Outcome\n" +
                    "9. List Total Quarterly Positive Cases by State\n" +
                    "10. COVID-19 Recent Statistics by Specific State\n");
        }
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void queryGlobal(SparkSession sparkSession)
     * Purpose  -> Method to permit queries to run on the Global.csv dataset.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static void queryGlobal(SparkSession sparkSession) throws Exception {
        Global_Queries db = new Global_Queries("hdfs://localhost:9000/COVID19/Global.csv", sparkSession);
        Scanner input = new Scanner(System.in);
        int choice;

        clearScreen();
        greeting();
        System.out.println("0. None, Go Back\n" +
                "1. Number of Tests Administered by Country\n" +
                "2. Number of Tests Administered by Continent\n" +
                "3. Number of Total Cases by Country\n" +
                "4. Number of Total Cases by Continent\n" +
                "5. Number of Total Cases Globally\n" +
                "6. Average Life Expectancy Once Tested Positive\n" +
                "7. Average Amount of New Cases by Country\n" +
                "8. Most Recent Deaths by Country\n" +
                "9. Top 'K' Countries by Total Cases on a Specific Date\n" +
                "10. Top 'K' Countries by Total Deaths on a Specific Date\n" +
                "11. Top 'K' Hospitalized Patients In Europe Based on Total Cases\n" +
                "12. Top 'K' ICU Patients In Europe Based on Total Cases\n" +
                "13. Total Number of Positive Cases Per Month\n" +
                "14. Which Month Saw the Greatest Number of Cases?\n" +
                "15. Total Cases by Country Per Quarter\n" +
                "16. Total Cases by Continent Per Quarter\n");

        while((choice = input.nextInt()) != 0) {
            switch (choice) {

                case 1: db.getNumOfTestsAdministeredByCountry(); break;
                case 2: db.getNumOfTestsAdministeredByContinent(); break;
                case 3: db.getMaxNumOfCasesByCountry(); break;
                case 4: db.getMaxNumOfCasesByContinent(); break;
                case 5: db.getMaxNumOfCasesGlobally(); break;
                case 6: db.getAvgLifeExpectancy(); break;
                case 7: db.getAvgNewCases(); break;
                case 8: db.getLatestCasesDeaths(); break;
                case 9: db.topKTotalCasesReportedByCountry(); break;
                case 10: db.topKDeathsReportedByCountry(); break;
                case 11: db.listTopKHospitalizedPatientDataInEurope(); break;
                case 12: db.listTopKICUPatientDataInEurope(); break;
                case 13: db.totalNumberOfPositiveCasesPerMonth(); break;
                case 14: db.monthNumWithGreatestNumberOfCases(); break;
                case 15: db.getCountryTotalCasesByQuarterOfYear(); break;
                case 16: db.getContinentTotalCasesByQuarterOfYear(); break;

                default: System.out.println("Invalid Input");
            }
            greeting();
            System.out.println("0. None, Go Back\n" +
                    "1. Number of Tests Administered by Country\n" +
                    "2. Number of Tests Administered by Continent\n" +
                    "3. Number of Total Cases by Country\n" +
                    "4. Number of Total Cases by Continent\n" +
                    "5. Number of Total Cases Globally\n" +
                    "6. Average Life Expectancy Once Tested Positive\n" +
                    "7. Average Amount of New Cases by Country\n" +
                    "8. Most Recent Deaths by Country\n" +
                    "9. Top 'K' Countries by Total Cases on a Specific Date\n" +
                    "10. Top 'K' Countries by Total Deaths on a Specific Date\n" +
                    "11. Top 'K' Hospitalized Patients In Europe Based on Total Cases\n" +
                    "12. Top 'K' ICU Patients In Europe Based on Total Cases\n" +
                    "13. Total Number of Positive Cases Per Month\n" +
                    "14. Which Month Saw the Greatest Number of Cases?\n" +
                    "15. Total Cases by Country Per Quarter\n" +
                    "16. Total Cases by Continent Per Quarter\n");
        }
    } // ---------------------------------------------------------------------

    /* MAIN TEST HARNESS */
    public static void main(String[] args) throws Exception {
        SparkSession sparkSession = SparkSession
                .builder()
                .appName("CSV Test App")
                .getOrCreate();
        sparkSession.sparkContext().setLogLevel("ERROR");

        recentStatistics(sparkSession);
        mainMenu(sparkSession);

        System.out.println("Session Shutting Down");
    } // ---------------------------------------------------------------------

    public static void recentStatistics(SparkSession sparkSession) throws Exception{
        Dataset<Row> USA = sparkSession
                .read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load("hdfs://localhost:9000/COVID19/USA.csv"),
                GLOBAL = sparkSession
                        .read()
                        .format("csv")
                        .option("header", "true")
                        .option("inferSchema", "true")
                        .load("hdfs://localhost:9000/COVID19/Global.csv");

        USA.createOrReplaceTempView("USA");
        GLOBAL.createOrReplaceTempView("Global");


        clearScreen();
        greeting();

        System.out.println("Top State of Recorded Positive Cases for the US:");
        sparkSession.sql("SELECT state_name, total_results_reported FROM USA WHERE '" + "2020-10-03" + "' = date " +
                "and overall_outcome = 'Positive' ORDER BY total_results_reported DESC LIMIT 1;").show(false);

        System.out.println("\nTop State of New Reported Results Last Month:");
        sparkSession.sql("SELECT state_name, SUM(new_results_reported) AS total_new FROM USA " +
                "WHERE overall_outcome = 'Positive' AND date >= '2020-09-01' AND date <= '2020-09-30' GROUP BY state_name ORDER BY total_new DESC LIMIT 1;").show(false);

        System.out.println("\nTop State of Tests Administered Last Month:");
        sparkSession.sql("SELECT state_name, SUM(new_results_reported) AS total_new FROM USA " +
                "WHERE date >= '2020-09-01' AND date <= '2020-09-30' GROUP BY state_name ORDER BY total_new DESC LIMIT 1;").show(false);

        System.out.println("\nTotal Deaths Globally:");
        sparkSession.sql("SELECT CAST(MAX(total_deaths) AS BIGINT) AS total_deaths " +
                "FROM Global " +
                "WHERE location = 'World';").show(false);

        System.out.println("\nTotal Number of Cases in the United States Last Month:");
        sparkSession.sql("SELECT CAST(SUM(new_cases) AS BIGINT) AS total_cases " +
                "FROM Global " +
                "WHERE location = 'United States' " +
                "AND date >= '2020-11-01' AND date <= '2020-11-31';").show(false);

        System.out.println("\nTop Country with the Most Cases Last Month:");
        sparkSession.sql("SELECT country, total_cases " +
                "FROM (SELECT location AS Country, CAST(SUM(new_cases) AS BIGINT) AS total_cases " +
                "FROM Global " +
                "WHERE date >= '2020-11-01' AND date <= '2020-11-31' " +
                "AND location != 'World' " +
                "GROUP BY location) " +
                "ORDER BY total_cases DESC LIMIT 1;").show(false);

        waitUntilEnter();
    }

    public static void waitUntilEnter() throws IOException {
        System.out.print("Press Enter to Continue . . .\n\n");
        int enter = System.in.read();
        while(enter != 10){
            enter = System.in.read();
        }
    }

    public static void theWholeShebang() throws Exception {
        waitUntilEnter();
        clearScreen();
    }
} // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
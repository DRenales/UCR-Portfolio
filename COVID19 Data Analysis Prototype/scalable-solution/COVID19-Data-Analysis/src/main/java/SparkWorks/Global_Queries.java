/* Local Package */
package SparkWorks;

/* Apache Spark */
import org.apache.spark.sql.*;

/* Java Libraries */
import java.io.*;
import java.io.BufferedReader;
import java.util.Scanner;

/* Local Class Definition */
public class Global_Queries {
    private static Dataset<Row> df;
    private static SparkSession sparkSession;
    private static Scanner input = new Scanner(System.in);
    private static SparkMainApp sma = new SparkMainApp();

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy, Gerardo Castro
     * Method   -> Global_Queries(String filepath, SparkSession sparksession)
     * Purpose  -> Constructor to initialize private data for the dataframe
     *             and sparkSession respectively.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> int choice
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public Global_Queries(String filepath, SparkSession sparksession) {
        this.sparkSession = sparksession;
        this.df = sparkSession
                .read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(filepath);
        df.createOrReplaceTempView("GLOBAL");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String getCase()
     * Purpose  -> Helper method to prompt the user for a specified case
     *             result. (Positive/Negative/Inconclusive/All)
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> int choice
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static String getCase() {
        Scanner input = new Scanner(System.in);
        System.out.print("Which result would you like to view ([P]ositive/[N]egative/[I]nconclusive)/[A]ll): ");
        String choice = input.nextLine();
        while (!verifyChoice(choice)) {
            System.out.println("Invalid Input");
            System.out.print("Which result would you like to view ([P]ositive/[N]egative/[I]nconclusive)/[A]ll): ");
            choice = input.nextLine();
        }

        return choice;
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> bool verifyChoice(String choice)
     * Purpose  -> Helper method to determine valid input choice.
     * -----------------------------------------------------------------------
     * Receives -> String choice
     * Returns  -> boolean
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static boolean verifyChoice(String choice) {
        switch(choice) {
            case "1":
            case "2":
                return true;
            default:
                return false;
        }
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> String getCountry()
     * Purpose  -> Helper method to determine valid input choice.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> String country
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static String getCountry() throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the desired country name: ");
        String country = input.nextLine();
        while (!verifyCountry(country.toUpperCase())) {
            System.out.println("Invalid Country Name.");
            System.out.print("Enter the desired country name: ");
            country = input.nextLine();
        }
        return country;
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String verifyCountry()
     * Purpose  -> Helper method to determine if the user input for a state
     *             name matches one of the valid state names in a .txt file.
     * -----------------------------------------------------------------------
     * Receives -> String countryName
     * Returns  -> bool
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static boolean verifyCountry(String countryName) throws Exception {
        String rootDir = System.getProperty("user.home"); // "dir => /root/file_name_here"
        File f = new File(rootDir + "/Country_Names.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String read;

        while ((read = br.readLine()) != null)
            if (read.toUpperCase().contains(countryName))
                return true;
        return false;
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> String getContinent()
     * Purpose  -> Method to grab continent user input.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> String
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static String getContinent() throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the desired continent name: ");
        String continent = input.nextLine();
        while (!verifyContinent(continent.toUpperCase())) {
            System.out.println("Invalid Continent Name.");
            System.out.print("Enter the desired continent name: ");
            continent = input.nextLine();
        }
        return continent;
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> String verifyContinent()
     * Purpose  -> Method to verify continent user input through checking the
     *             list of valid continents in a text file.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> String
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static boolean verifyContinent(String continentName) throws Exception {
        String rootDir = System.getProperty("user.home"); // "dir => /root/file_name_here"
        File f = new File(rootDir + "/Continent_Names.txt");
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String read;

        while ((read = br.readLine()) != null)
            if (read.toUpperCase().contains(continentName))
                return true;
        return false;
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> String reformatInput()
     * Purpose  -> Converts string to title case.
     *             united states -> United States
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> String
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static String reformatInput(String state) {
        Scanner scan = new Scanner(state);
        String upperCase = "";
        if (state.length() == 1 || state.length() == 2) return state.toUpperCase();
        while(scan.hasNext()){
            String fix = scan.next();
            upperCase += Character.toUpperCase(fix.charAt(0))+ fix.substring(1) + " ";
        }
        return(upperCase.trim());
    } // ---------------------------------------------------------------------

    /*
        Function: getQuarter
        Author: Dominic Renales
        Editors:
        Input: None
        Output: int
        Summary: Prompts the user to enter a number between one and four.
    */
    private static int getQuarter() {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter a quarter of the year you wish to evaluate (1-4): ");
        int quarter = input.nextInt();

        while (quarter < 1 || quarter > 4) {
            System.out.println("Invalid Value");
            System.out.print("Enter a quarter of the year you wish to evaluate: ");
            quarter = input.nextInt();
        }

        return quarter;
    }

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String firstQuarterCountry()
     * Purpose  -> Helper method to return the first quarter of a country.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> firstQuarterCountry(String country) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-01-01' <= date AND '2020-03-31' >= date " +
                "AND location = '" + country + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String secondQuarterCountry()
     * Purpose  -> Helper method to return the second quarter of a country.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> secondQuarterCountry(String country) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-04-01' <= date AND '2020-06-30' >= date " +
                "AND location = '" + country + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String thirdQuarterCountry()
     * Purpose  -> Helper method to return the third quarter of a country.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> thirdQuarterCountry(String country) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-07-01' <= date AND '2020-09-30' >= date " +
                "AND location = '" + country + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String fourthQuarterCountry()
     * Purpose  -> Helper method to return the fourth quarter of a country.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> fourthQuarterCountry(String country) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-10-01' <= date AND '2020-12-31' >= date " +
                "AND location = '" + country + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String firstQuarterContinent()
     * Purpose  -> Helper method to return the first quarter of a continent.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> firstQuarterContinent(String continent) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-01-01' <= date AND '2020-03-31' >= date " +
                "AND continent = '" + continent + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String secondQuarterContinent()
     * Purpose  -> Helper method to return the second quarter of a continent.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> secondQuarterContinent(String continent) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-04-01' <= date AND '2020-06-30' >= date " +
                "AND continent = '" + continent + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String thirdQuarterContinent()
     * Purpose  -> Helper method to return the third quarter of a continent.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> thirdQuarterContinent(String continent) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-07-01' <= date AND '2020-09-30' >= date " +
                "AND continent = '" + continent + "';");
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> String fourthQuarterContinent()
     * Purpose  -> Helper method to return the fourth quarter of a continent.
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    public static Dataset<Row> fourthQuarterContinent(String continent) throws Exception {
        return sparkSession.sql("SELECT date, total_cases FROM Global " +
                "WHERE '2020-10-01' <= date AND '2020-12-31' >= date " +
                "AND continent = '" + continent + "';");
    } // ---------------------------------------------------------------------


    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getNumOfTestsAdministeredByCountry()
     * Purpose  -> Method to get the greatest number of positive, negative or
     *             inconclusive COVID-19 cases in a specific country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// */
    public static void getNumOfTestsAdministeredByContinent() throws Exception {
        String continent = getContinent();
        continent = reformatInput(continent);

        sparkSession.sql("SELECT MAX(total_tests) AS total_number_of_tests " +
                "FROM GLOBAL " +
                "WHERE continent = '" + continent + "';").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getNumOfTestsAdministeredByCountry()
     * Purpose  -> Method to get the greatest number of positive, negative or
     *             inconclusive COVID-19 cases in a specific country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// */
    public static void getNumOfTestsAdministeredByCountry() throws Exception {
        String country = getCountry();
        country = reformatInput(country);

        sparkSession.sql("SELECT MAX(total_tests) AS total_number_of_tests " +
                "FROM GLOBAL " + "WHERE location = '" + country + "';").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Gerardo Castro Mata
     * Method   -> void getLargestNumOfCasesByCountry()
     * Purpose  -> Method to get the greatest number of tests administered
     *			   by a specified country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// */
    public static void getMaxNumOfCasesByCountry() throws Exception {
        System.out.print("Enter the desired country name: ");
        String country = input.nextLine();
        while (!verifyCountry(country.toUpperCase())) {
            System.out.println("Invalid Country Name.");
            System.out.print("Enter the desired country name: ");
            country = input.nextLine();
        }

        country = reformatInput(country);

        sparkSession.sql("SELECT MAX(total_cases) AS total_number_of_cases, date " +
                "FROM GLOBAL " +
                "WHERE location = '" + country + "'" +
                " GROUP BY date " +
                " ORDER BY total_number_of_cases DESC LIMIT 1;").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getLargestNumOfCasesByContinent()
     * Purpose  -> Method to get the greatest number of tests administered
     *			   by a specified continent.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// */
    public static void getMaxNumOfCasesByContinent() throws Exception {
        System.out.print("Enter the desired continent name: ");
        String continent = input.nextLine();
        while (!verifyContinent(continent.toUpperCase())) {
            System.out.println("Invalid Continent Name.");
            System.out.print("Enter the desired continent name: ");
            continent = input.nextLine();
        }

        continent = reformatInput(continent);

        sparkSession.sql("SELECT MAX(total_cases) AS total_number_of_cases " +
                "FROM GLOBAL " +
                "WHERE continent = '" + continent + "';").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy, Gerardo Castro Mata
     * Method   -> void getMaxNumOfCasesGlobally()
     * Purpose  -> Method to get the greatest number of tests administered
     *			   globally by returning the max value for each country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// */
    public static void getMaxNumOfCasesGlobally() throws Exception {
        sparkSession.sql("SELECT SUM(max_number_of_tests) AS total_number_of_cases " +
                "FROM " +
                "(SELECT MAX(total_tests) as max_number_of_tests " +
                "FROM GLOBAL " +
                "GROUP BY location);").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Gerardo Castro Mata
     * Modifier -> Dan Murphy
     * Method   -> void getAvgLifeExpectancy()
     * Purpose  -> Method to get the average life expectancy of a country
     *			   based on the most recent date.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// */
    public static void getAvgLifeExpectancy() throws Exception {
        System.out.print("Enter the desired country name: ");
        String country = input.nextLine();
        while (!verifyCountry(country.toUpperCase())) {
            System.out.println("Invalid Country Name.");
            System.out.print("Enter the desired country name: ");
            country = input.nextLine();
        }

        country = reformatInput(country);

        sparkSession.sql("SELECT life_expectancy AS Average_Life_Expectancy " +
                "FROM GLOBAL " +
                "WHERE location = '" + country + "' " +
                "AND date = (SELECT date " +
                "FROM GLOBAL " +
                "GROUP BY date " +
                "ORDER BY date DESC LIMIT 1);").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Gerardo Castro Mata
     * Modifier -> Dan Murphy
     * Method   -> void getAvgNewCases()
     * Purpose  -> Method to get the average number of new cases for either a
     *			   specific country or all countries.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// */
    public static void getAvgNewCases() throws Exception {
        Scanner keyboard = new Scanner(System.in);
        System.out.print("View average new cases per:\n1. Specific country\n2. All countries\n");
        String choice = keyboard.nextLine();
        while (!verifyChoice(choice)) {
            System.out.println("Invalid Input");
            System.out.print("View average new cases per:\n1. Specific country\n2. All countries\n");
            choice = keyboard.nextLine();
        }
        if (choice.equals("1")) {
            System.out.print("Enter the desired country name: ");
            String country = keyboard.nextLine();
            while (!verifyCountry(country.toUpperCase())) {
                System.out.println("Invalid Country Name.");
                System.out.print("Enter the desired country name: ");
                country = keyboard.nextLine();
            }
            sparkSession.sql("SELECT AVG(new_cases) AS Average_New_Cases " +
                    "FROM GLOBAL " +
                    "WHERE location = '" + country + "';").show();
        } else if (choice.equals("2")){
            sparkSession.sql("SELECT location AS Country, AVG(new_cases) AS Average_New_Cases  " +
                    "FROM GLOBAL " +
                    "GROUP BY location " +
                    "ORDER BY Average_New_Cases DESC;").show(1000, false);
        }
        else{
            System.out.println("Invalid input.\nPlease try again.\n\n");
        }

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Gerardo Castro Mata
     * Modifier -> Dan Murphy
     * Method   -> void getLatestCasesDeaths()
     * Purpose  -> Method to return the latest cases and latest deaths given a
     *			   specific country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// */
    public static void getLatestCasesDeaths() throws Exception {
        System.out.print("Enter the desired country: ");
        String country = input.nextLine();

        while (!verifyCountry(country.toUpperCase())) {
            System.out.println("Invalid Country Name.");
            System.out.print("Enter the desired country: ");
            country = input.nextLine();
        }
        country = reformatInput(country);
        sparkSession.sql("SELECT location AS Country, new_cases AS Latest_Cases, total_deaths AS Latest_Deaths " +
                "FROM GLOBAL " +
                "WHERE location = '" + country + "' " +
                "AND date = (SELECT date " +
                "FROM GLOBAL " +
                "GROUP BY date " +
                "ORDER BY date DESC LIMIT 1);").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void topKTotalCasesReportedByCountry()
     * Purpose  -> Method to return the top K results given a case outcome,
     *			   specified date and the value for K countries to list.
     *             Filters by total_cases in descending order.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// */
    public static void topKTotalCasesReportedByCountry() throws Exception {
        Scanner input = new Scanner(System.in);
        String continent = getContinent();
        continent = reformatInput(continent);

        System.out.print("Enter a date (YYYY-MM-DD): ");
        String date = input.nextLine();

        System.out.print("Enter the list size you want to see: ");
        int K = input.nextInt();
        while (K < 1) {
            System.out.println("Invalid Input");
            K = input.nextInt();
        }
        sparkSession.sql("SELECT location, total_cases, new_cases, total_cases_per_million, new_cases_per_million " +
                "FROM Global " +
                "WHERE '" + date + "' = date AND continent = '" + continent +
                "' ORDER BY total_cases DESC;").show(K);

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void topKDeathsReportedByCountry()
     * Purpose  -> Method to return the top K results given a case outcome,
     *			   specified date and the value for K countries to list.
     *             Filters by total_deaths in descending order.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 10 /// OPTION 10 /// OPTION 10 /// OPTION 10 /// OPTION 10 /// */
    public static void topKDeathsReportedByCountry() throws Exception {
        Scanner input = new Scanner(System.in);
        String continent = getContinent();
        continent = reformatInput(continent);

        System.out.print("Enter a date (YYYY-MM-DD): ");
        String date = input.nextLine();

        System.out.print("Enter the list size you want to see: ");
        int K = input.nextInt();
        while (K < 1) {
            System.out.println("Invalid Input");
            K = input.nextInt();
        }
        sparkSession.sql("SELECT location, total_deaths, new_cases, total_cases_per_million, new_cases_per_million " +
                "FROM Global "  +
                "WHERE '" + date + "' = date AND continent = '" + continent +
                "' ORDER BY total_deaths DESC;").show(K);

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void listTopKHospitalizedPatientDataInEurope()
     * Purpose  -> Method to return a list of European countries with
     *             hospitalized patient information.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 11 /// OPTION 11 /// OPTION 11 /// OPTION 11 /// OPTION 11 /// */
    public static void listTopKHospitalizedPatientDataInEurope() throws Exception {

        System.out.print("Enter the amount of countries you would like to see: ");
        int K = input.nextInt();
        while (K < 1) {
            System.out.println("Invalid Input");
            K = input.nextInt();
        }

        sparkSession.sql("SELECT location, hosp_patients, hosp_patients_per_million, total_cases, date " +
                "FROM Global " +
                "WHERE continent = 'Europe' " +
                "ORDER BY hosp_patients DESC;").show(K);

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void listTopKICUPatientDataInEurope()
     * Purpose  -> Method to return a list of European countries with ICU
     *             patient information.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 12 /// OPTION 12 /// OPTION 12 /// OPTION 12 /// OPTION 12 /// */
    public static void listTopKICUPatientDataInEurope() throws Exception {

        System.out.print("Enter the amount of countries you would like to see: ");
        int K = input.nextInt();
        while (K < 1) {
            System.out.println("Invalid Input");
            K = input.nextInt();
        }

        sparkSession.sql("SELECT location, icu_patients, icu_patients_per_million, total_cases, date " +
                "FROM Global " +
                "WHERE continent = 'Europe' " +
                "ORDER BY icu_patients DESC;").show(K);

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void quarterWithGreatestNumOfPositiveCases()
     * Purpose  -> Method to return the months of the year saw the greatest
     *             number of positive COVID19 cases in descending order by
     *             by country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 13 /// OPTION 13 /// OPTION 13 /// OPTION 13 /// OPTION 13 /// */
    public static void totalNumberOfPositiveCasesPerMonth() throws Exception {

        sparkSession.sql("SELECT MONTH(date) AS monthNum, SUM(total_cases) AS total_number_of_cases " +
                "FROM GLOBAL " +
                "GROUP BY monthNum ORDER BY total_number_of_cases DESC;").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void monthNumWithGreatestNumberOfCases()
     * Purpose  -> Method to return the month with the greatest number of
     *             total cases
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 14 /// OPTION 14 /// OPTION 14 /// OPTION 14 /// OPTION 14 /// */
    public static void monthNumWithGreatestNumberOfCases() throws Exception {

        sparkSession.sql("SELECT MONTH(date) AS MonthNum, SUM(total_cases) AS total_number_of_cases " +
                "FROM GLOBAL " +
                "GROUP BY MonthNum ORDER BY total_number_of_cases DESC LIMIT 1;").show();

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getTotalCasesByQuarterOfYear()
     * Purpose  -> Method to return the total number of positive cases and its
     *             respective quarter by a specified country.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 15 /// OPTION 15 /// OPTION 15 /// OPTION 15 /// OPTION 15 /// */
    public static void getCountryTotalCasesByQuarterOfYear() throws Exception {

        String country = getCountry();
        country = reformatInput(country);

        Dataset<Row> df1 = firstQuarterCountry(country),
                df2 = secondQuarterCountry(country),
                df3 = thirdQuarterCountry(country),
                df4 = fourthQuarterCountry(country);

        Dataset<Row> df1Max = df1.select(functions.max("total_cases").as("Total Cases")).withColumn("country", functions.lit(country)).withColumn("Quarter", functions.lit(1)),
                df2Max = df2.select(functions.max("total_cases").as("Total Cases")).withColumn("country", functions.lit(country)).withColumn("Quarter", functions.lit(2)),
                df3Max = df3.select(functions.max("total_cases").as("Total Cases")).withColumn("country", functions.lit(country)).withColumn("Quarter", functions.lit(3)),
                df4Max = df4.select(functions.max("total_cases").as("Total Cases")).withColumn("country", functions.lit(country)).withColumn("Quarter", functions.lit(4));

        Dataset<Row> MAX = df1Max.union(df2Max.union(df3Max.union(df4Max)));
        MAX.orderBy(MAX.col("Total Cases").desc()).show(false);

        sma.theWholeShebang();

    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getTotalCasesByQuarterOfYear()
     * Purpose  -> Method to return the total number of positive cases and its
     *             respective quarter by a specified continent.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 16 /// OPTION 16 /// OPTION 16 /// OPTION 16 /// OPTION 16 /// */
    public static void getContinentTotalCasesByQuarterOfYear() throws Exception {

        String continent = getContinent();
        continent = reformatInput(continent);

        Dataset<Row> df1 = firstQuarterContinent(continent),
                df2 = secondQuarterContinent(continent),
                df3 = thirdQuarterContinent(continent),
                df4 = fourthQuarterContinent(continent);

        Dataset<Row> df1Max = df1.select(functions.max("total_cases").as("Total Cases")).withColumn("continent", functions.lit(continent)).withColumn("Quarter", functions.lit(1)),
                df2Max = df2.select(functions.max("total_cases").as("Total Cases")).withColumn("continent", functions.lit(continent)).withColumn("Quarter", functions.lit(2)),
                df3Max = df3.select(functions.max("total_cases").as("Total Cases")).withColumn("continent", functions.lit(continent)).withColumn("Quarter", functions.lit(3)),
                df4Max = df4.select(functions.max("total_cases").as("Total Cases")).withColumn("continent", functions.lit(continent)).withColumn("Quarter", functions.lit(4));

        Dataset<Row> MAX = df1Max.union(df2Max.union(df3Max.union(df4Max)));
        MAX.orderBy(MAX.col("Total Cases").desc()).show(false);

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------



}// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
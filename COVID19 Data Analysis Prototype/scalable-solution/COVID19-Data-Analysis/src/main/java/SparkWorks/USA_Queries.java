package SparkWorks;

//Apache Spark Includes

import org.apache.spark.sql.*;

//Java Includes
import java.io.*;
import java.io.BufferedReader;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/*
 * USA Queries class which pulls from our HDFS dir COVID19/USA.csv
 * HDFS is used in conjunction with a spark instance and dataframe
 * Dataset solely operates on states within the US
 */
public class USA_Queries {
    private static Dataset<Row> df;
    private static SparkSession sparkSession;
    private static Scanner input = new Scanner(System.in);
    private static SparkMainApp sma = new SparkMainApp();


    /*
        Function: Queries
        Author: Dominic Renales
        Editors:
        Input: String, SparkSession
        Output: None
        Summary: Constructor that sets the private data field Dataset<Row>
            and SparkSession. Afterwards, create the temporary view of the
            csv file that will be looked at.
    */
    public USA_Queries(String filePath, SparkSession sparkSession) {
        this.sparkSession = sparkSession;
        this.df = sparkSession
                .read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", "true")
                .load(filePath);

        df.createOrReplaceTempView("USA");
    }

    /*
        Function: verifyState
        Author: Dominic Renales
        Modifier: Dan Murphy
        Input: String
        Output: boolean
        Summary: Creates a buffered reader for the hdfs filepath to determine the
            validity of the state name chosen by a user.
    */
    private static boolean verifyState(String state) throws Exception {
        String rootDir = System.getProperty("user.home"); // "dir => /root/file_name_here"
        BufferedReader br = new BufferedReader(new FileReader(new File(rootDir + "/USA_States.txt")));
        String read;

        while ((read = br.readLine()) != null)
            if (read.toUpperCase().contains(state))
                return true;
        return false;
    }

    /*
        Function: verifyCase
        Author: Dominic Renales
        Editors:
        Input: String
        Output: boolean
        Summary: Verifies that the chosen case result is valid.
    */
    private static boolean verifyCase(String caseResult) {
        switch (caseResult.toUpperCase()) {
            case "POSITIVE":
            case "P":
            case "NEGATIVE":
            case "N":
            case "INCONCLUSIVE":
            case "I":
            case "ALL":
            case "A":
                return true;
            default:
                return false;
        }
    }

    /*
        Function: getCase
        Author: Dominic Renales
        Editors:
        Input: None
        Output: String
        Summary: Prompts the user to enter the case result they want to query.
    */
    private static String getCase() {
        Scanner input = new Scanner(System.in);
        System.out.print("Which result would you like to view ([P]ositive/[N]egative/[I]nconclusive)/[A]ll): ");
        String caseResult = input.nextLine();
        while (!verifyCase(caseResult)) {
            System.out.println("Invalid Input");
            System.out.print("Which result would you like to view (Positive/Negative/Inconclusive: ");
            caseResult = input.nextLine();
        }

        return caseResult;
    }

    /*
        Function: getState
        Author: Dominic Renales
        Editors:
        Input: None
        Output: String
        Summary: Prompts the user to enter the state they want to query.
    */
    private static String getState() throws Exception {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter the desired state name: ");
        String state = input.nextLine();
        while (!verifyState(state.toUpperCase())) {
            System.out.println("Invalid State Name.");
            System.out.print("Enter the desired state name: ");
            state = input.nextLine();
        }

        return state;
    }

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
            Function: quarterOne
            Author: Dominic Renales
            Editors:
            Input: String, String
            Output: None
            Summary: Runs sequel queries on the one quarter of the year
        */
    private static Dataset<Row> quarterOne(String state, String caseResult) throws Exception {
        if(state.length() == 2)
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-01-01' <= date and '2020-03-31' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state = '" + state + "' ORDER BY date;");
        else
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-01-01' <= date and '2020-03-31' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state_name = '" + state + "' ORDER BY date;");
    }

    /*
        Function: quarterTwo
        Author: Dominic Renales
        Editors:
        Input: String, String
        Output: None
        Summary: Runs sequel queries on the two quarter of the year
    */
    private static Dataset<Row> quarterTwo(String state, String caseResult) throws Exception {
        if(state.length() == 2)
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-04-01' <= date and '2020-06-30' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state = '" + state + "' ORDER BY date;");
        else
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-04-01' <= date and '2020-06-30' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state_name = '" + state + "' ORDER BY date;");
    }

    /*
        Function: quarterThree
        Author: Dominic Renales
        Editors:
        Input: String, String
        Output: None
        Summary: Runs sequel queries on the third quarter of the year
    */
    private static Dataset<Row> quarterThree(String state, String caseResult) throws Exception {
        if(state.length() == 2)
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-07-01' <= date and '2020-09-31' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state = '" + state + "' ORDER BY date;");
        else
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-07-01' <= date and '2020-09-30' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state_name = '" + state + "' ORDER BY date;");
    }

    /*
        Function: quarterFour
        Author: Dominic Renales
        Editors:
        Input: String, String
        Output: Dataset<Row>
        Summary: Runs sequel queries on the fourth quarter of the year
    */
    private static Dataset<Row> quarterFour(String state, String caseResult) throws Exception {
        if(state.length() == 2)
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-10-01' <= date and '2020-12-31' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state = '" + state + "' ORDER BY date;");
        else
            return sparkSession.sql("SELECT * FROM USA " +
                    "WHERE '2020-10-01' <= date and '2020-12-31' >= date " +
                    "and overall_outcome = '" + caseResult + "' " +
                    "and state_name = '" + state + "' ORDER BY date;");
    }


    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dominic Renales
     * Modifier -> Dan Murphy
     * Method   -> void quarterHelper(String state, String caseResult)
     * Purpose  -> Helper function which allows Quarterly Reports to be shown.
     *             Used by USA Option 8 - listQuarterlyReportsByCase()
     * -----------------------------------------------------------------------
     * Receives -> String, String
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static void quarterHelper(String state, String caseResult) throws Exception {
        Dataset<Row> df1 = quarterOne(state, caseResult),
                df2 = quarterTwo(state, caseResult),
                df3 = quarterThree(state, caseResult),
                df4 = quarterFour(state, caseResult);

        /* Quarter 1 */
        Dataset<Row> df1Max = df1.select(functions.sum("new_results_reported").cast("BIGINT").as("Quarterly Reports"));
        df1Max = df1Max.withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(1));

        /* Quarter 2 */
        Dataset<Row> df2Max = df2.select(functions.sum("new_results_reported").cast("BIGINT").as("Quarterly Reports"));
        df2Max = df2Max.withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(2));

        /* Quarter 3 */
        Dataset<Row> df3Max = df3.select(functions.sum("new_results_reported").cast("BIGINT").as("Quarterly Reports"));
        df3Max = df3Max.withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(3));

        /* Quarter 4 */
        Dataset<Row> df4Max = df4.select(functions.sum("new_results_reported").cast("BIGINT").as("Quarterly Reports"));
        df4Max = df4Max.withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(4));

        /* UNION OF 4 QUARTERS => RETURNS MAX*/
        Dataset<Row> MAX = df1Max.union(df2Max.union(df3Max.union(df4Max)));
        MAX.orderBy(MAX.col("Quarterly Reports").desc()).show(false);
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> String reformatInput()
     * Purpose  -> Converts string to title case.
     *             (e.g. united states -> United States)
     * -----------------------------------------------------------------------
     * Receives -> String
     * Returns  -> String
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    private static String reformatInput(String state) {
        Scanner scan = new Scanner(state);
        String upperCase = "";
        if (state.length() == 2) return state.toUpperCase();
        if(state.length() == 1){
            if(state.toUpperCase().equals("P")) return "Positive";
            else if(state.toUpperCase().equals("N")) return "Negative";
            else if(state.toUpperCase().equals("I")) return "Inconclusive";
            else return "All";
        }
        while(scan.hasNext()){
            String fix = scan.next();
            upperCase += Character.toUpperCase(fix.charAt(0))+ fix.substring(1) + " ";
        }
        return(upperCase.trim());
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Dominic Renales
     * Method   -> void getNumOfSpecifiedOutcomesByState()
     * Purpose  -> Method to return the number of case results within a
     *			   specified date range within the USA.csv dataset.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// */
    public static void getNumOfSpecifiedOutcomesByState() throws Exception {
        String caseResult = getCase();
        caseResult = reformatInput(caseResult);

        String state = getState();
        state = reformatInput(state);

        System.out.println("\n\n");// Visual spacing
        if (!caseResult.equals("All")) {
            if (state.length() == 2)
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = '" + caseResult + "' AND state = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = '" + caseResult + "' AND state = '" + state + "' LIMIT 1);").show();
            else
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = '" + caseResult + "' AND state_name = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = '" + caseResult + "' AND state_name = '" + state + "' LIMIT 1);").show();
        } else {
            if (state.length() == 2) {
                System.out.println("POSITIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Positive' AND state = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Positive' AND state = '" + state + "' LIMIT 1);").show();
                System.out.println("NEGATIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Negative' AND state = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Negative' AND state = '" + state + "' LIMIT 1);").show();
                System.out.println("INCONCLUSIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Inconclusive' AND state = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Inconclusive' AND state = '" + state + "' LIMIT 1);").show();
            } else {
                System.out.println("POSITIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Positive' AND state_name = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Positive' AND state_name = '" + state + "' LIMIT 1);").show();
                System.out.println("NEGATIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Negative' AND state_name = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Negative' AND state_name = '" + state + "' LIMIT 1);").show();
                System.out.println("INCONCLUSIVE DATA:");
                sparkSession.sql("SELECT total_results_reported, date FROM USA WHERE overall_outcome = 'Inconclusive' AND state_name = '" + state + "' AND date = (SELECT MAX(date) FROM USA WHERE overall_outcome = 'Inconclusive' AND state_name = '" + state + "' LIMIT 1);").show();
            }
        }

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Dominic Renales
     * Method   -> void getNumOfTestsAdministeredByState()
     * Purpose  -> Method to return the number of tests given within a
     *			   specified date range within the USA.csv dataset.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// */
    public static void getNumOfTestsAdministeredByState() throws Exception {
        String state = getState();
        state = reformatInput(state);

        if (state.length() == 2)
            sparkSession.sql("SELECT SUM(new_results_reported) AS test_count FROM USA WHERE state ='" + state + "';").show();
        else
            sparkSession.sql("SELECT SUM(new_results_reported) AS test_count FROM USA WHERE state_name ='" + state + "';").show();

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Dominic Renales
     * Method   -> void getTotalNumOfSpecifiedCasesByDateRange()
     * Purpose  -> Method to return the number of case results within a
     *			   specified date range within the USA.csv dataset.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// */
    public static void getTotalNumOfSpecifiedCasesByDateRange() throws Exception{
        Scanner input = new Scanner(System.in);

        String caseResult = getCase();
        caseResult = reformatInput(caseResult);

        System.out.print("Enter a starting date (YYYY-MM-DD): ");
        String startDate = input.nextLine();
        System.out.print("Enter a ending date (YYYY-MM-DD): ");
        String endDate = input.nextLine();

        if (!caseResult.equals("All")) {
            sparkSession.sql("SELECT MAX(total_results_reported) AS total, date" +
                    " FROM USA" +
                    " WHERE '" + startDate + "' <= date and date <= '" + endDate + "' and overall_outcome = '" + caseResult +
                    "' GROUP BY date" +
                    " ORDER BY total DESC;").show(1000, false);
        }
        else {
            sparkSession.sql("SELECT MAX(total_results_reported) AS total, date" +
                    " FROM USA" +
                    " WHERE '" + startDate + "' <= date and date <= '" + endDate +
                    "' GROUP BY date" +
                    " ORDER BY total DESC;").show(1000, false);
        }

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------


    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Dominic Renales
     * Method   -> void getNumOfSpecifiedOutcomesByQuarterOfYear()
     * Purpose  -> Method to return the number of results given a state name,
     *			   case outcome and quarter of the year (1-4).
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */

    /* /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// */
    public static void getNumOfSpecifiedOutcomesByQuarterOfYear() throws Exception {
        String caseResult = getCase();
        caseResult = reformatInput(caseResult);

        String state = getState();
        state = reformatInput(state);

        int quarter = getQuarter();
        if (!caseResult.equals("All")) {
            if (quarter == 1) {
                Dataset<Row> df = quarterOne(state,caseResult);
                if(state.length()==2) {
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-03-01","2020-03-31"))
                            .show(35,false); }
                else {
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-03-01","2020-03-31"))
                            .show(35,false); }
            }
            else if (quarter == 2) {
                Dataset<Row> df = quarterTwo(state,caseResult);
                if(state.length() == 2) {
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-04-01", "2020-04-30"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-05-01", "2020-05-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-06-01", "2020-06-30"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);
                }
                else {
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-04-01", "2020-04-30"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-05-01", "2020-05-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-06-01", "2020-06-30"))
                            .show(35, false);

                    sma.theWholeShebang();
                }
            }
            else if (quarter == 3) {
                Dataset<Row> df = quarterThree(state,caseResult);
                if(state.length() == 2) {
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-07-01", "2020-07-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-08-01", "2020-08-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-09-01", "2020-09-30"))
                            .show(35, false);

                    sma.theWholeShebang();
                }
                else {
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-07-01", "2020-07-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-08-01", "2020-08-31"))
                            .show(35, false);
                    TimeUnit.SECONDS.sleep(3);

                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported"))
                            .filter(df.col("date").between("2020-09-01", "2020-09-30"))
                            .show(35, false);

                    sma.theWholeShebang();
                }
            }
            else {
                Dataset<Row> df = quarterFour(state,caseResult);
                if(state.length() == 2) {
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-10-01", "2020-10-30")).show(35, false);
                    TimeUnit.SECONDS.sleep(3);
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-11-01", "2020-11-30")).show(35, false);
                    TimeUnit.SECONDS.sleep(3);
                    df.select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-12-01", "2020-12-31")).show(35, false);
                    sma.theWholeShebang();
                }
                else {
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-10-01", "2020-10-30")).show(35, false);
                    TimeUnit.SECONDS.sleep(3);
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-11-01", "2020-11-30")).show(35, false);
                    TimeUnit.SECONDS.sleep(3);
                    df.select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).filter(df.col("date").between("2020-12-01", "2020-12-31")).show(35, false);
                    sma.theWholeShebang();
                }
            }
        } else {
            if (quarter == 1) {
                if (state.length() == 2) {
                    quarterOne(state, "Positive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterOne(state, "Negative").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterOne(state, "Inconclusive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
                else {
                    quarterOne(state, "Positive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterOne(state, "Negative").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterOne(state, "Inconclusive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
            }
            else if (quarter == 2) {
                if(state.length() == 2) {
                    quarterTwo(state, "Positive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterTwo(state, "Negative").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterTwo(state, "Inconclusive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
                else {
                    quarterTwo(state, "Positive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterTwo(state, "Negative").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterTwo(state, "Inconclusive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
            } //QUARTER 2 DATE RANGE
            else if (quarter == 3) {
                if (state.length() == 2) {
                    quarterThree(state, "Positive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterThree(state, "Negative").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterThree(state, "Inconclusive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
                else {
                    quarterThree(state, "Positive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterThree(state, "Negative").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterThree(state, "Inconclusive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
            } //QUARTER 3 DATE RANGE
            else {
                if(state.length() == 2) {
                    quarterFour(state, "Positive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterFour(state, "Negative").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterFour(state, "Inconclusive").select(df.col("date"), df.col("state"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
                else {
                    quarterFour(state, "Positive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterFour(state, "Negative").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    TimeUnit.SECONDS.sleep(3);
                    quarterFour(state, "Inconclusive").select(df.col("date"), df.col("state_name"), df.col("overall_outcome"), df.col("total_results_reported")).show(100, false);
                    sma.theWholeShebang();
                }
            } //QUARTER 4 DATE RANGE
        }
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Modifier -> Dominic Renales
     * Method   -> void topKResultsReportedByState()
     * Purpose  -> Method to return the top K results given a case outcome,
     *			   start date (until last recorded date) and the value for K.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */

    /* /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// */
    public static void topKResultsReportedByState() throws Exception {
        Scanner input = new Scanner(System.in);

        String caseResult = getCase();
        caseResult = reformatInput(caseResult);

        System.out.print("Enter a date to evaluate (YYYY-MM-DD): ");
        String date = input.nextLine();

        System.out.print("Enter the list size you want to see: ");
        int K = input.nextInt();
        while (K < 1) {
            System.out.println("Invalid Input");
            K = input.nextInt();
        }

        if (caseResult.equals("All")) {
            sparkSession.sql("SELECT state_name, overall_outcome, total_results_reported FROM USA WHERE '" + date + "' = date " +
                    "and overall_outcome = 'Positive' ORDER BY total_results_reported DESC;").show(K);
            sparkSession.sql("SELECT state_name, overall_outcome, total_results_reported FROM USA WHERE '" + date + "' = date " +
                    "and overall_outcome = 'Negative' ORDER BY total_results_reported DESC;").show(K);
            sparkSession.sql("SELECT state_name, overall_outcome, total_results_reported FROM USA WHERE '" + date + "' = date " +
                    "and overall_outcome = 'Inconclusive' ORDER BY total_results_reported DESC;").show(K);
        }
        else sparkSession.sql("SELECT state_name, overall_outcome, total_results_reported FROM USA WHERE '" + date + "' = date " +
                "and overall_outcome = '" + caseResult + "' ORDER BY total_results_reported DESC;").show(K);

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getTotalNumOfCasesByDateRange()
     * Purpose  -> Method to get the total number of total results reported
     *			   by a specified state.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// OPTION 6 /// */
    public static void getTotalNumOfCasesByDateRange() throws Exception {

        Scanner input = new Scanner(System.in);
        String caseResult = getCase();
        caseResult = reformatInput(caseResult);
        System.out.print("Enter the desired state name: ");
        String state = input.nextLine();
        while (!verifyState(state.toUpperCase())) {
            System.out.println("Invalid State Name.");
            System.out.print("Enter the desired state name: ");
            state = input.nextLine();
        }
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = input.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = input.nextLine();
        state = reformatInput(state);
        if (state.length() == 2) {
            sparkSession.sql("SELECT MAX(total_results_reported) AS total_cases " +
                    "FROM USA " +
                    "WHERE state = '" + state + "' AND overall_outcome = '" + caseResult +
                    "' AND '" + startDate + "' <= date and date <= '" + endDate + "';").show();
        }else{
            sparkSession.sql("SELECT MAX(total_results_reported) AS total_cases " +
                    "FROM USA " +
                    "WHERE state_name = '" + state + "' AND overall_outcome = '" + caseResult +
                    "' AND '" + startDate + "' <= date and date <= '" + endDate + "';").show();
        }

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dan Murphy
     * Method   -> void getTotalNumOfNewCasesByDateRange()
     * Purpose  -> Method to get the total number of new results reported
     *			   by a specified state.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// OPTION 7 /// */
    public static void getTotalNumOfNewCasesByDateRange() throws Exception {
        Scanner input = new Scanner(System.in);
        String caseResult = getCase();
        caseResult = reformatInput(caseResult);
        System.out.print("Enter the desired state name: ");
        String state = input.nextLine();
        while (!verifyState(state.toUpperCase())) {
            System.out.println("Invalid State Name.");
            System.out.print("Enter the desired state name: ");
            state = input.nextLine();
        }
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String startDate = input.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String endDate = input.nextLine();
        state = reformatInput(state);
        if (state.length() == 2) {
            sparkSession.sql("SELECT SUM(new_results_reported) AS new_cases " +
                    "FROM USA " +
                    "WHERE state = '" + state + "' and overall_outcome = '" + caseResult + "';").show();
        }else{
            sparkSession.sql("SELECT SUM(new_results_reported) AS new_cases " +
                    "FROM USA " +
                    "WHERE state_name = '" + state + "' and overall_outcome = '" + caseResult + "';").show();
        }

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dom Renales
     * Method   -> void listTotalQuarterlyReportsByCase()
     * Purpose  -> Method to get the total number of new results reported and
     *			   its respective quarter by specified state.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// OPTION 8 /// */
    public static void listTotalQuarterlyReportsByCase() throws Exception {
        String caseResult = getCase();
        caseResult = reformatInput(caseResult);

        String state = getState();
        state = reformatInput(state);

        if(!caseResult.equals("All")) {
            quarterHelper(state, caseResult);
        } else {
            System.out.println("POSITIVE DATA:");
            quarterHelper(state, "Positive");
            System.out.println("NEGATIVE DATA:");
            quarterHelper(state, "Negative");
            System.out.println("INCONCLUSIVE DATA:");
            quarterHelper(state, "Inconclusive");
        }

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dom Renales
     * Method   -> void listTotalQuarterlyReportsByState()
     * Purpose  -> Method to list the quarterly specified outcomes and its
     *			   respective quarter number by specified state.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// OPTION 9 /// */
    public static void listTotalQuarterlyReportsByState() throws Exception {
        String state = getState();
        state = reformatInput(state);

        Dataset<Row> df1 = quarterOne(state,"Positive").union(quarterOne(state, "Negative").union(quarterOne(state, "Inconclusive"))),
                df2 = quarterTwo(state,"Positive").union(quarterTwo(state, "Negative").union(quarterTwo(state, "Inconclusive"))),
                df3 = quarterThree(state,"Positive").union(quarterThree(state, "Negative").union(quarterThree(state, "Inconclusive"))),
                df4 = quarterFour(state,"Positive").union(quarterFour(state, "Negative").union(quarterFour(state, "Inconclusive")));

        Dataset<Row> df1Max = df1.select(functions.sum("new_results_reported").as("Total Reports")).withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(1)),
                df2Max = df2.select(functions.sum("new_results_reported").as("Total Reports")).withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(2)),
                df3Max = df3.select(functions.sum("new_results_reported").as("Total Reports")).withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(3)),
                df4Max = df4.select(functions.sum("new_results_reported").as("Total Reports")).withColumn("state", functions.lit(state)).withColumn("Quarter", functions.lit(4));

        Dataset<Row> MAX = df1Max.union(df2Max.union(df3Max.union(df4Max)));
        MAX.orderBy(MAX.col("Total Reports").desc()).show(false);

        sma.theWholeShebang();
    } // ---------------------------------------------------------------------

    /*
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     * Author   -> Dom Renales
     * Modifier -> Dan Murphy
     * Method   -> void recentEvents()
     * Purpose  -> Method to list the recent events of each state in the US
     *			   respective quarter number by specified state.
     * -----------------------------------------------------------------------
     * Receives -> NONE
     * Returns  -> NONE
     * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    /* /// OPTION 10 /// OPTION 10 /// OPTION 10 /// OPTION 10 /// OPTION 10 /// */
    public static void recentEvents() throws Exception {
        String homePath = System.getProperty("user.home"); // "dir => /root/file_name_here"
        BufferedReader br = new BufferedReader(new FileReader(new File(homePath + "/USA_States.txt")));
        String read;
        System.out.print("Enter the desired state name: ");
        String state = input.nextLine();
        while (!verifyState(state.toUpperCase())) {
            System.out.println("Invalid State Name.");
            System.out.print("Enter the desired state name: ");
            state = input.nextLine();
        }
        state = reformatInput(state);

        if (state.length() == 2) {
            sparkSession.sql("SELECT state, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state = '" + state + "' and overall_outcome = 'Positive' " +
                    "ORDER BY date DESC;").show(7);
            TimeUnit.MILLISECONDS.sleep(500);
            sparkSession.sql("SELECT state, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state = '" + state + "' and overall_outcome = 'Negative' " +
                    "ORDER BY date DESC;").show(7);
            TimeUnit.MILLISECONDS.sleep(500);
            sparkSession.sql("SELECT state, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state = '" + state + "' and overall_outcome = 'Inconclusive' " +
                    "ORDER BY date DESC;").show(7);
        }else{
            sparkSession.sql("SELECT state_name, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state_name = '" + state + "' and overall_outcome = 'Positive' " +
                    "ORDER BY date DESC;").show(7);
            TimeUnit.MILLISECONDS.sleep(500);
            sparkSession.sql("SELECT state_name, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state_name = '" + state + "' and overall_outcome = 'Negative' " +
                    "ORDER BY date DESC;").show(7);
            TimeUnit.MILLISECONDS.sleep(500);
            sparkSession.sql("SELECT state_name, date, overall_outcome, new_results_reported, total_results_reported FROM USA " +
                    "WHERE state_name = '" + state + "' and overall_outcome = 'Inconclusive' " +
                    "ORDER BY date DESC;").show(7);
        }


        sma.theWholeShebang();

    } // ---------------------------------------------------------------------
}// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
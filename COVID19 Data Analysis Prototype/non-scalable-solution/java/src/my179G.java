/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class my179G{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> my179G (String dbname, String dbport, String user,
	 *                        String passwd) throws SQLException
	 * Purpose  -> Method which creates a new instance of the DB Project and
	 *             serves as an initializing intermediary between the
	 *             server/localhost and the database.
	 * -----------------------------------------------------------------------
	 * @param hostname PSQL server hostname
	 * @param database Name of the database
	 * @param username the user name used to login to the database
	 * @param password the user login password
	 * @throws java.sql.SQLException when failed to make a connection.
	 * -----------------------------------------------------------------------
	 * Receives -> dbname, dbport, user, passwd
	 * Returns  -> NONE
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public my179G(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");

			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> void executeUpdate (String sql) throws SQLException
	 * Purpose  -> Method to execute an update SQL statement.
	 *             Update SQL instruction includes the following:
	 *             CREATE, INSERT, UPDATE, DELETE, DROP
	 * -----------------------------------------------------------------------
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * -----------------------------------------------------------------------
	 * Receives -> [String] sql
	 * Returns  -> NONE
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public void executeUpdate (String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int executeQueryAndPrintResult (String query)
   *                                            throws SQLException
   * Purpose  -> Method to execute an input query SQL instruction (i.e. SELECT).
   *             This method issues the query to the DBMS and outputs the
   *             results to standard out.
   * -----------------------------------------------------------------------
   * @param query the input query string
   * @return the number of rows returned
   * @throws java.sql.SQLException when failed to execute the query
   * -----------------------------------------------------------------------
   * Receives -> [String] sql
   * Returns  -> [int] rowCount
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> List<List<String>> executeQueryAndReturnResult(String query)
	 *                                                       throws SQLException
	 * Purpose  -> Method to execute an input query SQL instruction
	 *             (i.e. SELECT).
	 *             This method issues the query to the DBMS and returns the
	 *             results as a list of records.
	 *             Each record is a list of attribute values.
	 * -----------------------------------------------------------------------
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 * -----------------------------------------------------------------------
	 * Receives -> [String] query
	 * Returns  -> List<List<String>> result
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 * obtains the metadata object for the returned result set.  The metadata
		 * contains row and column info.
		*/
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;

		/* ------------------------------------------------- */

		/* Iterates through the result set and saves the data
		 * returned by the query.
		 */
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>();
		while (rs.next()){
			List<String> record = new ArrayList<String>();
			for (int i=1; i<=numCol; ++i)
				record.add(rs.getString (i));
			result.add(record);
		}//end while
		stmt.close ();
		return result;
	}//end executeQueryAndReturnResult

	/*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int executeQuery (String query) throws SQLException
   * Purpose  -> Method to execute an input query SQL instruction
   *             (i.e. SELECT).
   *             This method issues the query to the DBMS and returns the
   *             number of results.
   * -----------------------------------------------------------------------
   * @param query the input query string
   * @return the number of rows returned
   * @throws java.sql.SQLException when failed to execute the query
   * -----------------------------------------------------------------------
   * Receives -> [String] query
   * Returns  -> [int] rowCount
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}

	/*
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 * Author   -> Dr. Mariam Salloum
	 * Modifier -> Dan Murphy
	 * Method   -> int getCurrSeqVal(String sequence) throws SQLException
	 * Purpose  -> Method to fetch the last value from the sequence.
	 *             This method issues the query to the DBMS and returns the
	 *             current value of sequence used for the autogenerated keys.
	 * -----------------------------------------------------------------------
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 * -----------------------------------------------------------------------
	 * Receives -> [String] query
	 * Returns  -> [int] 1 || [int] -1
	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
	 */
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();

		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

  /*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> void cleanup()
   * Purpose  -> Method to close the physical connection if it is open.
   * -----------------------------------------------------------------------
   * Receives -> NONE
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 *
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */

  /*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> public static void main(String[] args)
   * Purpose  -> Driver method to permit PSQL execution.
   * -----------------------------------------------------------------------
   * Receives -> String[] args
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + my179G.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if

		my179G esql = null;

		try{
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new my179G(dbname, dbport, user, "");

			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("0. < EXIT");
				System.out.println("1. Largest number of positive, negative or inconclusive case outcomes by state");
				System.out.println("2. Largest number of tests administered in a specifed state");
				System.out.println("3. Largest number of positive, negative or inconclusive case outcomes in an ordered list of dates");
				System.out.println("4. Largest number of positive, negative or inconclusive cases in specified date range");
				System.out.println("5. List the number of positive, negative or inconclusive cases per state");
				/* Add further options here for terminal menu input --- */


				switch (readChoice()){
					case 0: keepon = false; break; /* Exit --- */
					case 1: getNumOfSpecifiedOutcomesByState(esql); break;
					case 2: getNumOfTestsAdministeredByState(esql); break;
					case 3: getTotalNumOfSpecifiedCasesByDateRange(esql); break;
					case 4: getNumOfSpecifiedOutcomesByQuarterOfYear(esql); break;
					case 5: listNumOfSpecifiedOutcomeCasesPerState(esql); break;
					/* Add further cases here for respective function calls --- */
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if
			}catch(Exception e){
				// ignored.
			}
		}
	}

  /*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dr. Mariam Salloum
   * Modifier -> Dan Murphy
   * Method   -> int readChoice()
   * Purpose  -> Method to read the users choice from the terminal menu.
   *             Returns only if a correct value is given.
   * -----------------------------------------------------------------------
   * Receives -> NONE
   * Returns  -> NONE
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
	public static int readChoice() {
		int input;
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice



	public static String getInput(String prompt) throws Exception {
        System.out.print("\tEnter " + prompt + ": ");
        String input = in.readLine();

        return input;
    }

  /*
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   * Author   -> Dan Murphy
   * Method   -> String inputDate
   * Purpose  -> Method to get the input of type date
   * -----------------------------------------------------------------------
   * Receives -> String prompt, boolean permitNull
   * Returns  -> Date 
   * -----------------------------------------------------------------------
   * @param prompt the input query string
   * @param permitNull whether or not the attribute could be null
   * @return the input string
   * @throws java.io.Exception when the length exceeds the predefined size
   * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   */
    public static String inputDate(String prompt, boolean permitNull) throws Exception {
        String input = getInput(prompt);

        if (input.equals("") && Boolean.compare(permitNull, false) == 0)  throw new Exception("The " + prompt + " should not be null");

        if (input.equals("")) return "null";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(input);

        return "'" + input + "'";
	}

	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> boolean isValidState(String state_name)
  	 * Purpose  -> Method to compare user input to a list of US states.
   	 * -----------------------------------------------------------------------
   	 * Receives -> String state_name
   	 * Returns  -> bool true/false
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */
	
	public static boolean isValidState(String state_name) throws Exception {

		if(!state_name.equals("Alabama")        && !state_name.equals("Alaska")
		&& !state_name.equals("Arizona")        && !state_name.equals("Arkansas")
		&& !state_name.equals("California")     && !state_name.equals("Colorado")
		&& !state_name.equals("Connecticut")    && !state_name.equals("Delaware")
		&& !state_name.equals("Florida")        && !state_name.equals("Georgia")
		&& !state_name.equals("Hawaii")         && !state_name.equals("Idaho")
		&& !state_name.equals("Illinois")       && !state_name.equals("Indiana")
		&& !state_name.equals("Iowa")           && !state_name.equals("Kansas")
		&& !state_name.equals("Kentucky")       && !state_name.equals("Lousiana")
		&& !state_name.equals("Maine")          && !state_name.equals("Maryland")
		&& !state_name.equals("Massachusetts")  && !state_name.equals("Michigan")
		&& !state_name.equals("Minnesota")      && !state_name.equals("Mississippi")
		&& !state_name.equals("Missouri")       && !state_name.equals("Montana")
		&& !state_name.equals("Nebraska")       && !state_name.equals("Nevada")
		&& !state_name.equals("New Hampshire")  && !state_name.equals("New Jersey")
		&& !state_name.equals("New Mexico")     && !state_name.equals("New York")
		&& !state_name.equals("North Carolina") && !state_name.equals("North Dakota")
		&& !state_name.equals("Ohio")           && !state_name.equals("Oklahoma")
		&& !state_name.equals("Oregon")         && !state_name.equals("Pennsylvania")
		&& !state_name.equals("Rhode Island")   && !state_name.equals("South Carolina")
		&& !state_name.equals("South Dakota")   && !state_name.equals("Tennessee")
		&& !state_name.equals("Texas")          && !state_name.equals("Utah")
		&& !state_name.equals("Vermont")        && !state_name.equals("Virginia")
		&& !state_name.equals("Washington")     && !state_name.equals("West Virginia")
		&& !state_name.equals("Wisconsin")      && !state_name.equals("Wyoming"))
			return false;
		return true;

	}/* End of method ----------------------------------------------------- */

	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> void getNumOfSpecifiedOutcomesByState(my179G esql)
  	 * Purpose  -> Method to get the greatest number of positive, negative or
	 *             inconclusive COVID-19 cases in a specific state.
   	 * -----------------------------------------------------------------------
   	 * Receives -> my179G esql
   	 * Returns  -> NONE
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */

	 /* /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// OPTION 1 /// */
	public static void getNumOfSpecifiedOutcomesByState(my179G esql) {

		/* Grab desired case outcome from user to prime the loop --- */
		String case_outcome;

		/* Catch user input to determine if case_outcome is valid --- */

		while(true) {
			try {
				System.out.println("\tEnter desired case outcome (Positive, Negative, Inconclusive): ");
				case_outcome = in.readLine();
				// IF outcome is valid, proceed to query ---
				if(    !case_outcome.equals("Positive")     && !case_outcome.equals("Negative")
					&& !case_outcome.equals("Inconclusive")) {
				throw new RuntimeException("Input must be Positive, Negative or Inconclusive");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}

		/* Grab desired case outcome from user to prime the loop --- */
		String state_name;
		
		/* Catch user input to determine if case_outcome is valid --- */
		while(true) {
			try {
				System.out.println("\tEnter desired state name: ");
				state_name = in.readLine();

				if(!isValidState(state_name)) {
				throw new RuntimeException("Please enter a valid (US) state name!");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}

	/* Try the following query ...
	 * IF valid query, call the method to execute and print the query results
	 * ELSE, exception handle is caught
	 */

	try{
		 String query = "SELECT COUNT(S.overall_outcome) FROM Study S WHERE S.overall_outcome = \'" + case_outcome + "\' AND S.sName = \'" + state_name + "\'";
		 System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
		 esql.executeQueryAndPrintResult(query);
		 System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
		 }catch(Exception e) {
		 System.err.println(e.getMessage());
	 }// End of try/catch to run the query --- //

 }/* End of method ----------------------------------------------------- */

 	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> void getNumOfTestsAdministeredByState(my179G esql)
  	 * Purpose  -> Method to get the greatest number of tests administered 
	 *			   by a specified state 
   	 * -----------------------------------------------------------------------
   	 * Receives -> my179G esql
   	 * Returns  -> NONE
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */

	 /* /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// OPTION 2 /// */
	 public static void getNumOfTestsAdministeredByState(my179G esql) {

		/* Grab desired case outcome from user to prime the loop --- */
		String state_name;

		/* Catch user input to determine if case_outcome is valid --- */
		while(true) {
			try {
				System.out.println("\tEnter desired state name: ");
				state_name = in.readLine();

				if(!isValidState(state_name)) {
				throw new RuntimeException("Please enter a valid (US) state name!");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}

		/* Try the following query ...
		* IF valid query, call the method to execute and print the query results
		* ELSE, exception handle is caught
		*/

		try{
			String query = "SELECT COUNT(*) FROM Study S WHERE S.sName = \'" + state_name + "\'";
			System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
			}catch(Exception e) {
			System.err.println(e.getMessage());
		}// End of try/catch to run the query --- //

	 }/* End of method ----------------------------------------------------- */

	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> void getTotalNumOfSpecifiedCasesByDateRange(my179G esql)
  	 * Purpose  -> Method to get the greatest number of positive, negative or 
	 *			   inconclusive results within a specified date range.
   	 * -----------------------------------------------------------------------
   	 * Receives -> my179G esql
   	 * Returns  -> NONE
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */

	 /* /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// OPTION 3 /// */
	 public static void getTotalNumOfSpecifiedCasesByDateRange(my179G esql) {

		/* Grab desired case outcome from user to prime the loop --- */
		String case_outcome;

		/* Catch user input to determine if case_outcome is valid --- */
		while(true) {
			try {
				System.out.println("\tEnter desired case outcome (Positive, Negative, Inconclusive): ");
				case_outcome = in.readLine();

				if(    !case_outcome.equals("Positive")     && !case_outcome.equals("Negative")
					&& !case_outcome.equals("Inconclusive") && !case_outcome.equals("P")
				    && !case_outcome.equals("N")            && !case_outcome.equals("I")) {
				throw new RuntimeException("Input must be Positive, Negative, Inconclusive, P, N or I");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}/* End of while true loop --- */

		/* Try the following query ...
		 * IF valid query, call the method to execute and print the query results
		 * ELSE, exception handle is caught
		 */

		try{
			String query =  "SELECT COUNT(S.overall_outcome) AS total, S.date_of_study\n" +
							"FROM Study S\n" +
							"WHERE %s <= S.date_of_study and S.date_of_study <= %s\n" + 
							"GROUP BY S.date_of_study\n" +
							"ORDER BY total DESC\n";

			String start = inputDate("start date", false);
			String end = inputDate("end date", false);
			query = String.format(query, start, end);
			
			System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
			}catch(Exception e) {
			System.err.println(e.getMessage());
		}// End of try/catch to run the query --- //

	}/* End of method ----------------------------------------------------- */

	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> void getNumOfSpecifiedOutcomesByQuarterOfYear(my179G esql)
  	 * Purpose  -> Method to get the greatest number of positive, negative or 
	 *			   inconclusive results within a specified quarter of the year.
   	 * -----------------------------------------------------------------------
   	 * Receives -> my179G esql
   	 * Returns  -> NONE
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */

	 /* /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// OPTION 4 /// */
	 public static void getNumOfSpecifiedOutcomesByQuarterOfYear(my179G esql) {

		/* Grab desired case outcome from user to prime the loop --- */
		String case_outcome;

		/* Catch user input to determine if case_outcome is valid --- */
		while(true) {
			try {
				System.out.println("\tEnter desired case outcome (Positive, Negative, Inconclusive): ");
				case_outcome = in.readLine();

				if(    !case_outcome.equals("Positive")     && !case_outcome.equals("Negative")
					&& !case_outcome.equals("Inconclusive") && !case_outcome.equals("P")
				    && !case_outcome.equals("N")            && !case_outcome.equals("I")) {
				throw new RuntimeException("Input must be Positive, Negative, Inconclusive, P, N or I");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}/* End of while true loop --- */

		/* Try the following query ...
		 * IF valid query, call the method to execute and print the query results
		 * ELSE, exception handle is caught
		 */

		try{
			String query =  "SELECT (SELECT COUNT(S.overall_outcome)\n" +
									"FROM Study S\n" + 
									"WHERE %s = S.date_of_study)\n" + 
									"-\n" + 
									"(SELECT COUNT(S.overall_outcome)\n" +  
									"FROM Study S\n" + 
									"WHERE %s = S.date_of_study) AS difference\n" + 
							"FROM Study S\n" +
							"ORDER BY difference LIMIT 1";// Forces only 1 result instance

			String end = inputDate("end date", false);
			String start = inputDate("start date", false);
			
			query = String.format(query, end, start);
			
			System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
			}catch(Exception e) {
			System.err.println(e.getMessage());
		}/* End of try/catch to run the query --- */

	}/* End of method ----------------------------------------------------- */

	/*
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 * Author   -> Dan Murphy
   	 * Method   -> void listNumOfSpecifiedOutcomeCasesPerState(my179G esql)
  	 * Purpose  -> Method to list the number of positive, negative or 
	 * 			   inconclusive cases as a list of states in descending order.
   	 * -----------------------------------------------------------------------
   	 * Receives -> my179G esql
   	 * Returns  -> NONE
   	 * =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
   	 */

	 /* /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// OPTION 5 /// */
	 public static void listNumOfSpecifiedOutcomeCasesPerState(my179G esql) {

		/* Grab desired case outcome from user to prime the loop --- */
		String case_outcome;

		/* Catch user input to determine if case_outcome is valid --- */
		while(true) {
			try {
				System.out.println("\tEnter desired case outcome (Positive, Negative, Inconclusive): ");
				case_outcome = in.readLine();

				if(    !case_outcome.equals("Positive")     && !case_outcome.equals("Negative")
					&& !case_outcome.equals("Inconclusive") && !case_outcome.equals("P")
				    && !case_outcome.equals("N")            && !case_outcome.equals("I")) {
				throw new RuntimeException("Input must be Positive, Negative, Inconclusive, P, N or I");
				}
				break;
			}catch (Exception e) {
				System.out.println(e);
				continue;
			}
		}/* End of while true loop --- */

		/* Try the following query ...
		 * IF valid query, call the method to execute and print the query results
		 * ELSE, exception handle is caught
		 */

		try{
			String query =  "SELECT COUNT(S.overall_outcome) AS total, S.sName\n" +
							"FROM Study S\n" +
							"WHERE S.overall_outcome = \'" + case_outcome + "\'" + "\n" + 
							"GROUP BY S.sName\n" +
							"ORDER BY total DESC\n";
			
			System.out.println("\n\n --- EXECUTING QUERY --- \n\n");
			esql.executeQueryAndPrintResult(query);
			System.out.println("\n\n --- END OF QUERY RESULTS --- \n\n");
			}catch(Exception e) {
			System.err.println(e.getMessage());
		}// End of try/catch to run the query --- //

	 }/* End of method ----------------------------------------------------- */

}/* End of my179G !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

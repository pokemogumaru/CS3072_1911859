
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CS3072_1911859 {
	private static FileWriterUtil csvLogger;
	private static FileWriterUtil txtLogger;
	public static void main(String[] args) throws Exception {
		//Start File Loggers:
		/*
        csvLogger = new FileWriterUtil(dateTime() + " TSP_2D_export.csv", "csv"); // Create CSV file logger 
        csvLogger.start(); // Start writing to file  
        txtLogger = new FileWriterUtil(dateTime() + " TSP_2D_export.txt", "txt");
		txtLogger.start();
        */
		
		//to test new_TSP:
		//test_new_TSP();
		
		//test_2D(); //To test our 2D representation of 1D TSP
		
		//To run the TSP solver:
		//SolveTSP solver = new SolveTSP(new_TSP()); //to run the hill climber to solve a TSP
		//System.out.println("CS3072_1911859: main: solver.return_solution() = " + solver.return_solution()); //Prints the total weight of solution to console
		
		//test_MST(); //To test our MST of a TSP
		
		MakeTSP maker = new MakeTSP(new_TSP(), true, 100, 1); //To run the TSP maker. input: TSP, harder (true) / easier (false), iterations for outer HC, repeats (use 1 for 1 run)
        
        /*
		double[][] array2D = convert_1D_to_2D(new_TSP());
		 // Generate CSV 
        generate_CSV_from_2D(array2D);
        //export TXT
        export_TXT_from_2D(array2D);
        
        // Close writer 
        csvLogger.close();
        txtLogger.close();
        */
	}

	public static void test_new_TSP()
	{//this method tests the initial TSP generator by counting values and showing each value
				double[] arr = new_TSP();
				  System.out.println("Number of items: " + arr.length); //prints the number of values
				  for(double value : arr) {
				    System.out.println(value); //prints each value as part of testing
				  }
				  System.out.println("end of TSP test");
	}
	
	public static double[] new_TSP() {
		//this method generates a new TSP
		/*
		 * generates a 45 element array of numbers ranging from 0.1 to 1.0 , e.g. 0.7
These numbers should be in order and repeating once 1 is reached. so this would be 0.1 , ... , 0.9 , 1.0 , 0.1 etc
returns a 1-D array of type double
0.0 indicates it is not possible to travel from one city to another
		 */
		  double[] arr = new double[45];
		  double value = 0.1; // Start with 0.1
		  for(int i = 0; i < arr.length; i++) {
		    arr[i] = value; 
		    if(value >= 1.0) { // Check if previous value was 1.0, reset to 0.1 if so
		      value = 0.1; 
		    }
		    else {
		      value += 0.1; // Add 0.1 otherwise 
		      value = Math.round(value * 10) / 10.0; //rounding fixes a strange java error I saw with some values e.g. 1.4000000000000001
		    }
		    //System.out.println("debug, value = : " + value);
		  }
		  return arr;
		}
	
	public static double[][] convert_1D_to_2D(double[] array_1D)
	{
		  int n = array_1D.length; // Get size of 1D array
		  if (n < 1)
		  {// should never be less than 1 element, giving a 2x2 matrix
			  double[][] array_2D = new double[1][1];
			  System.out.println("convert_1D_to_2D(): less than 1 unique element, likely an error.");
			  return array_2D;
		  }
		double x; //get size of width / height (called x)
		double sqrtTerm = Math.sqrt(1 + 8*n); // Calculate square root term 
		x = (1 + sqrtTerm) / 2; // Calculate x
		//test this
		System.out.println("convert_1D_to_2D(): x = " + x);
		// Create 2D array with x by x size
		double[][] array_2D = new double[(int) x][(int) x]; //new 2D distance matrix
		int a = 0; //used to track position in the 1D array
		double b = 0; //using this to track array_1D values as we use less reads
		for(int i = 0; i < x -1; i++) { //x-1 because we ignore 1st 0 value which would be from a city to the same city
			  for(int j = i+1; j < x; j++) {
				b = array_1D[a];
			    array_2D[i][j] = b; //populate 2D values
			    array_2D[j][i] = b;
				//System.out.println("b = " + b + ", array_2D[i][j] = " + array_2D[i][j] + ", array_2D[j][i] = " + array_2D[j][i] + ", a = " + a + ", i = " + i + ", j = " + j);
			    a++; //increment a
			  }
			}
		return array_2D;
	}
	
	public static int[][] convertToIntGraph(double[] array_1D)
	{//The same thing as method above for ints
		  int n = array_1D.length; // Get size of 1D array
		  if (n < 1)
		  {// should never be less than 1 element, giving a 2x2 matrix
			  int[][] array_2D = new int[1][1];
			  System.out.println("convertToIntGraph(): less than 1 unique element, likely an error."); return array_2D;
		  }
		double x; //get size of width / height (called x)
		double sqrtTerm = Math.sqrt(1 + 8*n); // Calculate square root term 
		x = (1 + sqrtTerm) / 2; // Calculate x
		System.out.println("x = " + x);	//test this
		// Create 2D array with x by x size
		int[][] array_2D = new int[(int) x][(int) x]; //new 2D distance matrix
		int a = 0; //used to track position in the 1D array
		int b = 0; //using this to track array_1D values as we use less reads
		for(int i = 0; i < x -1; i++) { //x-1 because we ignore 1st 0 value which would be from a city to the same city
			  for(int j = i+1; j < x; j++) {
				b = (int)array_1D[a];
			    array_2D[i][j] = b; //populate 2D values
			    array_2D[j][i] = b;
				//System.out.println("b = " + b + ", array_2D[i][j] = " + array_2D[i][j] + ", array_2D[j][i] = " + array_2D[j][i] + ", a = " + a + ", i = " + i + ", j = " + j);
			    a++; //increment a
			  }
			}
		return array_2D;
	}
	
	public static void test_2D()
	{//this method tests the initial TSP generator by counting values and showing each value
		double[] array1D = new_TSP();
		  System.out.println("test_2D(): Number of items: " + array1D.length); //prints the number of values
		  for(double value : array1D) {
		    System.out.println(value); //prints each value as part of testing
		  }
		  System.out.println("test_2D(): end of TSP test");
		  double[][] array2D = convert_1D_to_2D(array1D);
		  print_2D(array2D);
	}
	
	public static void print_2D(double[][] array2D)
	{ // Prints the contents of 2D array to console
		int iPlusOne = 0;
		  int jPlusOne = 0;
		  //using PlusOne variables to display 1 to 10 rather than 0 to 9
		  //better way to do this but using this for now.
		// Loop through each row (outer loop)
		  for(int i = 0; i < array2D.length; i++) {
		    // Loop through each column (inner loop) 
		    for(int j = 0; j < array2D[i].length; j++) {
		      iPlusOne = i +1;
		      jPlusOne = j +1;
		      // Print value and position    
		      System.out.println("print_2D(): Value at x=" + iPlusOne + ", y=" + jPlusOne + ": " + array2D[i][j]);
		      
		    }
		  }
		//output to csv if we want to
		  /*
	      try {
			generate_CSV_from_2D(array2D);
		} catch (Exception e) {
			System.out.println("csv failed");
			e.printStackTrace();
		}
		*/
	}
	public static void generate_CSV_from_2D(double[][] matrix) throws Exception {
		for(int y = 0; y < matrix.length; y++) {
	        //csvLogger.addColumnCSV(String.valueOf(y)); 
	        for(int x = 0; x < matrix[y].length; x++){
	            //csvLogger.addColumnCSV(String.valueOf(x));  
	            csvLogger.addRowCSV(String.valueOf(matrix[y][x])) ;
	        }
	        csvLogger.addColumnCSV("");
	    }
	}
	
	public static void export_TXT_from_2D(double[][] matrix) throws Exception {
	    for(int y = 0; y < matrix.length; y++) {
	        for(int x = 0; x < matrix[y].length; x++){
	            txtLogger.printTXT(matrix[y][x] + "  ");  // Print value followed by 2 spaces
	        }
	        txtLogger.addLineTXT(""); // Extra newline after each row
	    }
	}
	
	public static String dateTime()
	  { //returns dateTime as string, useful for naming files.
		  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"); //should be accurate enough to avoid filename conflicts and tell us when we made the logs.
		  LocalDateTime now = LocalDateTime.now();
		  String dateTime = now.format(formatter);
		  return dateTime;
	  }
	
	public static void test_MST() throws IOException
	{// Tests our MST graph of our new TSP
		  double[] distances = new_TSP(); // make new TSP
		  double g[][] = convert_1D_to_2D(distances); //store as 2D
		  double mst[][] = MST.PrimsMST(g); //calculate MST
		  print_2D(mst); //print the contents to console
		  System.out.println("test_MST(): MST_total(mst) = " + MST_total(mst)); //Print the value to console
		}
	
	public static double total_2D(double[][] array2D)
	{ //A method that returns the sum of all values in a 2D double graph
		double total = 0.0; //track total
		for(int i = 0; i < array2D.length; i++) {
		    for(int j = 0; j < array2D[i].length; j++) {
		      total = array2D[i][j] + total;
		    }
		  }
		return total;
	}
	
	public static double MST_total(double[][] graph)
	{ //A method that returns the sum of weights in a 2D double graph. Uses the total_2D method.
		double total = total_2D(graph); //use the existing method to do this
		total = total / 2; //since we don't want to count values twice
		return total;
	}

	}


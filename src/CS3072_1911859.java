
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CS3072_1911859 {

	public static void main(String[] args) throws IOException {
		//System.out.println("Start of Program");
		//to test new_TSP:
		//test_new_TSP();
		//sets i - the number of iterations the TSP modifier will run
		//int i = 1000; //will test 1000 iterations
		//sets j - the number of iterations the TSP solver (algorithm 2) will run inside each i iteration
		//int j = 1000; //will test 1000 iterations
		//total runtime should be approximately proportional to i.j
		
		//test_2D();
		
		SolveTSP solver = new SolveTSP(new_TSP());
	}

	public static void test_new_TSP()
	{
		//this method tests the initial TSP generator by counting values and showing each value
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

		    // Check if previous value was 1.0, reset to 0.1 if so
		    if(value >= 1.0) {
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
		// Get size of 1D array
		  int n = array_1D.length;
		  if (n < 1)
		  {
			  // should never be less than 1 element, giving a 2x2 matrix
			  double[][] array_2D = new double[1][1];
			  System.out.println("less than 1 unique element, likely an error.");
			  return array_2D;
			  
		  }
		  //get size of width / height (called x)
		  double x;
		// Calculate square root term 
		double sqrtTerm = Math.sqrt(1 + 8*n);
		// Calculate x
		x = (1 + sqrtTerm) / 2;
		//test this
		System.out.println("x = " + x);
		
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
	public static void test_2D()
	{
		//this method tests the initial TSP generator by counting values and showing each value
		double[] array1D = new_TSP();
		  System.out.println("Number of items: " + array1D.length); //prints the number of values
		  for(double value : array1D) {
		    System.out.println(value); //prints each value as part of testing
		  }
		  System.out.println("end of TSP test");
		  double[][] array2D = convert_1D_to_2D(array1D);
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
		      System.out.println("Value at x=" + iPlusOne + ", y=" + jPlusOne + ": " + array2D[i][j]);
		      
		    }
		  }
		//output to csv
	      try {
			generate_CSV_from_2D(array2D);
		} catch (Exception e) {
			System.out.println("csv failed");
			e.printStackTrace();
		}

	}
	public static void generate_CSV_from_2D(double[][] matrix) throws Exception {
		//This method attempts to generate a CSV for the 2D array
		  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
		  LocalDateTime now = LocalDateTime.now();
		  String filename = "2D_output_" + dtf.format(now) + ".csv";
		  //Making the file names contain the date time so they are unique and easy to find
		  PrintWriter writer = new PrintWriter("C:\\Users\\james\\Documents\\School\\Uni\\Year 3\\CS3072 FYP\\Misc\\test_output_data\\" + filename);
		  // Loop through rows
		// Outer loop for rows (y coordinate)
		  System.out.println("matrix.length = " + matrix.length);
		  

		  for(int y = 0; y < matrix.length; y++) {
			//System.out.println("matrix[y].length = " + matrix[y].length);
		    // Inner loop for columns (x coordinate)
		    for(int x = 0; x < matrix[y].length; x++){

		      // Print x,y,value on one line
		      writer.print(x + "," + y + "," + matrix[y][x]);

		   // Add comma if not last column
		      if(x != matrix[y].length - 1) {
		        writer.print(","); 
		      }

		    }
		    
		 // Add newline after each row
		    writer.println(); 

		  }

			  // Join String array   
			  //String csvLine = String.join(",", csvRow);
			  //writer.println(csvLine);  
		  
		  writer.close();
		  
		}
	
	}


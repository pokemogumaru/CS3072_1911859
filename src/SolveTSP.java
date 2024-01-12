import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random; // used for swapping random values
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class SolveTSP { //this class is used for solving TSPs
  private double[] distances;
  private static FileWriterUtil basicLog;
  private static FileWriterUtil fullLog;
  private static FileWriterUtil hillClimberFitnessLog;
  private static FileWriterUtil fitnessRepeatsLog;
  public SolveTSP(double[] distances) throws IOException {
	//START OF PROGRAM OPEN FILES
	//will have these files: basic, full, hillClimberFitness
	//planning to use filename format: SolveTSP-(loggingType)-dateTime
	// Create file writer instance
	basicLog = new FileWriterUtil(dateTime() + " SolveTSP basicLog.txt", "txt"); basicLog.start(); //create basic log instance and start using the file
	fullLog = new FileWriterUtil(dateTime() + " SolveTSP fullLog.txt", "txt"); fullLog.start(); //create fullLog instance and start using the file
	hillClimberFitnessLog = new FileWriterUtil(dateTime() + " SolveTSP hillClimberFitnessLog.csv", "csv"); hillClimberFitnessLog.start(); //create hillClimberFitnessLog instance and start using the file
	fitnessRepeatsLog = new FileWriterUtil(dateTime() + " SolveTSP fitnessRepeatsLog.csv", "csv"); fitnessRepeatsLog.start(); //used to track fitness at end of each iteration so we can see best fitness trend for repeats
	
	this.distances = distances;
    int numCities = calculateCitiesAmount(distances); //calculate number of cities from the 1D array TSP
    List<Integer> initialSolution = generateInitialSolution(numCities); //generate an initial solution based on the number of cities
    //ArrayList<Integer> initialSolution = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9, 2, 10));
    //the above uses a custom initial solution that is valid, no longer needed
    tourPrinter(initialSolution); //prints our initial solution for testing
    //double initialCost = generateTourCost(initialSolution, distances); //the cost of our initial solution
    //removed the above line as the initial solution is likely invalid and will appear the best due to 0 values
    int iterations = 3000; //used for hill climber
    List<Integer> hcSolution = null; //defining hcSolution before loop
    int repeats = 100; //used to repeat hill climber testing
    double generateTourCost = 0; //defining this outside of loop so that we can calculate cost once rather than twice
    for (int i = 1; i <= repeats; i++)
    {
    	//System.out.println("loop start"); //used for debug
    	hcSolution = hillClimberSolver(numCities, initialSolution, iterations, distances);
        //hasZeroValue(initialSolution,distances);
        //System.out.println("Best tour after hill climber:");
        //tourPrinter(hcSolution);
        generateTourCost = generateTourCost(hcSolution, distances);
        //System.out.println("Best total cost (including return to start): " + generateTourCost);
        fitnessRepeatsLog.addColumnCSV(String.valueOf(generateTourCost)); 
        fitnessRepeatsLog.addRowCSV(String.valueOf(i));
    }
    System.out.println("End of repeats");
   
    //debug:
    /*
    System.out.println("debug, cost of 10 -> 9 -> 7 -> 5 -> 6 -> 2 -> 4 -> 1 -> 3 -> 8 -> 10:");
    ArrayList<Integer> shouldBe2point4 = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9, 2, 10));
    System.out.println(generateTourCost(shouldBe2point4,distances));
    */
    
    
    //tourPrinter(optimalTour); //to print the optimal tour
    //END OF PROGRAM CLOSE FILES
    basicLog.close(); //stop using the file for basic log
    fullLog.close(); //stop using the file for log
    hillClimberFitnessLog.close(); //stop using the file for log
    fitnessRepeatsLog.close(); //stop using the file for log
  }

  public static int calculateCitiesAmount(double[] distances) throws IOException {
	// Get size of 1D array
	  int n = distances.length;
	  if (n < 1)
	  {// should never be less than 1 element, giving a 2x2 matrix
		  System.out.println("less than 1 unique element, likely an error.");
		  return 0;
	  }
	double x; //get size of width / height (called x)
	double sqrtTerm = Math.sqrt(1 + 8*n); // Calculate square root term 
	x = (1 + sqrtTerm) / 2;	// Calculate x
	String calculateCitiesXValue = ("calculateCitiesAmount: " + "x = " + x); fullLog.addLineTXT(calculateCitiesXValue);
	//System.out.println(calculateCitiesXValue); //don't really need this unless testing, 45 element 1D array should print x = 10
	return (int) x;
	  }

  public List<Integer> generateInitialSolution(int numCities) {
	  // generates an initial solution based on the number of cities
	  //Note that this solution may not be possible since we don't check it against 0 value distances
	  //Also note that we will have each city once. This is important as we will perform random swaps later.
	  //Knowing each city is visited once means we meet this acceptance criteria for this and all future solutions
	  List<Integer> cities = new ArrayList<>();
	  for(int i = 1; i <= numCities; i++) {
	    cities.add(i); //basic loop to give us a starting solution
	  }
	  System.out.println("Cities list: " + cities); //used for testing method. Probably comment out later.
	  return cities;
	}

  public static double[][] convert_1D_to_2D(double[] array_1D) throws IOException
	{
		// Get size of 1D array
		  int n = array_1D.length;
		  if (n < 1)
		  {
			  // should never be less than 1 element, giving a 2x2 matrix
			  double[][] array_2D = new double[1][1];
			  System.out.println("convert_1D_to_2D: less than 1 unique element, likely an error.");
			  return array_2D;
			  
		  }
		  //get size of width / height (called x)
		  double x;
		// Calculate square root term 
		double sqrtTerm = Math.sqrt(1 + 8*n);
		// Calculate x
		x = (1 + sqrtTerm) / 2;
		//test this
		String xValue = ("convert_1D_to_2D: x = " + x); fullLog.addLineTXT(xValue);
		//System.out.println(xValue);
		
		// Create 2D array with x by x size
		double[][] array_2D = new double[(int) x][(int) x]; //new 2D distance matrix
		int a = 0; //used to track position in the 1D array
		double b = 0; //using this to track array_1D values as we use less reads
		for(int i = 0; i < x-1; i++) { //x-1 because we ignore 1st 0 value which would be from a city to the same city
			  for(int j = i+1; j < x; j++) {
				b = array_1D[a];
			    array_2D[i][j] = b; //populate 2D values
			    array_2D[j][i] = b;
			    a++; //increment a
			    
			  }
			}
		return array_2D;
	}
  
  public static double generateTourCost(List<Integer> solution, double[] distances) throws IOException{
	  //A method to generate the total cost of a TSP solution.
	  //Takes in:
	  //solution, an arrayList of integers representing the cities to visit in order
	  //distances, the 1D array of distance values (the TSP we are trying to solve)
	  //step 1 - make 2D representation of the TSP distances. 1D distances > 2D distances
	  double[][] TSP_2D = convert_1D_to_2D(distances);
	  //print_2D(TSP_2D); //if we want to test the 2D representation
	  
	  //step 2 - calculate number of cities
	 int cities = calculateCitiesAmount(distances);
	  //step 3 - loop through cities array and find total cost of travel. Include cost of returning to start
	 double totalCost = 0.0;
	 double cost;
	 for(int i = 0; i < cities -1 ; i++) {
	   int from = solution.get(i) - 1;
	   //System.out.println("solution.get(i) - 1 = " + (solution.get(i) -1));
	   int to = solution.get(i+1) -1;
	   //System.out.println("solution.get(i+1) - 1 = " + (solution.get(i+1) -1));
	   cost = TSP_2D[from][to];
	   String fromCityToCityCost = ("from city = " + (from+1) + " to city = " + (to+1)+ " cost " + (i+1) + " = " + cost); //for debugging or testing
	   fullLog.addLineTXT(fromCityToCityCost); //generates a lot of text, use only when needed
	   if(cost == 0.0) {
	     String warning0 = ("generateTourCost: warning, 0 value found! i = " + i); //should never happen
	     System.out.println(warning0);
	     basicLog.addLineTXT(warning0);
		fullLog.addLineTXT(warning0);
	   }
	   totalCost += cost;
	   //System.out.println("total cost before returning to start = " + totalCost);
	 }
	 // Return to start city 
	 String returning = "returning to start"; fullLog.addLineTXT(returning); //useful for full log
	 //System.out.println(returning); //not really worth printing
	 int start = solution.get(0);
	 int end = solution.get(cities-1);
	 cost = TSP_2D[end-1][start-1]; //-1 for each value java counts from 0
	 String startEndCostString = ("start = " + start + " , end = " + end + " , cost = " + cost);
	 fullLog.addLineTXT(startEndCostString);
	 //System.out.println(startString);
	 totalCost += cost; //this will be final cost for this solution
	 String totalCostString = ("total cost after returning to start = " + totalCost);
	 basicLog.addLineTXT(totalCostString);
	fullLog.addLineTXT(totalCostString);
	//System.out.println(totalCostString);
	//step 4 - return  total cost
	 return totalCost; // for example 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10 the cost 5.0 is correct!
  }
  
  public static void print_2D(double[][] TSP_2D)
  {//used to test a 2D array in a nicely formatted way
	  int iPlusOne = 0;
	  int jPlusOne = 0;
	  //using PlusOne variables to display 1 to 10 rather than 0 to 9
	  //better way to do this but using this for now.
	// Loop through each row (outer loop)
	  for(int i = 0; i < TSP_2D.length; i++) {

	    // Loop through each column (inner loop) 
	    for(int j = 0; j < TSP_2D[i].length; j++) {
	      iPlusOne = i +1; //we count from 1 so add 1 to x and y
	      jPlusOne = j +1;
	      // Print value and position    
	      System.out.println("Value at x=" + iPlusOne + ", y=" + jPlusOne + ": " + TSP_2D[i][j]);
	      
	    }
	  }
  }
  
  public static int[] lookup_2D_from_1D_position(int a, int n) {
	  //used to get the position of a value in the 2D array from the 1D index (for TSPs)
	  //a is 1D index. n is number of cities
	  //O(N) squared algorithm
	  int x = 0;
	  int y = 0;
	  for(int i = 1; i <= n; i++) {
	    for(int j = i + 1; j <= n; j++) {
	      
	      if(a == 0) { //then stop, we have found the position we were looking for
	        x = i;
	        y = j;
	        break;
	      }
	      else { //keep looking!
	        a--;
	      }
	    }
	  }
	  int[] solution = new int[2]; //used to return two values
	  solution[0] = x;
	  solution[1] = y;
	  return solution;

	}
  
  public static boolean hasZeroValue(List<Integer> solution, double[] distances) throws IOException{
	  //We shouldn't need this given every city is connected to every other city in TSP.
	  //A method to check if there are 0 values, similar structure to generateTourCost.
	  //Takes in:
	  //solution, an arrayList of integers representing the cities to visit in order
	  //distances, the 1D array of distance values (the TSP we are trying to solve)
	  //step 1 - make 2D representation of the TSP distances. 1D distances > 2D distances
	  double[][] TSP_2D = convert_1D_to_2D(distances);
	  //print_2D(TSP_2D); //if we want to test the 2D representation
	  
	  //step 2 - calculate number of cities
	 int cities = calculateCitiesAmount(distances);
	  //step 3 - loop through cities array and find total cost of travel. Include cost of returning to start
	 //double totalCost = 0.0;
	 double cost;
	 for(int i = 0; i < cities -1 ; i++) {
	   int from = solution.get(i) -1;
	   //System.out.println("from = " + (from+1));
	   int to = from + 1;
	   //System.out.println("to = " + (to+1));
	   cost = TSP_2D[from][to];
	   //System.out.println("cost " + (i+1) + " = " + cost);
	   if(cost == 0.0) {
		 System.out.println("hasZeroValue: i = " + i + ". from = " + from + ". to = " + to + ". cost = " + cost);
	     System.out.println("hasZeroValue has found a zero value, returning true.");
	     return true; //at this point we know there is at least one 0.0 value
	   }
	   //totalCost += cost;
	   //System.out.println("total cost = " + totalCost);
	 }
	 // Return to start city 
	 //System.out.println("returning to start");
	 int start = solution.get(0);
	 //System.out.println("start = " + start);
	 int end = solution.get(cities-1);
	 //System.out.println("end = " + end);
	 cost = TSP_2D[end-1][start-1]; //-1 for each value java counts from 0
	 if (cost == 0.0)
	 {
		 System.out.println("hasZeroValue has found a zero value, returning true. Found when returning to start");
	     return true; //at this point we know there is at least one 0.0 value
	 }
	 //System.out.println("cost = " + cost);
	 //totalCost += cost; //this will be final cost for this solution
	 //System.out.println("total cost = " + totalCost);
	//step 4 - return  total cost
	 String noZeroValues = "hasZeroValue has found NO zero values, returning false" ; basicLog.addLineTXT(noZeroValues); fullLog.addLineTXT(noZeroValues);
	 System.out.println(noZeroValues);
	 return false; // if we make it here then no zero values
  }
  
  public static List<Integer> hillClimberSolver(int numCities, List<Integer> initialSolution, int iterations, double[] distances) throws IOException {
	  //A method to solve TSPs using a basic hill climber
	  //takes in number of cities, start solution, number of iterations, distances (TSP)
	  ArrayList<Integer> solution = new ArrayList<>(initialSolution);
	  double cost = Double.MAX_VALUE;// max value so we accept any new solution
	  String start = "Starting hill climber solver loop"; basicLog.addLineTXT(start); fullLog.addLineTXT(start);
	  //System.out.println(start);
	  for(int i = 0; i < iterations; i++) {
	  String HCLoggerI = ("hillClimberSolver: i = " + i); basicLog.addLineTXT(HCLoggerI); fullLog.addLineTXT(HCLoggerI);
	    ArrayList<Integer> newSolution = new ArrayList<>(solution); 
	    
	    // Swap two random cities
	    Random random = new Random(); //should be between 0 and numCities
	    int indexA = random.nextInt(numCities);
	    String indexALogger = ("chosen at random: indexA = " + indexA); fullLog.addLineTXT(indexALogger);
	    
	    boolean same = true;
	    int indexB = random.nextInt(numCities);
	    while (same)
	    { //no point swapping a value with itself...
	    	indexB = random.nextInt(numCities);
	    	if (indexB != indexA)
	    	{
	    		same = false;
	    	}
	    }
	    
	    String indexBLogger = ("chosen at random: indexB = " + indexB); fullLog.addLineTXT(indexBLogger);
	    int temp = newSolution.get(indexA);
	    newSolution.set(indexA, newSolution.get(indexB));
	    newSolution.set(indexB, temp);

	    boolean condition1 = false;
	    boolean condition2 = false;
	      
	    double newCost = generateTourCost(newSolution, distances);
	    
	    if(newCost < cost) {
	      condition1 = true;
	    }
	    
	    //if(!hasZeroValue(newSolution, distances)) {condition2 = true;}
	    //commenting out above line as this should always be true if all cities are connected.
	    condition2 = true;
	    if(condition1 && condition2) { //update solution and cost
	      solution = (ArrayList<Integer>) newSolution.clone();
	      cost = newCost;
	    }
	    //record current best cost:
	    String currentBestCost = ("Current best cost = " + cost); basicLog.addLineTXT(currentBestCost); fullLog.addLineTXT(currentBestCost); //in text loggers
	    hillClimberFitnessLog.addColumnCSV(String.valueOf(cost)); 
	    hillClimberFitnessLog.addRowCSV(String.valueOf(i));
	  }
	  
	  if(cost == 999.9) {
	    String cost999 = "No valid solution found that beat the initial solution"; basicLog.addLineTXT(cost999); fullLog.addLineTXT(cost999);
		System.out.println(cost999);
	  }
	  else {
		String outBestCost = ("Best cost: "+ cost); basicLog.addLineTXT(outBestCost); fullLog.addLineTXT(outBestCost);
		//System.out.println(outBestCost);
	  }
	  String finished = ("Hil Climber Finished after " +iterations + " iterations!"); basicLog.addLineTXT(finished); fullLog.addLineTXT(finished);
	  //System.out.println(finished);
	  solution.add(solution.get(0)); // Return to start city
	  return solution;
	}
  
  
  
	  public static void tourPrinter(List<Integer> optimalTour)
	  {//used to print a tour. E.g. 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10
		  System.out.println("Tour:");
		  // Loop through each city in the optimal tour
		  for(int i = 0; i < optimalTour.size(); i++) {
		    System.out.print(optimalTour.get(i)); // Print current city number 
		    if(i != optimalTour.size() - 1) { // If not the last city, print connector
		      System.out.print(" -> ");
		    }
		  }
		  System.out.println();
	  }
	  
	  public static String dateTime()
	  { //returns dateTime as string, useful for naming files.
		  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"); //should be accurate enough to avoid filename conflicts and tell us when we made the logs.
		  LocalDateTime now = LocalDateTime.now();
		  String dateTime = now.format(formatter);
		  return dateTime;
	  }
	  
}
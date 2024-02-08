import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random; // used for swapping random values
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SolveTSP { //this class is used for solving TSPs
  private double[] distances;
  private double result = 0.0; //will be used to store the value of the result for other classes to access. If there are repeats, it will store the last result
  //change these to true or false depending on whether you want this logging, all other references to these logs will run or not run depending on these boolean values
  private static FileWriterUtil basicLog; private static boolean UsebasicLog = false;
  private static FileWriterUtil fullLog; private static boolean UseFullLog = false;
  private static FileWriterUtil hillClimberFitnessLog; private static boolean UsehillClimberFitnessLogLog = false;
  private static FileWriterUtil fitnessRepeatsLog; private static boolean UsefitnessRepeatsLog = false;
  
  public SolveTSP(double[] distances, int iterations) throws IOException {
	Timer timer = new Timer(); timer.start(); //make timer instance and start timing. Doing this before opening files.
	//START OF PROGRAM, OPEN FILES (if needed)
	//will have these files: basic, full, hillClimberFitness.
	//filename format: SolveTSP-(loggingType)-dateTime
	// Create file writer instance
	if (UsebasicLog) {basicLog = new FileWriterUtil(dateTime() + " SolveTSP basicLog.txt", "txt"); basicLog.start();} //create basic log instance and start using the file
	if (UseFullLog) {fullLog = new FileWriterUtil(dateTime() + " SolveTSP fullLog.txt", "txt"); fullLog.start();} //create fullLog instance and start using the file
	if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog = new FileWriterUtil(dateTime() + " SolveTSP hillClimberFitnessLog.csv", "csv"); hillClimberFitnessLog.start();}//create hillClimberFitnessLog instance and start using the file
	if (UsefitnessRepeatsLog) {fitnessRepeatsLog = new FileWriterUtil(dateTime() + " SolveTSP fitnessRepeatsLog.csv", "csv"); fitnessRepeatsLog.start();}//used to track fitness at end of each iteration so we can see best fitness trend for repeats
	//End of initialising log files
	
	this.distances = distances;
    int numCities = calculateCitiesAmount(distances); //calculate number of cities from the 1D array TSP
    List<Integer> initialSolution = generateInitialSolution(numCities); //generate an initial solution based on the number of cities
    //tourPrinter(initialSolution); //prints our initial solution for testing
    //double initialCost = generateTourCost(initialSolution, distances); //the cost of our initial solution
    //removed the above line as the initial solution is likely invalid and will appear the best due to 0 values
    List<Integer> hcSolution = null; //defining hcSolution before loop
    int repeats = 1; //used to repeat hill climber testing
    double generateTourCost = 0; //defining this outside of loop so that we can calculate cost once rather than twice
    for (int i = 1; i <= repeats; i++)
    {
    	//System.out.println("loop start"); //used for debug
    	hcSolution = hillClimberSolver(numCities, initialSolution, iterations, distances);
        //hasZeroValue(initialSolution,distances);
        //System.out.println("Best tour after hill climber:");
        //tourPrinter(hcSolution);
        generateTourCost = generateTourCost(hcSolution, distances, numCities);
        //System.out.println("Best total cost (including return to start): " + generateTourCost);
        if (UsefitnessRepeatsLog) {fitnessRepeatsLog.addColumnCSV(String.valueOf(generateTourCost)); fitnessRepeatsLog.addRowCSV(String.valueOf(i));}
    }
    //System.out.println("End of repeats");
    result = generateTourCost;
   
    //debug:
    /*
    System.out.println("debug, cost of 10 -> 9 -> 7 -> 5 -> 6 -> 2 -> 4 -> 1 -> 3 -> 8 -> 10:");
    ArrayList<Integer> shouldBe2point4 = new ArrayList<>(Arrays.asList(1, 3, 4, 5, 6, 7, 8, 9, 2, 10));
    System.out.println(generateTourCost(shouldBe2point4,distances));
    */
    
    timer.stop();String result = timer.getTotal();
    if (UsebasicLog) {BasicLog_AddLineTXT("The SolveTSP method took: " + result);} if (UseFullLog) {FullLog_AddLineTXT("The SolveTSP method took: " + result);}
    //add to text loggers, have to do this before closing files
    
    //tourPrinter(optimalTour); //to print the optimal tour
    //END OF PROGRAM, CLOSE FILES:
    if (UsebasicLog) {basicLog.close();} //stop using the file for basic log
    if (UseFullLog) {fullLog.close();} //stop using the file for log
    if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.close();} //stop using the file for log
    if (UsefitnessRepeatsLog) {fitnessRepeatsLog.close();} //stop using the file for log
  }

  public static int calculateCitiesAmount(double[] distances) throws IOException { return calculateCitiesPartial(distances.length); }// Get size of 1D array
  
  public static int calculateCitiesPartial(int n) throws IOException
  {
	  //System.out.println("calculateCitiesPartial: n = " + n); //debug
	  if (n < 1)
	  {// should never be less than 1 element, giving a 2x2 matrix
		  String error = ("SolveTSP: calculateCitiesAmount(): less than 1 unique element, likely an error.");
		  BasicLog_AddLineTXT(error); FullLog_AddLineTXT(error);
		  System.out.println(error);
		  return 0;
	  }
	  switch(n)
	  {//in most likely cases this should speedup rather than slow down
	  case 3:return 3;
	  case 6: return 4;
	  case 10:return 5;
	  case 45:return 10;
	  case 55:return 11;
	  case 66:return 12;
	  case 78:return 13;
	  case 91:return 14;
	  case 105:return 15;
	  }
	double x = (1 + Math.sqrt(1 + 8*n) ) / 2; //calculate x
	if (UseFullLog) {String calculateCitiesXValue = ("calculateCitiesAmount: " + "x = " + x); FullLog_AddLineTXT(calculateCitiesXValue);}
	//System.out.println(calculateCitiesXValue); //don't really need this unless testing, 45 element 1D array should print x = 10
	return (int) x;
  }

  public List<Integer> generateInitialSolution(int numCities) throws IOException {
	  // generates an initial solution based on the number of cities
	  //Note that we will have each city once. This is important as we will perform random swaps later.
	  //Knowing each city is visited once means we meet this acceptance criteria for this and all future solutions
	  List<Integer> cities = new ArrayList<>();
	  for(int i = 1; i <= numCities; i++) {cities.add(i);}//basic loop to give us a starting solution
	  Collections.shuffle(cities); // Shuffle cities list 
	  //for(Integer city : cities) {System.out.print(city + " "); } //debug
	  if (UseFullLog) {FullLog_AddLineTXT("SolveTSP: generateInitialSolution(): Cities list: " + cities);} //used for testing method. Just keeping in full log
	  return cities;
	}
  
  public static double[][] convert_1D_to_2D(double[] array_1D) throws IOException
	{//returns a 2D version of a 1D TSP array
		//System.out.println("convert_1D_to_2D: calculateCitiesAmount(array_1D) = " + calculateCitiesAmount(array_1D));//debug
		int x = calculateCitiesAmount(array_1D); // Get size of 1D array using calculateCitiesAmount method
		double[][] array_2D = new double[(int) x][(int) x]; // Create 2D array with x by x size
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
  
  public static double generateTourCost(List<Integer> solution, double[] distances, int cities) throws IOException{
	  //A method to generate the total cost of a TSP solution.
	  //Takes in:
	  //solution, an arrayList of integers representing the cities to visit in order
	  //distances, the 1D array of distance values (the TSP we are trying to solve)
	  //step 1 - make 2D representation of the TSP distances. 1D distances > 2D distances
	  double[][] TSP_2D = convert_1D_to_2D(distances);
	  //print_2D(TSP_2D); //if we want to test the 2D representation
	  //step 2 - calculate number of cities - skipping this as we now take this parameter in
	 //int cities = calculateCitiesAmount(distances);
	  //step 3 - loop through cities array and find total cost of travel. Include cost of returning to start
	 double totalCost = 0.0;
	 double cost;
	 for(int i = 0; i < cities -1 ; i++) {
	   int from = solution.get(i) - 1;
	   //System.out.println("solution.get(i) - 1 = " + (solution.get(i) -1));
	   int to = solution.get(i+1) -1;
	   //System.out.println("solution.get(i+1) - 1 = " + (solution.get(i+1) -1));
	   cost = TSP_2D[from][to];
	   if (UseFullLog) {String fromCityToCityCost = ("from city = " + (from+1) + " to city = " + (to+1)+ " cost " + (i+1) + " = " + cost);FullLog_AddLineTXT(fromCityToCityCost);}
	   if(cost == 0.0) {
	     String warning0 = ("generateTourCost: warning, 0 value found! i = " + i); //should never happen
	     System.out.println(warning0);
	     BasicLog_AddLineTXT(warning0); FullLog_AddLineTXT(warning0);
	   }
	   totalCost += cost;
	   //System.out.println("total cost before returning to start = " + totalCost);
	 }
	 // Return to start city 
	 if (UseFullLog) {String returning = "returning to start"; FullLog_AddLineTXT(returning);} //useful for full log
	 //System.out.println(returning); //debug only
	 int start = solution.get(0);
	 int end = solution.get(cities-1);
	 cost = TSP_2D[end-1][start-1]; //-1 for each value java counts from 0
	 if (UseFullLog) {String startEndCostString = ("start = " + start + " , end = " + end + " , cost = " + cost); FullLog_AddLineTXT(startEndCostString);}
	 //System.out.println(startString);
	 totalCost += cost; //this will be final cost for this solution
	 if (UseFullLog || UsebasicLog) {String totalCostString = ("total cost after returning to start = " + totalCost);BasicLog_AddLineTXT(totalCostString); FullLog_AddLineTXT(totalCostString);}
	//System.out.println(totalCostString);
	//step 4 - return  total cost
	 return totalCost; // for example 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10 the cost 5.0 is correct!
  }
  
  public static void print_2D(double[][] TSP_2D){CS3072_1911859.print_2D(TSP_2D);}
  //used to test a 2D array in a nicely formatted way. Use the existing method in other class
  
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
	 String noZeroValues = "hasZeroValue has found NO zero values, returning false" ; BasicLog_AddLineTXT(noZeroValues); FullLog_AddLineTXT(noZeroValues);
	 System.out.println(noZeroValues);
	 return false; // if we make it here then no zero values
  }
  
  public static List<Integer> hillClimberSolver(int numCities, List<Integer> initialSolution, int iterations, double[] distances) throws IOException {
	  //A method to solve TSPs using a basic hill climber
	  //takes in number of cities, start solution, number of iterations, distances (TSP)
	  ArrayList<Integer> solution = new ArrayList<>(initialSolution);
	  double cost = Double.MAX_VALUE;// max value so we accept any new solution
	  if (UseFullLog || UsebasicLog) {String start = "Starting hill climber solver loop"; BasicLog_AddLineTXT(start); FullLog_AddLineTXT(start);}
	  //System.out.println(start);
	  for(int i = 0; i < iterations; i++) {
		if (UseFullLog || UsebasicLog) {String HCLoggerI = ("hillClimberSolver: i = " + i); BasicLog_AddLineTXT(HCLoggerI); FullLog_AddLineTXT(HCLoggerI);}
	    ArrayList<Integer> newSolution = new ArrayList<>(solution); 
	    // Swap two random cities
	    Random random = new Random(); //should be between 0 and numCities
	    int indexA = random.nextInt(numCities);
	    if (UseFullLog) { String indexALogger = ("chosen at random: indexA = " + indexA); FullLog_AddLineTXT(indexALogger);} //checking if full log is true to save time in cases it's false
	    boolean same = true;
	    int indexB = random.nextInt(numCities);
	    while (same)
	    { //no point swapping a value with itself...
	    	indexB = random.nextInt(numCities);
	    	if (indexB != indexA){same = false;}
	    }
	    if (UseFullLog) { String indexBLogger = ("chosen at random: indexB = " + indexB); FullLog_AddLineTXT(indexBLogger); } //checking if full log is true to save time in cases it's false
	    int temp = newSolution.get(indexA);
	    newSolution.set(indexA, newSolution.get(indexB));
	    newSolution.set(indexB, temp);
	    //boolean condition1 = false;
	    //boolean condition2 = false;
	    double newCost = generateTourCost(newSolution, distances,numCities);
	    if(newCost < cost) { //update solution and cost
	      solution = (ArrayList<Integer>) newSolution.clone();
	      cost = newCost;
	    }
	    //record current best cost:
	    if (UseFullLog || UsebasicLog) {String currentBestCost = ("Current best cost = " + cost); BasicLog_AddLineTXT(currentBestCost); FullLog_AddLineTXT(currentBestCost);} //in text loggers
	    if (UsehillClimberFitnessLogLog)
	    {//doing this only if UsehillClimberFitnessLogLog is true cuts down on overhead in cases it's false
	    	HillClimberFitnessLog_addColumnCSV(String.valueOf(cost)); 
		    HillClimberFitnessLog_addRowCSV(String.valueOf(i));
	    }
	  }
	  if(cost == Double.MAX_VALUE) {
	    String cost999 = "No valid solution found that beat the initial solution"; BasicLog_AddLineTXT(cost999); FullLog_AddLineTXT(cost999);
		System.out.println(cost999);
	  }
	  else {if (UseFullLog || UsebasicLog) {String outBestCost = ("Best cost: "+ cost); BasicLog_AddLineTXT(outBestCost); FullLog_AddLineTXT(outBestCost);}}
	  if (UseFullLog || UsebasicLog) {String finished = ("Hil Climber Finished after " +iterations + " iterations!"); BasicLog_AddLineTXT(finished); FullLog_AddLineTXT(finished);}
	  solution.add(solution.get(0)); // Return to start city
	  return solution;
	}
  
	  public static void tourPrinter(List<Integer> optimalTour) throws IOException
	  {//used to print a tour. E.g. 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10
		  String out = "";
		  System.out.println("Tour:");
		  FullLog_AddLineTXT("Tour:");
		  for(int i = 0; i < optimalTour.size(); i++) {// Loop through each city in the optimal tour
		    System.out.print(optimalTour.get(i)); // Print current city number 
		    out += optimalTour.get(i);
		    if(i != optimalTour.size() - 1) { // If not the last city, print connector
		      System.out.print(" -> ");
		      out += " -> ";
		    }
		  }
		  System.out.println();
		  FullLog_AddLineTXT(out);
	  }
	  
	  public static String dateTime()
	  { //returns dateTime as string, useful for naming files.
		  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"); //should be accurate enough to avoid filename conflicts and tell us when we made the logs.
		  LocalDateTime now = LocalDateTime.now();
		  String dateTime = now.format(formatter);
		  return dateTime;
	  }
	  
	  public static void FullLog_AddLineTXT(String input) throws IOException {if (UseFullLog) {fullLog.addLineTXT(input);}} //add to log if logging variable true
	  public static void BasicLog_AddLineTXT(String input) throws IOException {if (UsebasicLog) {basicLog.addLineTXT(input);}}
	  public static void HillClimberFitnessLog_addColumnCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addColumnCSV(input);}}
	  public static void HillClimberFitnessLog_addRowCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addRowCSV(input);}}
	  public double return_solution() {return result;} //Intended to provide the result to other classes that require it (outer hill climber)
}
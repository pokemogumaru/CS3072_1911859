import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SolveTSP { //this class is used for solving TSPs
  private double[] distances;
  public SolveTSP(double[] distances) {
    this.distances = distances;
    int numCities = calculateCitiesAmount(distances); //calculate number of cities from the 1D array TSP
    List<Integer> initialSolution = generateInitialSolution(numCities); //generate an initial solution based on the number of cities
    tourPrinter(initialSolution);
    double initialCost = generateTourCost(initialSolution, distances);
    
    
    //List<List<Integer>> permutations = generatePermutations(numCities);
    //List<List<Integer>> validPerms = acceptanceVisited(permutations); 
  //TODO: add a middle step here where we add an element to each ArrayList
    //this element will be copied from the 1st element from the list
    //as this will mean we return to the start
    //this must be done after we check each city is visited once
    //but must be done before the possible check as the return to start must be possible
    //List<List<Integer>> possibleTours = acceptancePossible(validPerms, distances);
    //List<Integer> optimalTour = getOptimalTour(possibleTours, distances);
    //tourPrinter(optimalTour); //to print the optimal tour
    // optimalTour contains best solution
  }

  public static int calculateCitiesAmount(double[] distances) {
	// Get size of 1D array
	  int n = distances.length;
	  if (n < 1)
	  {// should never be less than 1 element, giving a 2x2 matrix
		  System.out.println("less than 1 unique element, likely an error.");
		  return 0;
	  }
	  double x; //get size of width / height (called x)
	// Calculate square root term 
	double sqrtTerm = Math.sqrt(1 + 8*n);
	// Calculate x
	x = (1 + sqrtTerm) / 2;
	System.out.println("calculateCitiesAmount:"); //used for testing
	System.out.println("x = " + x); //45 element 1D array should print x = 10
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
		for(int i = 0; i < x-2; i++) { //x-2 because we count from 0 and we ignore 1st 0 value which would be from a city to the same city
			  for(int j = i+1; j < x; j++) {
				b = array_1D[a];
			    array_2D[i][j] = b; //populate 2D values
			    array_2D[j][i] = b;
			    a++; //increment a
			    
			  }
			}
		return array_2D;
	}
  
  public static double generateTourCost(List<Integer> solution, double[] distances){
	  //A method to generate the total cost of a TSP solution.
	  //Takes in:
	  //solution, an arrayList of integers representing the cities to visit in order
	  //distances, the 1D array of distance values (the TSP we are trying to solve)
	  //step 1 - make 2D representation of the TSP distances. 1D distances > 2D distances
	  double[][] TSP_2D = convert_1D_to_2D(distances);
	  print_2D(TSP_2D); //if we want to test the 2D representation
	  
	  //step 2 - calculate number of cities
	 int cities = calculateCitiesAmount(distances);
	  //step 3 - loop through cities array and find total cost of travel. Include cost of returning to start
	 double totalCost = 0.0;
	 double cost;
	 for(int i = 0; i < cities -1 ; i++) {
	   int from = solution.get(i) -1;
	   System.out.println("from = " + (from+1));
	   int to = from + 1;
	   System.out.println("to = " + (to+1));
	   cost = TSP_2D[from][to];
	   System.out.println("cost " + (i+1) + " = " + cost);
	   if(cost == 0.0) {
	     System.out.println("warning, 0 value found");
	   }
	   totalCost += cost;
	   System.out.println("total cost = " + totalCost);
	 }
	 // Return to start city 
	 System.out.println("returning to start");
	 int start = solution.get(0);
	 System.out.println("start = " + start);
	 int end = solution.get(cities-1);
	 System.out.println("end = " + end);
	 cost = TSP_2D[end-1][start-1];
	 System.out.println("cost = " + cost);
	 totalCost += cost;
	 System.out.println("total cost = " + totalCost);
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
  
  
  
  
  
	  public List<List<Integer>> acceptanceVisited(List<List<Integer>> permutations) {
		//TODO test and possible revamp
		  //Will remove since initial solution includes each city once
	    List<List<Integer>> valid = new ArrayList<>();
	    for(List<Integer> perm : permutations) {
	      if(new HashSet<>(perm).size() == perm.size()) {
	    	  //hash set should be O(1)
	        valid.add(perm);
	      }
	    }
	    return valid;
	  }

	  public List<List<Integer>> acceptancePossible(List<List<Integer>> permutations, double[] distances) {
	    // this method should check if each tour is possible (no 0 weight routes)
		// not functional yet
		  //TODO implement & change
		return permutations; //TO CHANGE
	  }

	  public List<Integer> getOptimalTour(List<List<Integer>> possibleTours, double[] distances) {
	    //this method finds the tour with lowest weight
		//TODO test this method
	    double minDist = Double.MAX_VALUE;
	    List<Integer> optimalTour = null;
	    for(List<Integer> tour : possibleTours) {
	      double tourDist = 0;
	      // Calculate tour distance
	      if(tourDist < minDist) {
	        minDist = tourDist;
	        optimalTour = tour;
	      }
	    }
	    return optimalTour;
	  }
	  
	  public static void tourPrinter(List<Integer> optimalTour)
	  {//used to print a tour. E.g. 1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> 9 -> 10
		  //TODO also print the total cost/weight of this solution
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
}
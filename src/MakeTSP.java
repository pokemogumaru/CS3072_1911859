import java.io.IOException;
import java.util.Random;

public class MakeTSP {
//This class will be used to generate / modify TSPs to minimise / maximise their difficulty
//input: iterations (int), maximise_difficulty (boolean), TSP[] (double)
//should make small change to any non 0 value in the 1D tsp or both values in 2D version
	public MakeTSP(double[] distances, boolean DifficultTrueEasyFalse, int iterations) throws IOException
	{
		// do all the heavy lifting in seperate method, add file logging & timing after
		// step 1 - for iterations do something (done)
		//step 2 - within loop keep making small changes and compare current fitness to old fitness (likely some methods for this)
		//step 3 - finish with final TSP, best solveTSP result and MST
		HillClimbMakeTSP(distances, DifficultTrueEasyFalse, iterations);
	}
	
	public static void HillClimbMakeTSP(double[] distances, boolean MaxOrMin, int iterations) throws IOException
	{
		//1 get current MST
		double MST_value = roundTo1dp(GetMST(distances));
		System.out.println("MakeTSP: HillClimbMakeTSP(): MST_value = " + MST_value);
		//2 get current TSP total cost
		SolveTSP solver = new SolveTSP(distances); //to run the hill climber to solve a TSP
		double TSP_value = roundTo1dp(solver.return_solution());
		System.out.println("MakeTSP: HillClimbMakeTSP: TSP_value = " + TSP_value); //Prints the total weight of solution to console
		//3 calculate current value. Higher end value means easy TSP to solve, lower end value means hard TSP so solve
		double MSTdivTSP = MST_value / TSP_value; //Not rounding this to 1 dp as longer decimals can be expected here
		System.out.println("MakeTSP: HillClimbMakeTSP(): MSTdivTSP = " + MSTdivTSP);
		for (int i = 1; i <= iterations; i++)
	    {
			//4 make small change to distances[]
			//Before:
			//System.out.println("Number of items: " + distances.length); for(double value : distances){System.out.println(value);} //Debug: prints the number of values & prints each value
			//After:
			double[] new_distances = mutate(distances);
			//System.out.println("Number of items: " + new_distances.length); for(double value : new_distances){System.out.println(value);} //Debug: prints the number of values & prints each value
			//5 reevaluate MST / TSP value
			double new_MST_value = roundTo1dp(GetMST(new_distances));
			System.out.println("MakeTSP: HillClimbMakeTSP(): new_MST_value = " + new_MST_value);
			solver = new SolveTSP(distances); //to run the hill climber to solve a TSP
			double new_TSP_value = roundTo1dp(solver.return_solution());
			System.out.println("MakeTSP: HillClimbMakeTSP: new_TSP_value = " + new_TSP_value);
			double new_MSTdivTSP = new_MST_value / new_TSP_value;
			System.out.println("MakeTSP: HillClimbMakeTSP(): new_MSTdivTSP = " + new_MSTdivTSP);
			//6 compare old and new value and make change if needed
			
			//7 print final distances[], MST, TSP cost and MST/TSP value
			
	    }
	}
	
	public static double GetMST(double[] distances)
	{
		double g[][] = convert_1D_to_2D(distances); //store as 2D
		double mst[][] = MST.PrimsMST(g); //calculate MST
		double answer = MST_total(mst);
		return answer;
	}
	
	public static double total_2D(double[][] array2D)
	{ //A method that returns the sum of all values in a 2D double graph
		double total = 0.0; //track total
		for(int i = 0; i < array2D.length; i++) {
		    for(int j = 0; j < array2D[i].length; j++) {
		      total += array2D[i][j];
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
		//System.out.println("convert_1D_to_2D(): x = " + x);
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
	
	public static double[] mutate(double[] array)
	{//randomly mutate a value in a 1D double array within a range
		  int index = new Random().nextInt(array.length); // Get random index
		  boolean valid = false;
		  double newValue = 0;
		  while (!valid)
		  {
			  newValue = roundTo1dp((double)((new Random().nextInt(10)) * 0.1)); // Generate random value between 0.1 to 1.0 in 0.1 increments
			  if (newValue > 0.0 && newValue < 1.1)
			  {//doing this for now to catch bad values
				  valid = true;
			  }
			  else
			  {
				  System.out.println("mutate(): Caught bad value. Will generate another value.");
			  }
		  }
		  System.out.println("mutate(): Swapping position: " + index + " current value: " + array[index] + " to new value: " + newValue);
		  array[index] = newValue; // Mutate value at index
		  return array; //return this
		}
	
	public static double roundTo1dp(double num){
		  // Multiply by 10 and round to long
		  long rounded = Math.round(num * 10); 
		  // Divide by 10 to get back to 1 dp
		  return (double)rounded / 10;
		}
}

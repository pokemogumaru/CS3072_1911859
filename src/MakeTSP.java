import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class MakeTSP {
//This class will be used to generate / modify TSPs to minimise / maximise their difficulty
//should make small change to any non 0 value in the 1D tsp or both values in 2D version
	private static FileWriterUtil basicLog; private static boolean UsebasicLog = true;
	private static FileWriterUtil fullLog; private static boolean UseFullLog = true;
	private static FileWriterUtil hillClimberFitnessLog; private static boolean UsehillClimberFitnessLogLog = true;
	private static FileWriterUtil fitnessRepeatsLog; private static boolean UsefitnessRepeatsLog = true;
	public MakeTSP(double[] distances, boolean DifficultTrueEasyFalse, int iterations, int repeats, int SolveIterations) throws IOException
	{
		Timer timer = new Timer(); timer.start(); //make timer instance and start timing. Doing this before opening files.
		//Open files:
		if (UsebasicLog) {basicLog = new FileWriterUtil(dateTime() + " MakeTSP basicLog.txt", "txt"); basicLog.start();} //create basic log instance and start using the file
		if (UseFullLog) {fullLog = new FileWriterUtil(dateTime() + " MakeTSP fullLog.txt", "txt"); fullLog.start();} //create fullLog instance and start using the file
		if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog = new FileWriterUtil(dateTime() + " MakeTSP hillClimberFitnessLog.csv", "csv"); hillClimberFitnessLog.start();}//create hillClimberFitnessLog instance and start using the file
		if (UsefitnessRepeatsLog) {fitnessRepeatsLog = new FileWriterUtil(dateTime() + " MakeTSP fitnessRepeatsLog.csv", "csv"); fitnessRepeatsLog.start();}//used to track fitness at end of each iteration so we can see best fitness trend for repeats
		//End of initialising log files
		String start_values = "MakeTSP: starting with DifficultTrueEasyFalse = " + DifficultTrueEasyFalse + " Outer iterations = " + iterations + " Inner iterations = " + SolveIterations;
		BasicLog_AddLineTXT(start_values); FullLog_AddLineTXT(start_values);
		for (int i = 1; i <= repeats; i++)
	    {
			System.out.println("Doing repeat number " + i);
			HillClimbMakeTSP(distances, DifficultTrueEasyFalse, iterations, SolveIterations);
	    }
		String finished_repeats = ("Finished doing (" + repeats + ") repeats");BasicLog_AddLineTXT(finished_repeats); FullLog_AddLineTXT(finished_repeats);
		timer.stop();String result = timer.getTotal();
	    String result1 = ("The SolveTSP method took: " + result);BasicLog_AddLineTXT(result1); FullLog_AddLineTXT(result1); //add to text loggers, have to do this before closing files
		//Close files:
		if (UsebasicLog) {basicLog.close();} //stop using the file for basic log
	    if (UseFullLog) {fullLog.close();} //stop using the file for log
	    if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.close();} //stop using the file for log
	    if (UsefitnessRepeatsLog) {fitnessRepeatsLog.close();} //stop using the file for log
	}
	
	public static void HillClimbMakeTSP(double[] distances, boolean MaxOrMin, int iterations, int SolveIterations) throws IOException
	{
		double MST_value = roundTo1dp(GetMST(distances)); //1 get current MST
		SolveTSP solver = new SolveTSP(distances, SolveIterations); //to run the hill climber to solve a TSP
		double TSP_value = roundTo1dp(solver.return_solution()); //2 get current TSP total cost
		double MSTdivTSP = MST_value / TSP_value; //3 calculate current value. Higher value means easy TSP to solve, lower end value means hard TSP so solve.Not rounding this to 1 dp as longer decimals can be expected here
		String start = ("MakeTSP: HillClimbMakeTSP(): MST_value = " + MST_value + " TSP_value = " + TSP_value + " MSTdivTSP = " + MSTdivTSP);BasicLog_AddLineTXT(start); FullLog_AddLineTXT(start);
		int changes = 0; //track how many changes we kept
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
			solver = new SolveTSP(distances, SolveIterations); //to run the hill climber to solve a TSP
			double new_TSP_value = roundTo1dp(solver.return_solution());
			double new_MSTdivTSP = new_MST_value / new_TSP_value;
			String temp_str = ("temp MST: " + new_MST_value + " temp TSP cost: " + new_TSP_value + " temp MST/TSP value: " + new_MSTdivTSP);BasicLog_AddLineTXT(temp_str); FullLog_AddLineTXT(temp_str);
			//6 compare old and new value and make change if needed
			if ( ((MaxOrMin) && (new_MSTdivTSP < MSTdivTSP)) || ((!MaxOrMin) && (new_MSTdivTSP > MSTdivTSP)) )
			{//We want harder TSPs and we found a harder TSP OR we want easier TSPs and we found a easier TSP
				distances = new_distances; //Update this variable to climb the hill
				TSP_value = new_TSP_value;
				MST_value = new_MST_value;
				MSTdivTSP = new_MSTdivTSP; //Update this variable to climb the hill
				String madeChange = ("( ((MaxOrMin) && (new_MSTdivTSP < MSTdivTSP)) || ((!MaxOrMin) && (new_MSTdivTSP > MSTdivTSP)) ) is true, made a change");
				BasicLog_AddLineTXT(madeChange); FullLog_AddLineTXT(madeChange);
				changes++;
				String changesSoFar = ("changes made to TSP so far: " + changes); BasicLog_AddLineTXT(changesSoFar); FullLog_AddLineTXT(changesSoFar);
				HillClimberFitnessLog_addColumnCSV(String.valueOf(changes)); //Doing this inside the if statement so we only record when there is a change made
			    HillClimberFitnessLog_addRowCSV(String.valueOf(MST_value));
			    HillClimberFitnessLog_addRowCSV(String.valueOf(TSP_value));
			    HillClimberFitnessLog_addRowCSV(String.valueOf(MSTdivTSP));
			}
			else {String noChange = ("made no change");FullLog_AddLineTXT(noChange);String changesSoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(changesSoFar);}
			
	    }
		//7 print final distances[], MST, TSP cost and MST/TSP value
		//MST_value = roundTo1dp(GetMST(distances)); //no longer needed
		//solver = new SolveTSP(distances); //to run the hill climber to solve a TSP //see comment below
		//TSP_value = roundTo1dp(solver.return_solution()); // no longer doing this here as the solution can vary
		//MSTdivTSP = MST_value / TSP_value; //Not rounding this to 1 dp as longer decimals can be expected here
		String totalChanges = ("total made to TSP: " + changes); BasicLog_AddLineTXT(totalChanges); FullLog_AddLineTXT(totalChanges);
		String end = ("final MST: " + MST_value + " final TSP cost: " + TSP_value + " final MST/TSP value: " + MSTdivTSP); BasicLog_AddLineTXT(end); FullLog_AddLineTXT(end);
		String finalDistances = "final distance array after: " + Arrays.toString(distances); BasicLog_AddLineTXT(finalDistances); FullLog_AddLineTXT(finalDistances);
		System.out.println(end);
	}
	
	public static double GetMST(double[] distances){return (MST_total(MST.PrimsMST(convert_1D_to_2D(distances))));}
	
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
	
	
	public static double MST_total(double[][] graph){ return (total_2D(graph) / 2.0);} //A method that returns the sum of weights in a 2D double graph. 
	
	public static double[][] convert_1D_to_2D(double[] array_1D)
	{//borrowed from CS3072_1911859 class
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
	
	public static double[] mutate(double[] array) throws IOException
	{//randomly mutate a value in a 1D double array within a range
		  int index = new Random().nextInt(array.length); // Get random index
		  boolean valid = false;
		  double newValue = 0;
		  while (!valid)
		  {
			  newValue = roundTo1dp((double)((new Random().nextInt(10)) * 0.1)); // Generate random value between 0.1 to 1.0 in 0.1 increments
			  if ( (newValue > 0.0 && newValue < 1.1) && (newValue != array[index])) {valid = true;}//doing this for now to catch bad values
			  else {String mutate_bad_val = ("mutate(): Caught bad value. Will generate another value.");FullLog_AddLineTXT(mutate_bad_val); }
		  }
		  String mutate_out = ("mutate(): Swapping position: " + index + " current value: " + array[index] + " to new value: " + newValue); FullLog_AddLineTXT(mutate_out);
		  array[index] = newValue; // Mutate value at index
		  return array; //return this
		}
	
	public static double roundTo1dp(double num){long rounded = Math.round(num * 10); return (double)rounded / 10;}
	// Multiply by 10 and round to long. Divide by 10 to get back to 1 dp
	
	public static String dateTime(){ return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));}
	//returns dateTime as string, useful for naming files. should be accurate enough to avoid filename conflicts.
	
	public static void FullLog_AddLineTXT(String input) throws IOException {if (UseFullLog) {fullLog.addLineTXT(input);}} //add to log if logging variable true
	public static void BasicLog_AddLineTXT(String input) throws IOException {if (UsebasicLog) {basicLog.addLineTXT(input);}}
	public static void HillClimberFitnessLog_addColumnCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addColumnCSV(input);}}
	public static void HillClimberFitnessLog_addRowCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addRowCSV(input);}}
}

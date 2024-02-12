import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class MakeTSP {
//This class will be used to generate / modify TSPs to minimise / maximise their difficulty
//should make small change to any non 0 value in the 1D tsp or both values in 2D version
	private static double classFitness; //final fitness
	private static double startFitness; //starting fitness, used by csv repeat logger
	private static double iterationOfLastChange; //uses to track when the last change was made.
	private static double totalChangesMade; //used by csv repeat logger
	private static String[] classDistancesRepeats; //for other classes to use
	private static double[] classFitnessRepeats; //for other classes to use
	private static double[] classDistances; //so MakeTSP can get the distance matrix from the HC
	private static FileWriterUtil basicLog; private static boolean UsebasicLog = true;
	private static FileWriterUtil fullLog; private static boolean UseFullLog = true;
	private static FileWriterUtil hillClimberFitnessLog; private static boolean UsehillClimberFitnessLogLog = true;
	private static FileWriterUtil fitnessRepeatsLog; private static boolean UsefitnessRepeatsLog = true;
	public MakeTSP(int NumCities, boolean DifficultTrueEasyFalse, int iterations, int repeats, int SolveIterations, String type, double val1, double val2, int populationSize) throws IOException
	{
		//val1: initialTemp in SA, crossoverRate in GA. val2: coolingRate in SA, mutationRate in GA
		//System.out.println("MakeTSP: NumCities = " + NumCities); //debug
		String[] distanceRepeats = new String[repeats];
		double[] fitnessRepeats = new double[repeats];
		Timer timer = new Timer(); timer.start(); //make timer instance and start timing. Doing this before opening files.
		//Open files:
		openFiles(NumCities,DifficultTrueEasyFalse,iterations,SolveIterations);
		//End of initialising log files
		String start_values = "MakeTSP: starting with DifficultTrueEasyFalse = " + DifficultTrueEasyFalse + " Outer iterations = " + iterations + " Inner iterations = " + SolveIterations + " Number of cities = " + NumCities + " type = " + type;
		
		BasicLog_AddLineTXT(start_values); FullLog_AddLineTXT(start_values);
		if (type.equals("HC"))
		{
			//do basic hill climber
			for (int i = 1; i <= repeats; i++)
		    {
				String doing = ("Doing repeat number " + i); BasicLog_AddLineTXT(doing); FullLog_AddLineTXT(doing);
				String searchable = ("Searchable: repeat" + i); BasicLog_AddLineTXT(searchable); FullLog_AddLineTXT(searchable);
				double[] TSP = CS3072_1911859.new_TSP(NumCities); //doing this each time so we get a new TSP each repeat
				HillClimbMakeTSP(TSP, DifficultTrueEasyFalse, iterations, SolveIterations);
				if (UsefitnessRepeatsLog) {fitnessRepeatsLogger(i);} //records values per repeat in csv log
				fitnessRepeats[i-1] = classFitness;
				distanceRepeats[i-1] = Arrays.toString(classDistances);
				
		    }
		}
		else if (type.equals("SA"))
		{
			//do SA
			String start_values2 =  " initialTemp = " + val1 + " coolingRate = " + val2;
			BasicLog_AddLineTXT(start_values2); FullLog_AddLineTXT(start_values2);
			for (int i = 1; i <= repeats; i++)
		    {
				String doing = ("Doing repeat number " + i); BasicLog_AddLineTXT(doing); FullLog_AddLineTXT(doing);
				String searchable = ("Searchable: repeat" + i); BasicLog_AddLineTXT(searchable); FullLog_AddLineTXT(searchable);
				double[] TSP = CS3072_1911859.new_TSP(NumCities); //doing this each time so we get a new TSP each repeat
				SimulatedAnnealingMakeTSP(TSP, DifficultTrueEasyFalse, iterations, SolveIterations, val1, val2); //val1 initialTemp, val2 coolingRate
				if (UsefitnessRepeatsLog) {fitnessRepeatsLogger(i);} //records values per repeat in csv log
				fitnessRepeats[i-1] = classFitness;
				distanceRepeats[i-1] = Arrays.toString(classDistances);
				
		    }
		}
		else if (type.equals("GA"))
		{
			//do GA
			String start_values2 =  " crossoverRate = " + val1 + " mutationRate = " + val2 + " populationSize = " + populationSize;
			BasicLog_AddLineTXT(start_values2); FullLog_AddLineTXT(start_values2);
			for (int i = 1; i <= repeats; i++)
		    {
				String doing = ("Doing repeat number " + i); BasicLog_AddLineTXT(doing); FullLog_AddLineTXT(doing);
				String searchable = ("Searchable: repeat" + i); BasicLog_AddLineTXT(searchable); FullLog_AddLineTXT(searchable);
				double[] TSP = CS3072_1911859.new_TSP(NumCities); //doing this each time so we get a new TSP each repeat
				GeneticHillClimbMakeTSP(TSP, DifficultTrueEasyFalse, iterations, SolveIterations, populationSize, val1, val2 ); //val1 , val2 
				//double[] distances, boolean MaxOrMin, int iterations, int SolveIterations, int populationSize, double crossoverRate, double mutationRate
				if (UsefitnessRepeatsLog) {fitnessRepeatsLogger(i);} //records values per repeat in csv log
				fitnessRepeats[i-1] = classFitness;
				distanceRepeats[i-1] = Arrays.toString(classDistances);
				
		    }
		}
		else
		{
			System.out.println("LOUD WARNING MakeTSP: type not valid option");
		}
		
		classDistancesRepeats = distanceRepeats;
		classFitnessRepeats = fitnessRepeats;
		String finished_repeats = ("Finished doing (" + repeats + ") repeats");BasicLog_AddLineTXT(finished_repeats); FullLog_AddLineTXT(finished_repeats);
		timer.stop();String result = timer.getTotal();
	    String result1 = ("The SolveTSP method took: " + result);BasicLog_AddLineTXT(result1); FullLog_AddLineTXT(result1); //add to text loggers, have to do this before closing files
		//Close files:
	    closeFiles();
	}
	
	public static void HillClimbMakeTSP(double[] distances, boolean MaxOrMin, int iterations, int SolveIterations) throws IOException
	{
		double MST_value = GetMST(distances); //1 get current MST
		SolveTSP solver = new SolveTSP(distances, SolveIterations); //to run the hill climber to solve a TSP
		double TSP_value = solver.return_solution(); //2 get current TSP total cost
		double MSTdivTSP = MST_value / TSP_value; //3 calculate current value. Higher value means easy TSP to solve, lower end value means hard TSP so solve.Not rounding this to 1 dp as longer decimals can be expected here
		String start = ("MakeTSP: HillClimbMakeTSP(): starting MST_value = " + MST_value + " starting TSP_value = " + TSP_value + " starting MSTdivTSP = " + MSTdivTSP);
		BasicLog_AddLineTXT(start); FullLog_AddLineTXT(start);
		startFitness = MSTdivTSP;
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
			double new_MST_value = GetMST(new_distances);
			solver = new SolveTSP(distances, SolveIterations); //to run the hill climber to solve a TSP
			double new_TSP_value = solver.return_solution();
			double new_MSTdivTSP = new_MST_value / new_TSP_value;
			if (UsebasicLog) {String temp_str = ("temp MST: " + new_MST_value + " temp TSP cost: " + new_TSP_value + " temp MST/TSP value: " + new_MSTdivTSP);
			FullLog_AddLineTXT(temp_str);}
			//6 compare old and new value and make change if needed
			if ( ((MaxOrMin) && (new_MSTdivTSP < MSTdivTSP)) || ((!MaxOrMin) && (new_MSTdivTSP > MSTdivTSP)) )
			{//We want harder TSPs and we found a harder TSP OR we want easier TSPs and we found a easier TSP
				distances = new_distances; //Update this variable to climb the hill
				TSP_value = new_TSP_value;
				MST_value = new_MST_value;
				MSTdivTSP = new_MSTdivTSP; //Update this variable to climb the hill
				if (UseFullLog) {String madeChange = ("( ((MaxOrMin) && (new_MSTdivTSP < MSTdivTSP)) || ((!MaxOrMin) && (new_MSTdivTSP > MSTdivTSP)) ) is true, made a change");
				 FullLog_AddLineTXT(madeChange);}
				changes++;
				if (UseFullLog) {String changesSoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(changesSoFar);}
				if (UsehillClimberFitnessLogLog)
				{//Doing this inside the if statement so we only record when there is a change made
					HillClimberFitnessLog_addRowCSV(String.valueOf(changes)); 
				    HillClimberFitnessLog_addRowCSV(String.valueOf(MST_value));
				    HillClimberFitnessLog_addRowCSV(String.valueOf(TSP_value));
				    HillClimberFitnessLog_addColumnCSV(String.valueOf(MSTdivTSP));
				}
				iterationOfLastChange = i; //update iterationOfLastChange
			}
			else {if (UseFullLog){String noChange = ("made no change");FullLog_AddLineTXT(noChange);String SoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(SoFar);}}
			
	    }
		//7 print final distances[], MST, TSP cost and MST/TSP value
		//MST_value = roundTo1dp(GetMST(distances)); //no longer needed
		//solver = new SolveTSP(distances); //to run the hill climber to solve a TSP //see comment below
		//TSP_value = roundTo1dp(solver.return_solution()); // no longer doing this here as the solution can vary
		//MSTdivTSP = MST_value / TSP_value; //Not rounding this to 1 dp as longer decimals can be expected here
		String totalChanges = ("total changes made to TSP: " + changes); BasicLog_AddLineTXT(totalChanges); FullLog_AddLineTXT(totalChanges);
		String end = ("final MST: " + MST_value + " final TSP cost: " + TSP_value + " final MST/TSP value: " + MSTdivTSP); BasicLog_AddLineTXT(end); FullLog_AddLineTXT(end);
		String finalDistances = "final distance array after: " + Arrays.toString(distances); BasicLog_AddLineTXT(finalDistances); FullLog_AddLineTXT(finalDistances);
		//System.out.println(end);
		totalChangesMade = changes; finalDistances = Arrays.toString(distances); //used for loggers
		classFitness = MSTdivTSP;
		classDistances = distances;
	}
	
	public static void SimulatedAnnealingMakeTSP(double[] distances, boolean MaxOrMin, int iterations, int SolveIterations, double initialTemperature, double coolingRate) throws IOException {
	    double MST_value = GetMST(distances); // Calculate initial MST value
	    SolveTSP solver = new SolveTSP(distances, SolveIterations); // Initialize TSP solver
	    double TSP_value = solver.return_solution(); // Calculate initial TSP value
	    double MSTdivTSP = MST_value / TSP_value; // Calculate initial MST/TSP ratio
	    String start = ("MakeTSP: SimulatedAnnealingMakeTSP(): starting MST_value = " + MST_value + " starting TSP_value = " + TSP_value + " starting MSTdivTSP = " + MSTdivTSP);
	    BasicLog_AddLineTXT(start);
	    FullLog_AddLineTXT(start);
	    startFitness = MSTdivTSP;
	    int changes = 0; // Track number of changes made
	    for (int i = 1; i <= iterations; i++) {
	        double[] new_distances = mutate(distances); // Generate a new candidate solution by making a small change to the current solution
	        double new_MST_value = GetMST(new_distances); // Evaluate MST value of the new candidate solution
	        solver = new SolveTSP(distances, SolveIterations); // Solve TSP for the new candidate solution
	        double new_TSP_value = solver.return_solution();
	        double new_MSTdivTSP = new_MST_value / new_TSP_value; // Calculate MST/TSP ratio for the new candidate solution
	        // Calculate acceptance probability based on current and new fitness values and temperature
	        double temperature = initialTemperature / Math.log(1 + i);
	        double acceptanceProbability = acceptanceProbability(MSTdivTSP, new_MSTdivTSP, temperature);
	        double random = Math.random();
	        if ((MaxOrMin && acceptanceProbability > random ) || (!MaxOrMin && acceptanceProbability < random)) {
	            // Determine whether to accept the new solution based on MaxOrMin & acceptance probability
	        	// Update current solution
	        	distances = new_distances;
	            TSP_value = new_TSP_value;
	            MST_value = new_MST_value;
	            MSTdivTSP = new_MSTdivTSP;
	            changes++;
	            iterationOfLastChange = i; // Update iterationOfLastChange
	            if (UseFullLog) {String madeChange = ("((MaxOrMin && acceptanceProbability > random ) || (!MaxOrMin && acceptanceProbability < random)) is true, made a change");
				 FullLog_AddLineTXT(madeChange);}
				changes++;
				if (UseFullLog) {String changesSoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(changesSoFar);}
				if (UsehillClimberFitnessLogLog)
				{//Doing this inside the if statement so we only record when there is a change made
					HillClimberFitnessLog_addRowCSV(String.valueOf(changes)); 
				    HillClimberFitnessLog_addRowCSV(String.valueOf(MST_value));
				    HillClimberFitnessLog_addRowCSV(String.valueOf(TSP_value));
				    HillClimberFitnessLog_addColumnCSV(String.valueOf(MSTdivTSP));
				}
	        }
	    }

	    String totalChanges = ("total changes made to TSP: " + changes); // Log total changes made
	    BasicLog_AddLineTXT(totalChanges);
	    FullLog_AddLineTXT(totalChanges);
	    String end = ("final MST: " + MST_value + " final TSP cost: " + TSP_value + " final MST/TSP value: " + MSTdivTSP);
	    BasicLog_AddLineTXT(end);
	    FullLog_AddLineTXT(end);
	    String finalDistances = "final distance array after: " + Arrays.toString(distances); // Log final values
	    BasicLog_AddLineTXT(finalDistances);
	    FullLog_AddLineTXT(finalDistances);
	    totalChangesMade = changes; // Store total changes made and final distances for logging
	    finalDistances = Arrays.toString(distances);
	    classFitness = MSTdivTSP;
	    classDistances = distances;
	}

	private static double acceptanceProbability(double currentFitness, double newFitness, double temperature) {
		// Function to calculate acceptance probability for Simulated Annealing
	    if (newFitness > currentFitness) {
	        return 1.0; // Always accept a better solution
	    }
	    // Accept worse solutions with a certain probability determined by temperature
	    return Math.exp((newFitness - currentFitness) / temperature);
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
	{
		  int n = array_1D.length; // Get size of 1D array
		  //System.out.println("convert_1D_to_2D(): n = " + n); //debug
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
		//System.out.println("convert_1D_to_2D(): x = " + x); //debug
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
	{//small change to random value
		  int index = new Random().nextInt(array.length); // Get random index
		  boolean valid = false;
		  double newValue = 0;
		  int invalidCount = 0;
		  while (!valid)
		  {
			  double min = Double.MIN_VALUE;
			  double max = 0.00001;
			  newValue = min + (max - min) * new Random().nextDouble(); // Generate random double between minimum to max
			  //System.out.println(newValue);
			  Random random = new Random();
			  boolean randomBoolean = random.nextBoolean();
			  //System.out.println(randomBoolean);
			  if ( (randomBoolean && (newValue + array[index] < 1.0)) || (!randomBoolean && (array[index] - newValue < 0.00)) )
				//if (random boolean true and new value will be < 1 then add small change) or (boolean false and newValue < 0 )
			  {newValue += array[index];}
			  else{newValue -= array[index];} //else subtract small change
			  if ( (newValue > 0.0 && newValue < 1.0) && (newValue != array[index])) {valid = true;}//doing this for now to catch bad values
			  else {invalidCount += 1;String mutate_bad_val = ("mutate(): Caught bad value. Will generate another value.");FullLog_AddLineTXT(mutate_bad_val); 
			  if (invalidCount == 100)
			  {//this if statement will slow the program slightly but is needed for debug. Should remove when root cause is fixed.
				  String invalidCountWarning = "LOUD Warning: invalidCount = 100. This likely indicates an infinite loop here.";
				  BasicLog_AddLineTXT(invalidCountWarning); FullLog_AddLineTXT(invalidCountWarning);
				  System.out.println(invalidCountWarning);
				  String Warning2 = "Warning info: index = " + index + " array[index] = " + array[index] +  " newValue = " + newValue + " randomBoolean = " + randomBoolean;
				  BasicLog_AddLineTXT(Warning2); FullLog_AddLineTXT(Warning2);
				  System.out.println(Warning2);
				  newValue = 0.5; //give ourselves a new valid value to get out of this loop
				  valid = true;
			  }
			  }
		  }
		  String mutate_out = ("mutate(): Swapping position: " + index + " current value: " + array[index] + " to new value: " + newValue); FullLog_AddLineTXT(mutate_out);
		  array[index] = newValue; // Mutate value at index
		  return array; //return this
		}
	
	public static double[] mutateRandom(double[] array) throws IOException
	{//randomly mutate a value in a 1D double array within a range
		  int index = new Random().nextInt(array.length); // Get random index
		  boolean valid = false;
		  double newValue = 0;
		  while (!valid)
		  {
			  newValue = new Random().nextDouble(); // Generate random value between 0.1 to 1.0 in 0.1 increments
			  if ( (newValue > 0.0 && newValue < 1.0) && (newValue != array[index])) {valid = true;}//doing this for now to catch bad values
			  else { String mutate_bad_val = ("mutate(): Caught bad value. Will generate another value.");FullLog_AddLineTXT(mutate_bad_val);
			  }
		  }
		  String mutate_out = ("mutate(): Swapping position: " + index + " current value: " + array[index] + " to new value: " + newValue); FullLog_AddLineTXT(mutate_out);
		  array[index] = newValue; // Mutate value at index
		  return array; //return this
		}
	
	private static void hillClimberFitnessStart() throws IOException
	{
		hillClimberFitnessLog.addRowCSV("number of changes");
		hillClimberFitnessLog.addRowCSV("MST cost");
		hillClimberFitnessLog.addRowCSV("TSP cost");
		hillClimberFitnessLog.addRowCSV("fitness (MST/TSP)");
		hillClimberFitnessLog.addColumnCSV(""); 
	}
	
	private static void fitnessRepeatsStart() throws IOException
	{
		fitnessRepeatsLog.addRowCSV("repeat number");
		fitnessRepeatsLog.addRowCSV("starting fitness");
		fitnessRepeatsLog.addRowCSV("best fitness");
		fitnessRepeatsLog.addRowCSV("iteration at last change");
		fitnessRepeatsLog.addRowCSV("number of changes");
		fitnessRepeatsLog.addColumnCSV(""); 
	}
	
	private static void fitnessRepeatsLogger(int i) throws IOException
	{
		fitnessRepeatsLog.addRowCSV(String.valueOf(i));
		fitnessRepeatsLog.addRowCSV(String.valueOf(startFitness)); 
		fitnessRepeatsLog.addRowCSV(String.valueOf(classFitness)); 
		fitnessRepeatsLog.addRowCSV(String.valueOf(iterationOfLastChange));  
		fitnessRepeatsLog.addColumnCSV(String.valueOf(totalChangesMade)); 
	}
	
	private static void openFiles(int NumCities, boolean DifficultTrueEasyFalse, int iterations, int SolveIterations) throws IOException
	{
		String variables = " MakeTSP " + NumCities + " cities, " + DifficultTrueEasyFalse + " DifficultTrueEasyFalse, " + iterations + " inner, " + SolveIterations + " outer";
		if (UsebasicLog) {basicLog = new FileWriterUtil(dateTime() + variables + " basicLog.txt", "txt"); basicLog.start();} //create basic log instance and start using the file
		if (UseFullLog) {fullLog = new FileWriterUtil(dateTime() + variables + " fullLog.txt", "txt"); fullLog.start();} //create fullLog instance and start using the file
		if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog = new FileWriterUtil(dateTime() + variables+ " hillClimberFitnessLog.csv", "csv"); hillClimberFitnessLog.start();}//create hillClimberFitnessLog instance and start using the file
		if (UsefitnessRepeatsLog) {fitnessRepeatsLog = new FileWriterUtil(dateTime() + variables+ " fitnessRepeatsLog.csv", "csv"); fitnessRepeatsLog.start();}//used to track fitness at end of each iteration so we can see best fitness trend for repeats
		if (UsefitnessRepeatsLog) {fitnessRepeatsStart();}
		if (UsehillClimberFitnessLogLog) {hillClimberFitnessStart();}
	}
	
	private static void closeFiles() throws IOException
	{
		if (UsebasicLog) {basicLog.close();} //stop using the file for basic log
	    if (UseFullLog) {fullLog.close();} //stop using the file for log
	    if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.close();} //stop using the file for log
	    if (UsefitnessRepeatsLog) {fitnessRepeatsLog.close();} //stop using the file for log
	}
	
	public static String[] getDistances(){return classDistancesRepeats;}//will be used by other class to get final distances
	
	public static double[] getClassFitness(){return classFitnessRepeats;} //returns classFitness
	
	public static double roundTo1dp(double num){long rounded = Math.round(num * 10); return (double)rounded / 10;}
	// Multiply by 10 and round to long. Divide by 10 to get back to 1 dp
	
	public static String dateTime(){ return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));}
	//returns dateTime as string, useful for naming files. should be accurate enough to avoid filename conflicts.
	
	public static void FullLog_AddLineTXT(String input) throws IOException {if (UseFullLog) {fullLog.addLineTXT(input);}} //add to log if logging variable true
	public static void BasicLog_AddLineTXT(String input) throws IOException {if (UsebasicLog) {basicLog.addLineTXT(input);}}
	public static void HillClimberFitnessLog_addColumnCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addColumnCSV(input);}}
	public static void HillClimberFitnessLog_addRowCSV(String input) throws IOException {if (UsehillClimberFitnessLogLog) {hillClimberFitnessLog.addRowCSV(input);}}
	
	
	
	public static void GeneticHillClimbMakeTSP(double[] distances, boolean MaxOrMin, int iterations, int SolveIterations, int populationSize, double crossoverRate, double mutationRate) throws IOException {

	    double MST_value = GetMST(distances); 
	    SolveTSP solver = new SolveTSP(distances, SolveIterations);
	    double TSP_value = solver.return_solution();
	    double startFitness = MST_value / TSP_value;
	    int changes = 0; // Track number of changes made
	    double[][] population = generateInitialPopulation(distances, populationSize);
	    for (int i = 0; i < iterations; i++) {
	        double[][] parents = selectParents(population, distances, SolveIterations);
	        double[][] offspring = mutate(crossover(parents, crossoverRate), mutationRate);
	        evaluateFitness(offspring, distances, SolveIterations);  //TODO revisit: does this update by reference or do I need to rethink the logic here
	        population = survivors(offspring, population); 
	        double bestFitness = getBestFitness(population);
	        String bestFitnessSTR = "GeneticHillClimbMakeTSP: bestFitness = " + String.valueOf(bestFitness); FullLog_AddLineTXT(bestFitnessSTR);
	        if (isBetter(bestFitness, startFitness, MaxOrMin)) {
	            distances = getBestSolution(population);
	            startFitness = bestFitness;
	            // Log changes:
	            if (UseFullLog) {String madeChange = ("isBetter(bestFitness, startFitness, MaxOrMin is true, made a change");
				 FullLog_AddLineTXT(madeChange);}
				changes++;
				if (UseFullLog) {String changesSoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(changesSoFar);}
				if (UsehillClimberFitnessLogLog)
				{//Doing this inside the if statement so we only record when there is a change made
					HillClimberFitnessLog_addRowCSV(String.valueOf(changes)); 
				    HillClimberFitnessLog_addRowCSV(String.valueOf(MST_value));
				    HillClimberFitnessLog_addRowCSV(String.valueOf(TSP_value));
				    HillClimberFitnessLog_addColumnCSV(String.valueOf(startFitness));
				}
				iterationOfLastChange = i; //update iterationOfLastChange
	        } 
	        else {
	        	String noChange = ("made no change");FullLog_AddLineTXT(noChange);String SoFar = ("changes made to TSP so far: " + changes); FullLog_AddLineTXT(SoFar); // Log no change
	        }
	    }
	    String totalChanges = ("total changes made to TSP: " + changes); BasicLog_AddLineTXT(totalChanges); FullLog_AddLineTXT(totalChanges);
		String end = ("final MST: " + MST_value + " final TSP cost: " + TSP_value + " final MST/TSP value: " + startFitness); BasicLog_AddLineTXT(end); FullLog_AddLineTXT(end);
		String finalDistances = "final distance array after: " + Arrays.toString(distances); BasicLog_AddLineTXT(finalDistances); FullLog_AddLineTXT(finalDistances);
		//System.out.println(end);
		totalChangesMade = changes; finalDistances = Arrays.toString(distances); //used for loggers
		classFitness = startFitness;
		classDistances = distances;
	}
	
	private static double[][] generateInitialPopulation(double[] distances, int populationSize) {
		//Creates random initial population solutions by shuffling distance array
		//Provides starting genetic diversity
	    double[][] population = new double[populationSize][distances.length];
	    for (int i = 0; i < populationSize; i++) {
	        double[] solution = shuffle(distances); 
	        population[i] = solution;
	    }
	    return population;
	}
	static double[] shuffle(double[] array) { //used by generateInitialPopulation to randomize array order
		//randomly swaps elements in the array to generate a random ordering which creates new solution candidates
	    Random rnd = new Random();
	    for (int i = array.length - 1; i > 0; i--)
	    {
	        int index = rnd.nextInt(i + 1);
	        // Simple swap
	        double a = array[index];
	        array[index] = array[i];
	        array[i] = a;
	    }
	    return array;
	}
	static double[][] selectParents(double[][] population, double[] distances, int innerIterations) throws IOException {
		//Chooses parents for crossover
	    double[][] parents = new double[2][distances.length];
	    double[] fitness = calculateFitness(population, distances, innerIterations);
	    // Roulette wheel selection
	    parents[0] = selectViaRoulette(population, fitness); 
	    parents[1] = selectViaRoulette(population, fitness);
	    return parents;
	}
	static double[] calculateFitness(double[][] population, double[] distances, int innerIterations) throws IOException {
		//evaluates each member of the population to get an array of fitness scores. Needed for selections and survival
	    double[] fitness = new double[population.length];
	    for (int i = 0; i < population.length; i++) {
	        double[] solution = population[i];
	        double mst = GetMST(solution); 
	        SolveTSP solver = new SolveTSP(solution,innerIterations);
	        double tsp = solver.return_solution();
	        fitness[i] = mst / tsp; 
	    }
	    return fitness;
	}

	static double[] selectViaRoulette(double[][] population, double[] fitness) {
		//Random selection weighted by fitness. Higher fitness means higher chance of selection
	    double totalFitness = sum(fitness);
	    double random = Math.random() * totalFitness;
	    double runningSum = 0;
	    for (int i = 0; i < population.length; i++) {
	        runningSum += fitness[i];
	        if (runningSum > random) { return population[i];}
	    }
	    return population[population.length - 1];
	}
	static double sum(double[] array) {double total = 0;for(double value : array) {total += value;}return total;} //iterates through the array and totals up all the values
	
	static double[][] crossover(double[][] parents, double crossoverRate) {
		//Exchanges sequence sections between parents. Creates new offspring solutions
	    int length = parents[0].length;
	    if (Math.random() > crossoverRate) {return parents;}
	    double[][] offspring = new double[2][length]; 
	    int split = (int)(Math.random() * length);
	    // Single point crossover
	    for (int i = 0; i < split; i++) {
	        offspring[0][i] = parents[0][i]; 
	        offspring[1][i] = parents[1][i];
	    }
	    for (int i = split; i < length; i++) {
	        offspring[0][i] = parents[1][i];
	        offspring[1][i] = parents[0][i];
	    }
	    return offspring;   
	}
	private static double[][] mutate(double[][] offspring, double mutationRate) {
		//Randomly alters offspring solutions. Introduces genetic diversity
	    for (double[] child : offspring) {
	        for (int j = 0; j < child.length; j++) {
	            if (Math.random() < mutationRate) {
	                double rand = Math.random() * 50 - 25;
	                child[j] += rand;
	            }
	        }
	    }
	    return offspring;
	}
	private static void evaluateFitness(double[][] offspring, double[] distances, int innerIterations) throws IOException {
		//Gets updated fitness after mutations. Stores scores with solutions
	    for (double[] solution : offspring) {
	        double mst = GetMST(solution);
	        SolveTSP solver = new SolveTSP(solution, innerIterations); 
	        double tsp = solver.return_solution();
	        double fitness = mst / tsp;
	        solution[solution.length - 1] = fitness; 
	    }
	}
	static double[][] survivors(double[][] offspring, double[][] population) {
		//Determines fittest solutions to carry over. Maintains constant population size
	    double[][] combined = concatenate(offspring, population);
	    double[] fitness = getFitness(combined);
	    Arrays.sort(fitness); 
	    double[][] nextGen = new double[population.length][];
	    for (int i = 0; i < population.length; i++) {
	        nextGen[i] = combined[indexOf(fitness, i)];
	    }
	    return nextGen;   
	}
	
	static double[][] concatenate(double[][] first, double[][] second) {// Concatenate two 2D arrays
	    int firstLen = first.length;
	    int secondLen = second.length;
	    double[][] result = new double[firstLen + secondLen][];
	    System.arraycopy(first, 0, result, 0, firstLen); 
	    System.arraycopy(second, 0, result, firstLen, secondLen);
	    return result;
	}
	
	static double[] getFitness(double[][] population) {// Get array of fitness values from 2D array
	    double[] fitness = new double[population.length];
	    for(int i = 0; i < fitness.length; i++) {
	        fitness[i] = population[i][population[i].length - 1]; 
	    }
	    return fitness;
	}

	static int indexOf(double[] array, double value) {
	    int minIndex = 0;
	    double minDiff = Double.MAX_VALUE;  
	    for(int i = 0; i < array.length; i++) {
	        double diff = Math.abs(array[i] - value);
	        if(diff < minDiff) {
	            minDiff = diff;
	            minIndex = i;
	        }
	    }
	    return minIndex;
	}
	static double getBestFitness(double[][] population) {
	    double bestFitness = 0;
	    for (double[] member : population) { 
	        if (member[member.length - 1] > bestFitness) {
	            bestFitness = member[member.length - 1];  
	        }
	    }
	    return bestFitness;
	}

	private static double[] getBestSolution(double[][] population) {
	    double bestFitness = 0; 
	    double[] best = null;
	    for (double[] member : population) {
	        if (member[member.length - 1] > bestFitness) {
	            bestFitness = member[member.length - 1];
	            best = member;
	        }
	    }
	    return best; 
	}

	static boolean isBetter(double newFitness, double oldFitness, boolean MaxOrMin) {
	    if (MaxOrMin && newFitness < oldFitness) {return true;}
	    if (!MaxOrMin && newFitness > oldFitness) {return true;}
	    return false;
	}
}

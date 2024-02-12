
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CS3072_1911859 {
	private static FileWriterUtil csvLogger;
	private static FileWriterUtil RLog;private static boolean UseRLog = true;
	private static FileWriterUtil txtLogger;
	public static void main(String[] args) throws Exception {
		//Start File Loggers:
		if (UseRLog) {RLog = new FileWriterUtil(dateTime() + " RLog.R", "txt"); RLog.start();} //create log instance and start using the file
		
		//to test new_TSP_1dp_ascending (old method):
		//test_new_TSP();
		
		//test_2D(3); //To test our 2D representation of 1D TSP. input is number of cities
		
		//To run the TSP solver:
		//SolveTSP solver = new SolveTSP(new_TSP(10), 100); //to run the hill climber to solve a TSP
		//System.out.println("CS3072_1911859: main: solver.return_solution() = " + solver.return_solution()); //Prints the total weight of solution to console
		
		//test_MST(10); //To test our MST of a TSP. Input is number of cities
		
		//To run the TSP maker. input: TSP, harder (true) / easier (false), iterations for outer HC, repeats (use 1 for 1 run), iterations for inner HC
		
		int iterations = 100; int NumCities = 5; //number of iterations and number of cities
		int repeats = 2; String type = "GA"; //number of repeats and type of HC to use (HC = basic hill climber) (SA = simulated annealing)
		double val1 = 100; double val2 = 0.99; int populationSize = 0; //initialTemp & coolingRate used in SA. populationSize ignored in SA. 100-1000 & 0.8-0.99 typical values
		//double val1 = 0.7; double val2 = 0.05; int populationSize = 100; //crossoverRate (0.6-0.9), mutationRate (0.01-0.1) and populationSize (50-100) used in GA
		MakeTSPsAndPlotWithR(NumCities, true, iterations, repeats, iterations, type, val1,val2, populationSize);
		MakeTSPsAndPlotWithR(NumCities, false, iterations, repeats, iterations,type, val1,val2, populationSize);
		NumCities = 10; //repeating with other amount of cities
		MakeTSPsAndPlotWithR(NumCities, true, iterations, repeats, iterations, type, val1,val2, populationSize);		
		MakeTSPsAndPlotWithR(NumCities, false, iterations, repeats, iterations, type, val1,val2, populationSize);	
		
		//testPrintR();
		
        // Close writer 
		if (UseRLog) {RLog.close();} //stop using the file for basic log
	}

	public static void test_new_TSP_1dp_ascending(){test_printer(new_TSP_1dp_ascending());} //tests the initial TSP generator by counting values and showing each value
	
	public static void test_new_TSP() throws IOException {test_printer(new_TSP(10));} //same as other test method but for updated TSP generator
	
	public static double[] new_TSP(int cities) throws IOException
	{//populates and returns an array of x random values between 0 and 1 of type double
		  double[] arr = new double[( ((cities * cities) - cities) / 2 )];
		  Random rand = new Random();
		  for(int i = 0; i < arr.length; i++) {arr[i] = rand.nextDouble(); }
		  //System.out.println("CS3072_1911859: new_TSP(): cities = " + cities + " arr.length = " + arr.length); //debug
		  return arr;
	}
	
	public static void test_printer(double[] arr)
	{
		System.out.println("Number of items: " + arr.length); //prints the number of values
		  for(double value : arr) {System.out.println(value); } //prints each value as part of testing
		  System.out.println("end of TSP test");
	}
	
	public static double[] new_TSP_1dp_ascending() {
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
		    if(value >= 1.0) {  value = 0.1;  }// Check if previous value was 1.0, reset to 0.1 if so
		    else {
		      value += 0.1; // Add 0.1 otherwise 
		      value = Math.round(value * 10) / 10.0; //rounding fixes a strange java error I saw with some values e.g. 1.4000000000000001
		    }
		    //System.out.println("debug, value = : " + value);
		  }
		  return arr;
		}
	
	public static double[][] convert_1D_to_2D(double[] array_1D) throws IOException{return SolveTSP.convert_1D_to_2D(array_1D);} //use method in other class
	
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
	
	public static void test_2D(int x) throws IOException
	{//this method tests the initial TSP generator by counting values and showing each value
		double[] array1D = new_TSP(x);
		  System.out.println("test_2D(): Number of items: " + array1D.length); //prints the number of values
		  for(double value : array1D) { System.out.println(value); }//prints each value as part of testing
		  System.out.println("test_2D(): end of TSP test");
		  double[][] array2D = convert_1D_to_2D(array1D);
		  print_2D(array2D);
	}
	
	public static void print_2D(double[][] TSP_2D)
	  {//used to test a 2D array in a nicely formatted way
		  for(int i = 0; i < TSP_2D.length; i++) {
		    for(int j = 0; j < TSP_2D[i].length; j++) {
		      System.out.println("Value at x=" + (i+1) + ", y=" + (j+1) + ": " + TSP_2D[i][j]);// Print value and position. i+1 & j+1 as index counts from 0
		    }
		  }
	  }
	
	public static void printSquareR(double[][] array2D, String name)
	{ //similar to printSquare but with R studio formatting
		  System.out.println();
		  int nrow = array2D.length;
		  System.out.print(name + " <- matrix(c(");
		  for(int i = 0; i < nrow; i++) {
			  //System.out.print("(");
		    for(int j = 0; j < array2D[i].length; j++) {
		    if ((i + 1 == nrow) && (j + 1 == nrow)) {System.out.print(array2D[i][j]+ "),"); }// Print value and position for last element
		    else {System.out.print(array2D[i][j]+ ", "); }// Print value and position for not last element 
		  }
		    //System.out.print("),");
		    System.out.println();
		  }
		  System.out.println("nrow = " + nrow + ", ncol = "+ nrow +")");//assuming it's square
		  System.out.println("print(" + name + ")");
	}
	
	public static void logSquareR(double[][] array2D, String name) throws IOException
	{
		RLog.addLineTXT("");
		int nrow = array2D.length;
		RLog.printTXT(name + " <- matrix(c(");
		  for(int i = 0; i < nrow; i++) {
			  //System.out.print("(");
		    for(int j = 0; j < array2D[i].length; j++) {
		    if ((i + 1 == nrow) && (j + 1 == nrow)) {RLog.printTXT(array2D[i][j]+ "),"); }// Print value and position for last element
		    else {RLog.printTXT(array2D[i][j]+ ", "); }// Print value and position for not last element 
		  }
		    //System.out.print("),");
		    RLog.addLineTXT("");
		  }
		  RLog.addLineTXT("nrow = " + nrow + ", ncol = "+ nrow +")");//assuming it's square
		  RLog.addLineTXT("print(" + name + ")");
	}
	
	public static void printSquare(double[][] array) {
		int n = array.length; //assume square
		  for(int i = 0; i < n; i++) {
		    for(int j = 0; j < n; j++) {
		      System.out.print(array[i][j] + " ");
		    }
		    System.out.println();
		  }
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
	
	public static void test_MST(int x) throws IOException
	{// Tests our MST graph of our new TSP
		  double[] distances = new_TSP(x); // make new TSP
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
	
	
	public static double MST_total(double[][] graph){ return (total_2D(graph)) / 2; } //Returns the sum of MST cost
	//use the total_2D method to count sum of values. Divide by 2 since we don't want to count values twice.
	
	public static void testPrintR() throws IOException
	{//can use this to quickly print some 2D arrays to console when needed.
		double[] hard = {0.6973137768529732, 0.7173975401481106, 0.8121678358099457, 0.6066188902068832, 0.6842276119430029, 0.0015752268248071521, 0.18122046670661063, 0.6488066954514633, 0.16039846889680975, 0.3616177881839977, 0.787119186426413, 0.39842197416618974, 0.15014820213380298, 0.6855653959407451, 0.6234810198813764, 0.0017957863533194507, 0.5908682992299912, 0.6130378860100635, 0.18114114945187587, 0.10129995634689615, 0.6347975474309869, 0.21239564157006086, 0.3582638830895831, 0.5976270377548586, 0.13200551417018835, 0.8960381945414662, 0.31713632140465664, 0.5386905961073206, 0.3977441755386576, 0.8203789868008425, 0.4967786765569153, 0.6961788593112921, 0.9377597710440467, 0.12470653026113465, 0.03310825590707378, 0.15218864294314338, 0.16420673484567005, 0.022456532218007696, 0.8075806194618824, 0.7655863231516281, 0.7375048240793066, 0.3725638604010526, 0.5432352224513644, 0.5812482028549827, 0.9801468724040088};
		double[] easy = {0.475649211613259, 0.6881440319720299, 0.19153825350184406, 0.44889946126174907, 0.6867317503052026, 0.8311307709247071, 0.8969775921500719, 0.24653056581939126, 0.48785612097576836, 0.7601998468109316, 0.08487435049837877, 0.8574758272044032, 0.7350181339083649, 0.30592414287126546, 0.6206566572221529, 0.2925497292822392, 0.6530117744458656, 0.7047521217234005, 0.5590050727960773, 0.07719903645200876, 0.35735423609158967, 0.09428531127091933, 0.3350702254681923, 0.09651865981641983, 0.27711646337680185, 0.19395238581271257, 0.8929092988757384, 0.45183245294744234, 0.363078275698108, 0.8085589395302673, 0.4919996082071373, 0.1305868855277228, 0.6654408030594076, 0.6720305001865299, 0.2650634751842085, 0.2746541865315578, 0.9757841333584442, 0.8326099597482396, 0.20355590314452277, 0.7377266114578032, 0.3979162225982308, 0.5176869593377945, 0.4138248244002356, 0.7759597537385036, 0.5667366112863903};
		System.out.println("hard" + ":");
		printR(hard,"hard");
		System.out.println("easy" + ":");
		printR(easy,"easy");
	}
	
	public static void fitR(String name) throws IOException{
		String var1 = ("fit = cmdscale(" + name + ",eig=TRUE, k=2)");
		if (UseRLog){RLog.addLineTXT(var1);}
		else{System.out.println(var1); }
		}
	
	public static void plotR() throws IOException{
		String var1 = ("plot(fit$points)");
		if (UseRLog){RLog.addLineTXT(var1);}
		else{System.out.println(var1); }
		}
	
	public static void meanDistancesR(String name) throws IOException
	{
		String var1 = ("distances <- dist(" + name + ")");
		String var2 = ("avg_distance <- mean(as.matrix(distances))");
		String var3 = ("print(paste(\"Average Distance:\", round(avg_distance, 2)))");
		if (UseRLog)
		{
			RLog.addLineTXT(var1);
			RLog.addLineTXT(var2);
			RLog.addLineTXT(var3);
		}
		else
		{
			System.out.println(var1); 
			System.out.println(var2);
			System.out.println(var3);
		}
	}
	public static void savePlotR(String name) throws IOException
	{
		String var1 = ("filePath <- \"C:/Users/james/Documents/TSP_FYP_data/_Rplots\"");//Set file path and name
		String var2 = ("fileName <- \""+ name + ".png\"");
		String var3 = ("png(file.path(filePath, fileName), width = 7280, height = 4320, res = 500)");
		String var4 = ("plot(fit$points)");
		String var5 = ("dev.off()");
		if (UseRLog)
		{
			RLog.addLineTXT(var1);
			RLog.addLineTXT(var2);
			RLog.addLineTXT(var3);
			RLog.addLineTXT(var4);
			RLog.addLineTXT(var5);
		}
		else
		{
			System.out.println(var1); 
			System.out.println(var2);
			System.out.println(var3);
			System.out.println(var4);
			System.out.println(var5);
		}
		
	}
	public static void printR(double[] arr, String name) throws IOException
	{
		if (UseRLog){logSquareR(convert_1D_to_2D(arr), name);}
		else{printSquareR(convert_1D_to_2D(arr), name);}
		fitR(name);
		meanDistancesR(name);
		plotR();
		savePlotR(name);
		//System.out.println();
	}

	public static double[][] convertStringToDouble2D(String[] distanceRepeats)
	{
		// Parse each string to a double[]  
		double[][] result = new double[distanceRepeats.length][];
		for (int i = 0; i < distanceRepeats.length; i++) {
		  String[] splits = distanceRepeats[i].substring(1, distanceRepeats[i].length()-1).split(", ");
		  result[i] = new double[splits.length];
		  for (int j = 0; j < splits.length; j++) {
		    result[i][j] = Double.parseDouble(splits[j].trim()); 
		  }
		}
		return result;
	}
	
	public static void CallR (double[][] outerIndexInnerDistances,double[] fitnessRepeats, int numCities) throws IOException
	{
		//System.out.println("CallR: numCities = " + numCities); //debug
		//System.out.println("CallR: fitnessRepeats.length = " + fitnessRepeats.length); //debug
		for(int i = 0; i < fitnessRepeats.length; i++) {
			  //System.out.println("CS3072_1911859: CallR(): i = " + i); //debug
			  double[] innerArray = outerIndexInnerDistances[i]; // Extract inner array for current outer index
			  // Call printR with inner array and corresponding string 
			  printR(innerArray, "numCities" + numCities + "fitness" + Double.toString(fitnessRepeats[i]) ); 
			}
	}
	
	private static void MakeTSPsAndPlotWithR(int NumCities,boolean DifficultTrueEasyFalse, int innerIterations, int repeats, int outerIterations, String type,
			double initialTemp, double coolingRate, int populationSize) throws IOException
	{
		new MakeTSP(NumCities, DifficultTrueEasyFalse, innerIterations, repeats, outerIterations, type, initialTemp, coolingRate, populationSize); 
		String[] distanceRepeats = MakeTSP.getDistances(); //change to 2D double
		double[] fitnessRepeats = MakeTSP.getClassFitness(); //"fitness" + fitnessRepeats could be filename
		double[][] outerIndexInnerDistances = convertStringToDouble2D(distanceRepeats);
		CallR(outerIndexInnerDistances, fitnessRepeats, NumCities);
	}
	
	}


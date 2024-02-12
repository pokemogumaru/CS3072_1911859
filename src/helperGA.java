import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class helperGA {
	private static ArrayList<String> globalStrings = new ArrayList<>(); //used to store arrays of strings for logging
	public static double[][] selectParents(double[][] population, double[] distances, int innerIterations) throws IOException {
		//Chooses parents for crossover
		String distLengthInnerIter = ("selectParents: distances.length = " + distances.length + " innerIterations = " + innerIterations);
		globalStrings.add(distLengthInnerIter); 
		//System.out.println(debug);
	    double[][] parents = new double[2][distances.length];
	    double[] fitness = calculateFitness(population, distances, innerIterations);
	    globalStrings.add("selectParents: fitness = " + Arrays.toString(fitness)); //this is a large amount of text to write
	    // Roulette wheel selection
	    parents[0] = selectViaRoulette(population, fitness);
	    globalStrings.add("selectParents: parents[0] = " + Arrays.toString(parents[0]));
	    parents[1] = selectViaRoulette(population, fitness);
	    globalStrings.add("selectParents: parents[1] = " + Arrays.toString(parents[1]));
	    MakeTSP.fullLogStrings(globalStrings);//when done, give the arrayList of strings back to be logged
	    return parents;
	}
	private static double[] calculateFitness(double[][] population, double[] distances, int innerIterations) throws IOException {
		//evaluates each member of the population to get an array of fitness scores. Needed for selections and survival
	    double[] fitness = new double[population.length];
	    for (int i = 0; i < population.length; i++) {
	        double[] solution = population[i];
	        double mst = MakeTSP.GetMST(solution); 
	        SolveTSP solver = new SolveTSP(solution,innerIterations);
	        double tsp = solver.return_solution();
	        fitness[i] = mst / tsp; 
	    }
	    //don't call fullLogStrings as not public method
	    return fitness;
	}
	private static double[] selectViaRoulette(double[][] population, double[] fitness) {
		//Random selection weighted by fitness. Higher fitness means higher chance of selection
	    double totalFitness = sum(fitness);
	    double random = Math.random() * totalFitness;
	    double runningSum = 0;
	    for (int i = 0; i < population.length; i++) {
	        runningSum += fitness[i];
	        if (runningSum > random) { return population[i];}
	    }
	  //don't call fullLogStrings as not public method
	    return population[population.length - 1];
	}
	
	private static double sum(double[] array) {double total = 0;for(double value : array) {total += value;}return total;}
	//iterates through the array and totals up all the values

	public static double[][] survivors(double[][] offspring, double[][] population) {
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
	
	private static double[][] concatenate(double[][] first, double[][] second) {// Concatenate two 2D arrays
	    int firstLen = first.length;
	    int secondLen = second.length;
	    double[][] result = new double[firstLen + secondLen][];
	    System.arraycopy(first, 0, result, 0, firstLen); 
	    System.arraycopy(second, 0, result, firstLen, secondLen);
	    //don't call fullLogStrings as not public method
	    return result;
	}
	
	private static double[] getFitness(double[][] population) {// Get array of fitness values from 2D array
	    double[] fitness = new double[population.length];
	    for(int i = 0; i < fitness.length; i++) {
	        fitness[i] = population[i][population[i].length - 1]; 
	    }
	    return fitness;
	}

	private static int indexOf(double[] array, double value) {
	    int minIndex = 0;
	    double minDiff = Double.MAX_VALUE;  
	    for(int i = 0; i < array.length; i++) {
	        double diff = Math.abs(array[i] - value);
	        if(diff < minDiff) {
	            minDiff = diff;
	            minIndex = i;
	        }
	    }
	    //don't call fullLogStrings as not public method
	    return minIndex;
	}
}

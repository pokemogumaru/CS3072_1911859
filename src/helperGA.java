import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class helperGA {
	//This class contains methods used by the genetic algorithm.
	//all public methods start by clearing the arrayList string and call the logger before returning.
	//all private methods do not do this as they will start and finish with call to one of the public method.
	private static ArrayList<String> globalStrings = new ArrayList<>(); //used to store arrays of strings for logging
	public static double[][] selectParents(double[][] population, double[] distances, int innerIterations) throws IOException {
		//Chooses parents for crossover
		//if (!globalStrings.isEmpty()) {System.out.println("helperGA: selectParents: globalStrings was not empty.");} //debug
		globalStrings.clear(); //clear the string arrayList
		if (distances == null) {System.out.println("LOUD WARNING: helperGA: selectParents: distances == null !!!");} //debug
		String distLengthInnerIter = ("helperGA: selectParents: distances.length = " + distances.length + " innerIterations = " + innerIterations);
		globalStrings.add(distLengthInnerIter); 
		//System.out.println(debug);
	    double[][] parents = new double[2][distances.length];
	    double[] fitness = calculateFitness(population, distances, innerIterations);
	    globalStrings.add("helperGA: selectParents: fitness = " + Arrays.toString(fitness)); //this is a large amount of text to write
	    // Roulette wheel selection
	    parents[0] = selectViaRoulette(population, fitness);
	    globalStrings.add("helperGA: selectParents: parents[0] = " + Arrays.toString(parents[0]));
	    parents[1] = selectViaRoulette(population, fitness);
	    globalStrings.add("helperGA: selectParents: parents[1] = " + Arrays.toString(parents[1]));
	    MakeTSP.fullLogStrings(globalStrings);//when done, give the arrayList of strings back to be logged
	    return parents;
	}
	private static double[] calculateFitness(double[][] population, double[] distances, int innerIterations) throws IOException {
		//evaluates each member of the population to get an array of fitness scores. Needed for selections and survival
	    double[] fitness = new double[population.length];
	    for (int i = 0; i < population.length; i++) {
	        double[] solution = population[i]; // Get the solution for the current member
	        double mst = MakeTSP.GetMST(solution); // Calculate the minimum spanning tree (MST) for the solution
	        SolveTSP solver = new SolveTSP(solution,innerIterations); // Create a SolveTSP instance to solve the TSP for the current solution
	        double tsp = solver.return_solution(); // Get the result of solving the TSP
	        fitness[i] = mst / tsp; // Calculate fitness score for the current solution
	    }
	    globalStrings.add("helperGA: calculateFitness: fitness = " + Arrays.toString(fitness));
	    //don't call fullLogStrings as not public method
	    return fitness;
	}
	private static double[] selectViaRoulette(double[][] population, double[] fitness) {
		//Random selection weighted by fitness. Higher fitness means higher chance of selection
	    double totalFitness = sum(fitness); // Calculate the total fitness of the population
	    double random = Math.random() * totalFitness; // Generate a random number within the range of total fitness
	    double runningSum = 0; // Generate a random number within the range of total fitness
	    for (int i = 0; i < population.length; i++) {
	        runningSum += fitness[i]; // Add the fitness score of the current member to the running sum
	        if (runningSum > random) { return population[i];} // Return the selected individual
	    }
	  //don't call fullLogStrings as not public method
	    return population[population.length - 1];
	}
	
	private static double sum(double[] array) {double total = 0;for(double value : array) {total += value;}return total;}
	//iterates through the array and totals up all the values

	public static double[][] survivors(double[][] offspring, double[][] population) throws IOException {
		//Determines fittest solutions to carry over. Maintains constant population size
		globalStrings.clear(); //clear the string arrayList
	    double[][] combined = concatenate(offspring, population); // Concatenate offspring and population arrays
	    double[] fitness = getFitness(combined); // Get fitness values for the combined population
	    Arrays.sort(fitness); // Sort the fitness values in ascending order
	    double[][] nextGen = new double[population.length][]; // Create an array to store the next generation
	    for (int i = 0; i < population.length; i++) {
	        nextGen[i] = combined[indexOf(fitness, i)]; // Select the best individuals for the next generation
	        //globalStrings.add("helperGA: survivors: nextGen[" + i + "] = " + Arrays.toString(nextGen[i])); //this is a VERY large amount of text to write
	    }
	    MakeTSP.fullLogStrings(globalStrings);//when done, give the arrayList of strings back to be logged
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
	    globalStrings.add("helperGA: getFitness: fitness = " + Arrays.toString(fitness));
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
	public static double[][] mutate(double[][] offspring, double mutationRate) {
		//Randomly alters offspring solutions. Introduces genetic diversity
		globalStrings.clear(); //clear the string arrayList
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
	public static double[] getBestSolution(double[][] population) throws IOException {
		globalStrings.clear(); //clear the string arrayList
	    double bestFitness = 0; 
	    double[] best = null;
	    for (double[] member : population) {
	        if (member[member.length - 1] > bestFitness) {
	            bestFitness = member[member.length - 1];
	            best = member;
	        }
	    }
	    globalStrings.add("helperGA: getBestSolution: best = " + Arrays.toString(best));
		if (best == null) {System.out.println("LOUD WARNING: helperGA: getBestSolution: distances == null !!!");} //debug
	    MakeTSP.fullLogStrings(globalStrings);//when done, give the arrayList of strings back to be logged
	    return best; 
	}
	public static double[][] generateInitialPopulation(double[] distances, int populationSize) {
		//Creates random initial population solutions by shuffling distance array
		//Provides starting genetic diversity
		globalStrings.clear(); //clear the string arrayList
	    double[][] population = new double[populationSize][distances.length];
	    for (int i = 0; i < populationSize; i++) {
	        double[] solution = shuffle(distances); 
	        population[i] = solution;
	    }
	    return population;
	}
	private static double[] shuffle(double[] array) { //used by generateInitialPopulation to randomize array order
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
	public static double getBestFitness(double[][] population) throws IOException {
		globalStrings.clear(); //clear the string arrayList
	    double bestFitness = 0;
	    for (double[] member : population) { 
	        if (member[member.length - 1] > bestFitness) {
	            bestFitness = member[member.length - 1];  
	        }
	    }
	    globalStrings.add("helperGA: getBestFitness: bestFitness = " + String.valueOf(bestFitness));
	    MakeTSP.fullLogStrings(globalStrings);//when done, give the arrayList of strings back to be logged
	    return bestFitness;
	}
	public static double[][] crossover(double[][] parents, double crossoverRate) {
		//Exchanges sequence sections between parents. Creates new offspring solutions
		globalStrings.clear(); //clear the string arrayList
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
	public static boolean isBetter(double newFitness, double oldFitness, boolean MaxOrMin) {
		globalStrings.clear(); //clear the string arrayList
	    if (MaxOrMin && newFitness < oldFitness) {return true;}
	    if (!MaxOrMin && newFitness > oldFitness) {return true;}
	    return false;
	}
}

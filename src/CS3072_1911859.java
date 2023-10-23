
public class CS3072_1911859 {

	public static void main(String[] args) {
		//to test new_TSP:
		test_new_TSP();
		//sets i - the number of iterations the TSP modifier will run
		int i = 1000; //will test 1000 iterations
		//sets j - the number of iterations the TSP solver (algorithm 2) will run inside each i iteration
		int j = 1000; //will test 1000 iterations
		//total runtime should be approximately proportional to i.j
		
		
		
	}

	public static void test_new_TSP()
	{
		//this method tests the initial TSP generator by counting values and showing each value
				double[] arr = new_TSP();
				  System.out.println("Number of items: " + arr.length); //prints the number of values
				  for(double value : arr) {
				    System.out.println(value); //prints each value as part of testing
				  }
	}
	
	public static double[] new_TSP() {
		//this method generates a new TSP
		/*
		 * generates a 45 element array of numbers ranging from 0.0 to 1.0 , e.g. 0.7
These numbers should be in order and repeating once 1 is reached. so this would be 0.0 , 0.1 , ... , 0.9 , 1.0 , 0.0 , 0.1 etc
returns a 1-D array of type double
0.0 indicates it is not possible to travel from one city to another
		 */
		  double[] arr = new double[45];
		  double value = 0.0; // Start with 0.0

		  for(int i = 0; i < arr.length; i++) {

		    arr[i] = value; 

		    // Check if previous value was 1.0, reset to 0.0 if so
		    if(value >= 1.0) {
		      value = 0.0; 
		    }
		    else {
		      value += 0.1; // Add 0.1 otherwise 
		      value = Math.round(value * 10) / 10.0; //rounding fixes a strange java error I saw with some values e.g. 1.4000000000000001
		    }
		    //System.out.println("debug, value = : " + value);
		  }

		  return arr;
		}
	
}

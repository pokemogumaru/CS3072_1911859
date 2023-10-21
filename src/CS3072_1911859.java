
public class CS3072_1911859 {

	public static void main(String[] args) {
		//to test new_TSP:
		test_new_TSP();
		

	}

	public static void test_new_TSP()
	{
		//start of new_TSP test
				double[] arr = new_TSP();
				  
				  System.out.println("Number of items: " + arr.length);

				  for(double value : arr) {
				    System.out.println(value);
				  }

				//end of new_TSP test
	}
	
	public static double[] new_TSP() {
		//this method generates a new TSP
		/*
		 * generates a 45 element array of numbers ranging from 0.1 to 1.0 , e.g. 0.7
These numbers should be in order and repeating once 1 is reached. so this would be 0.1 , 0.2 , ... , 0.9 , 1.0 , 0.1 , 0.2 etc
returns a 1-D array of type double
		 */
		  double[] arr = new double[45];
		  double value = 0.1; // Start with 0.1

		  for(int i = 0; i < arr.length; i++) {

		    arr[i] = value; 

		    // Check if previous value was 1.0, reset to 0.1 if so
		    if(value >= 1.0) {
		      value = 0.1; 
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

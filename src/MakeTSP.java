
public class MakeTSP {
//This class will be used to generate / modify TSPs to minimise / maximise their difficulty
//input: iterations (int), maximise_difficulty (boolean), TSP[] (double)
//should make small change to any non 0 value in the 1D tsp or both values in 2D version
	public MakeTSP(double[] distances, boolean MaxOrMin, int iterations)
	{
		// do all the heavy lifting in seperate method, add file logging & timing after
		// step 1 - for iterations do something
		//step 2 - within loop keep making small changes and compare current fitness to old fitness (likely some methods for this
		//step 3 - finish with final TSP, best solveTSP result and MST
	}
	
	public static void HillClimbMakeTSP(double[] distances, boolean MaxOrMin, int iterations)
	{
		//TODO Need starting fitness = MST / TSP:
		//1 get MST
		//2 get TSP total cost
		//3 calculate value. Higher end value means easy TSP to solve, lower end value means hard TSP so solve
		//3.1 print the above
		for (int i = 1; i <= iterations; i++)
	    {
			//4 make small change to distances[]
			//5 reevaluate MST / TSP value
			//6 compare old and new value and make change if needed
			//7 print final distances[], MST, TSP cost and MST/TSP value
	    }
	}
}

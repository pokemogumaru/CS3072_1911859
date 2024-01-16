// This code is largely sourced from this page with adaptations made to better work in my program.
// Original code written by Aakash Hasija
// https://www.geeksforgeeks.org/prims-minimum-spanning-tree-mst-greedy-algo-5/#:~:text=Below%20is%20the,Javascript
// Accessed 16/01/2024

import java.io.IOException;

public class MST {

  private int V;
  
  public MST(double[] distances) throws IOException {
    V = calculateNumVertices(distances); 
  }

  private int calculateNumVertices(double[] distances) throws IOException {
    // Use existing method to get V:
	  int n = distances.length; // Get size of 1D array
	  if (n < 1)
	  {// should never be less than 1 element, giving a 2x2 matrix
	  System.out.println("less than 1 unique element, likely an error.");
	  return 0;
	  }
	  double x; //get size of width / height (called x)
	  double sqrtTerm = Math.sqrt(1 + 8*n); // Calculate square root term
	  x = (1 + sqrtTerm) / 2; // Calculate x
	  String calculateCitiesXValue = ("calculateCitiesAmount: " + "x = " + x); //fullLog.addLineTXT(calculateCitiesXValue);
	  //System.out.println(calculateCitiesXValue); //don't really need this unless testing, 45 element 1D array should print x = 10
    int V = (int) x;
    //int V = SolveTSP.calculateCitiesAmount(distances); //legacy code, had problems
    return V;
  }

  //A utility function to print the constructed MST
  // stored in parent[]
  void printMST(int parent[], int graph[][])
  {
      System.out.println("Edge \tWeight");
      for(int i = 1; i < V; i++) {
    	    double weight = graph[i][parent[i]] / 1000.0; 
    	    System.out.println(parent[i] + " - " + i + "\t" + weight); 
    	  }
  }

//A utility function to find the vertex with minimum
  // key value, from the set of vertices not yet included
  // in MST
  int minKey(int key[], Boolean mstSet[])
  {
      // Initialize min value
      int min = Integer.MAX_VALUE, min_index = -1;
      for (int v = 0; v < V; v++)
          if (mstSet[v] == false && key[v] < min) {
              min = key[v];
              min_index = v;
          }

      return min_index;
  }
  
  void primMST(int[][] graph) {
    // Update to use the V instance variable
    int parent[] = new int[V]; 
    int key[] = new int[V];
    Boolean mstSet[] = new Boolean[V];
    
 // Initialize all keys as INFINITE
    for (int i = 0; i < V; i++) {
        key[i] = Integer.MAX_VALUE;
        mstSet[i] = false;
    }

    // Always include first 1st vertex in MST.
    // Make key 0 so that this vertex is picked as first vertex
    key[0] = 0;
   
    parent[0] = -1; // First node is always root of MST

    // The MST will have V vertices
    for (int count = 0; count < V - 1; count++) {
         
        // Pick the minimum key vertex from the set of
        // vertices not yet included in MST
        int u = minKey(key, mstSet);

        // Add the picked vertex to the MST Set
        mstSet[u] = true;

        // Update key value and parent index of the
        // adjacent vertices of the picked vertex.
        // Consider only those vertices which are not
        // yet included in MST
        for (int v = 0; v < V; v++)

            // graph[u][v] is non zero only for adjacent
            // vertices of m mstSet[v] is false for
            // vertices not yet included in MST Update
            // the key only if graph[u][v] is smaller
            // than key[v]
            if (graph[u][v] != 0 && mstSet[v] == false
                && graph[u][v] < key[v]) {
                parent[v] = u;
                key[v] = graph[u][v];
            }
    }

    // Print the constructed MST
    printMST(parent, graph);
  }

}
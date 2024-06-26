// This code is from CS2004 worksheet 7
// Credit goes to original authors, the primary aim of my FYP is not to calculate MSTs so reusing this code simplifies implementation
// Accessed 18/01/2024
// This code will be used by the outer hill climber

import java.util.*;

public class MST
{
	//Search for the next applicable edge
	static private Edge LocateEdge(ArrayList<Integer> v,ArrayList<Edge> edges)
	{
		for (Iterator<Edge> it = edges.iterator(); it.hasNext();)
		{
	        Edge e = it.next();
			int x = e.i;
			int y = e.j;
			int xv = v.indexOf(x);
			int yv = v.indexOf(y);
			if (xv > -1 && yv == -1)
			{
				return(e);
			}
			if (xv == -1 && yv > -1)
			{
				return(e);
			}
		}
		//Error condition
		return(new Edge(-1,-1,0.0));
	}
	@SuppressWarnings("unchecked")
	//d is a distance matrix, high value edges are more costly
	//Assume that d is symmetric and square
	public static double[][] PrimsMST(double[][] d)
	{
		int i,j,n = d.length;
		double res[][] = new double[n][n];
		//Store edges as an ArrayList
		ArrayList<Edge> edges = new ArrayList<Edge>();
		for(i=0;i<n-1;++i)
		{
			for(j=i+1;j<n;++j)
			{
				//Only non zero edges
				if (d[i][j] != 0.0) edges.add(new Edge(i,j,d[i][j]));
			}
		}
		//Sort the edges by weight
		Collections.sort(edges,new CompareEdge());
		//Don't do anything more if all the edges are zero
		if (edges.size() == 0) return(res);
		//List of variables that have been allocated
		ArrayList<Integer> v = new ArrayList<Integer>();
		//Pick cheapest edge
		v.add(edges.get(0).i);
		//Loop while there are still nodes to connect
		while(v.size() != n)
		{
			Edge e = LocateEdge(v,edges);
			if (v.indexOf(e.i) == -1) v.add(e.i);
			if (v.indexOf(e.j) == -1) v.add(e.j);
			res[e.i][e.j] = e.w;
			res[e.j][e.i] = e.w;
		}
		return(res);
	}
	
	public static double total_2D(double[][] array2D)
	{ //A method that returns the sum of all values in a 2D double graph. I have added this method into this class.
		double total = 0.0; //track total
		for(int i = 0; i < array2D.length; i++) {
		    for(int j = 0; j < array2D[i].length; j++) {
		      total = array2D[i][j] + total;
		    }
		  }
		return total;
	}
	
	public static double MST_total(double[][] graph)
	{ //A method that returns the sum of weights in a 2D double graph. Uses the total_2D method. I have added this method into this class.
		double mst[][] = PrimsMST(graph); //calculate MST
		double total = total_2D(mst); //use the existing method to do this
		total = total / 2; //since we don't want to count values twice
		return total;
	}
	
}
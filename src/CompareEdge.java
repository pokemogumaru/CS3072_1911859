// This code is from CS2004 worksheet 7
// Credit goes to original authors, the primary aim of my FYP is not to calculate MSTs so reusing this code simplifies implementation
// Accessed 18/01/2024
// This code will be used by the MST class

//Compare edge weights - used to sort an ArrayList of edges
public class CompareEdge implements java.util.Comparator 
{
	public int compare(Object a, Object b) 
	{
		if (((Edge)a).w < ((Edge)b).w) return(-1);
		if (((Edge)a).w > ((Edge)b).w) return(1);
		return(0);
	}
}

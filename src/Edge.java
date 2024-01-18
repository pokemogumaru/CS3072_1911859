// This code is from CS2004 worksheet 7
// Credit goes to original authors, the primary aim of my FYP is not to calculate MSTs so reusing this code simplifies implementation
// Accessed 18/01/2024

//Store an edge, from node i to j with weigh w
public class Edge extends Object
{
	public int i,j;
	public double w;
	Edge(int ii,int jj,double ww)
	{
		i = ii;
		j = jj;
		w = ww;
	};
	public void Print()
	{
		System.out.print("(");
		System.out.print(i);
		System.out.print(",");
		System.out.print(j);
		System.out.print(",");
		System.out.print(w);
		System.out.print(")");
	}
};
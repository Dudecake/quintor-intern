package nl.quintor.cluster.load_generator;

public class LoadResult
{
	static long counter = 0;
	final long id;
	final int count;
	final double time;
	
	public LoadResult(int count, double time)
	{
		this.id = counter++;
		this.count = count;
		this.time = time;
	}
	
	public long getId()
	{
		return id;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public double getTime()
	{
		return time;
	}
}

package nl.quintor.cluster.load_generator;

public class LoadProducer implements Runnable
{
	private long startTime;
	double load;
	long duration;
	
	/**
	 * Constructor which generates the load
	 * @param load Load to be generated, if negative load will be 100%.
	 * Note: load given may or may not be representative of actual load on system.
	 * @param duration Duration which to generate the load for in milliseconds,
	 * if negative load will be generated for 30-60 seconds
	 */
	public LoadProducer(double load, long duration)
	{
		startTime = -1;
		if (load < 0.0 || load > 100.0) this.load = 100.0;
		else this.load = load;
		if (duration < 0) this.duration = App.r.nextInt(30000) + 30000;
		else this.duration = duration;
	}
	
	/**
	 * Constructor which generates the load
	 * @param startTime Start time from a previous incantation
	 * @param loadLoad to be generated, if negative load will be 100%.
	 * Note: load given may or may not be representative of actual load on system.
	 * @param duration Duration which to generate the load for in milliseconds,
	 * if negative load will be generated for 30-60 seconds
	 */
	public LoadProducer(long startTime, double load, long duration)
	{
		if (startTime < 0) this.startTime = System.currentTimeMillis();
		else this.startTime = startTime;
		if (load < 0.0 || load > 100.0) this.load = 100.0;
		else this.load = load;
		if (duration < 0) this.duration = App.r.nextInt(30000) + 30000;
		else this.duration = duration;
	}
		
	public void run()
	{
		if (startTime < 0) startTime = System.currentTimeMillis();
		// System.getenv("MARATHON_APP_ID");
		try
		{
			while (System.currentTimeMillis() - startTime < duration)
			{
				if (System.currentTimeMillis() % 100 == 0)
				{
					Thread.sleep((long)(100.0 - load));
				}
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}
}

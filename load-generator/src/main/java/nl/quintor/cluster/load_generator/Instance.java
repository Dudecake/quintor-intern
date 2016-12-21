package nl.quintor.cluster.load_generator;

import javax.persistence.*;

@Entity
public class Instance
{
	@Id
	@GeneratedValue
	private Long id;
	@Column(unique = true, nullable = false)
	private String instanceId;
	@Column(nullable = false)
	private long startTime;
	@Column(name = "loadperc" ,nullable = false)
	private double load;
	@Column(nullable = false)
	private int threads;
	@Column(nullable = false)
	private long duration;
	@Column(nullable = false)
	private int count;
	@Column(nullable = false)
	private int sleep;
	
	/**
	 * Required by JPA spec, shouldn't be used directly
	 */
	protected Instance() {  }
	
	public Instance(long startTime, double load, long duration, int threads)
	{
		instanceId = App.APPID;
		if (startTime < 0) this.startTime = System.currentTimeMillis();
		else this.startTime = startTime;
		if (load < 0.0 || load > 100.0) this.load = 100.0;
		else this.load = load;
		if (duration < 0) this.duration = App.r.nextInt(30000) + 30000;
		else this.duration = duration;
		if (threads < 0) this.threads = 3;
		else this.threads = threads;
		count = 0;
		sleep = 0;
	}
	
	public Instance(long startTime, double load, long duration, int threads, int count, int sleep)
	{
		instanceId = App.APPID;
		if (startTime < 0) this.startTime = System.currentTimeMillis();
		else this.startTime = startTime;
		if (load < 0.0 || load > 100.0) this.load = 100.0;
		else this.load = load;
		if (duration < 0) this.duration = App.r.nextInt(30000) + 30000;
		else this.duration = duration;
		if (threads < 0) this.threads = 3;
		else this.threads = threads;
		if (count < 0) this.count = 1;
		else this.count = count;
		if (sleep < 0) this.sleep = 300;
		else this.sleep = sleep;
	}
	
	public String getInstanceId()
	{
		return instanceId;
	}
	
	public long getStartTime()
	{
		return startTime;
	}
	
	public double getLoad()
	{
		return load;
	}
	
	public long getDuration()
	{
		return duration;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public int getThreads()
	{
		return threads;
	}
	
	public int getSleep()
	{
		return sleep;
	}
}
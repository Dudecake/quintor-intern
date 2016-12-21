package nl.quintor.cluster.statesaver_jms;

import java.io.Serializable;
import java.time.LocalTime;

public class Count implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3981821042232657064L;

	private long id;
	private int startCount;
	private int endCount;
	private int count;
	private String sessionId;
	private LocalTime timestamp;

	protected Count()
	{
	}

	public Count(int count, String sessionId)
	{
		this.startCount = count;
		this.endCount = count;
		this.count = count;
		this.timestamp = LocalTime.now();
		this.sessionId = sessionId;
	}

	public int getStartCount()
	{
		return startCount;
	}

	public void setStartCount(int startCount)
	{
		this.startCount = startCount;
	}

	public int getEndCount()
	{
		return endCount;
	}

	public void setEndCount(int endCount)
	{
		this.endCount = endCount;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public LocalTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(LocalTime timestamp)
	{
		this.timestamp = timestamp;
	}
}

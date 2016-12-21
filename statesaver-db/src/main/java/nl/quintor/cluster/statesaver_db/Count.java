package nl.quintor.cluster.statesaver_db;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Count
{
	@Id
	@GeneratedValue
	private long id;
	@Column
	private int startCount;
	@Column
	private int endCount;
	@Column
	private int count;
	@Column
	private String sessionId;
	@Column
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

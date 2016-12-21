package nl.quintor.cluster.statesaver_jms.net;

import java.io.Serializable;

import nl.quintor.cluster.statesaver_jms.Count;

public class StateRequest implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 517153800555964735L;
	private String sessionId;

	public StateRequest(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public boolean isReqFor(Count count)
	{
		return count.getSessionId().equals(sessionId);
	}

	public String getSessionId()
	{
		return sessionId;
	}
}

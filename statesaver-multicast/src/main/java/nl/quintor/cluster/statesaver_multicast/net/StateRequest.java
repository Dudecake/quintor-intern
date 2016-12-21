package nl.quintor.cluster.statesaver_multicast.net;

import java.io.Serializable;

import nl.quintor.cluster.statesaver_multicast.Count;

public class StateRequest implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -187786158414241839L;
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

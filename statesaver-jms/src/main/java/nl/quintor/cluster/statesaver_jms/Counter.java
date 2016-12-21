package nl.quintor.cluster.statesaver_jms;

import java.io.IOException;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.quintor.cluster.statesaver_jms.net.StateRequest;

public class Counter implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4529548772399723119L;
	private static final Logger logger = LoggerFactory.getLogger(Counter.class);
	private String sessionId;
	private Count count;

	public Counter(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public boolean unFinishedCount()
	{
		boolean res = false;
		// TODO: get state from queue
		// count = App.getInstance().sendRequest(new StateRequest(sessionId));
		if (count != null && (count.getCount() != count.getEndCount()))
		{
			res = true;
		}
		logger.info("res:" + String.valueOf(res));
		return res;
	}

	public void resumeCount()
	{
		// TODO: get state from queue
		// StateRequest request = new StateRequest(sessionId);
		// count = App.getInstance().sendRequest(request);
		// int increment = count.getEndCount() - count.getCount();
		// for (int i = 0; i < increment; i++)
		// {
		// count = App.getInstance().sendRequest(request);
		// count.setCount(count.getCount() + 1);
		// App.getInstance().sendCount(count);
		// }
	}

	public int getCount()
	{
		if (count != null)
			return count.getCount();
		return -1;
	}

	// @Transactional
	public void count(int increment)
	{
		StateRequest request = new StateRequest(sessionId);
		count = App.getInstance().requestState(request);
		if (count == null)
		{
			count = new Count(0, sessionId);
		}
		count.setEndCount(count.getEndCount() + increment);
		try
		{
			App.send(count);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		for (int i = 0; i < increment; i++)
		{
			// count = App.getInstance().sendRequest(request);
			count.setCount(count.getCount() + 1);
			try
			{
				App.send(count);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}

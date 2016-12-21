package nl.quintor.cluster.statesaver_db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counter
{
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
		while (App.repository() == null)
		{
		}
		count = App.repository().findOneBySessionId(sessionId);
		if (count != null && (count.getCount() != count.getEndCount()))
		{
			res = true;
		}
		logger.info("res:" + String.valueOf(res));
		return res;
	}

	public void resumeCount()
	{
		count = App.repository().findOneBySessionId(sessionId);
		int increment = count.getEndCount() - count.getCount();
		for (int i = 0; i < increment; i++)
		{
			// count = App.repository().findOneBySessionId(sessionId);
			count.setCount(count.getCount() + 1);
			App.instance().saveCount(count);
		}
	}

	public int getCount()
	{
		if ((count = App.repository().findOneBySessionId(sessionId)) != null)
			return count.getCount();
		return -1;
	}

	// @Transactional
	public void count(int increment)
	{
		count = App.repository().findOneBySessionId(sessionId);
		if (count == null)
		{
			count = new Count(0, sessionId);
		}
		count.setEndCount(count.getEndCount() + increment);
		App.instance().saveCount(count);
		for (int i = 0; i < increment; i++)
		{
			// count = App.repository().findOneBySessionId(sessionId);
			count.setCount(count.getCount() + 1);
			App.instance().saveCount(count);
		}
	}
}

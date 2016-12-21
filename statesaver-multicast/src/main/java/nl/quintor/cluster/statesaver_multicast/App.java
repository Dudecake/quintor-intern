package nl.quintor.cluster.statesaver_multicast;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.quintor.cluster.statesaver_multicast.net.MulticastListener;
import nl.quintor.cluster.statesaver_multicast.net.MulticastSender;
import nl.quintor.cluster.statesaver_multicast.net.StateRequest;

@Controller
@EnableAutoConfiguration
public class App
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static App instance;
	private MulticastListener listener;
	private MulticastSender sender;

	public App()
	{
		instance = this;
		try
		{
			listener = new MulticastListener(5001);
			sender = new MulticastSender();
			listener.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * returns the instance of the App class
	 * 
	 * @return the instance
	 */
	public static App getInstance()
	{
		return instance;
	}

	/**
	 * Sends the count object to the multicast group
	 * 
	 * @param count
	 * @return
	 */
	public boolean sendCount(Count count)
	{
		boolean res = false;
		try
		{
			sender.send(count);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Requests a Count object corresponding to the sessionId
	 * 
	 * @param request
	 *            the StateRequest object
	 * @return the Count object if it exists, else null
	 */
	public Count sendRequest(StateRequest request)
	{
		Count res = null;
		try
		{
			sender.send(request);
			res = listener.requestState(request);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Mapping for healthcheck
	 * 
	 * @return
	 */
	@RequestMapping("/health")
	Health health()
	{
		return Health.up().build();
	}

	/**
	 * Mapping for the counter
	 * 
	 * @param session
	 *            httpSession of the client
	 * @param count
	 *            number to increment the counter
	 * @return
	 */
	@RequestMapping("count")
	@ResponseBody
	String count(HttpSession session, @RequestParam(value = "count", defaultValue = "5") Integer count)
	{
		String sessionID = session.getId();
		logger.info("Request from: " + sessionID);
		Counter counter = new Counter(sessionID);
		counter.count(count);
		return sessionID + ' ' + counter.getCount();
	}

	/**
	 * entrypoint of application
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		logger.info(System.getProperty("os.version"));
		SpringApplication.run(App.class, args);
		// Counter counter = new Counter(instanceId);
		// if (counter.unFinishedCount())
		// counter.resumeCount();
	}
}

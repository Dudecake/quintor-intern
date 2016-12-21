package nl.quintor.cluster.statesaver_db;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class App
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static App instance;
	@Autowired
	private CounterRepository repository;

	public App()
	{
		instance = this;
	}

	public static App instance()
	{
		return instance;
	}

	/**
	 * Gets the repository to store/retrieve the count objects
	 * 
	 * @return the repository
	 */
	public static CounterRepository repository()
	{
		return instance.repository;
	}

	@Async
	public void saveCount(Count count)
	{
		repository.save(count);
		logger.info("saved count for: " + count.getSessionId());
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
		logger.info(sessionID);
		Counter counter = new Counter(sessionID);
		counter.count(count);
		return sessionID + ' ' + String.valueOf(counter.getCount());
	}

	/**
	 * entrypoint of application
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		SpringApplication.run(App.class, args);
		// Counter counter = new Counter(instanceId);
		// if (counter.unFinishedCount())
		// counter.resumeCount();
	}
}

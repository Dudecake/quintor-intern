package nl.quintor.cluster.load_generator;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
public class App
{
	public static final Random r = new Random();
	public static final String APPID;
	private static App instance;
	private static ExecutorService executor = Executors.newFixedThreadPool(4);
	@Autowired
	private LoaderRepository repository;

	static
	{
		String appId = System.getenv("MESOS_TASK_ID");
		APPID = appId.substring(appId.indexOf('-') + 1);
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			@Override
			public void run()
			{
				executor.shutdown();
			}
		});
	}

	public App()
	{
		instance = this;
	}

	public static LoaderRepository getRepository()
	{
		return instance.repository;
	}

	@RequestMapping("/")
	@ResponseBody
	String home()
	{
		return "";
	}

	@RequestMapping("/health")
	Health health()
	{
		return Health.up().build();
	}

	/**
	 * Mapping that generates a load
	 * 
	 * @param threads
	 *            Amount of threads to use
	 * @param load
	 *            Load in percent per thread
	 * @param duration
	 *            Duration of the load
	 * @param count
	 *            Times the load loop needs to run
	 * @param sleep
	 *            Milliseconds to sleep between the runs
	 * @return
	 */
	@RequestMapping("/intload")
	@ResponseBody
	LoadResult intermittentLoad(@RequestParam(value = "threads", defaultValue = "3") Integer threads,
			@RequestParam(value = "load", defaultValue = "-1.0") Double load,
			@RequestParam(value = "duration", defaultValue = "-1") Long duration,
			@RequestParam(value = "count", defaultValue = "1") Integer count,
			@RequestParam(value = "sleep", defaultValue = "30") Integer sleep)
	{
		long startTime = System.nanoTime();
		if (threads <= 0)
			threads = 1;
		if (sleep <= 0)
			sleep = 30;
		Instance ins = new Instance(System.currentTimeMillis(), load, duration, threads, count, sleep);
		repository.save(ins);
		Future<?>[] futures = new Future<?>[threads];
		for (int i = 0; i < count; i++)
		{
			System.out.println("Started loop " + String.valueOf(i + 1));
			for (int j = 0; j < futures.length; j++)
			{
				futures[j] = executor.submit(new LoadProducer(load, duration));
			}
			for (int j = 0; j < futures.length; j++)
			{
				try
				{
					futures[j].get();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				System.out.println("Ended loop " + String.valueOf(i + 1));
				System.out.println("Sleeping for " + String.valueOf(sleep));
				Thread.sleep(sleep);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		repository.delete(ins);
		// return String.format(res, (System.nanoTime() - startTime) /
		// 1000000000.0);
		return new LoadResult(count, (System.nanoTime() - startTime) / 1000000000.0);
	}

	/**
	 * Mapping that generates a load
	 * 
	 * @param threads
	 *            Amount of threads to use
	 * @param load
	 *            Load in percent per thread
	 * @param duration
	 *            Duration of the load
	 * @return String saying how long the load was generated
	 */
	@RequestMapping("/load")
	@ResponseBody
	LoadResult load(@RequestParam(value = "threads", defaultValue = "3") Integer threads,
			@RequestParam(value = "load", defaultValue = "-1.0") Double load,
			@RequestParam(value = "duration", defaultValue = "-1") Long duration)
	{
		long startTime = System.nanoTime();
		if (threads <= 0)
			threads = 1;
		Future<?>[] futures = new Future<?>[threads];
		for (int i = 0; i < futures.length; i++)
		{
			futures[i] = executor.submit(new LoadProducer(load, duration));
		}
		for (int i = 0; i < futures.length; i++)
		{
			try
			{
				futures[i].get();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		// return String.format(res, (System.nanoTime() - startTime) /
		// 1000000000.0);
		return new LoadResult(1, (System.nanoTime() - startTime) / 1000000000.0);
	}

	public static void main(String[] args)
	{
		SpringApplication.run(App.class, args);
		Instance instance = App.getRepository().findOneByInstanceId(APPID);
		if (instance != null)
		{
			int sleep = instance.getSleep();
			if (sleep != 0)
			{
				Future<?>[] futures = new Future<?>[instance.getThreads()];
				for (int i = 0; i < instance.getCount(); i++)
				{
					System.out.println("Started loop " + String.valueOf(i + 1));
					for (int j = 0; j < futures.length; j++)
					{
						futures[j] = executor.submit(new LoadProducer(
								instance.getStartTime() + ((instance.getDuration() + instance.getSleep()) * i),
								instance.getLoad(), instance.getDuration()));
					}
					for (int j = 0; j < futures.length; j++)
					{
						try
						{
							futures[j].get();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					try
					{
						System.out.println("Ended loop " + String.valueOf(i + 1));
						System.out.println("Sleeping for " + String.valueOf(sleep));
						Thread.sleep(sleep);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				App.getRepository().delete(instance);
			}
			else
			{
				Future<?>[] futures = new Future<?>[instance.getThreads()];
				for (int i = 0; i < instance.getCount(); i++)
				{
					System.out.println("Started loop " + String.valueOf(i + 1));
					for (int j = 0; j < futures.length; j++)
					{
						futures[j] = executor.submit(new LoadProducer(
								instance.getStartTime() + ((instance.getDuration() + instance.getSleep()) * i),
								instance.getLoad(), instance.getDuration()));
					}
					for (int j = 0; j < futures.length; j++)
					{
						try
						{
							futures[j].get();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					try
					{
						System.out.println("Ended loop " + String.valueOf(i + 1));
						System.out.println("Sleeping for " + String.valueOf(sleep));
						Thread.sleep(sleep);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				App.getRepository().delete(instance);
			}
		}
	}
}

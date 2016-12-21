package nl.quintor.cluster.statesaver_jms;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Topic;
import javax.servlet.http.HttpSession;

import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.quintor.cluster.statesaver_jms.net.StateRequest;

@Controller
@EnableAutoConfiguration
@EnableJms
public class App
{
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static App instance;
	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private Topic topic;
	private Map<String, Count> states;
	private StateRequest lastReq;
	private Object lock;

	public App()
	{
		this.states = new HashMap<String, Count>();
		this.lock = new Object();
		instance = this;
	}

	@Bean
	public Topic topic()
	{
		return new ActiveMQTopic("statesaver.topic");
	}

	@JmsListener(destination = "statesaver.topic")
	public void readByteArray(byte[] message)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(message);
		ObjectInput in = null;
		try
		{
			in = new ObjectInputStream(bis);
			Object obj = in.readObject();
			if (Count.class.isInstance(obj))
			{
				Count count = (Count)obj;
				if (states.containsKey(count.getSessionId()))
					states.remove(count.getSessionId());
				states.put(count.getSessionId(), count);
				if (lastReq != null && lastReq.isReqFor(count))
				{
					synchronized (lock)
					{
						lock.notifyAll();
					}
				}
				logger.info("Received state of: " + count.getSessionId());
			}
			else if (StateRequest.class.isInstance(obj))
			{
				StateRequest req = (StateRequest)obj;
				if (states.containsKey(req.getSessionId()))
					App.send(states.get(req.getSessionId()));
			}
			else
				logger.info("received some message");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves the result of a previous request for the state
	 * 
	 * @param req
	 *            StateRequest object to retrieve
	 * @return the retrieved Count object if it exists, else null
	 */
	public Count requestState(StateRequest req)
	{
		Count res = null;
		lastReq = req;
		try
		{
			synchronized (lock)
			{
				lock.wait(100);
				res = states.get(req.getSessionId());
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return res;
	}

	public static void send(Serializable message) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(message);
		out.flush();
		byte[] data = bos.toByteArray();
		bos.close();
		instance.jmsMessagingTemplate.convertAndSend(instance.topic, data);
	}

	public static App getInstance()
	{
		return instance;
	}

	@RequestMapping("/health")
	Health health()
	{
		return Health.up().build();
	}

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

	public static void main(String[] args)
	{
		SpringApplication.run(App.class, args);
	}
}

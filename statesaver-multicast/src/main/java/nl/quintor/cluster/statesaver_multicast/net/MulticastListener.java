package nl.quintor.cluster.statesaver_multicast.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.quintor.cluster.statesaver_multicast.App;
import nl.quintor.cluster.statesaver_multicast.Count;

public class MulticastListener extends Thread
{
	private static final Logger logger = LoggerFactory.getLogger(MulticastListener.class);
	private final MulticastSocket socket;
	private final InetAddress group;
	private DatagramPacket packet;
	private boolean running;
	private Map<String, Count> states;
	private StateRequest lastReq;
	private Object lock;

	/**
	 * Construct a MulticastListener object with the given port
	 * 
	 * @param port
	 *            the port to use
	 * @throws IOException
	 */
	public MulticastListener(int port) throws IOException
	{
		this.socket = new MulticastSocket(port);
		this.group = InetAddress.getByName("224.0.0.93");
		this.socket.joinGroup(group);
		this.states = new HashMap<String, Count>();
		this.lock = new Object();
		this.running = true;
		this.setDaemon(true);
	}

	/**
	 * Construct a MulticastListener object with the given group and port
	 * 
	 * @param group
	 *            multicast group to join
	 * @param port
	 * @throws IOException
	 */
	public MulticastListener(InetAddress group, int port) throws IOException
	{
		this.socket = new MulticastSocket(port);
		this.group = group;
		this.socket.joinGroup(group);
		this.states = new HashMap<String, Count>();
		this.lock = new Object();
		this.running = true;
		this.setDaemon(true);
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

	@Override
	public void run()
	{
		try
		{
			byte[] data = new byte[4096];
			while (running)
			{
				packet = new DatagramPacket(data, data.length);
				socket.receive(packet);

				ByteArrayInputStream bis = new ByteArrayInputStream(data);
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
							App.getInstance().sendCount(states.get(req.getSessionId()));
					}
				}
				finally
				{
					if (in != null)
						in.close();
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void disconnect() throws IOException
	{
		running = false;
		socket.close();
	}
}

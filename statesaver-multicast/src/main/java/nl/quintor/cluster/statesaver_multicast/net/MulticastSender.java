package nl.quintor.cluster.statesaver_multicast.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MulticastSender
{
	private static final Logger logger = LoggerFactory.getLogger(MulticastSender.class);
	private static final int port = 5000;
	private DatagramSocket socket;
	private InetAddress group;

	/**
	 * Construct a MulticastSender object with port 5000 and group 244.0.0.93
	 * 
	 * @throws IOException
	 */
	public MulticastSender() throws IOException
	{
		socket = new DatagramSocket(port);
		this.group = InetAddress.getByName("224.0.0.93");
	}

	/**
	 * Constructs a MulticastListener object with port 5000 and a given group
	 * 
	 * @param group
	 *            multicast group to join
	 * @throws SocketException
	 */
	public MulticastSender(InetAddress group) throws SocketException
	{
		socket = new DatagramSocket(port);
		this.group = group;
	}

	/**
	 * Sends the given object to the multicast group
	 * 
	 * @param massage
	 *            the message to send
	 * @throws IOException
	 */
	public void send(Serializable massage) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = new ObjectOutputStream(bos);
		out.writeObject(massage);
		out.flush();
		byte[] data = bos.toByteArray();
		bos.close();

		logger.info("Data len: " + data.length);
		DatagramPacket packet = new DatagramPacket(data, data.length, group, port + 1);
		socket.send(packet);
	}

	public void disconnect()
	{
		socket.close();
	}
}

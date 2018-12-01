import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * This test class will be used as a mock test. this class will be mocking the server Raspberry Pi, while testing the Greenhouse Pi communications
 * @author Danilo Vucetic
 *
 */
public class GreenHouseStubTest {
	
	
	//IP address of the server
	public static InetAddress localIP = null;
	public static int commandPort = 5510; // the port for the CommandReceiveExecute thread
	public static int serverPort = 5511; //port of the server.. this is the test program. 
	public static DatagramSocket socket = null;
	
	//initial test state:
	public static final boolean fanStatus = false;
	public static final float temp = (float) -23.2222;
	public static final float humd = (float) 88.8812;
	
	//threads
	private static Thread dataT;
	private static Thread comT;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			//since we are testing, we want to be able to do so on the local machine.. 
			localIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			System.err.println("was not able to reach the host specified");
			fail("Local host could not be found");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//everything is already torn down.. just need to kill all threads and processes
		//System.exit(0);
	}

	@Before
	public void setUp() throws Exception {
		try{
			socket = new DatagramSocket(serverPort);
		}catch (SocketException e) {
			System.err.println("Could not open socket. Closing program");
			e.printStackTrace();
			fail("Socket exception occured.. most likely that the socket is already in use. ");
		}
		
		//Creating the data structure which will be passed to the threads. Initializing with known test data! 
		GreenhouseData grd = new GreenhouseData();
		grd.setFanActive(fanStatus);
		grd.setRelativeHumidity(humd);
		grd.setTemperature(temp);
		
		//since the threads end irregularly, we have to change the ports we use!
		
		dataT = new Thread(new DataReceiveTransmit(grd, serverPort, localIP, true), "DRT");
		comT = new Thread(new CommandReceiveExecute(grd, commandPort, serverPort, localIP, true), "COM");		
	}

	@SuppressWarnings("deprecation")
	@After
	public void tearDown() throws Exception {
		socket.close();
		comT.stop();
		dataT.stop();
		
		comT = null;
		dataT = null;
		
		//This should not be necessary but it is due to the Thread.stop() methods above. These mean that you essentially have the thread killing itself and leaving
		//all of it's resources.. meaning that we do not shut the ports that we were using. SO... we need to change the port every time. 
		commandPort--;
		serverPort++;
	}
	

	/**
	 * This test checks that data packets from the GP are correctly formatted
	 */
	@Test
	public void correctData(){
		//Start the threads that run in GreenhouseMain
		dataT.start();
		comT.start();
		
		//Receive a packet
		byte[] buf = new byte[500];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		
		//receive the data packet
		try{
			socket.receive(receivePacket);
			byte[] ackBuf = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.DATA);
			DatagramPacket ack = new DatagramPacket(ackBuf, ackBuf.length);
			//immediately send ack
			socket.connect(localIP, receivePacket.getPort());
			socket.send(ack);
			socket.disconnect();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now check the data packet for correctness:
		String type = null;
		String data = null;
		byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				data = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-2));
				System.out.println(data);
				System.out.println("Received type: " + type + "; received data = " + data);
				break;
			}
		
		}
		
		assert(type.equals("DATA"));
		//FIXME: update this to the JSON text!!!
		assert(data.equals("Temprature: " + temp + ";Humidity: " + humd + "fanStatus: "  + fanStatus + "; This is update number: "));
		
	}
	
	@Test
	public void correctData2(){
		//Start the threads that run in GreenhouseMain
		dataT.start();
		comT.start();
		
		//Receive a packet
		byte[] buf = new byte[500];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		
		//receive the data packet
		try{
			socket.receive(receivePacket);
			byte[] ackBuf = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.DATA);
			DatagramPacket ack = new DatagramPacket(ackBuf, ackBuf.length);
			//immediately send ack
			socket.connect(localIP, receivePacket.getPort());
			socket.send(ack);
			socket.disconnect();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now check the data packet for correctness:
		String type = null;
		String data = null;
		byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				data = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-2));
				System.out.println(data);
				System.out.println("Received type: " + type + "; received data = " + data);
				break;
			}
		
		}
		
		//receive the data packet
				try{
					socket.receive(receivePacket);
					byte[] ackBuf = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.DATA);
					DatagramPacket ack = new DatagramPacket(ackBuf, ackBuf.length);
					//immediately send ack
					socket.connect(localIP, receivePacket.getPort());
					socket.send(ack);
					socket.disconnect();
				}catch(IOException ioe){
					ioe.printStackTrace();
				}
				
				//now check the data packet for correctness:
				String type2 = null;
				String data2 = null;
				byte[] recData2 = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
				for(int n = 0; n < receivePacket.getLength(); n++){
					if(recData2[n] == '\0'){
						//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
						type2 = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
						data2 = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-2));
						System.out.println(data2);
						System.out.println("Received type: " + type2 + "; received data = " + data2);
						break;
					}
				
				}
		
		
		
		assert(type.equals("DATA"));
		//FIXME: update this to the JSON text!!!
		assert(data.equals("Temp: " + temp + ";Humi: " + humd + "fanStatus: "  + fanStatus + "; This is update number: "));
		
	}
	
}

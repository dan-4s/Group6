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
import org.junit.runners.JUnit4;
import junit.framework.TestCase;

/**
 * This test class will be used as a mock test. this class will be mocking the server Raspberry Pi, while testing the Greenhouse Pi communications
 * @author Danilo Vucetic
 *
 */
public class GreenhouseMockTest {
	
	
	//IP address of the server
	public static InetAddress localIP = null;
	public static int commandPort = 5510; // the port for the CommandReceiveExecute thread
	public static int dataPort = 5509; // the port for the DataReceiveTransmit thread
	public static int serverPort = 5511; //port of the server.. this is the test program. 
	public static DatagramSocket socket = null;
	
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
	}

	@After
	public void tearDown() throws Exception {
		socket.close();
	}
	
	/**
	 * In this test we are sending an incorrect command to the GP. 
	 */
	@Test
	public void test1() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
		String args[] = {"testing"};
		GreenhouseMain.main(args);
		
		
		byte[] buf = new byte[500];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		
		//receive the data packet
		try{
			socket.receive(receivePacket);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//send the incorrect command packet
		byte[] command = "COMMAND\0this is an incorrect command\0".getBytes();
		DatagramPacket packet = new DatagramPacket(command, command.length);
		try{
			socket.connect(localIP, commandPort);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
		
		//receive the error packet
		buf = new byte[500];
		receivePacket = new DatagramPacket(buf, buf.length);
		
		try{
			socket.receive(receivePacket);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now that a packet has been received, we can find it's type and respond accordingly. 
		String message = new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
		System.out.println("Received Message: " + message);
		
		String type = null;
		byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				System.out.println("Received type: " + type);
				break;
			}
		
		}
		
		assert(type.equals("ERROR"));
	}
	

	/**
	 * In this test we are observing the operation of GreenhouseMain when it sends unreciprocated data 3 times. We expect an ERROR packet to be sent. 
	 */
	@Test
	public void test2() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
		String args[] = {"testing"};
		GreenhouseMain.main(args); //TODO: GreenhouseMain is fine for individual tests, but is absolute shit for running all tests at ONCE CHANGE THIS!!!!!
		//TODO: make all the threads yourself!!
		
		for(int i = 0; i < 4; i++){
			byte[] buf = new byte[500];
			DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
			
			try{
				socket.receive(receivePacket);
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
			
			//now that a packet has been received, we can find it's type and respond accordingly. 
			String message = new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
			System.out.println("Received Message: " + message);
			
			String type = null;
			byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
			for(int n = 0; n < receivePacket.getLength(); n++){
				if(recData[n] == '\0'){
					//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
					type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
					System.out.println("Received type: " + type);
					break;
				}
			
			}
			
			if(i == 3){
				//This is the error case we were hoping to see. The error message is irrelevant. 
				assert(type.equals("ERROR"));
				return;
				
			}else if(type.equals("DATA")){
				
			}else if (type.equals("ACK")){
				//This should be from a command that we sent out. Since this is the first packet to be received it is an error
				fail("Expected a DATA packet not an ACK!");
			}else{
				fail("Should have received a data packet, instead received: " + new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getData().length)));
			}
		}
	}
	
	

	

}

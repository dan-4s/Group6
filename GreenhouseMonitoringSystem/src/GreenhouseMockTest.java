import static org.junit.Assert.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.xml.crypto.Data;

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
				System.out.println("Received type: " + type + "; received data = " + data);
				break;
			}
		
		}
		
		assert(type.equals("DATA"));
		//TODO: update this to the JSON text!!!
		assert(data.equals("Temp: " + temp + ";Humi: " + humd + "fanStatus: "  + fanStatus + "; This is update number: "));
		
	}

	
	/**
	 * This test sends an incorrect ACK packet to the GP data thread and expects an error packet in response. 
	 */
	@Test
	public void incorrectAckToGPData(){
		//Start the threads that run in GreenhouseMain
		dataT.start();
		comT.start();
		
		//Receive a packet
		byte[] buf = new byte[500];
		DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
		
		//receive the data packet
		try{
			socket.receive(receivePacket);
			byte[] ackBuf = "qwe\0\0999l".getBytes();
			DatagramPacket ack = new DatagramPacket(ackBuf, ackBuf.length);
			//immediately send ack
			socket.connect(localIP, receivePacket.getPort());
			socket.send(ack);
			socket.disconnect();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		buf = new byte[500];
		receivePacket = new DatagramPacket(buf, buf.length);
		
		//receive the error packet
		try{
			socket.receive(receivePacket);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now check the error packet for correctness:
		String type = null;
		String eMessage = null;
		byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				eMessage = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-1));
				System.out.println("Received type: " + type + "; received data = " + eMessage);
				break;
			}
		
		}
		System.out.println(eMessage);
		assert(type.equals("ERROR"));
		assert(eMessage.equals("Did not receive correct/valid ack"));
	}
	
	
	
	/**
	 * In this test we are sending a good command with the same fan status. We should get an ack back and the next data packet should have the same fan status!
	 */
	@Test
	public void normalOperation1() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
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
		
		//send the command packet
		byte[] command = CreateGreenhouseMessage.command(fanStatus);
		DatagramPacket packet = new DatagramPacket(command, command.length); 
		try{
			socket.connect(localIP, commandPort);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
		
		//receive the ack packet
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
		
		assert(type.equals("ACK"));
		
		//receive the data packet
		buf = new byte[500];
		receivePacket = new DatagramPacket(buf, buf.length);
		
		try{
			socket.receive(receivePacket);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now that a packet has been received, we can find it's type and respond accordingly. 
		message = new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
		System.out.println("Received Message: " + message);
		
		type = null;
		String data = null;
		recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				data = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-2));
				System.out.println("Received type: " + type + "; received data = " + data);
				break;
			}
		
		}
		
		assert(type.equals("DATA"));
		//TODO: update this to the JSON text!!!
		assert(data.equals("Temp: " + temp + ";Humi: " + humd + "fanStatus: "  + fanStatus + "; This is update number: "));
	}

	
	/**
	 * In this test we are sending a good command with the different fan status. We should get an ack back and the next data packet should have the new fan status!
	 */
	@Test
	public void normalOperation2() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
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
		
		//send the command packet
		byte[] command = CreateGreenhouseMessage.command(!fanStatus);
		DatagramPacket packet = new DatagramPacket(command, command.length); 
		try{
			socket.connect(localIP, commandPort);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
		
		//receive the ack packet
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
		
		assert(type.equals("ACK"));
		
		//receive the data packet
		buf = new byte[500];
		receivePacket = new DatagramPacket(buf, buf.length);
		
		try{
			socket.receive(receivePacket);
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		//now that a packet has been received, we can find it's type and respond accordingly. 
		message = new String(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
		System.out.println("Received Message: " + message);
		
		type = null;
		String data = null;
		recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
		for(int n = 0; n < receivePacket.getLength(); n++){
			if(recData[n] == '\0'){
				//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
				type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, n));
				data = new String(Arrays.copyOfRange(receivePacket.getData(), n+1, receivePacket.getLength()-2));
				System.out.println("Received type: " + type + "; received data = " + data);
				break;
			}
		
		}
		
		assert(type.equals("DATA"));
		//TODO: update this to the JSON text!!!
		assert(data.equals("Temp: " + temp + ";Humi: " + humd + "fanStatus: "  + !fanStatus + "; This is update number: "));
	}
	
	/**
	 * In this test we are sending an incorrect command to the GP. 
	 */
	@Test
	public void incorrectCommand1() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
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
			socket.connect(localIP, receivePacket.getPort());
			socket.send(ack);
			socket.disconnect();
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
	public void unreciprocatedData() {
		// we expect the program to send data packets every 2 seconds.. because I programmed it to do so..
		//so set up all of our stuff first, then enable the program to run and start the testing. 
		
		//Start the threads that run in GreenhouseMain
		dataT.start();
		comT.start();
		
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
	
	/**
	 * 
	 */
	@Test
	public void invalidHeader1(){
		
	}
	

}

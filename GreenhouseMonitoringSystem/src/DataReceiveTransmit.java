import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * This Class implements Runnable and so may be run as a separate thread. Each thread is responsible for listening to a 
 * USB serial port which receives data from the Arduino. It uses the communication protocol as defined in the design
 * document to send the data to the server.
 * 
 * @author Danilo Vucetic
 *
 */
public class DataReceiveTransmit implements Runnable{

	private GreenhouseData struct;
	private int receiveTransmitPort;
	private int serverPort;
	private InetAddress serverIP;
	private DatagramSocket socket;
	
	private int numUnreciprocated = 0;
	
	private final int TIMEOUT_LENGTH = 500; //500ms timeout for sockets waiting on a packet.
	private final int MAX_SIZE = 500;
	
	public DataReceiveTransmit(GreenhouseData grd, int receiveTransmitPort, int serverPort, InetAddress serverIP){
		struct = grd;
		this.receiveTransmitPort = receiveTransmitPort;
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT_LENGTH);
		} catch (SocketException socketEx) {
			socketEx.printStackTrace();
			System.exit(1);
		}
	}
	
	public void run() {
		System.out.println("This thread is working: " + Thread.currentThread().getName());
		struct.print();
		int updateNum = 1;
		while(true){
			
			//check the serial port. make sure to do a blocking call!!!
			//get the data
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//update the data structure
			
			
			//update the server
			updateServer("This is update number: " + updateNum);
			updateNum++;
		}
		
	}

	/**
	 * This method sends the JSON to the server using the communications protocol defined in the design
	 * @param JSON
	 */
	private void updateServer(String JSON){
		
		//Send the data to the server and wait for a response
		byte[] data = CreateGreenhouseMessage.data(JSON);
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try{
			socket.connect(serverIP, serverPort);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
		
		//wait for a response for 500ms; expecting ACK packet so will directly send to the ackDecode
		byte[] rBuf = new byte[MAX_SIZE];
		DatagramPacket receivePacket = new DatagramPacket(rBuf, rBuf.length);
		try{
			socket.receive(receivePacket);
		}catch (SocketTimeoutException e){
			numUnreciprocated++;
			System.err.println("DRT: Did not receive response from server, this is unreciprocated response #: " + numUnreciprocated);
		}catch(IOException ioe){
			ioe.printStackTrace();
			System.exit(1);
		}
		
		//Checking if the received packet is an ACK packet, and if it is then that it is a DATA ack
		String ack = CreateGreenhouseMessage.acknowledgeDecode(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
		if(ack == null){
			System.err.println("DRT: ACK is null");
		}else if(ack.equals("DATA")){
			System.out.println("DRT: Successfully received ack from server after sending data");
			numUnreciprocated = 0;
		}else{
			System.err.println("DRT: Did not receive correct ack: " + ack);
		}
		
		//Checking if the number of errors has been reached
		if(numUnreciprocated >= 3){
			sendErrorMessage("Due to " + numUnreciprocated + " unreciprocated packets. The Server might be off or not responding.");
			numUnreciprocated = 0;
			System.err.println("DRT: exiting due to number of unreciprocated messages!");
			System.exit(1);
		}
		
	}
	
	/**
	 * This method send an error message to the server using the communications protocol defined in the design
	 * @param errorMessage
	 */
	private void sendErrorMessage(String errorMessage){
		System.err.println("DRT: " + errorMessage);
		byte[] err = CreateGreenhouseMessage.error(errorMessage);
		DatagramPacket packetErr = new DatagramPacket(err, err.length);
		try{
			socket.connect(serverIP, serverPort);
			socket.send(packetErr);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
	}
	

}

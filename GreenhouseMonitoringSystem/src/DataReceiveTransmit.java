import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import org.json.*;

/**
 * This Class implements Runnable and so may be run as a separate thread. Each thread is responsible for listening to a 
 * USB serial port which receives data from the Arduino. It uses the communication protocol as defined in the design
 * document to send the data to the server.
 * 
 * @author Danilo Vucetic and Jacob Martin
 *
 */
public class DataReceiveTransmit implements Runnable{

	private GreenhouseData struct;
	private int serverPort;
	private InetAddress serverIP;
	private DatagramSocket socket;
	
	private int numUnreciprocated = 0;
	private boolean underTest;
	private int numUnreciprocatedSerial = 0;
	
	private final int TIMEOUT_LENGTH = 500; //500ms timeout for sockets waiting on a packet.
	private final int MAX_SIZE = 500;
	
	public DataReceiveTransmit(GreenhouseData grd, int serverPort, InetAddress serverIP, boolean underTest){
		struct = grd;
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(TIMEOUT_LENGTH);
		} catch (SocketException socketEx) {
			socketEx.printStackTrace();
			System.exit(1);
		}
		this.underTest = underTest;
	}
	
	public void run() {
		System.out.println("This thread is working: " + Thread.currentThread().getName());
		struct.print();
		TwoWaySerialComm serialPort = new TwoWaySerialComm();
	    new Thread(serialPort).start();
	    JSONObject tempJSON = null;
		while(true){
			if(!underTest){
				//check the serial port. 
				try{
				        try {
					    //only want to update the data every 2 or so seconds. This is because the arduino can only pull
					    //new data every 2 seconds so there is no point in polling more than that. This also means that this thread
				        //isn't hogging clock cycles by constantly polling. 
					     Thread.sleep(2000);
				        } catch (InterruptedException e) {
					     e.printStackTrace();
			          	}
					tempJSON= serialPort.getSerialJSON();
					struct.setRelativeHumidity((float)Float.parseFloat(tempJSON.getString("humidity")));
					struct.setTemperature((float)Float.parseFloat(tempJSON.getString("temperature")));
					//struct.setJSON(tempJSON);
				}catch (Exception e) {
				        e.printStackTrace();
					numUnreciprocatedSerial++;
					System.err.println("DRT: Did not receive response from Sensor, this is unreciprocated response #: " + numUnreciprocatedSerial);
					//Checking if the number of errors has been reached
					if(numUnreciprocatedSerial >= 3){
						sendErrorMessage("Due to " + numUnreciprocatedSerial + " unreciprocated packets. The Sensor might be off or not responding.");
						numUnreciprocatedSerial = 0; //setting back to zero so that if there are 3 unreciprocated again we can send the error message again. 
					}
				}
			}else{
				//don't do anything.. this is the stub section..
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
			//update the server
			try {
				updateServer(struct.getJSON().toString());
			} catch (JSONException e) {
				e.printStackTrace();
				//JSON invalid format.
				//this will only be sent if we messed up the arduino code
				sendErrorMessage("Something seriously went wrong if you are seeing this.");
			}
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
			//Checking if the number of errors has been reached
			if(numUnreciprocated >= 3){
				sendErrorMessage("Due to " + numUnreciprocated + " unreciprocated packets. The Server might be off or not responding.");
				numUnreciprocated = 0; //setting back to zero so that if there are 3 unreciprocated again we can send the error message again. 
			}
			return;
		}catch(IOException ioe){
			ioe.printStackTrace();
			//THIS means that there has been some error with the IP address, or the system is not connected via an ethernet cable. 
			//No choice but to exit the program
			System.exit(1);
		}
		
		//Checking if the received packet is an ACK packet, and if it is then that it is a DATA ack
		String ack = CreateGreenhouseMessage.acknowledgeDecode(Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength()));
		if(ack == null){
			System.err.println("DRT: Error with ACK packet");
			sendErrorMessage("Did not receive correct/valid ack");
		}else if(ack.equals("DATA")){
			System.out.println("DRT: Successfully received ack from server after sending data");
			numUnreciprocated = 0;
		}else{
			System.err.println("DRT: Did not receive correct ack: " + ack);
			sendErrorMessage("Did not receive correct/valid ack");
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

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
 * @author Danilo Vucetic
 *
 */
public class DataReceiveTransmit implements Runnable{

	private GreenhouseData struct;
	private int serverPort;
	private InetAddress serverIP;
	private DatagramSocket socket;
	private JSONObject tempJSON;
	
	private int numUnreciprocated = 0;
	private boolean underTest;
	
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
		int updateNum = 1;
		TwoWaySerialComm serialPort = new TwoWaySerialComm();
	    new Thread(serialPort).start();
		while(true){
			
			if(!underTest){
				//check the serial port. 
				try{
				        try {
					    //only want to update the data every 2 or so seconds. This is because the arduino can only pull
					    //new data every 2 seconds so there is no point in polling more than that.
					     Thread.sleep(2000);
				        } catch (InterruptedException e) {
					     // TODO Auto-generated catch block
					     e.printStackTrace();
			          	}
					tempJSON= serialPort.getSerialJSON();
					struct.setRelativeHumidity((float)Float.parseFloat(tempJSON.getString("humidity")));
					struct.setTemperature((float)Float.parseFloat(tempJSON.getString("temperature")));
					//struct.setJSON(tempJSON);
				}catch (Exception e) {
					System.out.println("Error: not reading serial data"+ e);
				}
			}else{
				//don't do anything.. this is the stub section..
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			//update the server
			//TODO: update this to the JSON text!!
			try {
				updateServer(struct.getJSON());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			updateNum++;
		}
		
	}

	/**
	 * This method sends the JSON to the server using the communications protocol defined in the design
	 * @param JSON
	 */
	private void updateServer(JSONObject JSON){
		
		//Send the data to the server and wait for a response
		byte[] data = CreateGreenhouseMessage.data(JSON.toString());
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
				numUnreciprocated = 0;
				//System.err.println("DRT: exiting due to number of unreciprocated messages!");
				//TODO: Do something here!!! Was doing :System.exit(1);, but that's a bit much!
			}
			return;
		}catch(IOException ioe){
			ioe.printStackTrace();
			//TODO: is this actually acceptable???
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

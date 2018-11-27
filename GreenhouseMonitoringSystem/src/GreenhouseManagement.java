import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.json.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CRLReason;
import java.util.Arrays;



/**
 * This class should be running on the Server Pi and should be listening to it's UDP port for any data or ACKs.
 * @author Danilo Vucetic
 *
 */

public class GreenhouseManagement {

	private static int dataPort = 5509; //data port on the greenhouse pi
	private InetAddress greenhouseIP; //TODO: for not this should be local host, later it should be across a network.
	private static int commandPort = 5510; //command port on the greenhouse pi
	private static int serverPort = 5511; //this is my port
	private static int commandUnreciprocated = 0;
	
	private static DatagramSocket socket;
	
	public static void main(String []args){
		//set up the socket
		try {
			socket = new DatagramSocket(serverPort);
		} catch (SocketException e) {
			System.err.println("Could not open socket. Closing program");
			e.printStackTrace();
			System.exit(1);
		}
		boolean fanSTAT = false;
		
		while(true){
			//receive message
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
			for(int i = 0; i < receivePacket.getLength(); i++){
				if(recData[i] == '\0'){
					//assuming that this is the first occurrence if null byte, then we know that the previous bytes are the string of type
					type = new String(Arrays.copyOfRange(receivePacket.getData(), 0, i));
					System.out.println("Received type: " + type);
					break;
				}
			}
			
			//We can only get ack from commands, since we do not send data.. Also we should get data and error so this is checked
			if(type.equals("ACK")){
				System.out.println("ACK received for: " + CreateGreenhouseMessage.acknowledgeDecode(recData));
				commandUnreciprocated--;
				continue;
			}else if(type.equals("DATA")){
				System.out.println("DATA received: " + CreateGreenhouseMessage.dataDecode(recData));
				try {
					updateDatabase(CreateGreenhouseMessage.dataDecode(recData));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				acknowledge(receivePacket.getPort(), receivePacket.getAddress(), CreateGreenhouseMessage.MessageType.DATA);
			}else if(type.equals("ERROR")){
				System.err.println("ERROR received: " + CreateGreenhouseMessage.errorDecode(recData));
				//TODO: do something about the error!!!
			}else{
				System.err.println("Received packet is not of right type");
			}
			
			//Error checking
			if(commandUnreciprocated >= 3){
				commandUnreciprocated = 0;
				System.err.println("3 commands have gone unreciprocated! This should be addressed!");
			}
			
			
			//TODO: check database for command... then send it to the command port on the GP
			//assuming that a command is present, we can just send it over to the GP
			//TODO: remove the sleep, in place to mimic query of dtb
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//sending command, want a timeout of 500ms to get a response. otherwise just indicate error has occurred
			System.out.println("sending command! port = " + commandPort);
			sendCommand(commandPort, receivePacket.getAddress(), fanSTAT); 
			fanSTAT = !fanSTAT;
			
			
		}
	}
	
	
	/**
	 * This method, given the port and IP to respond to, will send an ACK packet to the machine
	 * @param port the port of the machine
	 * @param IPAddress the address of the machine on the network
	 * @param type the message type being acknowledged
	 */
	private static void acknowledge(int port, InetAddress IPAddress, CreateGreenhouseMessage.MessageType type){
		byte[] ack = CreateGreenhouseMessage.acknowledge(type);
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		try{
			socket.connect(IPAddress, port);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	/**
	 * This method send the command to the command port of the GP as specified by port.
	 * @param port: command port of the GP
	 * @param IPAddress: address of the GP
	 * @param fanStatus: This is what the fan should be changed to. true meaning on, false meaning off. 
	 */
	private static void sendCommand(int port, InetAddress IPAddress, boolean fanStatus){
		byte[] ack = CreateGreenhouseMessage.command(fanStatus);
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		try{
			socket.connect(IPAddress, port);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
		commandUnreciprocated++;
	}
	
	//TODO: Jacob this is you
	private static String pullFromDatabase(){
		try {
			URL obj = new URL("https://greenhousedata-cef98.firebaseio.com/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1/sensorData/Sensor1");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			int responseCode = con.getResponseCode();
			System.out.println("GET Response Code :: " + responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				// print result
				return response.toString();
			} else {
				System.out.println("GET request not worked");
				return "";
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: "+ e);
		}
		return null;
	}
	
	//TODO: Jacob this is you
	private static void updateDatabase(String JSON) throws JSONException{
		try {
			URL obj = new URL("https://greenhousedata-cef98.firebaseio.com/users/TFSInAIyjZasPfyanDjsveMmdRH2/greenHouses/-LO6EC8taWQp_X6WGnS1/sensorData/Sensor1.json");
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("PUT");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");

			// For POST only - START
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			os.write(JSON.getBytes());
			os.flush();
			os.close();
			// For POST only - END
			int responseCode = con.getResponseCode();
			System.out.println("POST Response Code :: " + responseCode);

		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("ERROR: "+ e);
		}
	}
	
	
}

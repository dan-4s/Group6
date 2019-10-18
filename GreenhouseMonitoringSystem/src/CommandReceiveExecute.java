import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * This Class implements Runnable and so may be run as a separate thread. Each thread is responsible for listening to a 
 * port which receives commands, and executing the command. It uses the communication protocol as defined in the design
 * document. 
 * 
 * @author Danilo Vucetic and Jacob Martin
 *
 */
public class CommandReceiveExecute implements Runnable{

	private GreenhouseData struct;
	private int serverPort;
	private InetAddress serverIP;
	private DatagramSocket socket;
	private boolean underTest;
	
	public CommandReceiveExecute(GreenhouseData grd, int commandReceivePort, int serverPort, InetAddress serverIP, boolean underTest){
		struct = grd;
		this.serverPort = serverPort;
		this.serverIP = serverIP;
		try {
			socket = new DatagramSocket(commandReceivePort);
		} catch (SocketException socketEx) {
			socketEx.printStackTrace();
			System.exit(1);
		}
		this.underTest = underTest;
	}
	
	public void run() {
		//Logging print statement
		System.out.println("This thread is working: " + Thread.currentThread().getName());
		
		//command wait loop: This essentially waits for a packet to come in. When a packet arrives, it attempts to decode and execute the command.
		while(true){
			//receive a packet. 
			byte[] rBuf = new byte[500];
			DatagramPacket receivePacket = new DatagramPacket(rBuf, rBuf.length);
			try{
				System.out.println("CRE: waiting for a packe!!!");
				socket.receive(receivePacket);
			}catch(IOException ioe){
				//This error means that we were unable to connect to the SP, so we will exit the program
				System.err.println("unable to connect to the SP, exiting the program!");
				System.exit(1);
			}
			
			//decoding the packet for the new fan status / the command. 
			Boolean newFanStatus = null;
			try{
				byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
				newFanStatus = CreateGreenhouseMessage.commandDecode(recData);
			}catch(Exception e){
				e.printStackTrace();
				sendErrorMessage("Error occured when decoding command message. Make sure that command is correct format and can be decoded.");
				continue;
			}
			
			if(newFanStatus == null){
				//This is an error in decoding the packet. 
				sendErrorMessage("Fan status NULL, unacceptable state. Make sure the data is in correct form.");
				continue;
			}
			
			System.out.println("CRE: command received, executing now, command states: " + newFanStatus);
			boolean currentStatus = struct.getFanActive();
			if(newFanStatus && !currentStatus){
				//turn on the fan
				struct.setFanActive(newFanStatus);
				turnFanOn();
			}else if(!newFanStatus && currentStatus){
				//turn the fan off
				struct.setFanActive(newFanStatus);
				turnFanOff();
			}
			
			//send the ack
			acknowledge();
		}
	}

	/**
	 * This method turns the fan on by setting a GPIO pin high. Currently a stub since the fan was not implemented
	 */
	private void turnFanOn(){
		if(!underTest){
			//pin.high();

		}else{
			//Here, since we know that we are under test, we should just change the value in the data structure but not actually attempt to access any hardware.. 
			//this operation is actually already done for us above. so allow this code to do nothing!
		}
	}
	
	/**
	 * This method turns the fan off by de-asserting a GPIO pin. Currently a stub since the fan was not implemented
	 */
	private void turnFanOff(){
		if(!underTest){
			//pin.low();
		}else{
			//Here, since we know that we are under test, we should just change the value in the data structure but not actually attempt to access any hardware.. 
			//this operation is actually already done for us above. so allow this code to do nothing!
		}
	}
	
	/**
	 * Creates and sends the acknowledge packet. Since this thread can only accept commands, the only ack's we are sending will be to command messages. 
	 */
	private void acknowledge(){
		byte[] ack = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.COMMAND);
		DatagramPacket packet = new DatagramPacket(ack, ack.length);
		try{
			socket.connect(serverIP, serverPort);
			socket.send(packet);
			socket.disconnect();
		} catch (IOException io) {
			io.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * This method send an error message to the server using the communications protocol defined in the design
	 * @param errorMessage
	 */
	private void sendErrorMessage(String errorMessage){
		System.err.println(errorMessage);
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

import java.awt.SecondaryLoop;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.cert.CRLReason;
import java.util.Arrays;


/**
 * This Class implements Runnable and so may be run as a separate thread. Each thread is responsible for listening to a 
 * port which receives commands, and executing the command. It uses the communication protocol as defined in the design
 * document. 
 * 
 * @author Danilo Vucetic
 *
 */


public class CommandReceiveExecute implements Runnable{

	private GreenhouseData struct;
	private int commandReceivePort;
	private int serverPort;
	private InetAddress serverIP;
	private DatagramSocket socket;
	private boolean underTest;
	
	public CommandReceiveExecute(GreenhouseData grd, int commandReceivePort, int serverPort, InetAddress serverIP, boolean underTest){
		struct = grd;
		this.commandReceivePort = commandReceivePort;
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
		System.out.println("This thread is working: " + Thread.currentThread().getName());
		struct.print();
		
		//TODO: remove this.. TESTING PURPOSES ONLY!!!
		//works
		byte[] aaa = CreateGreenhouseMessage.data("");
		String out = CreateGreenhouseMessage.dataDecode(aaa);
		System.out.println("The data: " + out);
		
		//works
		aaa = CreateGreenhouseMessage.error("12345");
		out = CreateGreenhouseMessage.errorDecode(aaa);
		System.out.println("The error: " + out);
		
		//works
		aaa = CreateGreenhouseMessage.command(true);
		Boolean oo = CreateGreenhouseMessage.commandDecode(aaa);
		System.out.println("The com: " + oo);
		
		aaa = CreateGreenhouseMessage.command(true);
		oo = CreateGreenhouseMessage.commandDecode(null);
		System.out.println("The com: " + oo);
		
		//works
		aaa = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.DATA);
		out = CreateGreenhouseMessage.acknowledgeDecode(aaa);
		System.out.println("The ack: " + out);
		
		//command wait loop:
		while(true){
			byte[] rBuf = new byte[500];
			DatagramPacket receivePacket = new DatagramPacket(rBuf, rBuf.length);
			try{
				socket.receive(receivePacket);
			}catch(IOException ioe){
				ioe.printStackTrace();
				System.exit(1);
			}
			
			Boolean newFanStatus = null;
			try{
				byte[] recData = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
				newFanStatus = CreateGreenhouseMessage.commandDecode(recData);
			}catch(Exception e){
				e.printStackTrace();
				sendErrorMessage("Error occured when decoding command message. Make sure that command is correct format and can be decoded.");
			}
			
			if(newFanStatus == null){
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

	private void turnFanOn(){
		if(!underTest){
			//TODO: code, make sure to change the data structure
		}else{
			//Here, since we know that we are under test, we should just change the value in the data structure but not actually attempt to access any hardware.. 
			//this operation is actually already done for us above. so allow this code to do nothing!
		}
	}
	
	
	private void turnFanOff(){
		if(!underTest){
			//TODO: code, make sure to change the data structure
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

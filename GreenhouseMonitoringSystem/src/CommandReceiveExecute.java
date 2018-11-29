import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

//TODO: remove if we are not using!
//FOR GPIO ACCESS
//import com.pi4j.io.gpio.GpioController;
//import com.pi4j.io.gpio.GpioFactory;
//import com.pi4j.io.gpio.GpioPinDigitalOutput;
//import com.pi4j.io.gpio.PinState;
//import com.pi4j.io.gpio.RaspiPin;

//final GpioController gpio = GpioFactory.getInstance();
//final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MOTOR",PinState.HIGH);



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
		
		//command wait loop:
		while(true){
			byte[] rBuf = new byte[500];
			DatagramPacket receivePacket = new DatagramPacket(rBuf, rBuf.length);
			try{
				System.out.println("CRE: waiting for a packet !!!");
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
				continue;
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
			//TODO: make sure this actually works!
			//pin.high();

		}else{
			//Here, since we know that we are under test, we should just change the value in the data structure but not actually attempt to access any hardware.. 
			//this operation is actually already done for us above. so allow this code to do nothing!
		}
	}
	
	
	private void turnFanOff(){
		if(!underTest){
			//TODO: make sure this works
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

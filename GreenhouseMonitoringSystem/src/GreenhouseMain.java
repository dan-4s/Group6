import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is the main program for each greenhouse for the Greenhouse Monitoring System. 
 * This program creates the threads: DataReceiveTransmit and CommandReceiveExecute, and 
 * the GreenhouseData data structure. 
 * 
 * @author Danilo Vucetic
 *
 */

public class GreenhouseMain {

	public static void main (String []args){
		//Start both threads here, create the data structure as well. 
		
		//TODO: change all of the ports to user input as well as the IP address
		//These are the ports and addresses required for communications between the Pi's
		int serverPort = 5511; //port of the server
		
		//IP address of the server
		InetAddress serverIP = null;
		try {
			serverIP = InetAddress.getLocalHost(); //TODO: for now just using local host, but later should change to the actual IP
		} catch (UnknownHostException e) {
			System.err.println("was not able to reach the host specified");
		} 
		
		int commandPort = 5510; // the port for the CommandReceiveExecute thread
		int dataPort = 5509; // the port for the DataReceiveTransmit thread
		
		//Creating the data structure which will be passed to the threads
		GreenhouseData grd = new GreenhouseData();
		grd.setFanActive(true);
		grd.setRelativeHumidity(0);
		grd.setTemperature(-1);
		
		Thread data = new Thread(new DataReceiveTransmit(grd, dataPort, serverPort, serverIP), "DRT");
		Thread com = new Thread(new CommandReceiveExecute(grd, commandPort, serverPort, serverIP), "COM");
		data.start();
		com.start();
		
		
		
	}
}
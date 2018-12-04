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
		boolean underTest = false; //default, testing is off. 
		InetAddress serverIP = null;
		
		String ip = "10.0.0.61"; //this is SP if we are using the RPi
		
		//First check to see if we are testing:
		if(args.length == 1){
			if(args[0].equals("testing")){
				//This means that we are mock testing. System must be set up for mock testing by sending flags to other classes and threads
				underTest = true; 
			}
		}
		
		//These are the ports and addresses required for communications between the Pi's
		int serverPort = 5511; //port of the server
		int commandPort = 5510; // the port for the CommandReceiveExecute thread
		
		//Setting the IP address of the server
		try {
			if(underTest){
				serverIP = InetAddress.getLocalHost();
			}else{
				serverIP = InetAddress.getByName(ip); 
			}
		} catch (UnknownHostException e) {
			//exit the program if the connection was not made. This means that the cables are most likely not connected 
			System.err.println("was not able to reach the host specified");
			System.exit(0);
		}
		
		//Creating the data structure which will be passed to the threads. Initializing with incorrect data.
		GreenhouseData grd = new GreenhouseData();
		
		//creating and starting the threads. 
		Thread data = new Thread(new DataReceiveTransmit(grd, serverPort, serverIP, underTest), "DRT");
		Thread com = new Thread(new CommandReceiveExecute(grd, commandPort, serverPort, serverIP, underTest), "COM");
		data.start();
		com.start();
	}
}

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
		//TODO: change this from true to false when the serial code is added and when we actually want to turn the fan on and off. 
		boolean underTest = false;
		String ip = "10.0.0.61"; //this is SP if we are using the RPi
		//First check to see if we are testing:
		if(args.length == 1){
			if(args[0].equals("testing")){
				//This means that we are mock testing. System must be set up for mock testing by sending flags to other classes and threads
				underTest = true;
				ip = "127.0.0.1";//this is the local host.. if we are testing.. 
			}
			
		}
		
		//TODO: change all of the ports to user input as well as the IP address
		//These are the ports and addresses required for communications between the Pi's
		int serverPort = 5511; //port of the server
		int commandPort = 5510; // the port for the CommandReceiveExecute thread
		
		//IP address of the server
		InetAddress serverIP = null;
		try {
			serverIP = InetAddress.getByName(ip); //this will fetch the ip address for either local host or the SP based on testing flag
		} catch (UnknownHostException e) {
			System.err.println("was not able to reach the host specified");
		} 
		
		
		
		//Creating the data structure which will be passed to the threads. Initializing with incorrect data.
		//TODO: stop initializing with incorrect data. do so in the toString or getJSON() methods where if no data is present then put null, or known wrong values. 
		GreenhouseData grd = new GreenhouseData();
		
		Thread data = new Thread(new DataReceiveTransmit(grd, serverPort, serverIP, underTest), "DRT");
		Thread com = new Thread(new CommandReceiveExecute(grd, commandPort, serverPort, serverIP, underTest), "COM");
		data.start();
		com.start();
		
	
	}
}

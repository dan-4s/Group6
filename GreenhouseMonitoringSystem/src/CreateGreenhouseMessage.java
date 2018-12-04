import java.util.Arrays;

/**
 * This class is meant to standardize the creation of messages between the greenhouse system. It should make 
 * messaging consistent. 
 * @author Danilo Vucetic
 *
 */


public abstract class CreateGreenhouseMessage {

	public enum MessageType{
		ACK,
		COMMAND,
		DATA,
		ERROR;
	}
	
	/**
	 * Produces the byte array for an acknowledge packet
	 * @param ackType: the type of the acknowledge, can be DATA or COMMAND
	 * @return byte array of the message
	 */
	public static byte[] acknowledge(MessageType ackType){
		String ackString = null;
		switch(ackType){
		case COMMAND:
			ackString = ("ACK\0COMMAND\0");
			break;
		case DATA:
			ackString = ("ACK\0DATA\0");
			break;
		default:
			System.err.println("We only acknowledge the data and command packets, no others. The type passed does not match these 2.");
			return null;
		}
		
		byte[] ackBytes = ackString.getBytes();
		return ackBytes;
	}
	
	/**
	 * Decodes acknowledge packets
	 * @param ack the byte array of the received packet
	 * @return String of what was being acknowledged
	 */
	public static String acknowledgeDecode(byte[] ack){
		int minLength = 4;
		//check for error
		if(ack == null){
			return null;
		}
		if(ack.length < minLength+1 || !( new String(Arrays.copyOfRange(ack, 0, minLength))).equals("ACK\0") || ack[ack.length-1] != '\0'){
			return null;
		}
		String ackS = new String(Arrays.copyOfRange(ack, minLength, ack.length-1));
		if(ackS.equals("COMMAND")){
			return "COMMAND";
		}else if(ackS.equals("DATA")){
			return "DATA";
		}else{
			System.err.println("The message type in the data passed does not correspond with either commmand or data packet, so illegal!");
			return null;
		}
		
	}
	
	/**
	 * Produces the byte array of a command message
	 * @param newFanStatus: the status that the fan should be changed to
	 * @return the byte string of the command message
	 */
	public static byte[] command(boolean newFanStatus){
		String dataString = ("COMMAND\0"+Boolean.toString(newFanStatus)+"\0");
		byte[] dataBytes = dataString.getBytes();
		return dataBytes;
	}
	
	/**
	 * Decodes command packets
	 * @param com the byte array of the packet
	 * @return The new fan status
	 */
	public static Boolean commandDecode(byte[] com){
		int minLength = 8;
		//check for error
		if(com == null){
			return null;
		}
		if(com.length <= minLength+1 || !( new String(Arrays.copyOfRange(com, 0, minLength))).equals("COMMAND\0") || com[com.length-1] != '\0'){
			return null;
		}
		String stringCom = new String(Arrays.copyOfRange(com, minLength, com.length-1));
		Boolean fanS = null;
		if(stringCom.equals("true") || stringCom.equals("false")){
			fanS = Boolean.parseBoolean(stringCom);
		}
		return fanS;
		
	}
	
	/**
	 * Produces the data byte array for a data message given the JSON with the data
	 * @param JSON: the data packed into a JSON
	 * @return byte array of data packet
	 */
	public static byte[] data(String JSON){
		if(JSON == null || JSON.equals("")){
			return null;
		}
		String dataString = ("DATA\0"+JSON+"\0");
		byte[] dataBytes = dataString.getBytes();
		return dataBytes;
	}
	
	/**
	 * Returns the data within a data packet
	 * @param data the bytes from the packet
	 * @return data as a string
	 */
	public static String dataDecode(byte[] data){
		int minLength = 5;
		//check for error
		if(data == null){
			return null;
		}
		if(data.length < minLength+1 || !( new String(Arrays.copyOfRange(data, 0, minLength))).equals("DATA\0") || data[data.length-1] != '\0'){
			return null;
		}
		
		String dataS = new String(Arrays.copyOfRange(data, minLength, data.length-1));
		return dataS;
	}
	
	/**
	 * Produces the byte array for error message
	 * @param errorMessage the error message
	 * @return the byte array of an error packet
	 */
	public static byte[] error(String errorMessage){
		if(errorMessage == null){
			return null;
		}
		String errorString = ("ERROR\0"+errorMessage+"\0");
		byte[] errorBytes = errorString.getBytes();
		return errorBytes;
	}
	
	/**
	 * Decodes error packets
	 * @param error the byte array of the error packet
	 * @return The error in String form
	 */
	public static String errorDecode(byte[] error){
		int minLength = 6;
		//check for error in packet
		if(error == null){
			return null;
		}
		if(error.length < minLength+1 || !( new String(Arrays.copyOfRange(error, 0, minLength))).equals("ERROR\0") || error[error.length-1] != '\0'){
			return null;
		}
		
		String errorS = new String(Arrays.copyOfRange(error, minLength, error.length-1));
		return errorS;
	}
	
}

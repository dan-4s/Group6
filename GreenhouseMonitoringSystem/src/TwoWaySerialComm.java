import com.fazecast.jSerialComm.*;
import org.json.*;
class TwoWaySerialComm{
    
	public static void main(String[] args)
    {
        TwoWaySerialComm com = new TwoWaySerialComm();
        com.listen();
    }

    public void listen()
    {
    	  SerialPort comPort = SerialPort.getCommPorts()[0];
          comPort.openPort();
          String last="";
          String current;
          String temp = null;
          JSONObject jsonObj = null;

       
          try {
              while (true)
              {
              	
                  while (comPort.bytesAvailable() == 0)
                      Thread.sleep(3000);
                  byte[] readBuffer = new byte[comPort.bytesAvailable()];
               
                  int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                  current = new String(readBuffer);
                  if(!current.equals(last))
                	temp = new String(readBuffer);
                	try{
                		jsonObj = new JSONObject(temp);
                        System.out.println(">>> " + temp );
                        last = current;
                	}
                	catch(Exception e){
                		System.out.println(jsonObj);
                	}
                  	
              }
          } catch (Exception e) { e.printStackTrace(); }
          comPort.closePort();
    }
    
}
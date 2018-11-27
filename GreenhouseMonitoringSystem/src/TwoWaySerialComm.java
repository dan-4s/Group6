import com.fazecast.jSerialComm.*;
import org.json.*;
class TwoWaySerialComm implements Runnable{
	JSONObject jsonObj;
	
	TwoWaySerialComm() {
        this.jsonObj = new JSONObject();
    }
    
    public void run()
    {
    	  SerialPort comPort = SerialPort.getCommPorts()[0];
          comPort.openPort();
          String last="";
          String current;
          String temp = null;
    
       
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
                		this.jsonObj = new JSONObject(temp);
                		System.out.println("recived:   "+temp);
                        last = current;
                	}
                	catch(Exception e){
                		System.out.println(this.jsonObj);
                	}
                  	
              }
          } catch (Exception e) { e.printStackTrace(); }
          comPort.closePort();
    }
    
    protected synchronized JSONObject getSerialJSON() {
		return this.jsonObj;
	}
    
}
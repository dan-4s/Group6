import com.fazecast.jSerialComm.*;
import org.json.*;
class TwoWaySerialComm implements Runnable{
	JSONObject jsonObj;
	
	TwoWaySerialComm() {
        this.jsonObj = new JSONObject();
    }
    
    public void run()
    {
    	 try {
    		SerialPort comPort = SerialPort.getCommPorts()[0];
    		comPort.openPort();
       	 
	         String last="";
	         String current;
	         String temp = null;

	         while (true)
	            {
			System.out.println("TWOWAY HERE1");
			while (comPort.bytesAvailable() == 0){
			    System.out.println("TWOWAY HERE2");
			    Thread.sleep(3000);
			}
			byte[] readBuffer = new byte[comPort.bytesAvailable()];
			current = new String(readBuffer);
			System.out.println("JSON = "+ current);
			if(!current.equals(last)) {
			    temp = new String(readBuffer);
			    this.jsonObj = new JSONObject(temp);
			    System.out.println("recived:   "+temp);
			    last = current;
			}
			System.out.println("TWOWAY HERE3");
		    }
    	 } catch (ArrayIndexOutOfBoundsException exception) {
            // Output expected ArrayIndexOutOfBoundsException.
        	System.out.println("can not Read from serialPort: Most likely sensor not connected");
        	exception.printStackTrace();
        	//return;
        } catch (Exception exception) {
            // Output unexpected Exceptions.
        	exception.printStackTrace();
        	//return;
        	
        }
    	
    }
    
    protected synchronized JSONObject getSerialJSON() {
		return this.jsonObj;
	}
    
}

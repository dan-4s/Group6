import com.fazecast.jSerialComm.*;
class TwoWaySerialComm{
    
    int main(String arg)
    {
        TwoWaySerialComm com = new TwoWaySerialComm();
        com.listen();
        return 0;
    }

    public void listen()
    {
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        String last="";
        String current;
        try {
            while (true)
            {
                while (comPort.bytesAvailable() == 0)
                    Thread.sleep(3000);

                byte[] readBuffer = new byte[comPort.bytesAvailable()];
                int numRead = comPort.readBytes(readBuffer, readBuffer.length);
                current = new String(readBuffer);
                if(!current.equals(last))
                    System.out.println(">>> " + (new String(readBuffer)) );
                last = current;
            }
        } catch (Exception e) { e.printStackTrace(); }
        comPort.closePort();
    }
}
import static org.junit.Assert.*;

import java.util.Arrays;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * 
 */

/**
 * @author Danilo Vucetic
 *
 */
public class CreateGreenhouseMessageTest extends TestCase{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
	}

	//The acknowledge() tests
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledge(CreateGreenhouseMessage.MessageType)}.
	 */
	@Test
	public void testAcknowledge1() {
		byte[] ackBytes = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.DATA);
		String ackS = new String(Arrays.copyOfRange(ackBytes, 0, 3));
		assert(ackS.equals("ACK"));
		ackS = new String(Arrays.copyOfRange(ackBytes, 4, ackBytes.length-1));
		assert(ackS.equals("DATA"));
	}

	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledge(CreateGreenhouseMessage.MessageType)}.
	 */
	@Test
	public void testAcknowledge2() {
		byte[] ackBytes = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.COMMAND);
		String ackS = new String(Arrays.copyOfRange(ackBytes, 0, 3));
		assert(ackS.equals("ACK"));
		ackS = new String(Arrays.copyOfRange(ackBytes, 4, ackBytes.length-1));
		assert(ackS.equals("COMMAND"));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledge(CreateGreenhouseMessage.MessageType)}.
	 */
	@Test
	public void testAcknowledge3() {
		byte[] ackBytes = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.ACK);
		assert(ackBytes==null);
	}

	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledge(CreateGreenhouseMessage.MessageType)}.
	 */
	@Test
	public void testAcknowledge4() {
		byte[] ackBytes = CreateGreenhouseMessage.acknowledge(CreateGreenhouseMessage.MessageType.ERROR);
		assert(ackBytes==null);
	}
	
	
	
	//acknowledge decode tests
	//data: [65, 67, 75, 0, 68, 65, 84, 65, 0]
	//command: [65, 67, 75, 0, 67, 79, 77, 77, 65, 78, 68, 0]
	//error: [65, 67, 75, 0, 69, 82, 82, 79, 82, 0]
	//ack: [65, 67, 75, 0, 65, 67, 75, 0]
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode1() {
		byte[] dataACK = {65, 67, 75, 0, 68, 65, 84, 65, 0};
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(dataACK);
		assert(output.equals("DATA"));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode2() {
		byte[] commandACK = {65, 67, 75, 0, 67, 79, 77, 77, 65, 78, 68, 0};
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(commandACK);
		assert(output.equals("COMMAND"));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode3() {
		byte[] ack = null;
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode4() {
		byte[] ack = {65, 67, 75};
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode5() {
		byte[] ack = "AAA\0\0".getBytes();
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode6() {
		byte[] ack = "ACK\0\0".getBytes();
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode7() {
		byte[] ack = {65, 67, 75, 0, 69, 82, 82, 79, 82, 0}; //error ack message, should return null
		String output;
		output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#acknowledgeDecode(byte[])}.
	 */
	@Test
	public void testAcknowledgeDecode8() {
		byte[] ack = {65, 67, 75, 0, 65, 67, 75, 0}; //ack ack message, should return null
		String output = CreateGreenhouseMessage.acknowledgeDecode(ack);
		assert(output == null);
	}
	
	//Command tests
	/**
	 * Test method for {@link CreateGreenhouseMessage#command(boolean)}.
	 */
	@Test
	public void testCommand1() {
		Boolean input = true;
		byte[] out = CreateGreenhouseMessage.command(input);
		
		String ackS = new String(Arrays.copyOfRange(out, 0, 7));
		assert(ackS.equals("COMMAND"));
		
		ackS = new String(Arrays.copyOfRange(out, 8, out.length-1));
		assert(ackS.equals("true"));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#command(boolean)}.
	 */
	@Test
	public void testCommand2() {
		Boolean input = false;
		byte[] out = CreateGreenhouseMessage.command(input);
		
		String ackS = new String(Arrays.copyOfRange(out, 0, 7));
		assert(ackS.equals("COMMAND"));
		
		ackS = new String(Arrays.copyOfRange(out, 8, out.length-1));
		assert(ackS.equals("false"));
	}
	
	//Command decode testing
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode1() {
		byte[] input = null;
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode2() {
		byte[] input = {1, 2, 3, 4};
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode3() {
		byte[] input = "DDDDDDD\0\0".getBytes();
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode4() {
		byte[] input = "COMMAND\0\0".getBytes();
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode5() {
		byte[] input = "COMMAND\0a\0".getBytes();
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode6() {
		byte[] input = "COMMAND\0true\0".getBytes();
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == true);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#commandDecode(byte[])}.
	 */
	@Test
	public void testCommandDecode7() {
		byte[] input = "COMMAND\0false\0".getBytes();
		Boolean output = CreateGreenhouseMessage.commandDecode(input);
		assert(output == false);
	}
	
	//data testing
	/**
	 * Test method for {@link CreateGreenhouseMessage#data(java.lang.String)}.
	 */
	@Test
	public void testData1() {
		String data = null;
		byte[] output = CreateGreenhouseMessage.data(data);
		assert(output == null);
	}

	/**
	 * Test method for {@link CreateGreenhouseMessage#data(java.lang.String)}.
	 */
	@Test
	public void testData2() {
		String data = "";
		byte[] output = CreateGreenhouseMessage.data(data);
		assert(output == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#data(java.lang.String)}.
	 */
	@Test
	public void testData3() {
		String data = "data";
		byte[] output = CreateGreenhouseMessage.data(data);
		String ackS = new String(Arrays.copyOfRange(output, 0, 4));
		assert(ackS.equals("DATA"));
		
		ackS = new String(Arrays.copyOfRange(output, 5, output.length-1));
		assert(ackS.equals("data"));
	}
	
	
	
	
	
	//data decode testing
	/**
	 * Test method for {@link CreateGreenhouseMessage#dataDecode(byte[])}.
	 */
	@Test
	public void testDataDecode1() {
		byte[] input = null;
		String out = CreateGreenhouseMessage.dataDecode(input);
		assert(out ==  null);
	}

	/**
	 * Test method for {@link CreateGreenhouseMessage#dataDecode(byte[])}.
	 */
	@Test
	public void testDataDecode2() {
		byte[] input = {1, 2, 3, 4, 5};
		String out = CreateGreenhouseMessage.dataDecode(input);
		assert(out ==  null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#dataDecode(byte[])}.
	 */
	@Test
	public void testDataDecode3() {
		byte[] input = "DDDD\0\0".getBytes();
		String out = CreateGreenhouseMessage.dataDecode(input);
		assert(out ==  null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#dataDecode(byte[])}.
	 */
	@Test
	public void testDataDecode4() {
		byte[] input = "DATA\0\0".getBytes();
		String out = CreateGreenhouseMessage.dataDecode(input);
		assert(out.equals(""));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#dataDecode(byte[])}.
	 */
	@Test
	public void testDataDecode5() {
		byte[] input = "DATA\0SOMEdata\0".getBytes();
		String out = CreateGreenhouseMessage.dataDecode(input);
		assert(out.equals("SOMEdata"));
	}
	
	//Error testing
	/**
	 * Test method for {@link CreateGreenhouseMessage#error(java.lang.String)}.
	 */
	@Test
	public void testError1() {
		String input = null;
		byte[] out = CreateGreenhouseMessage.error(input);
		assert(out == null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#error(java.lang.String)}.
	 */
	@Test
	public void testError2() {
		String input = "";
		byte[] out = CreateGreenhouseMessage.error(input);

		String ackS = new String(Arrays.copyOfRange(out, 0, 5));
		assert(ackS.equals("ERROR"));
		
		ackS = new String(Arrays.copyOfRange(out, 6, out.length-1));
		assert(ackS.equals(""));
		
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#error(java.lang.String)}.
	 */
	@Test
	public void testError3() {
		String input = "err";
		byte[] out = CreateGreenhouseMessage.error(input);

		String ackS = new String(Arrays.copyOfRange(out, 0, 5));
		assert(ackS.equals("ERROR"));
		
		ackS = new String(Arrays.copyOfRange(out, 6, out.length-1));
		assert(ackS.equals("err"));
		
	}
	
	//Error DECODE testing
	/**
	 * Test method for {@link CreateGreenhouseMessage#errorDecode(byte[])}.
	 */
	@Test
	public void testErrorDecode1() {
		byte[] input = null;
		String out = CreateGreenhouseMessage.errorDecode(input);
		assert(out ==  null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#errorDecode(byte[])}.
	 */
	@Test
	public void testErrorDecode2() {
		byte[] input = {1, 2, 3, 4, 5, 6};
		String out = CreateGreenhouseMessage.errorDecode(input);
		assert(out ==  null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#errorDecode(byte[])}.
	 */
	@Test
	public void testErrorDecode3() {
		byte[] input = "EEEEE\0\0".getBytes();
		String out = CreateGreenhouseMessage.errorDecode(input);
		assert(out ==  null);
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#errorDecode(byte[])}.
	 */
	@Test
	public void testErrorDecode4() {
		byte[] input = "ERROR\0\0".getBytes();
		String out = CreateGreenhouseMessage.errorDecode(input);
		assert(out.equals(""));
	}
	
	/**
	 * Test method for {@link CreateGreenhouseMessage#errorDecode(byte[])}.
	 */
	@Test
	public void testErrorDecode5() {
		byte[] input = "ERROR\0SOMEerror\0".getBytes();;
		String out = CreateGreenhouseMessage.errorDecode(input);
		assert(out.equals("SOMEerror"));
	}

}

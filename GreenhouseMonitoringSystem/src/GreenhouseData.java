import org.json.*;

/**
 * This class is the data structure in which the greenhouse data is stored. All methods are synchronized as to
 * disallow the threads from concurrent access of the data. 
 * 
 * @author Danilo Vucetic
 *
 */
public class GreenhouseData {
	//The state variables of the greenhouse
	private float temperature;
	private float relativeHumidity;
	private boolean fanActive;
	
	public GreenhouseData(){
		temperature = 0;
		relativeHumidity = 0;
		fanActive=false;
	}
	
	/**
	 * Gets the current temperature of the greenhouse
	 * @return temperature
	 */
	protected synchronized float getTemperature() {
		return temperature;
	}

	/**
	 * Sets the temperature of the greenhouse
	 * @param temperature
	 */
	protected synchronized void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	/**
	 * Gets the relative humidity of the greenhouse
	 * @return relativeHumidity
	 */
	protected synchronized float getRelativeHumidity() {
		return relativeHumidity;
	}

	/**
	 * Sets the relative humidity of the greenhouse
	 * @param relativeHumidity
	 */
	protected synchronized void setRelativeHumidity(float relativeHumidity) {
		this.relativeHumidity = relativeHumidity;
	}

	/**
	 * Gets the state of the fan. Returns true if the fan is active, false if the fan is not active. 
	 * @return fanActive
	 */
	protected synchronized boolean getFanActive() {
		return fanActive;
	}

	/**
	 * Sets the state of the fan. True means the fan is on, false means the fan is off.
	 * @param fanActive
	 */
	protected synchronized void setFanActive(boolean fanActive) {
		this.fanActive = fanActive;
	}
	/**
	 * returns json representation of greenhousedata
	 * @throws JSONException 
	 */
	protected synchronized JSONObject getJSON() throws JSONException {
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("humidity", this.relativeHumidity);
		jsonObj.put("temperature", this.temperature);
		jsonObj.put("fanStatus", this.fanActive);
		return jsonObj;
	}
	
	/**
	 * Prints the current greenhouse data as well as the name of the requesting thread
	 */
	public synchronized void print(){
		System.out.println("from thread " + Thread.currentThread().getName() + ": Current greenhouse temperature: " + Float.toString(temperature) + ", humidity: " + Float.toString(relativeHumidity) + "; fan status: " + Boolean.toString(fanActive));
	}
}

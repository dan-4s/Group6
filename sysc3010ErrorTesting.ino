
#include <LiquidCrystal.h>
#include <DHT.h>
#include <ArduinoJson.h>

const int d4=4,d5=5,d6=6,d7=7;
LiquidCrystal LCD(12,11,d4,d5,d6,d7);

#define DHTPIN 13
#define DHTTYPE DHT11


DHT dht(DHTPIN, DHTTYPE);

boolean gloablFail= false;

int write_counter = 0;

float Temp =0;
float Humidity =0;

void setup() {
  //LCD setup 
 LCD.begin(16,2);
 LCD.setCursor(0,0);
  LCD.print("Running Tests...");
  //Serial setup
  Serial.begin(9600);
  //DHT setup
  dht.begin();
  runTests();
}

void checkHumidityNumber(){
  float h = dht.readHumidity();
  if (isnan(h)){
    Serial.println("Humidity num: Fail\n");
    gloablFail= true;
  }
  else{
    Serial.print("Humidity Num: Pass\n");
  }
}
void checkTemperatureNumber(){
  float t = dht.readTemperature();
  if (isnan(t)){
    Serial.println("Temprature Num: Fail\n");
    gloablFail= true;
  }
  else{
    Serial.print("Temprature Num: Pass\n");
  }
}

void checkTemperatureCelsius(){
  float tf = dht.readTemperature(true);
  float tc = dht.readTemperature();
  if (isnan(tf)||isnan(tc)||tc==tf){
    Serial.println("Temprature celsius: Fail\n");
    gloablFail= true;
  }
  else{
    Serial.print("Temprature Celsius: Pass\n");
  }
}

void runTests(){
  checkHumidityNumber();
  checkTemperatureNumber();
  checkTemperatureCelsius();
  LCD.clear();
  if(gloablFail==true){
    LCD.print("Tests Failed...");
    Serial.print("Sensor is not configured properly please fix failed processes\n");
  }
  else{
    Serial.print("Sensor is configured properly\n");
    LCD.print("Tests Passed...");
  }
}

void loop() {
  StaticJsonBuffer<200> jsonBuffer;
  if(gloablFail==false){
    JsonObject& root = jsonBuffer.createObject();

    root["Temperature"] = "gps";
    root["Humidity"] = 1351824120;
    delay(3000);
    write_counter = write_counter+1;
    float Temp = dht.readTemperature();
    float Humidity = dht.readHumidity();
    root["Temperature"] = Temp;
    root["Humidity"] = Humidity;
    root.printTo(Serial);
  
    LCD.clear();
    LCD.setCursor(0,0);
    LCD.write("T:");
    LCD.print(Temp);
    LCD.write(" H:");
    LCD.print(Humidity);
  
    LCD.setCursor(0,1);
    LCD.write("W.R.T.C: ");
    LCD.print(write_counter+1);
  }
}

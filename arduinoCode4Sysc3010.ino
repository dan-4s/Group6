
#include <LiquidCrystal.h>
#include <DHT.h>

const int d4=4,d5=5,d6=6,d7=7;
LiquidCrystal LCD(12,11,d4,d5,d6,d7);

#define DHTPIN 13
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

int write_counter = 0;

float Temp =0;
float Humidity =0;

void setup() {
  //LCD setup 
 LCD.begin(16,2);
 LCD.setCursor(0,0);
  LCD.print("Initializing...");
  //Serial setup
  Serial.begin(9600);
  //DHT setup
  dht.begin();
  sleep(1000);

  //LCD screen preparation 
  LCD.clear();
  //Initialize the cursor at the point to write the tempereture
  LCD.setCursor(0,0);
  LCD.write("T:");
  //Move the cursor 7 points,2 for the word above, 4 spaces for temp reading, 1 space for spacing
  LCD.setCursor(4,0);
  LCD.write(" H:");
}

void loop() {
  delay(3000);
  write_counter = write_counter+1;
  Temp = dht.readTemperature();
  Humidity = dht.readHumidity();
  Serial.print("{ \"Temperature\":");
  Serial.print(Temp);
  Serial.print(", \"Humidity\":");
  Serial.print(Humidity);
  Serial.print("}");

  //LCD 
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

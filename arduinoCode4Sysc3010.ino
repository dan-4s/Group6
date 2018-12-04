
#include <LiquidCrystal.h>
#include <DHT.h>

const int d4=4,d5=5,d6=6,d7=7;
LiquidCrystal LCD(12,11,d4,d5,d6,d7);

#define DHTPIN 13
#define DHTTYPE DHT11
DHT dht(DHTPIN, DHTTYPE);

int write_counter = 0;

int Temp =0;
int Humidity =0;
int TempPrev =0;
int HumidityPrev=0;

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
  //Move the cursor 6 points,2 for the word above, 4 spaces for temp reading
  LCD.setCursor(5,0);
  LCD.write(" H:");
  //Move the cursor down the secod row of the lcd to print the data counter
  LCD.setCursor(0,1);
  LCD.write("W.R.T.C: ");
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

  //write the temperature to the LCD
  //writing to LCD consumes some considerable amount of power so only write when we have changes
  if(!(Temp==TempPrev)
  {
    LCD.setCursor(2,0);
    LCD.print(Temp);
  }
  //write the humidity to the LCD
  //writing to LCD consumes some considerable amount of power so only write when we have changes
  if(!(Humidity==HumudityPrev)
  {
    LCD.setCursor(9,0);
    LCD.print(Humidity);
  }

  LCD.setCursor(0,1);
  LCD.write("W.R.T.C: ");
  LCD.print(write_counter+1);
}

/*
 * Bluetooth Sender/ Receiver.
 *
 * Works with HC05 Module. 
 * 
 * Sendet an und empfängt von einem Android Gerät. Die empfagenen Daten werden über
 * den seriellen Monitor und einem OLED Display ausgegeben. Wenn ein Knopf, der über Pin 10/ C7 am Tensy 
 * abgefragt wird, gedrückt wurde, so wird eine Nachricht an den Androiden geschickt.
 *
 * Zu complilieren und hochladen darf das HC05 Modul nicht angeschlossen sein
 * (Versorgungsspannung und Erde dürfen dran sein, es reicht, wenn RX und TX
 * abgeklemmt werden.
 *
 * Android- Seitig wird diese App benötigt:
 * ./0_Vorlagen/Networking/BluetoothConector/app/src/main/java/berthold/bluetoothconector/MainActivity.java
 *
 */

// OLED Display
#include <SPI.h>
#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>

// Serial
#include <SoftwareSerial.h>

// OLED Display
#define SCREEN_WIDTH 128 // OLED display width, in pixels
#define SCREEN_HEIGHT 32 // OLED display height, in pixels

// Declaration for an SSD1306 display connected to I2C (SDA, SCL pins)
#define OLED_RESET     4  // Reset pin # (or -1 if sharing Arduino reset pin)
Adafruit_SSD1306 display(SCREEN_WIDTH, SCREEN_HEIGHT, &Wire, OLED_RESET);

// Serial
// - Pin 7 ist RX
// - Pin 8 ist TX
#define txPin 3
#define rxPin 4

//                RX  TX
SoftwareSerial BT(4,   3);

// Input button
int IN_PIN=PIN_C7;
int pinState;

// Control
char received;
int index;

/*
 * Einmal
 */
void setup()  
{
  // Display
  display.begin(SSD1306_SWITCHCAPVCC, 0x3C);
  display.clearDisplay();
  showText("Warte.....");
  display.display();
  
  // set digital pin to control as an output
  pinMode(rxPin, INPUT);
  pinMode(txPin,OUTPUT);
  // set the data rate for the SoftwareSerial port
  BT.begin(9600);

  // Setup input pin 
  pinMode (IN_PIN,INPUT_PULLUP);

}

/*
 * Forever, forever and forever
 */
void loop() 
{
  // Werden Daten empfangen?
  while (!BT.available()){
    // Nein, wurde der Sende- Konopf gedrückt?
    pinState=digitalRead(IN_PIN);
    if (pinState==LOW){
      // Ja, sende eine Nachricht über Bluetooth...
      BT.println("Hi, I'am Here!!!!!");
      showText("Send data....");
    }
  }
     
  // Empfangene Daten löschen..
  char wholeData[255];
  
  // Wurde etwas empfangen?
  while (BT.available()>0){
      received=BT.read();
      wholeData[index]=received;
      if (index<=255) index++;
  }
  
  // Empfangene Daten anzeigen..
  if (index>0){
    wholeData[index]='\0';
    Serial.println(wholeData);
    showText(wholeData);
  }
  index=0;
  // Again.....
}

/*
 * Text im display anzeigen.
 *
 */
void showText(String text)
{
  display.clearDisplay();
    display.setTextSize(1);            
    display.setTextColor(SSD1306_WHITE);       
    display.setCursor(3,4);             
    display.println(text);
    display.display();
}
  

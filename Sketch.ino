#include "ESP8266.h"
#include <SoftwareSerial.h>
#include <math.h>
 
#define SSID     "name wifi"
#define PASSWORD "pass"
#define TEMP_PIN A0
 
SoftwareSerial mySerial(4, 5);
ESP8266 wifi(mySerial);
String ip = "35.242.232.105"; // server address
int port = 1338;
String responce = "";
byte packet[5];
void setup(void) {
  Serial.begin(9600);
  mySerial.begin(9600);
  if (wifi.joinAP(SSID, PASSWORD)) {
    Serial.println(ip +":"+ port);
  } else {
    Serial.println("Wi-Fi connection error");
  }
}
 
void loop(void) {
  int temp = analogRead(TEMP_PIN);
  if (wifi.registerUDP(ip, port)) {
  packet[2] = (byte)((temp >> 8) & 0xff);
  packet[3] = (byte)(temp & 0xff);
  packet[4] = 0x13;
    wifi.send(packet, 5);
    wifi.unregisterUDP();
  } else {
    Serial.println("create TCP error");
  }
  delay(1000);
}

#define BLYNK_PRINT Serial
#include <ESP8266WiFi.h>
#include <Blynk.h>
#include <BlynkSimpleEsp8266.h> //library blynk
#include <AccelStepper.h>
#define IN1 D3 //set pin stepper
#define IN2 D4
#define IN3 D5
#define IN4 D6
#define Rain A0
#define LDR D8
WidgetLCD lcd(V3);
AccelStepper stepper(AccelStepper::HALF4WIRE, IN1, IN3, IN2, IN4); 
#define buttonClockwisePin V1
#define buttonCounterclockwisePin V2

char auth[]="jCh0eDOBI8tFwxz7x0binomdAeRnOa5V";
char ssid[] ="xMIPA 7";
char pass[]="123456789";

void setup() {
  // put your setup code here, to run once:
Serial.begin(115200);
 pinMode(Rain, INPUT);
 pinMode(LDR, INPUT);
Blynk.begin(auth,ssid,pass, "iot.amikom.ac.id",8080);
 stepper.setMaxSpeed(500);
  stepper.setAcceleration(100);
}
void rainSensor() {
  int Rvalue = analogRead(Rain);
  //mapping 0-1023 ke 100-0
  Rvalue = map(Rvalue, 0, 1023, 100, 0);
  
  Blynk.virtualWrite(V0, Rvalue);

  Serial.println(Rvalue);
}
void LDRsensor() {
  bool value = digitalRead(LDR);
  //gaada cahaya = 1
  if (value == 1) {
    WidgetLED LED(V4);
    LED.off();
  } else {
    //ada = 0 / else
    WidgetLED LED(V4);
    LED.on();
  }}
void loop() {
  // put your main code here, to run repeatedly:
  rainSensor();
  LDRsensor();
  stepper.runSpeed();
  Blynk.run();
 bool value = digitalRead(LDR);
  int Rvalue = analogRead(Rain);//baca sensor hujan
  int currentStatus = 0;  
  Rvalue = map(Rvalue, 0, 1023, 100, 0);//mapping hujan
  //Kode utama
  if (value != 1 && Rvalue < 10 && currentStatus != 1) {
    stepper.runToNewPosition(-2700);//motor kluar
    lcd.print(0,1,"             ");
    lcd.print(0,1,"Baju dijemur");
    currentStatus = 1;  // Ubah status menjadi 1
  } else if ( Rvalue >= 20 && currentStatus != 2) {
    stepper.runToNewPosition(2700);//motor masuk
    lcd.print(0,1,"             ");
    lcd.print(0,1,"baju diangkat");
    currentStatus = 2;  // Ubah status menjadi 2
  }
}

BLYNK_WRITE(buttonClockwisePin) {
  int buttonClockwiseState = param.asInt();
  //masuk
  if (buttonClockwiseState == HIGH) {
    stepper.runToNewPosition(2700);
    lcd.print(0,1,"             ");
    lcd.print(0,1,"baju diangkat");

  } else {
    //if (digitalRead(buttonClockwisePin) == LOW && digitalRead(buttonCounterclockwisePin) == LOW) {
      stepper.setSpeed(0);  // Set speed to 0 to stop the motor
      stepper.disableOutputs();  // Disable motor outputs
      Serial.println("Motor Stopped");
    //}
  }
}
BLYNK_WRITE(buttonCounterclockwisePin) {
  int buttonCounterclockwiseState = param.asInt();
  //keluaar
  if (buttonCounterclockwiseState == HIGH) {
     stepper.runToNewPosition(-2700);
   lcd.print(0,1,"             ");
    lcd.print(0,1,"Baju dijemur");
  } else {
    //if (digitalRead(buttonClockwisePin) == LOW && digitalRead(buttonCounterclockwisePin) == LOW) {
      stepper.setSpeed(0);  // Set speed to 0 to stop the motor
      stepper.disableOutputs();  // Disable motor outputs
      Serial.println("Motor Stopped");
    //}
  }
}

#include <SoftwareSerial.h>
#include "ant_lib.h"

SoftwareSerial mySerial(2, 3);
AntMaster am(mySerial, 4);  // second argument is pin number for status LED

void setup()  
{
  Serial.begin(57600);
  Serial.println("ant master config routine");
  
  am.config_loop();
}

void loop() // run over and over
{
  delay(500); am.ledon();
  am.sendHRM(3);  // send your data here
  delay(500); am.ledoff();
}

/*
 * dht.h
 *
 *  Created on: Jan 3, 2014
 *      Author: kvv
 */

#ifndef DHT_H_
#define DHT_H_

#define MAXTIMINGS 85

#define DHT11 11
#define DHT22 22
#define DHT21 21
#define AM2301 21

typedef struct DHT {
  uint8_t data[6];
  uint8_t _pin, _type, _count;
  uint8_t read(void);
  unsigned long _lastreadtime;
  uint8_t firstreading;
} _DHT;

void init(uint8_t pin, uint8_t type, uint8_t count=6);
float readTemperature(uint8_t s);
float convertCtoF(float);
float readHumidity(void);

#endif /* DHT_H_ */

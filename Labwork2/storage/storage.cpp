#include <stdio.h>
#include <stdlib.h>
#include <conio.h>

extern "C" {
#include "storage.h"
#include <interface.h>
}

/* DAQ Initialization */
void initializeHardwarePorts() {
	createDigitalInput(0);
	createDigitalInput(1);
	createDigitalOutput(2);
	writeDigitalU8(2, 0);
}

 /* BITS */
int getBitValue(uInt8 value, uInt8 bit_n)
// given a byte value, returns the value of its bit n
{
	return(value & (1 << bit_n));
}

void setBitValue(uInt8* variable, int n_bit, int new_value_bit)
// given a byte value, set the n bit to value
{
	uInt8 mask_on = (uInt8)(1 << n_bit);
	uInt8 mask_off = ~mask_on;
	if (new_value_bit) *variable |= mask_on;
	else *variable &= mask_off;
}

/* X Axis */
void moveXRight() {
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 6, 0); // set bit 6 to low level
	setBitValue(&p, 7, 1); // set bit 7 to high level

	writeDigitalU8(2, p); // update port 2
}

void moveXLeft(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 6, 1); // set bit 6 to high level
	setBitValue(&p, 7, 0); // set bit 7 to low level

	writeDigitalU8(2, p); // update port 2
}
void stopX(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 6, 0); // set bit 6 to low level
	setBitValue(&p, 7, 0); // set bit 7 to low level

	writeDigitalU8(2, p); // update port 2
}
int getXPos(){
	uInt8 p0 = readDigitalU8(0);

	if (!getBitValue(p0, 2))
		return 1;
	if (!getBitValue(p0, 1))
		return 2;	
	if (!getBitValue(p0, 0))
		return 3;
	return(-1);
}

/* Z Axis*/
void moveZUp(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 2, 0); // set bit 2 to low level
	setBitValue(&p, 3, 1); // set bit 3 to high level

	writeDigitalU8(2, p); // update port 2
}
void moveZDown(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 2, 1); // set bit 2 to high level
	setBitValue(&p, 3, 0); // set bit 3 to low level

	writeDigitalU8(2, p); // update port 2
}
void stopZ(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 2, 0); // set bit 2 to low level
	setBitValue(&p, 3, 0); // set bit 3 to low level

	writeDigitalU8(2, p); // update port 2
}
int getZPos(){
	uInt8 p1 = readDigitalU8(1);
	uInt8 p0 = readDigitalU8(0);

	if (!getBitValue(p1, 3))
		return 1;
	if (!getBitValue(p1, 2))
		return 2;
	if (!getBitValue(p1, 1))
		return 3;
	if (!getBitValue(p1, 0))
		return 4;
	if (!getBitValue(p0, 7))
		return 5;
	if (!getBitValue(p0, 6))
		return 6;
	return(-1);
}

/* Y Axis*/
void moveYInside(){
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 4, 0); // set bit 4 to low level
	setBitValue(&p, 5, 1); // set bit 5 to high level

	writeDigitalU8(2, p); // update port 2
}
void moveYOutside() {
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 4, 1); // set bit 4 to high level
	setBitValue(&p, 5, 0); // set bit 5 to low level

	writeDigitalU8(2, p); // update port 2
}
void stopY() {
	uInt8 p = readDigitalU8(2); // read port 2

	setBitValue(&p, 4, 0); // set bit 4 to low level
	setBitValue(&p, 5, 0); // set bit 5 to low level

	writeDigitalU8(2, p); // update port 2
}
int getYPos(){
	uInt8 p0 = readDigitalU8(0);

	if (!getBitValue(p0, 5))
		return 1;
	if (!getBitValue(p0, 4))
		return 2;
	if (!getBitValue(p0, 3))
		return 3;
	return(-1);
}

/* Switches */
int getSwitch1(){
	uInt8 p = readDigitalU8(1); // read port 2

	return getBitValue(p, 5);
}
int getSwitch2(){
	uInt8 p = readDigitalU8(1); // read port 2

	return getBitValue(p, 6);
}
int getSwitch1_2(){
	//Faz oq?
	return 0;
}

/* Leds */
void ledOn(int led){
	//Qual led?
}
void ledsOff(){
	//Qual led?
}

int main() {

	initializeHardwarePorts();

	system("cls");
	printf("COISAS\n");

	int tecla = 0;
	while (1) {
		tecla = _getch();

		printf("%c\n", tecla);
		switch (tecla) {

		case 'w':
			moveZUp();
			break;
		case 's':
			moveZDown();
			break;
		case 'a':
			moveXLeft();
			break;
		case 'd':
			moveXRight();
			break;
		case 'q':
			moveYInside();
			break;
		case 'e':
			moveYOutside();
			break;
		case ' ':
			stopX();
			stopY();
			stopZ();
			break;

			//IgnorarEsc
		case 27:
			printf("\nSaida do programa");
			exit(1);
			break;

		default:
			break;
		}
	}
}
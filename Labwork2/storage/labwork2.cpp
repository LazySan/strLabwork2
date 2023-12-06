#include "my_interaction_functions.h"
#include <interface.h>
#include <FreeRTOS.h>
#include <task.h>
#include <timers.h>
#include <semphr.h>
#include <interrupts.h>

void main() {

	int tecla = 0;
	system("cls");
	printf("\nMenu");
	printf("\nc - Calibrar cilindros");
	printf("\nb - Inserir blocos");
	printf("\nm - Controlo manual");
	printf("\nh - Mostrar historico");
	printf("\ne - Estatistica");

	createDigitalInput(0);
	createDigitalInput(1);
	createDigitalOutput(2);

	tecla = _getch();
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
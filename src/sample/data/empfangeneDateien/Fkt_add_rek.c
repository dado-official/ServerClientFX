#include<stdio.h>

int add(int zahl) {
	 if(zahl > 1) {
		return zahl + add(zahl-1);
	} else {
		return 1;
	}
}


int main() {
	int zahl = 0;
printf("Gib eine Zahl ein\n");
scanf("%d", &zahl);
printf( "%d\n",add(zahl));


	return 0;
}

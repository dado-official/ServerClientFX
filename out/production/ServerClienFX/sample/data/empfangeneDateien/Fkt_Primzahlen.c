#include <stdio.h> #include <math.h>  int moodf(int zahl) {   int i = 2;   int rest = 0;    while (i <= 9) {                                  //die Zahl durch alle Ganzzahlen von 2 bis 9 dividieren     if (i != zahl){                                 //Wenn die eingegebene Zahl durch die gleiche Zahl dividiert werden soll, soll diese Division übersprungen werden*       rest = zahl % i;                              //berechnung ob bei der Division Rest bleibt       if (rest == 0){                               //Wenn kein Rest übrig bleibt ist es keine Primzahl und die Schleife wird unterbrochen         break;         return 0;       }       i++;                                          //Ansonsten wird der die Zahl durch die die eingegebene Zahl dividiert wird erhöht     } else {       i++;                                          //*Erhöhung der Zahl wenn die beiden Zahlen gleich sind     }   }   return rest; }   int main () {   int zahl = 0;   printf("Gib eine Zahl ein\n");                    //Eingabe der Zahle   scanf("%d", &zahl);    int rest = moodf(zahl);                           //Aufruf der Funktion     if (rest > 0) {                                   //Wenn bei allen Divisionen Reste übrig gebliben ist ist es eine Primzahl     printf("Deine Zahl ist eine Primzahl\n");   } else {                                          //Ansonsten ist es keine Primzahl     printf("Deine Zahl ist keine Primzahl\n");     return 0; }
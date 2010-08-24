#include <stdio.h>

int main()
{
char sentence [] = "Benny AE";
char str[20];
int i;

sscanf (sentence, "%s %x", str, &i);
printf("%s -> %x", str, i);

return 0;
}
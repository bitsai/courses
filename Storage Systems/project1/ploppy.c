#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include "project1.h"

main()
{
int count;
int fd;
int measurements = 10000;
char buffer[SECTOR];

FILE *fout = fopen("ploppy.txt", "w");

float time1;
float time2;
float outtime;
struct timeval tv;

for (count = 0; count < SECTOR; count++)
{
   buffer[count] = 'X';
}

fd = ds_open("/dev/disksim", O_WRONLY);

for (count = 0; count < measurements; count++)
{
   ds_lseek(fd, 0, SEEK_CUR);
   ds_write(fd, buffer, SECTOR);

   ds_gettimeofday(&tv);
   time1 = (float) tv.tv_sec*1000 + (float) tv.tv_usec/1000;

   ds_lseek(fd, SECTOR, SEEK_CUR);
   ds_write(fd, buffer, SECTOR);

   ds_gettimeofday(&tv);
   time2 = (float) tv.tv_sec*1000 + (float) tv.tv_usec/1000;

   outtime = time2 - time1;

   fprintf(fout, "%d ", count);
   fprintf(fout, "%f\n", outtime);
}
ds_close(fd);
fclose(fout);
}

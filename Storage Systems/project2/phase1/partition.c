#include <stdio.h>
#include <stdlib.h>
#include <string.h>	
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include "ext2_fs.h"
#include "genhd.h"

#if defined(__FreeBSD__)
#define lseek64 lseek
#endif

/* linux: lseek64 declaration needed here to eliminate compiler warning. */
extern int64_t lseek64(int, int64_t, int);

const unsigned int SECTOR_SZ=512;

/*------------------------------------------------------------------------------
* Name: readSectors 
*
* Action:  read a specified number of sectors into a buffer.
*
* Inputs:
*   int devFd: device file handle
*   int64 firstSect: the starting sector number to read.
*                sector numbering starts with 0.
*   int numSects: the number of sectors to read.  must be >= 1.
*
* Outputs:
*   void *intoBuf: the requested number of blocks are copied into here.
*
* Modifies:
*   void *intoBuf
*
* Returns:
*   number of sectors or -1 on error.
*-----------------------------------------------------------------------------*/
int readSectors ( int devFd, int64_t firstSect, unsigned int numSects, void *intoBuf )
{
  int rc;
  int64_t lrc;

  lrc = lseek64 ( devFd, firstSect * SECTOR_SZ, SEEK_SET );
  if ( lrc != firstSect * SECTOR_SZ ) 
  {
    fprintf ( stderr, "Seek to position %lld failed: returned %lld\n", 
	    firstSect * SECTOR_SZ, lrc );
    return -1;
  }

  rc = read ( devFd, intoBuf, SECTOR_SZ * numSects );
  if ( rc != SECTOR_SZ * numSects ) 
  {
    fprintf ( stderr, "Read block %lld length %d failed: returned %d\n", 
	    firstSect, numSects, rc );
    return -1;
  }
  return numSects;
}

/*-----------------------------------------------------------------------------
 * Name: printPartition
 *
 * Action: prints out information of the indexed partition within the input array.
 *
 * Inputs: 
 *   struct partition *partitions: array of partitions to print.
 *   int count: desired position in array of partitions to print.
 *-----------------------------------------------------------------------------*/

int printPartition(struct partition *partitions, int count)
{
  printf("\nPartition Number: %d\t", count+1);
  printf("Partition Type: 0x%x ", partitions[count].sys_ind);
  if ((int) partitions[count].sys_ind == 0)
  {
    printf("(unused)\n\t\t");
  }
  if ((int) partitions[count].sys_ind == 5)
  {
    printf("(Extended)\n\t\t");
  }
  if ((int) partitions[count].sys_ind == 130)
  {
    printf("(Linux swap)\n\t\t");
  }
  if ((int) partitions[count].sys_ind == 131)
  {
    printf("(ext2)\n\t\t");
  }
  printf("\tStart: %d\t", partitions[count].start_sect);
  printf("Length: %d\n", partitions[count].nr_sects);

  return 0;
}

/*-----------------------------------------------------------------------------
 * Name: readPartition
 *
 * Action: reads a buffer and returns a partition entry contained with the buffer.
 *
 * Inputs:
 *   int MBR: 1 if we are currently in the MBR, 0 otherwise.
 *   int start: starting byte address of desired partition entry.
 *   unsigned char *buf: buffer to be read from.
 *   struct partition *partitions: array of partitions to be modified.
 *   int count: desired position in array of partitions to be modified.
 *   offset: added to start_sect of an extended partition entry to calculate 
 *      sector number of next jump.
 *
 * Modifies:
 *   struct partition *partitions: array of partitions to be modified.
 *
 * Returns: 
 *   int: 0 for an empty entry, jump address to next sector for an extended entry,
 *      1 otherwise.
 *-----------------------------------------------------------------------------*/

int readPartition(int MBR, int start, unsigned char *buf, struct partition *partitions, int count, int offset)
{
  if ((MBR == 0) && ((int) buf[start + 4] == 0))
  {
    return 0;
  }

  if ((MBR == 0) && ((int) buf[start + 4] == 5))
  {
    return buf[start + 8] + buf[start + 9] * 256 + buf[start + 10] * 65536 + buf[start + 11] * 16777216 + offset;
  }

  partitions[count].boot_ind = buf[start];
  partitions[count].head = buf[start + 1];
  partitions[count].sector = buf[start + 2];
  partitions[count].cyl = buf[start + 3];
  partitions[count].sys_ind = buf[start + 4];
  partitions[count].end_head = buf[start + 5];
  partitions[count].end_sector = buf[start + 6];
  partitions[count].end_cyl = buf[start + 7];
  partitions[count].start_sect = buf[start + 8] + buf[start + 9] * 256 + buf[start + 10] * 65536 + buf[start + 11] * 16777216 + offset;
  partitions[count].nr_sects = buf[start + 12] + buf[start + 13] * 256 + buf[start + 14] * 65536 + buf[start + 15] * 16777216;

  if ((int) buf[start + 4] == 0)
  {
    return 0;
  }

  if ((int) buf[start + 4] == 5)
  {
    return buf[start + 8] + buf[start + 9] * 256 + buf[start + 10] * 65536 + buf[start + 11] * 16777216 + offset;
  }

  return 1;
}

/*------------------------------------------------------------------------------
* Name:  main
* Action:  Print out partition tables.
*-----------------------------------------------------------------------------*/
int main ()
{
  int           rc;                       /* Return code        */
  int           fd;                       /* Device descriptor  */           
  int           sector;   		          /* IN: sector to read */
  unsigned char buf[SECTOR_SZ];	          /* temporary buffer   */

  struct partition partitions[32];
  int count;
  int jump;
  int start = 446;
  int totalcount;

  /* Open the device */
  fd = open ( "disk", O_RDWR ); 
  if ( fd == -1 ) 
  {
    perror ( "Could not open device file" );
    exit(-1);
  }

  /* Read the sector */
  sector = atoi( "0" );
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  /* Read the first 4 partitions in MBR and print them */
  for(count = 0; count < 4; count++)
  {
    jump = readPartition(1, start + count*16, buf, partitions, count, 0);
	printPartition(partitions, count);
  }

  /* Update total count of entries in array */
  totalcount = count;

  /* Traverse extended partitions, printing out entries, until we hit an empty entry */
  while (jump > 0)
  {
    /* Read the sector */
    sector = jump;
    rc = readSectors ( fd, sector, 1, buf );
    if ( rc == -1 )
    {
      perror ( "Could not read sector" );
      exit(-1);
    }

  /* Reset count and jump information */
    count = 0;
    jump = 1;

  /* Traverse sector, printing each entry, until we hit either an empty or extended entry */
    while (jump == 1)
    {
      jump = readPartition(0, start + count*16, buf, partitions, totalcount, sector);
      if (jump == 1)
      {
		printPartition(partitions, totalcount);

        count = count + 1;
        totalcount = totalcount + 1;
      }
    }
  }

  close(fd);
  return 0;
}

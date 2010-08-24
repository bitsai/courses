/*******************************************************************************
*
*    $Id: readwrite.c,v 1.2 2002/10/07 15:17:19 randal Exp $
*
*    Randal C. Burns
*    Department of Computer Science
*    Johns Hopkins University
*
*    $Source: /home/randal/repository/src/stlab/readwrite.c,v $
*    $Date: 2002/10/07 15:17:19 $        
*    $Revision: 1.2 $
*
*  readwrite.c
*  
*  Code to read and write sectors to a "disk" file.
*  This is a support file for the "fsck" storage systems laboratory.
*  
*******************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>	
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#if defined(__FreeBSD__)
#define lseek64 lseek
#endif

/* linux: lseek64 declaration needed here to eliminate compiler warning. */
extern int64_t lseek64(int, int64_t, int);

const unsigned int SECTOR_SZ=512;

/*------------------------------------------------------------------------------
* Name:  printSector
* Action:  Perform a hex dump of the data in a sector.
*            Stolen and reformatted from JLG.
*-----------------------------------------------------------------------------*/
void printSector ( unsigned char *theBuf )
{
  int i;
  for ( i=0; i < SECTOR_SZ; i++ ) 
  {
    printf ( "%02x", theBuf[i] );

    if ( ! (( i+1 ) % 32 ))
    {
      printf ( "\n" );	      /* line break after 32 bytes */
    }
    else if ( ! (( i+1 ) % 4 ))
    {
      printf ( " " );	      /* space after 4 bytes */
    }
  }
}

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

  if ( numSects == 1 ) 
  {
    printf ( "Reading sector %lld\n", firstSect );
  }
  else 
  {
    printf ( "Reading sectors %lld--%lld\n", firstSect, firstSect + ( numSects - 1 ));
  }

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


/*------------------------------------------------------------------------------
* Name: writeSectors 
*
* Action: write a buffer into a specified number of sectors.
*
* Inputs:
*   int devFd: device file handle
*   int64 startSect: the starting sector number to write.
*                sector numbering starts with 0.
*   int numSects: the number of sectors to write.  must be >= 1.
*   void *fromBuf: the requested number of blocks are copied from here.
*
* Outputs:
*   int devFD: the disk into which to write.
*
* Modifies:
*   int devFD: the disk into which to write.
*
* Returns:
*   number of sectors or -1 on error.
*-----------------------------------------------------------------------------*/
int writeSectors ( int devFd, int64_t firstSect, unsigned int numSects, void *fromBuf )
{
  int rc;
  int64_t lrc;

  if ( numSects == 1 ) 
  {
    printf ( "Writing sector  %lld\n", firstSect );
  }
  else 
  {
    printf ( "Writing sectors %lld--%lld\n", firstSect, firstSect + ( numSects-1 ));
  }

  lrc = lseek64 ( devFd, firstSect * SECTOR_SZ, SEEK_SET ); 
  if ( lrc != firstSect * SECTOR_SZ ) 
  {
    fprintf ( stderr, "Seek to position %lld failed: rc %lld\n", 
           	    firstSect * SECTOR_SZ, lrc );
    return -1;
  }

  rc = write ( devFd, fromBuf, SECTOR_SZ * numSects ); 
  if ( rc != SECTOR_SZ * numSects ) 
  {
    fprintf ( stderr, "Write block %lld length %d failed: rc %d\n", 
                firstSect, numSects, rc);
    return -1;
  }
  return numSects;
}


/*------------------------------------------------------------------------------
* Name:  main
* Action:  Read the sector specified on the command line.
*-----------------------------------------------------------------------------*/
int main (int argc, char **argv)
{
  /* This is a sample program.  If you want to print out sector 57 of
   * the disk, then run the program as:
   *
   *    ./readwrite disk 57
   *
   * You'll of course want to replace this with your own functions.
   */

  int           rc;                       /* Return code */
  int           fd;                       /* Device descriptor */           
  int           sector;   			          /* IN: sector to read */
  unsigned char buf[SECTOR_SZ];	          /* temporary buffer */

  /* Open the device */
  fd = open ( argv[1], O_RDWR ); 
  if ( fd == -1 ) 
  {
    perror ( "Could not open device file" );
    exit(-1);
  }

  /* Read the sector */
  sector = atoi( argv[2] );
  printf ( "Dumping sector %d:\n", sector );
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  /* Dump the sector */
  printSector ( buf );

  close(fd);
  return 0;
}

/*******************************************************************************
*
*  Revsion History 
*    
*  $Log: readwrite.c,v $
*  Revision 1.2  2002/10/07 15:17:19  randal
*  Cleaned up readwrite.c from JLG to comply to my formatting.
*
*
*    
*******************************************************************************/

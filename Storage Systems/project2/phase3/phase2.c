#include <stdio.h>
#include <stdlib.h>
#include <string.h>	
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <math.h>
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
 * Name: readPartition
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
 * Name: getPartitions
 *-----------------------------------------------------------------------------*/

int getPartitions(int fd, struct partition *partitions)
{
  int count;
  int jump;
  int rc;
  int sector;
  int start = 446;
  int totalcount;

  unsigned char buf[SECTOR_SZ];

  /* Read sector 0 */
  sector = 0;
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  /* Read first 4 partitions from sector 0 */
  for(count = 0; count < 4; count++)
  {
    jump = readPartition(1, start + count*16, buf, partitions, count, 0);
  }

  totalcount = count;

  /* Read extended partitions */
  while (jump > 0)
  {
    sector = jump;
    rc = readSectors ( fd, sector, 1, buf );
    if ( rc == -1 )
    {
      perror ( "Could not read sector" );
      exit(-1);
    }

    count = 0;
    jump = 1;

    while (jump == 1)
    {
      jump = readPartition(0, start + count*16, buf, partitions, totalcount, sector);
      if (jump == 1)
      {
        count = count + 1;
        totalcount = totalcount + 1;
      }
    }
  }

  return totalcount;
}

/*------------------------------------------------------------------------------
 * Name: readSuperBlock
 *-----------------------------------------------------------------------------*/

struct ext2_super_block readSuperBlock(int fd, int p_start)
{
  int rc;
  int sector;
  struct ext2_super_block superblock;
  unsigned char buf[SECTOR_SZ];

  /* Read sector */
  sector = p_start + 2;
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  superblock.s_inodes_count = buf[0] + buf[1] * 256 + buf[2] * 65536 + buf[3] * 16777216;
  superblock.s_blocks_count = buf[4] + buf[5] * 256 + buf[6] * 65536 + buf[7] * 16777216;
  superblock.s_r_blocks_count = buf[8] + buf[9] * 256 + buf[10] * 65536 + buf[11] * 16777216;
  superblock.s_free_blocks_count = buf[12] + buf[13] * 256 + buf[14] * 65536 + buf[15] * 16777216;
  superblock.s_free_inodes_count = buf[16] + buf[17] * 256 + buf[18] * 65536 + buf[19] * 16777216;
  superblock.s_first_data_block = buf[20] + buf[21] * 256 + buf[22] * 65536 + buf[23] * 16777216;
  superblock.s_log_block_size = buf[24] + buf[25] * 256 + buf[26] * 65536 + buf[27] * 16777216;
  superblock.s_log_frag_size = buf[28] + buf[29] * 256 + buf[30] * 65536 + buf[31] * 16777216;
  superblock.s_blocks_per_group = buf[32] + buf[33] * 256 + buf[34] * 65536 + buf[35] * 16777216;
  superblock.s_frags_per_group = buf[36] + buf[37] * 256 + buf[38] * 65536 + buf[39] * 16777216;
  superblock.s_inodes_per_group = buf[40] + buf[41] * 256 + buf[42] * 65536 + buf[43] * 16777216;
  superblock.s_magic = buf[56] + buf[57] * 256;
  superblock.s_first_ino = buf[84] + buf[85] * 256 + buf[86] * 65536 + buf[87] * 16777216;
  superblock.s_inode_size = buf[88] + buf[89] * 256;
  superblock.s_block_group_nr = buf[90] + buf[91] * 256;

  return superblock;
}

/*------------------------------------------------------------------------------
 * Name: readGroupDescriptor
 *-----------------------------------------------------------------------------*/

struct ext2_group_desc readGroupDescriptor(int fd, int sector, int offset)
{
  int rc;

  struct ext2_group_desc groupdescriptor;
  unsigned char buf[SECTOR_SZ];

  /* Read sector */
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  groupdescriptor.bg_block_bitmap = buf[0 + offset] + buf[1 + offset] * 256 + buf[2 + offset] * 65536 + buf[3 + offset] * 16777216;
  groupdescriptor.bg_inode_bitmap = buf[4 + offset] + buf[5 + offset] * 256 + buf[6 + offset] * 65536 + buf[7 + offset] * 16777216;
  groupdescriptor.bg_inode_table = buf[8 + offset] + buf[9 + offset] * 256 + buf[10 + offset] * 65536 + buf[11 + offset] * 16777216;
  groupdescriptor.bg_free_blocks_count = buf[12 + offset] + buf[13 + offset] * 256;
  groupdescriptor.bg_free_inodes_count = buf[14 + offset] + buf[15 + offset] * 256;
  groupdescriptor.bg_used_dirs_count = buf[16 + offset] + buf[17 + offset] * 256;

  return groupdescriptor;
}

/*------------------------------------------------------------------------------
 * Name: getGroupDescriptors
 *-----------------------------------------------------------------------------*/

int getGroupDescriptors(int fd, int p_start, struct ext2_group_desc *gds)
{
  int count = 0;
  int offset = 0;
  int totalcount = 0;
  int gd_location;

  struct ext2_group_desc gd;

  /* Read group descriptors from first sector */
  gd_location = p_start + 4;
  gd = readGroupDescriptor(fd, gd_location, offset);

  while ((gd.bg_inode_table != 0) && (count < 16))
  {
    gds[totalcount] = gd;
    count = count + 1;
	offset = count * 32;
	totalcount = totalcount + 1;
    gd = readGroupDescriptor(fd, gd_location, offset);
  }

  count = 0;
  offset = 0;

  /* Read group descriptors from second sector */
  gd_location = p_start + 4 + 1;
  gd = readGroupDescriptor(fd, gd_location, offset);

  while ((gd.bg_inode_table != 0) && (count < 16))
  {
    gds[totalcount] = gd;
    count = count + 1;
	offset = count * 32;
	totalcount = totalcount + 1;
    gd = readGroupDescriptor(fd, gd_location, offset);
  }

  return totalcount;
}

/*------------------------------------------------------------------------------
 * Name: readInode
 *-----------------------------------------------------------------------------*/

struct ext2_inode readInode(int fd, int sector, int in_offset)
{
  int count = 0;
  int offset = 0;
  int pointer;
  int rc;

  struct ext2_inode inode;
  unsigned char buf[SECTOR_SZ];

  /* Read sector */
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  inode.i_mode = buf[0 + in_offset] + buf[1 + in_offset] * 256;
  inode.i_size = buf[4 + in_offset] + buf[5 + in_offset] * 256 + buf[6 + in_offset] * 65536 + buf[7 + in_offset] * 16777216;
  inode.i_links_count = buf[26 + in_offset] + buf[27 + in_offset] * 256;
  inode.i_blocks = (buf[28 + in_offset] + buf[29 + in_offset] * 256 + buf[30 + in_offset] * 65536 + buf[31 + in_offset] * 16777216) / 2;

  pointer = buf[40 + in_offset + offset] + buf[41 + in_offset + offset] * 256 + buf[42 + in_offset + offset] * 65536 + buf[43 + in_offset + offset] * 16777216;

  while (count < 15)
  {
    inode.i_block[count] = pointer;
    count = count + 1;
    offset = count * 4;
    pointer = buf[40 + in_offset + offset] + buf[41 + in_offset + offset] * 256 + buf[42 + in_offset + offset] * 65536 + buf[43 + in_offset + offset] * 16777216;
  }

  return inode;
}

/*------------------------------------------------------------------------------
 * Name: getInode
 *-----------------------------------------------------------------------------*/

struct ext2_inode getInode(int fd, int inode_num, struct ext2_group_desc *gds, int p_start, int inodes_per_group)
{
  int x;
  int y;
  int remainder;

  int byte_offset;
  int group_num;
  int sector;
  int sector_offset;

  struct ext2_inode myinode;

  x = inode_num;
  y = inodes_per_group;
  group_num = x / y;
  remainder = fmod(x, y);

  x = remainder;
  y = 4;
  sector_offset = x / y;
  remainder = fmod(x, y);

  sector = gds[group_num].bg_inode_table * 2 + p_start + sector_offset;

  if (remainder > 0)
  {
    byte_offset = (remainder - 1) * 128;
  }
  else { byte_offset = 0; }

  myinode = readInode(fd, sector, byte_offset);
  return myinode;
}

/*------------------------------------------------------------------------------
 * Name: readDir
 *-----------------------------------------------------------------------------*/

struct ext2_dir_entry_2 readDir(int fd, int sector, int offset)
{
  int count;
  int rc;

  struct ext2_dir_entry_2 mydir;
  unsigned char buf[SECTOR_SZ];

  /* Read sector */
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  mydir.inode = buf[0 + offset] + buf[1 + offset] * 256 + buf[2 + offset] * 65536 + buf[3 + offset] * 16777216;
  mydir.rec_len = buf[4 + offset] + buf[5 + offset] * 256;
  mydir.name_len = buf[6 + offset];
  mydir.file_type = buf[7 + offset];

  for (count = 0; count < mydir.name_len; count++)
  {
    mydir.name[count] = buf[8 + offset + count];
  }

  return mydir;
}

/*------------------------------------------------------------------------------
 * Name: printDirs
 *-----------------------------------------------------------------------------*/

int printDirs(int fd, int dir_in_location)
{
  int count;
  int dir_count = 0;
  int offset = 0;

  struct ext2_dir_entry_2 dir;

  dir = readDir(fd, dir_in_location, offset);

  while (dir.rec_len != 0)
  {
    printf("\nEntry %d \tInode: %d", dir_count + 1, dir.inode);
    printf("\tName: ");

    for (count = 0; count < dir.name_len; count++)
	{
    printf("%c", dir.name[count]);
	}

    offset = offset + dir.rec_len;
    dir_count = dir_count + 1;
    dir = readDir(fd, dir_in_location, offset);
  }

  return dir_count;
}

/*------------------------------------------------------------------------------
 * Name: checkIndirect
 *-----------------------------------------------------------------------------*/

int checkIndirect(int fd, int level, int location, struct ext2_group_desc *gds, int p_start, int blocks_per_group, int inodes_per_group)
{
  int count;
  int offset;
  int sector_count;

  int pointer;
  int rc;
  int sector;

  unsigned char buf[SECTOR_SZ];

  for (sector_count = 0; sector_count < 2; sector_count++)
  {
    count = 0;
    offset = 0;

    sector = location + sector_count;
    rc = readSectors ( fd, sector, 1, buf );
    if ( rc == -1 )
    {
      perror ( "Could not read sector" );
      exit(-1);
    }

    pointer = buf[0 + offset] + buf[1 + offset] * 256 + buf[2 + offset] * 65536 + buf[3 + offset] * 16777216;

    while ((pointer != 0) && (count < 128))
    {
      if (level == 1)
      { rc = checkAllocation(fd, 0, pointer, gds, p_start, blocks_per_group, inodes_per_group); }
      if (level == 2)
      { rc = checkIndirect(fd, 1, pointer, gds, p_start, blocks_per_group, inodes_per_group); }
      if (level == 3)
      { rc = checkIndirect(fd, 2, pointer, gds, p_start, blocks_per_group, inodes_per_group); }

      if (rc == 0) 
      { return 0; }
    
      count = count + 1;
      offset = count * 4;
      pointer = buf[0 + offset] + buf[1 + offset] * 256 + buf[2 + offset] * 65536 + buf[3 + offset] * 16777216;
    }
  }

  return 1;
}

/*------------------------------------------------------------------------------
 * Name: checkBitmap
 *-----------------------------------------------------------------------------*/

int checkBitmap(int in, int bit_position)
{
  int i;
  int bits[8];

  for(i = 7; i>=0; i--) 
  {
    if((1<<i) & in)
    {
	  bits[i] = 1;
    }
    else
    {
	  bits[i] = 0;
    }
  }

  if (bits[bit_position] == 1) 
  { return 1; }
  else 
  { return 0; }
}

/*------------------------------------------------------------------------------
 * Name: checkAllocation
 *-----------------------------------------------------------------------------*/

int checkAllocation (int fd, int choice, int inode_num, struct ext2_group_desc *gds, int p_start, int blocks_per_group, int inodes_per_group)
{
  int x;
  int y;
  int remainder;

  int byte;
  int group_num;
  int sector_offset;

  int rc;
  int sector;

  int bit;
  int location;

  unsigned char buf[SECTOR_SZ];

  x = inode_num;

  /* 0 = Block being checked, 1 = Inode being checked */
  if (choice == 0) {y = blocks_per_group; }
  else { y = inodes_per_group; }

  group_num = x / y;
  remainder = fmod(x, y);

  x = remainder;
  y = 2048;
  sector_offset = x / y;
  remainder = fmod(x, y);

  x = remainder;
  y = 8;
  byte = x / y;
  remainder = fmod(x, y);

  bit = remainder;

  /* 0 = Block being checked, 1 = Inode being checked */
  if (choice == 0) { location = gds[group_num].bg_block_bitmap * 2 + p_start + sector_offset; }
  else { location = gds[group_num].bg_inode_bitmap * 2 + p_start + sector_offset; }

  /* Read sector */
  sector = location;
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  return checkBitmap((int) buf[byte], bit);
}

/*------------------------------------------------------------------------------
 * Name:  checkBlockAllocation
 *-----------------------------------------------------------------------------*/

int checkBlockAllocation(int fd, int *blocks, struct ext2_group_desc *gds, int p_start, int blocks_per_group, int inodes_per_group)
{
  int count = 0;
  int totalcount = 0;
  int location;

  while ((blocks[count] != 0) && (count < 15))
  {
    count = count + 1;
    totalcount = totalcount + 1;
  }

  for (count = 0; count < totalcount; count++)
  {
    if (checkAllocation(fd, 0, blocks[count], gds, p_start, blocks_per_group, inodes_per_group) == 0) 
    { return 0; }
  }

  if (totalcount > 12)
  {
    location = blocks[12] * 2 + p_start;
    return checkIndirect(fd, 1, location, gds, p_start, blocks_per_group, inodes_per_group);
  }

  if (totalcount > 13)
  {
    location = blocks[13] * 2 + p_start;
    return checkIndirect(fd, 2, location, gds, p_start, blocks_per_group, inodes_per_group);
  }

  if (totalcount > 14)
  {
    location = blocks[14] * 2 + p_start;
    return checkIndirect(fd, 3, location, gds, p_start, blocks_per_group, inodes_per_group);
  }

  return 1;
}

/*------------------------------------------------------------------------------
 * Name: printFastSymbolic
 *-----------------------------------------------------------------------------*/

void printFastSymbolic(int fd, int inode_num, int length, struct ext2_group_desc *gds, int p_start, int inodes_per_group)
{
  int count = 0;
  int offset = 0;

  int x;
  int y;
  int remainder;
  int rc;

  int byte_offset;
  int group_num;
  int sector;
  int sector_offset;

  unsigned char buf[SECTOR_SZ];

  x = inode_num;
  y = inodes_per_group;
  group_num = x / y;
  remainder = fmod(x, y);

  x = remainder;
  y = 4;
  sector_offset = x / y;
  remainder = fmod(x, y);

  sector = gds[group_num].bg_inode_table * 2 + p_start + sector_offset;

  if (remainder > 0)
  {
    byte_offset = (remainder - 1) * 128;
  }
  else { byte_offset = 0; }

  /* Read sector */
  rc = readSectors ( fd, sector, 1, buf );
  if ( rc == -1 )
  {
    perror ( "Could not read sector" );
    exit(-1);
  }

  for(count = 0; count < length; count++)
  {
    printf("%c", buf[40 + byte_offset + count]);
  }
}

/*------------------------------------------------------------------------------
 * Name:  printFileMode
 *-----------------------------------------------------------------------------*/

void printFileMode(int in)
{
  if (in >= 12 * (pow(16, 3)))
  { printf("12 (Socket)"); }
  else if (in >= 10 * (pow(16, 3)))
  { printf("10 (Symbolic Link)"); }
  else if (in >= 8 * (pow(16, 3)))
  { printf("8 (Regular File)"); }
  else if (in >= 6 * (pow(16, 3)))
  { printf("6 (Block Device)"); }
  else if (in >= 4 * (pow(16, 3)))
  { printf("4 (Directory)"); }
  else if (in >= 2 * (pow(16, 3)))
  { printf("2 (Character Device)"); }
  else if (in >= 1 * (pow(16, 3)))
  { printf("1 (FIFO)"); }
}

/*------------------------------------------------------------------------------
 * Name:  main
 *-----------------------------------------------------------------------------*/

int main ()
{
  int           rc;                       /* Return code        */
  int           fd;                       /* Device descriptor  */           
  int           sector;                   /* IN: sector to read */

  struct partition partitions[16];
  struct ext2_group_desc groupdescriptors[32];
  struct ext2_inode inode;
  struct ext2_super_block superblock;

  int block_size;
  int dir_in_location;
  int partition_start;
  int start = 446;

  int inode_num;
  int dir_count;
  int gd_count;
  int partition_count;

  /* Open the device */
  fd = open ( "disk", O_RDWR ); 
  if ( fd == -1 ) 
  {
    perror ( "Could not open device file" );
    exit(-1);
  }

  /* Read the partitions */
  partition_count = getPartitions(fd, partitions);
  partition_start = partitions[0].start_sect;

  /* Read the Superblock */
  superblock = readSuperBlock(fd, partition_start);
  if (superblock.s_magic == EXT2_SUPER_MAGIC)
  { printf("\nSuperblock Magic Number: %x (Correct)", superblock.s_magic); }
  else
  { printf("\nSuperblock Magic Number: %x (Incorrect)", superblock.s_magic); }

  block_size = pow(2, superblock.s_log_block_size) * 1024;
  printf("\nBlock Size: %d\n", block_size);

  /* Read the group descriptors */
  gd_count = getGroupDescriptors(fd, partition_start, groupdescriptors);

  /* Read the root inode */
  inode_num = 2;
  inode = getInode(fd, inode_num, groupdescriptors, partition_start, superblock.s_inodes_per_group);
  printf("\nRoot Inode File Mode: ");
  printFileMode(inode.i_mode);

  /* See if root inode is allocated */
  if (checkAllocation(fd, 1, inode_num, groupdescriptors, partition_start, superblock.s_blocks_per_group, superblock.s_inodes_per_group) == 1) 
  { printf("\nInode Allocated\n"); }
  else 
  { printf("\nInode Not Allocated\n"); }

  /* Read the data for root inode */
  printf("\nRoot Inode Directory Data...");
  dir_in_location = partition_start + (inode.i_block[0] * 2);
  dir_count = printDirs(fd, dir_in_location);

  /* Read the oz inode */
  inode_num = 28;
  inode = getInode(fd, inode_num, groupdescriptors, partition_start, superblock.s_inodes_per_group);
  
  /* Read the data for oz inode */
  printf("\n\nOz Directory Data...");
  dir_in_location = partition_start + (inode.i_block[0] * 2);
  dir_count = printDirs(fd, dir_in_location);

  /* Read the ohmy.txt inode */
  inode_num = 4021;
  inode = getInode(fd, inode_num, groupdescriptors, partition_start, superblock.s_inodes_per_group);
  printf("\n\nohmy.txt Inode Data Blocks: %d", inode.i_blocks);

  /* See if ohmy.txt's blocks are allocated */
  if (checkBlockAllocation(fd, inode.i_block, groupdescriptors, partition_start, superblock.s_blocks_per_group, superblock.s_inodes_per_group) == 1) 
  { printf("\nAll Blocks Allocated"); }
  else 
  { printf("\nNot All Blocks Allocated"); }

  /* Read the glinda inode */
  inode_num = 30;
  inode = getInode(fd, inode_num, groupdescriptors, partition_start, superblock.s_inodes_per_group);
  printf("\n\nglinda Inode File Mode: ");
  printFileMode(inode.i_mode);

  printf("\nglinda Link: ");
  printFastSymbolic(fd, inode_num, inode.i_size, groupdescriptors, partition_start, superblock.s_inodes_per_group);

  close(fd);
  return 0;
}

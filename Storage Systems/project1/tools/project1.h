/*
 * 600.319/419 Project 1
 *
 * Title:       project1.h
 * Author:      Zachary Peterson (zachary@cs.jhu.edu)
 * Description: A character/block device interface for the 
 *              DiskSim simulator.
 *
 * Comments:    All systems calls prototyped in this file
 *              are analogs of actual system calls that
 *              you'll find on your favorite flavor of UNIX.
 *              I tried to keep their look and feel as close
 *              to the original calls as possible, but at the
 *              same time, had no real interest in re-implementing
 *              a UNIX kernel.  This means, some options and
 *              functionality have been omitted. Mind you, 
 *              everything you need to complete this project is
 *              included.  Don't spaz if or when they behave a little
 *              differently. However, please feel free to use the real
 *              'man' pages as an reasonably accurate reference; 
 *              I did my best to paraphrase the pages here.
 *
 */


#define SECTOR 512 /* Size of the disk sector in bytes */


/********* DISKSIM SYSTEM CALLS *********/

/* ds_open - open a character/block device
 * #include <sys/types.h>
 * #include <sys/stat.h>
 * #include <fcntl.h>
 *
 * PRE:  None.
 * POST: ds_open converts the pathname 'dev' into a
 *       device descriptor (non-negative int) for
 *       subsequent I/O (read/write).  Use the 'flags'
 *       field to specify what kind of I/O.
 * RET:  ds_open returns a new device descriptor, or
 *       -1 if an error occurred (e.g. you tried to
 *       open something other than "/dev/disksim".)
 * FLAGS:
 * 
 * O_RDONLY     Open for reading only.
 * O_WRONLY     Open from writing only.
 * O_RDWR       Open for reading and writing.
 */

int ds_open(const char* dev, int flags);


/* ds_close - Closes a device descriptor.
 * #include <unistd.h>
 *
 * PRE:  'devid' is a valid device descriptor.
 * POST: ds_close closes a device descriptor, 
 *       preventing further I/O and so that the
 *       descriptor may be reused.
 * RET:  ds_close returns 0 on success, or -1
 *       if an error occurred.
 */

int ds_close(int devid);


/* ds_read - Read from a device descriptor.
 * #include <unistd.h>
 *
 * PRE:  'devid' is a valid device descriptor.
 * POST: ds_read attempts to read up to 'nbyte'
 *       bytes from device descriptor 'devid' into
 *       a buffer starting at 'buf'.
 * RET:  On success, the number of bytes read is
 *       returned.  On error, -1 is returned.
 */

ssize_t ds_read(int devid, void *buf, size_t nbyte);


/* ds_write - Write to a device description.
 * #include <unistd.h>
 *
 * PRE:  devid is a valid device descriptor.
 * POST: ds_write writes up to 'nbyte' bytes to
 *       the device referenced by the device
 *       descriptor 'devid' from the buffer starting
 *       at 'buf'.
 * RET:  On success, the number of bytes written is
 *       returned. On error, -1 is returned.
 */

ssize_t ds_write(int devid, const void *buf, size_t nbyte);


/* ds_lseek - reposition read/write device offset.
 * #include <sys/types.h>
 * #include <unistd.h>
 *
 * PRE:  devid is a valid device descriptor.
 * POST: The ds_lseek call repositions the offset of
 *       the device descriptor 'devid' to the argument
 *       'offset' according to the directive 'whence'.
 * RET:  Upon success, ds_lseek returns the resulting
 *       offset location as measured in bytes from
 *       the beginning of the device. Otherwise,
 *       -1 is returned on failure.
 * WHENCE:
 * o If whence is SEEK_SET, the pointer is set to 'offset'
 *   bytes.
 *
 * o If whence is SEEK_CUR, the pointer is set to its
 *   current location plus 'offset'.
 *
 */

off_t ds_lseek(int devid, off_t offset, int whence);


/* ds_gettimeofday - Get time.
 * #include <sys/time.h>
 *
 * PRE:  'tv' is a valid timeval pointer.
 * POST: ds_gettimeofday gets DiskSim's notion of the current
 *       time.  The current DiskSim time is expressed in
 *       elapsed seconds and microseconds since 00:00 of
 *       the start of the DiskSim simulator.  Keep in mind that
 *       ds_gettimeofday is a slight variant of the actual
 *       gettimeofday function.  The real gettimeofday returns
 *       wall clock time (advanced by the passing of time/life), 
 *       while ds_gettimeofday returns DiskSim's notion of time, 
 *       advanced only by DiskSim events and not the actual 
 *       passing of time. Who knew that storage systems would
 *       combine advanced theories of relativity with a
 *       deep philosophical understanding of passing time
 *       AND could be so much FUN?!
 *
 * RET:  The 'tv' argument points to a timeval structure, which
 *       includes the following members:
 *        
 *        long    tv_sec;    // seconds since 00:00 DiskSim time
 *        long    tv_usec;   // and microseconds
 *
 */

int ds_gettimeofday(struct timeval *tv);

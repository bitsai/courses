
/*******************************   stemmer.c   ********************************
 *
 *  Program to demonstrate and test the Porter stemming function.  This 
 *  program takes a single filename on the command line and lists stemmed
 *  terms on stdout.
 *
**/

#include <stdio.h>
#include <ctype.h>

#include "stem.h"

/******************************************************************************/
/********************   Private Defines and Data Structures   *****************/

#define EOS                           '\0'

/******************************************************************************/
/************************   Private Function Definitions   ********************/

#ifdef __STDC__

static char * GetNextTerm( FILE *stream, int size, char *term );

#else

static char * GetNextTerm();

#endif

/*FN***************************************************************************

        GetNextTerm( stream, size, term )

   Returns: char * -- buffer with the next input term, NULL at EOF

   Purpose: Grab the next token from an input stream

   Plan:    Part 1: Return NULL immediately if there is no input
            Part 2: Initialize the local variables
            Part 3: Main Loop: Put the next word into the term buffer
            Part 4: Return the output buffer

   Notes:   None.
**/

static char *
GetNextTerm( stream, size, term )
   FILE *stream;  /* in: source of input characters */
   int size;      /* in: bytes in the output buffer */
   char *term;    /* in/out: where the next term in placed */
   {
   char *ptr;  /* for scanning through the term buffer */
   int ch;     /* current character during input scan */

           /* Part 1: Return NULL immediately if there is no input */
   if ( EOF == (ch = getc(stream)) ) return( NULL );

                  /* Part 2: Initialize the local variables */
   *term = EOS;
   ptr = term;

        /* Part 3: Main Loop: Put the next word into the term buffer */
   do
      {
         /* scan past any leading non-alphabetic characters */
      while ( (EOF != ch ) && !isalpha(ch) ) ch = getc( stream );

         /* copy input to output while reading alphabetic characters */
      while ( (EOF != ch ) && isalpha(ch) )
         {
         if ( ptr == (term+size-1) ) ptr = term;
         *ptr++ = ch;
         ch = getc( stream );
         }

         /* terminate the output buffer */
      *ptr = EOS;
      }
   while ( (EOF != ch) && !*term );

                    /* Part 4: Return the output buffer */
   return( term );

   } /* GetNextTerm */

/******************************************************************************/
/*FN***************************************************************************

        main( argc, argv )

   Returns: int -- 0 on success, 1 on failure

   Purpose: Program main function

   Plan:    Part 1: Open the input file
            Part 2: Process each word in the file
            Part 3: Close the input file and return

   Notes:   
**/

int
main( argc, argv )
   int argc;     /* in: how many arguments */
   char *argv[]; /* in: text of the arguments */
   {
   char term[64];   /* for the next term from the input line */
   FILE *stream;    /* where to read characters from */

                       /* Part 1: Open the input file */
   if ( !(stream = fopen(argv[1],"r")) ) exit(1);

                  /* Part 2: Process each word in the file */
   while( GetNextTerm(stream,64,term) )
      if ( Stem(term) ) (void)printf( "%s\n", term );

                 /* Part 3: Close the input file and return */
   (void)fclose( stream );
   return(0);

   } /* main */


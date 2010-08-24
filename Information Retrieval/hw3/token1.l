%C

SPACE				[ ]
WHITE_SPACE			[ \t\n]
NEWLINE				[\n]
NON_NEWLINE			[^\n]
JUNK				[^\040-\176]
OPEN_SINGLE_QUOTE		[\`]
CLOSE_SINGLE_QUOTE		[\']

%%

[\n][.][ITAWK][^\n]*			|
[.?!]+[ \t]+			        |
[0-9][0-9.,]*[0-9]			|
['][sdmSDM][ \t]+                       |
['][lL][lL][ \t]+			|
['][rR][eE][ \t]+			|
[nN['][tT][ \t]+                        |
[.]+					|
[-]+					|
[!?]+					|
{OPEN_SINGLE_QUOTE}+			|
{CLOSE_SINGLE_QUOTE}+			|
[A-Za-z0-9]+				|
[^0-9A-Za-z\n\t ] 			{printf("%s\n", yytext); } ;

{WHITE_SPACE}|{JUNK}		        ;

%%

yywrap()
{
  printf("\n");
  return(1);
}

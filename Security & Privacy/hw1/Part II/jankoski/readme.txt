Installation instructions

	This website was implemented a Fedora core 2 box with default installions of apache 2.

	On this disk you will find two zipped folders – cgi-bin and htdocs.Extract each folder in place of the corresponding folder in the apache tree structure. After extracting, ensure all the .cgi files have read and execute privileges across the board. There are two files in the cgi-bin, userlist and pf.dat. Ensure that these files have read and write privileges across the board, if not the web site will not be able to retain user information.  All scripts look for perl in the /usr/bin/ folder. If this is different on your box, each script will need to be manually changed accordingly. 
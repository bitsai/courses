Benny Tsai
600.315
Database Systems
Final Project

Files:
- Final.html: This file is the web front-end of the database.  You can see this project in action at http://dbase.cs.jhu.edu:8080/~btsai/Final.html.  Some caveats: the first time you execute a query, you must log in with my Oracle user ID and password (user ID = btsai_03, password = harpoon).  I believe you must log in using my user ID/password because the page calls on some procedures with very strict access permissions.  Now, the first time you do this, the page will most likely return an Oracle error.  Hit "Back" to the main page, refresh the page, and then try the query again.  It should work now (if it doesn't, you'll have to experiment with a sequence of "Refresh", "Back", and "Forward" operations).  I'm not sure why this happens; mayhaps some weird lag while the browser connects with the database?

*** The values asked for by the webpage, "Character Level", "Strength", and "Dexterity" should all be numeric values.

- Final.pl: This perlscript, when executed, extracts information about weapons and armor from the webpages at battle.net.

- finalProcs.sql: This file contains the PL/SQL procedures called on by the webpage.

- finalTables.sql: This file contains the table definitions used in the database.

- finalValues.sql: This file contains manual input of information about magic properties.
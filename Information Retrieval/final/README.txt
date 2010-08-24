Final Project
600.466 - Information Retrieval

Benny Tsai
Sanford Philips

Name: TravelBot
Description:  A web robot which serves your trip planning needs.  It also 
outputs various pieces of information helpful for SAC group trip planning.

***PART 1: Agent.pl
How to run:  Execute with the command "perl agent.pl".  The first things 
you'll see are queries for various trip details, such as roundtrip/one way, 
departure/return date, origin information, and destination information.

IMPORTANT: Date should be entered as numerical month/numerical date.  Ex. 
6/21
IMPORTANT: Origin and destination city/state should be entered as city comma 
space 2-letter state abbreviation.  Ex. Baltimore, MD

You may choose to enter street addresses and zip codes, but these are 
optional.  After that, you should see the main TravelBot menu.  Each option 
is described below.

1. Get Driving Costs => This option calculates and returns total driving 
time, total distance, and information such as SAC van rental costs, vehicle 
and van gas costs.  Also, detailed driving directions a la Mapquest are 
saved to directions.txt.

2. Get Flying Costs => Gets the lowest airfare from Continental, Delta, and 
United Airlines.  The web robot uses itn.net to search for best fare amongst 
Continental, Delta, and United Airlines.  This takes a while since it runs 3 
separate searches.  Why not use sites like Travelocity, Orbitz, or Expedia, 
you ask?  Because they all seem to ban web robots (the first two in their 
legal agreement, and the third via some weird scripting scheme).  Itn.net 
has its own all-airlines search function, but the main page is also 
off-limits to web robots.  So i'm doing sort of a piggy-backing, with the 
result that i had to pick 3 airlines and do 3 searches.

3. Get Train Costs => Gets the lowest fare available from Amtrak.  Also 
outputs the train schedule corresponding to this lowest fare.

4. Compare All Costs=> Calls all of the above 3 commands to compare the 3 
modes of transportation.

5. Get Destination Info => Prints out a summary of Historical sites, Tourist 
sites, Food places, Lodging places, Cab services, and Car rentals near the 
destination.  The 3 nearest places in each category are displayed.  A more 
detailed version of this list including addresses and phone numbers for each 
place is saved to info.txt.

6. Change Origin/Destination, Dates => Repeats the queries from the 
beginning of the program, allowing new origin/destination, etc. to be 
entered.

7. Change Mileages, Costs => Displays the current values for SAC van rental 
cost, SAC van mileage, vehicle mileage, and gas cost.  Allows them to be 
altered.  The new values will be used until the program is exited.  The 
default settings are: SAC van rental cost = 0.36/mile; SAC van mileage = 18 
mpg; vehicle mileage = 25 mpg; gas cost = $1.65/gallon.

8. Exit => Exits TravelBot.

***Part 2  Web Interface
Description:  A CGI interface between the robot and a static html page that 
dynamically displays the information requested.

To run it yourself:  Put index.html in your public_html directory and change the 
form ACTION field to point to [your cgi-bin directory]/agentcgi.pl.  Then put
agentcgi.pl in your cgi-bin directory.

To see it in action: Point your web browser to hops.cs.jhu.edu/~sanford and play away.
Be very patient as the cgi is slow and so is hops, so 1-2 minute retrival times are
the norm.

The webpage can do everything the text-based bot can do except for door to door directions
(in favor of a cleaner interface).

Other Miscellaneous Info:
1. Getting flight information takes the longest.  Driving info is next 
longest.  Train info is relatively fast.
2. Obviously, not all cities have an airport or a train station.  The 
respective options will notify the user when such a situation is 
encountered.
3. The error checking is minimal; you can easily spoof the system by 
entering a return date earlier than the departure date, incorrectly entered 
dates or city/state info, etc.  I figured that it's more important to get 
functions implemented rather than program in all sorts of safeguards to make 
the program absolutely idiot-proof.
4. For departure and return times, the program defaults to noon for all 
forms that ask for it.
5. Default year is 2003.  I'll change it for next year's edition.
6. The costs listed here are for one adult, one van, one vehicle, etc.
7. Content.txt has basically a copy of what the user sees.
8. Log.txt records the various activities made by the web robot.

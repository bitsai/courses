use strict;
use LWP::RobotUA;

my $ROBOT_NAME = 'BTsaiBot/1.0';
my $ROBOT_MAIL = 'btsai@cs.jhu.edu';

my $ua = new LWP::RobotUA $ROBOT_NAME, $ROBOT_MAIL;
$ua->delay(0.016);

open LOG, ">log.txt";
open CONTENT, ">content.txt";
open INFO, ">info.txt";
open DIRECTIONS, ">directions.txt";

my ($option, $round_trip);

my ($depart_date, $depart_month, $depart_day);
my ($return_date, $return_month, $return_day);

my ($origin, $origin_city, $origin_state);
my ($destination, $dest_city, $dest_state);

my ($origin_street, $origin_zip, $dest_street, $dest_zip);

my $SAC_van_cost = 0.36;
my $SAC_van_mileage = 18;
my $vehicle_mileage = 25;
my $gas_cost = 1.65;

# Start main program

&get_settings;

while ()
{
	&menu;
}

##########################################################
## GET_SETTINGS
##########################################################

sub get_settings
{
	print <<"EndOfMenu";

	CHOOSE:
	  1 = One-way
	  2 = Round-trip

EndOfMenu
;

	print "Enter Option: ";
	$round_trip = <>;
	if ($round_trip != 1 and $round_trip != 2) { exit 0; }
	$round_trip--;

	print "\n";
	print CONTENT "Round_trip = ".$round_trip."\n";

# Get departure and return dates

	$depart_date = &input("Departure Month/Day: ");
	my @depart_date = split(/\//, $depart_date);
	$depart_month = &get_month($depart_date[0]);
	$depart_day = $depart_date[1];

	$return_date = &input("Return Month/Day: ");
	my @return_date = split(/\//, $return_date);
	$return_month = &get_month($return_date[0]);
	$return_day = $return_date[1];

	&output("\n");

# Origin data

	$origin = &input("Origin City, State Abbrev.: ");
	$origin_street = &input("Origin Street (Optional): ");
	$origin_street =~ s/\s/\+/g;
	$origin_zip = &input("Origin Zip (Optional): ");

	my @origin = split(/,\s+/, $origin);
	$origin_city = $origin[0];
	$origin_city =~ s/\s/\+/g;
	$origin_state = $origin[1];

	&output("\n");

# Destination data

	$destination = &input("Destination City, State Abbrev.: ");
	$dest_street = &input("Destination Street (Optional): ");
	$dest_street =~ s/\s/\+/g;
	$dest_zip = &input("Destination Zip (Optional): ");

	my @destination = split(/,\s+/, $destination);
	$dest_city = $destination[0];
	$dest_city =~ s/\s/\+/g;
	$dest_state = $destination[1];
}

##########################################################
## MENU
##########################################################

sub menu
{
	print <<"EndOfMenu";

	============================================================
	================    Welcome to TravelBot    ================
	============================================================

	OPTIONS:
	  1 = Get Driving Costs
	  2 = Get Flying Costs
	  3 = Get Train Costs
	  4 = Compare All Costs
	  5 = Get Destination Info
	  6 = Change Origin/Destination, Dates
	  7 = Change Mileages, Costs
	  8 = Exit

	============================================================

EndOfMenu
;

	print "Enter Option: ";
	$option = <>;
	chomp $option;
	print CONTENT "\nOption = ".$option."\n";

	if ($option == 1) 
	{
		print CONTENT "Get Driving Costs\n";
		&mapquest(0);
	}
	elsif ($option == 2) 
	{
		print CONTENT "Get Flying Costs\n";
		&itn;
	}
	elsif ($option == 3) 
	{
		print CONTENT "Get Train Costs\n";
		&amtrak;
	}
	elsif ($option == 4)
	{
		print CONTENT "Compare All Costs\n";
		&mapquest(0);
		&itn;
		&amtrak;
	}
	elsif ($option == 5) 
	{
		print CONTENT "Get Destination Info\n";
		&mapquest(1);
	}
	elsif ($option == 6)
	{
		print CONTENT "Change Origin/Destination, Dates\n";
		&get_settings;
	}
	elsif ($option == 7)
	{
		print CONTENT "Change Mileages, Costs\n";
		&change_defaults;
	}
	else
	{
		print CONTENT "Exit\n";

		close LOG;
		close CONTENT;
		close DIRECTIONS;
		close INFO;

		exit 0;
	}
}

##########################################################
## MAPQUEST
##########################################################

sub mapquest
{
	my $op = shift; # 0 = output driving info, 1 = get destination info
	my ($URL, $input, $content, $ID);
	$URL = "http://www.mapquest.com/rtp/";

# Get Mapquest roadtrip plan ID number

	$content = &GET($URL);
	$content =~ /(\w{8}-\w{5}-\w{5}-\w{8})/;
	$ID = $1;

# Input plan name and choose one-way/round-trip

	if ($round_trip == 0)
	{
		$input = "rtpid=$ID&name=Agent&oneway.x=5&oneway.y=5";
		&GET($URL."startroute.adp?".$input);
	}
	else
	{
		$input = "rtpid=$ID&name=Agent&roundtrip.x=5&roundtrip.y=5";
		&GET($URL."startroute.adp?".$input);
	}	

# Input origin

	$input = "rtpid=$ID&address=$origin_street&city=$origin_city&state=$origin_state&zip=$origin_zip&x=5&y=5";
	&GET($URL."createroute.adp?".$input);

# Input destination

	$input = "rtpid=$ID&address=$dest_street&city=$dest_city&state=$dest_state&zip=$dest_zip&x=5&y=5";
	&GET($URL."createroute.adp?".$input);

	if ($op == 0)
	{ &mapquest_output($ID); }
	else
	{ &mapquest_info($ID); }
}

##########################################################
## ITN
##########################################################

sub itn
{
	my $URL = "http://www.itn.net/cgi/air";
	my ($rt_ow, $input, $content);
	&output("\n");

# Set round trip/one way

	if ($round_trip == 0)
	{ $rt_ow = "One+Way"; }
	else
	{ $rt_ow = "Round+Trip"; }

# Get Continental lowest price

	$input = $URL."?stamp=NEWCOOKY*itn%2Ford%3DNEWREC,itn/air/united&airline=Continental&persons=1&air_avail=10&depart=$origin&dest.0=$destination&mon_abbr.0=$depart_month&date.0=$depart_day&hour_ampm.0=12 pm&mon_abbr.1=$return_month&date.1=$return_day&hour_ampm.1=12 pm&rt_ow=$rt_ow&best_itins=2&return_to=best_itins";
	&get_fare("Continental", $input);

# Get Delta lowest price

	$input = $URL."?stamp=NEWCOOKY*itn%2Ford%3DNEWREC,itn/air/united&airline=Delta&persons=1&air_avail=10&depart=$origin&dest.0=$destination&mon_abbr.0=$depart_month&date.0=$depart_day&hour_ampm.0=12 pm&mon_abbr.1=$return_month&date.1=$return_day&hour_ampm.1=12 pm&rt_ow=$rt_ow&best_itins=2&return_to=best_itins";
	&get_fare("Delta", $input);

# Get United lowest price

	$input = $URL."?stamp=NEWCOOKY*itn%2Ford%3DNEWREC,itn/air/united&airline=United&persons=1&air_avail=10&depart=$origin&dest.0=$destination&mon_abbr.0=$depart_month&date.0=$depart_day&hour_ampm.0=12 pm&mon_abbr.1=$return_month&date.1=$return_day&hour_ampm.1=12 pm&rt_ow=$rt_ow&best_itins=2&return_to=best_itins";
	&get_fare("United", $input);
}

##########################################################
## AMTRAK
##########################################################

sub amtrak
{
	my ($URL, $request, $response, $content);
	my ($input, $price, $dep_station, $dest_station);

# Get station abbreviations

	$URL = "http://tickets.amtrak.com/Amtrak/availability";
	$input = "storefront=1003&pageID=main&departMonth1=$depart_month&departDay1=$depart_day&inputDepartureDate=$depart_month $depart_day, 2003&inputDepartureStation=$origin&inputDestinationStation=$destination&inputDepartureTime=1200&inputNumberOfTravelers=1";
	$content = &POST($URL, $input);

	if ($content =~ /\$RCSfile: src\/html\/availability.htm/)
	{
		$dep_station = $origin;
		$dest_station = $destination;
	}
	elsif ($content =~ /We could not match the station code you entered/)
	{
		&output("\nNo Amtrak Station Found For Origin And/Or Destination\n");
		return;
	}
	else
	{
		$content =~ /"inputDepartureStation".+"(.+)"/;
		$dep_station = $1;
		$content =~ /"inputDestinationStation".+"(.+)"/;
		$dest_station = $1;
	}

# Input data

	$URL = "http://tickets.amtrak.com/Amtrak/availability";

	if ($round_trip == 0)
	{
		$input = "storefront=1003&pageID=main&inputDepartureDate=$depart_month $depart_day, 2003&inputDepartureStation=$dep_station&inputDestinationStation=$dest_station&inputDepartureTime=1200&inputNumberOfTravelers=1";
	}
	else
	{
		$input = "storefront=1003&pageID=main&inputDepartureDate=$depart_month $depart_day, 2003&inputDepartureStation=$dep_station&inputDestinationStation=$dest_station&inputDepartureTime=1200&inputNumberOfTravelers=1&inputReturnDate=$return_month $return_day, 2003&inputReturnTime=1200";
	}

	$content = &POST($URL, $input);
	$content =~ /action="(.+)" method="POST"/;
	$URL = $1;

# Select trains and passenger type

	if ($round_trip == 0)
	{
		$input = "selectedTripOption0=0&passengerType0=ADU";
	}
	else
	{
		$input = "selectedTripOption0=0&selectedTripOption1=0&passengerType0=ADU";
	}

	$content = &POST($URL, $input);

# Output itinerary

	&output("\nAmtrak Schedule:\n");

	while ($content =~ s/<.+>From <.+>(.+)<.+>( on \d{2}\/\d{2}\/\d{2} to )<.+>(.+)<.+>( on \d{2}\/\d{2}\/\d{2})<.+>//)
	{
		&output("$1$2$3$4\n");
	}

# Parse returned price

	$content =~ /TOTAL\sFARE:<.+>\n<.+>\n<.+>(\d+\.\d{2})/;
	$price = $1;

	if ($price !~ /\d+\.\d{2}/)
	{ $price = ""; }

	&output("\nAmtrak Fare: ".$price."\n");
}

##########################################################
## CHANGE_DEFAULTS
##########################################################

sub change_defaults
{
	&output("\n");
	&output("Current SAC Van Cost: ".$SAC_van_cost."\n");
	&output("Current SAC Van Mileage: ".$SAC_van_mileage."\n");
	&output("Current Vehicle Mileage: ".$vehicle_mileage."\n");
	&output("Current Gas Cost: ".$gas_cost."\n");
	&output("\n");

	$SAC_van_cost = &input("New SAC Van Cost (Dollars/Mile): ");
	$SAC_van_mileage = &input("New SAC Van Mileage: ");
	$vehicle_mileage = &input("New Vehicle Mileage: ");
	$gas_cost = &input("New Gas Cost (Dollars/Gallon): ");
}

##########################################################
## MAPQUEST_OUTPUT
##########################################################

sub mapquest_output
{
	my $ID = shift;
	my $content = &GET("http://www.mapquest.com/rtp/routeoverview.adp?rtpid=$ID");

# Get total time and distance

	$content =~ /Total Distance:<.*>(.*)<.*>/;
	my $distance = $1;

	$content =~ /Estimated Driving Time:<.*>(.*)<.*>/;
	my $time = $1;

# Output

	&mapquest_directions($ID);

	&output("\nTotal Driving Distance: ".$distance."\n");
	&output("Estimated Driving Time:".$time."\n");
	&output("Driving Directions Saved To Directions.txt\n");

	&output("\nSAC Van Rental Cost: ".($distance * $SAC_van_cost)."\n");
	&output("SAC Van Gas Cost: ".($distance / $SAC_van_mileage * $gas_cost)."\n");
	&output("Vehicle Gas Cost: ".($distance / $vehicle_mileage * $gas_cost)."\n");
}

##########################################################
## MAPQUEST_INFO
##########################################################

sub mapquest_info
{
	my $ID = shift;
	my ($type, $URL);
	print "\n";

# Get Historical sites

	$type = "HISTORICAL";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=1&rtpid=$ID&destid=2&rtpsc=3&x=5&y=5";
	&get_info($type, $URL);

# Get Tourist sites

	$type = "TOURIST";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=1&rtpid=$ID&destid=2&rtpsc=7&x=5&y=5";
	&get_info($type, $URL);

# Get Car sites

	$type = "CARS";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=6&rtpid=$ID&destid=2&rtpsc=2&x=5&y=5";
	&get_info($type, $URL);

# Get Cab sites

	$type = "CABS";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=6&rtpid=$ID&destid=2&rtpsc=4&x=5&y=5";
	&get_info($type, $URL);

# Get Food sites

	$type = "FOOD";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=7&rtpid=$ID&destid=2&rtpsc=1&x=5&y=5";
	&get_info($type, $URL);

# Get Lodging sites

	$type = "LODGING";
	$URL = "http://www.mapquest.com/rtp/dirsearch.adp?rtpc=8&rtpid=$ID&destid=2&rtpsc=1&x=5&y=5";
	&get_info($type, $URL);

	&output("Addresses + Phone #'s Saved To Info.txt\n");
}

sub test
{
	my $ID = shift;
	my ($type, $URL);
}

##########################################################
## MAPQUEST_DIRECTIONS
##########################################################

sub mapquest_directions
{
	my $ID = shift;

	for (my $count = 1; $count < ($round_trip + 2); $count++)
	{
		my $page = &GET("http://www.mapquest.com/rtp/directions.adp?rtpid=$ID&segment=$count");

		if ($count == 1)
		{ print DIRECTIONS "$origin_city to $dest_city\n"; }
		else
		{ print DIRECTIONS "$dest_city to $origin_city\n"; }

		while ($page =~ s/(\d{1,2}:)<.+>\n\s+<.+>(.*)<.+>\n\s+<.+>&nbsp;(.*)<.+>//)
		{
			print DIRECTIONS "$1 $2\n";
			print DIRECTIONS "\tDistance: $3\n";
		}

		print DIRECTIONS "\n";
	}
}

##########################################################
## OUTPUT
##########################################################

sub output
{
	my $output = shift;
	print $output;
	print CONTENT $output;
}

##########################################################
## INPUT
##########################################################

sub input
{
	my $query = shift;
	print "Please Enter ".$query;
	my $input = <>;
	chomp $input;
	print CONTENT $query.$input."\n";
	return $input;
}

##########################################################
## GET
##########################################################

sub GET
{
	my $URL = shift;
	print LOG "GET $URL\n";

	my $request = new HTTP::Request GET => $URL;
	my $response = $ua->request($request);

	print LOG "Code = ".$response->code."\n";
	print LOG "Base = ".$response->base."\n\n";

	my $content = $response->content;
	return $content;
}

##########################################################
## POST
##########################################################

sub POST
{
	my $URL = shift;
	my $input = shift;
	my ($request, $response);

	print LOG "POST $URL\n";
	print LOG "DATA $input\n";

	$request = new HTTP::Request POST => $URL;
	$request->content_type("application/x-www-form-urlencoded");
	$request->content($input);
	$response = $ua->request($request);

	print LOG "Code = ".$response->code."\n";
	print LOG "Base = ".$response->base."\n\n";

	if ($response->code == 302)
	{
		$URL = $response->header("Location");
		return &GET($URL);
	}

	return $response->content;
}

##########################################################
## GET_INFO
##########################################################

sub get_info
{
	my $type = shift;
	my $URL = shift;
	my $count = 1;
	my $content;

	print "$type\n";
	print INFO "$type\n";
	$content = &GET($URL);

	while ($content =~ s/pid=">(.+)<.+><.+><.+>\n\s+&nbsp;&nbsp; (.+)<.+>&nbsp;&nbsp; (.+)<br>&nbsp;&nbsp; (\d{3}-\d{3}-\d{4})<.+>// and $count <= 3)
	{
		print "$count. $1\n";
		print INFO "$count.\t$1\n";
		print INFO "\t$2\n";
		print INFO "\t$3\n";
		print INFO "\tPhone: $4\n";
		$count++;
	}

	print "\n";
	print INFO "\n";
}

##########################################################
## GET_FARE
##########################################################

sub get_fare
{
	my $airline = shift;
	my $URL = shift;

# POST (via GET) data and parse returned price

	my $content = &GET($URL);
	my @prices;

	while ($content =~ s/<.+>(\d{3}\.\d{2})<.+>\sUSD//)
	{ push (@prices, $1); }

	if (scalar @prices == 0)
	{
		&output("No match found for $airline\n");
		return;
	}

# Output lowest price

	@prices = sort { ($a <=> $b); } @prices;
	&output("$airline Lowest Cost: ".$prices[0]."\n");
}

##########################################################
## GET_MONTH
##########################################################

sub get_month
{
	my $input = shift;

	if ($input == 1) { return "Jan"; }
	if ($input == 2) { return "Feb"; }
	if ($input == 3) { return "Mar"; }
	if ($input == 4) { return "Apr"; }
	if ($input == 5) { return "May"; }
	if ($input == 6) { return "Jun"; }
	if ($input == 7) { return "Jul"; }
	if ($input == 8) { return "Aug"; }
	if ($input == 9) { return "Sep"; }
	if ($input == 10) { return "Oct"; }
	if ($input == 11) { return "Nov"; }
	if ($input == 12) { return "Dec"; }

	return "Apr"; # Default = my birth month
}

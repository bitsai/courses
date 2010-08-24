#!/usr/local/bin/perl -wT
use strict;
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use CGI::Pretty qw/:standard/;
use Fcntl qw(:flock :seek);

### Declarations ###

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';
my $salt = ")a8*!--&";
my $content = "";

### Control logic ###

# Create page content based on form action

if (param('action') eq "portfolio") {
	if (&authenticate == 1) { $content = &showPortfolio; }
	else { $content = qq|<p>Authentication failed!</p>|; }
}
elsif (param('action') eq "history") {
	if (&authenticate == 1) { $content = &showHistory; }
	else { $content = qq|<p>Authentication failed!</p>|; }
}
elsif (param('action') eq "buy") {
	if (&authenticate == 1) {
		if (&validate(param('symbol'), param('shares')) == 1) {
			&updatePortfolio(param('symbol'), param('shares'));
			&updateHistory("Buy", param('symbol'), param('shares'));
			
			$content = qq|<p>Transaction successful!</p>\n|;
			$content .= &showPortfolio;
		}
		else { $content = qq|<p>Invalid transaction!</p>|; }
	}
	else { $content = qq|<p>Authentication failed!</p>|; }
}
elsif (param('action') eq "sell") {
	if (&authenticate == 1) {
		if (&validate(param('symbol'), param('shares')) == 1) {
			&updatePortfolio(param('symbol'), -param('shares'));
			&updateHistory("Sell", param('symbol'), param('shares'));
			
			$content = qq|<p>Transaction successful!</p>\n|;
			$content .= &showPortfolio;
		}
		else { $content = qq|<p>Invalid transaction!</p>|; }
	}
	else { $content = qq|<p>Authentication failed!</p>|; }
}
elsif (param('action') eq "register") {
	my $username = &getUserName;
	
	if ($username) { $content = &doRegistration; }
	else { $content = qq|<p>Invalid registration!</p>|; }
}
else { $content = qq|<p>No action was selected!</p>|; }

### HTML output ###

# Content is created by the above control logic

print header,
<<END_HTML;
<html>
<head><title>Top 5 Stocks</title></head>
<body>
<div align="center" style="color:#000000; font-family: Tahoma, helvetica, arial">
	<h1>Welcome to Top 5 Stocks!</h1>
	$content
	<p><a href="index.pl">Back to Main</a></p>
</div>
</body>
</html>
END_HTML

### Subroutines ###

# This is the heart of the program's security features
# We use the user name to pipe open files in a number of places, so we need to filter it carefully
# First, the username can only be one word long
# Next, accept only usernames with no dangerous characters
# If an illegal character is found, we don't bother trying to correct; reject right away
sub getUserName {
	my $tainted_username = uc param('username') || '';
	if ( scalar(split(/\s+/, $tainted_username)) > 1) { return ''; }
	if ( $tainted_username =~ /^([^\&;\`\\\"\|*\?\~\<\>\^\(\)\[\]\{\}\$\n\r\0]+)$/ ) { return $1; }
	else { return ''; }
}

# Authenticate user name and password
sub authenticate {
	my $username = &getUserName;
	if ($username eq '') { return 0; }
	my $password = param('password') || '';
	my $passhash = crypt($password, $salt);
	my %users = ();

	open(IN, "users.txt");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);
	
	while (my $line = <IN>) {
		my ($un, $ph) = split(/\s+/, $line);
		$users{$un} = $ph;
	}

	close(IN);
	if ($users{$username} eq $passhash) { return 1; }
	else { return 0; }
}

# This subroutine checks the stock symbol and number of shares for buy/sell actions
# If the stock symbol is not in the stocks file, reject
# If the number of shares contains anything other than numeric chars, reject
sub validate {
	my $input_symbol = shift;
	my $input_shares = shift;
	my %symbols = ();

	open(IN, "stocks.txt");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($name, $symbol, $price, $previousPrice) = split(/\s+/, $line);
		$symbols{$symbol} = 1;
	}

	close(IN);
	if ($symbols{$input_symbol} && $input_shares =~ /^[0-9]+$/) { return 1; }
	else { return 0; }
}

# Generate HTML to display user stock portfolio
sub showPortfolio {
	my $output = "";
	my %portfolio = ();
	my $username = &getUserName;

	open(IN, "$username.portfolio");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($symbol, $quantity) = split(/\s+/, $line);
		$portfolio{"$symbol"} = $quantity;
	}

	open(IN, "stocks.txt");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	$output = qq|<p>$username\'s stock portfolio.</p>
	<table bgcolor="#000000" border="0" cellpadding="2" cellspacing="2" style="font: 10pt"> 
		<tr align="center" style="background-color:#CCCCCC">
			<td><strong>Name</strong></td>
			<td><strong>Symbol</strong></td>
			<td><strong>Quantity</strong></td>
			<td><strong>Price</strong></td>
			<td><strong>Value</strong></td>
			<td><strong>Previous Price</strong></td>
			<td><strong>Previous Value</strong></td>
			<td><strong>Change</strong></td>
		</tr>\n|;

	while (my $line = <IN>) {
		my ($name, $symbol, $price, $previousPrice) = split(/\s+/, $line);

		if ($portfolio{"$symbol"}) {
			my $value = $portfolio{"$symbol"} * $price;
			my $previousValue = $portfolio{"$symbol"} * $previousPrice;
			my $change = &percentFormatted(($price - $previousPrice) / $previousPrice * 100);

			$output .= qq|		<tr align="center" style="background-color:#FFFFFF">
				<td>$name</td>
				<td>$symbol</td>
				<td>$portfolio{"$symbol"}</td>
				<td>\$$price</td>
				<td>\$$value</td>
				<td>\$$previousPrice</td>
				<td>\$$previousValue</td>
				<td>$change</td>
			</tr>\n|;
		}
	}

	close(IN);
	$output .= qq|	</table>|;
	return $output;
}

# Generate HTML to display user trade history
sub showHistory {
	my $output = "";
	my $username = &getUserName;

	open(IN, "$username.history");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	$output = qq|<p>$username\'s trade history.</p>
	<table bgcolor="#000000" border="0" cellpadding="2" cellspacing="2" style="font: 10pt"> 
		<tr align="center" style="background-color:#CCCCCC">
			<td><strong>Date</strong></td>
			<td><strong>Action</strong></td>
			<td><strong>Symbol</strong></td>
			<td><strong>Quantity</strong></td>
			<td><strong>Price</strong></td>
		</tr>\n|;

	while (my $line = <IN>) {
		my ($date, $action, $symbol, $quantity, $price) = split(/\s+/, $line);

		$output .= qq|		<tr align="center" style="background-color:#FFFFFF">
			<td>$date</td>
			<td>$action</td>
			<td>$symbol</td>
			<td>$quantity</td>
			<td>\$$price</td>
		</tr>\n|;
	}

	close(IN);
	$output .= qq|	</table>|;
	return $output;
}

# Update user stock portfolio
# If the newly traded stock was already in the portfolio, modify its quantity
# If the newly traded stock was not already in the portfolio, add it to portfolio
sub updatePortfolio {
	my $newSymbol = shift;
	my $newShares = shift;
	my %portfolio = ();
	my $username = &getUserName;

	open(IN, "$username.portfolio");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($symbol, $quantity) = split(/\s+/, $line);
		$portfolio{"$symbol"} = $quantity;
	}
	
	close(IN);
	if ($portfolio{"$newSymbol"}) { $portfolio{"$newSymbol"} = $portfolio{"$newSymbol"} + $newShares; }
	else { $portfolio{"$newSymbol"} = $newShares; }
	
	open(OUT, ">$username.portfolio");
	flock(OUT, LOCK_EX);
	seek(OUT, 0, SEEK_END);
	
	foreach my $symbol (sort keys %portfolio) {
		my $quantity = $portfolio{"$symbol"};
		print OUT "$symbol\t$quantity\n";
	}
	
	close(OUT);
}

# Update user trade history
sub updateHistory {
	my $username = &getUserName;
	my $action = shift;
	my $symbol = shift;
	my $shares = shift;
	my $price = &getPrice($symbol);

	open(OUT, ">>$username.history");
	flock(OUT, LOCK_EX);
	seek(OUT, 0, SEEK_END);

	my ($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
			
	$year += 1900;
	print OUT "$mon\/$mday\/$year\t$action\t$symbol\t$shares\t$price\n";
	close(OUT);
}

# Given a stock symbol, look up its current price from stocks file
sub getPrice {
	my $targetSymbol = shift;
	open(STOCKS, "stocks.txt");
	flock(STOCKS, LOCK_SH); 
	seek(STOCKS, 0, SEEK_SET);

	while (my $line = <STOCKS>) {
		my ($name, $symbol, $price, $previousPrice) = split(/\s+/, $line);
		if ($symbol eq $targetSymbol) { return $price; }
	}

	close(STOCKS);
	return 0.0;
}

# Handle new user registration
# If the user name already exists, reject
# Otherwise, add user name and hash of password to users file
# Add history file for user
# Add portfolio file for user
sub doRegistration {
	my $output = "";
	my $username = &getUserName;
	my $password = param('password') || '';
	my $passhash = crypt($password, $salt);		
	my %users = ();

	open(IN, "users.txt");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($un, $ph) = split(/\s+/, $line);
		$users{$un} = $ph;
	}

	close(IN);
		
	if ($users{$username}) { $output = qq|<p>User name already taken!</p>|; }
	else {		
		system("touch $username.history");
		system("chmod 600 $username.history");
			
		system("touch $username.portfolio");
		system("chmod 600 $username.portfolio");

		open(OUT, ">>users.txt");
		flock(OUT, LOCK_EX);
		seek(OUT, 0, SEEK_END);
			
		print OUT "$username\t$passhash\n";
		close(OUT);
		$output = qq|<p>Registration successful!</p>|;
	}

	return $output;
}

# Given a real number of arbitrary precision, convert it to 2-decimal places format
# Tacks on percentage sign at the end since it's only used for displaying percentages
# Courtesy of William Bontrager
sub percentFormatted {
	my $n = shift;
	my $minus = $n < 0 ? '-' : '';
	$n = abs($n);
	$n = int(($n + .005) * 100) / 100;
	$n .= '.00' unless $n =~ /\./;
	$n .= '0' if substr($n,(length($n) - 2),1) == '.';
	chop $n if $n =~ /\.\d\d0$/;
	return "$minus$n%";
}

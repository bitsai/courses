#!/usr/local/bin/perl -wT
use strict;
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use CGI::Pretty qw/:standard/;
use Fcntl qw(:flock :seek);

### Declarations ###

my $stocks = &getStocks;
my $symbols = &getSymbols;
my $users = &getUsers;

### HTML output ###

print header,
<<END_HTML;
<html>
<head><title>Top 5 Stocks</title></head>
<body>
<div align="center" style="color:#000000; font-family: Tahoma, helvetica, arial">
	<h1>Welcome to Top 5 Stocks!</h1>
	<p>Here you can see the status of all tracked stocks.</p>
	<table bgcolor="#000000" border="0" cellpadding="2" cellspacing="2" style="font: 10pt">
		<tr align="center" style="background-color:#CCCCCC">
			<td><strong>Name</strong></td>
			<td><strong>Symbol</strong></td>
			<td><strong>Price</strong></td>
			<td><strong>Previous Price</strong></td>
			<td><strong>Change</strong></td>
		</tr>
$stocks
	</table>
	<p>Registered users, you may choose an action from below.</p>
	<table bgcolor="#000000" border="0" cellpadding="2" cellspacing="2" style="font: 10pt">
		<form name="actionsForm" action="controller.pl" method=post>
			<tr align="center" style="background-color:#CCCCCC">
				<td><input type="radio" name="action" value="portfolio"></td>
				<td colspan="2"><strong>View Portfolio</strong></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td><input type="radio" name="action" value="history"></td>
				<td colspan="2"><strong>View History</strong></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td><input type="radio" name="action" value="buy"></td>
				<td colspan="2"><strong>Buy Stocks</strong></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td><input type="radio" name="action" value="sell"></td>
				<td colspan="2"><strong>Sell Stocks</strong></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td></td>
				<td><strong>Symbol:</strong></td> 
				<td>
					<select name="symbol">
						<option value="">-NONE-</option>
						$symbols
					</select>
				</td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td></td>
				<td><strong>Shares:</strong></td>
				<td><input type="text" name="shares" size="10" maxlength="10"></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td></td>
				<td><strong>User Name:</strong></td>
				<td>
					<select name="username">
						<option value="">-----------------NONE----------------</option>
						$users
					</select>
				</td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td></td>
				<td><strong>Password:</strong></td>
				<td><input maxlength="30" name="password" size="30" type="password"></td>
			</tr>

			<tr align="center" style="background-color:#FFFFFF">
				<td colspan="3"><input type="submit" value="Go!"></td>
			</tr>
		</form>
	</table>
	<p>New users, register here.</p>
	<p>User names are case-insensitive.</p>
	<p>Passwords are case-sensitive.</p>
	<table bgcolor="#000000" border="0" cellpadding="2" cellspacing="2" style="font: 10pt">
		<form name="registerForm" action="controller.pl" method=post>
			<input type="hidden" name="action" value="register">
		
			<tr align="center" style="background-color:#CCCCCC">
				<td><strong>User Name:</strong></td>
				<td><input maxlength="30" name="username" size="30" type="text"></td>
			</tr>

			<tr align="center" style="background-color:#CCCCCC">
				<td><strong>Password:</strong></td>
				<td><input maxlength="30" name="password" size="30" type="password"></td>
			</tr>

			<tr align="center" style="background-color:#FFFFFF">
				<td colspan="2"><input type="submit" value="Register!"></td>
			</tr>
		</form>
	</table>
</div>
</body>
</html>
END_HTML

### Subroutines ###

# Generate HTML table data from stocks information read from stocks file
sub getStocks {
	my $output = "";
	open(STOCKS, "stocks.txt");
	flock(STOCKS, LOCK_SH); 
	seek(STOCKS, 0, SEEK_SET);

	while (my $line = <STOCKS>) {
		my ($name, $symbol, $price, $previousPrice) = split(/\s+/, $line);
		my $change = &percentFormatted(($price - $previousPrice) / $previousPrice * 100);

		$output .= qq|		<tr align="center" style="background-color:#FFFFFF">
			<td>$name</td>
			<td>$symbol</td>
			<td>\$$price</td>
			<td>\$$previousPrice</td>
			<td>$change</td>
		</tr>\n|;
	}

	close(STOCKS);
	return $output;
}

# Generate list of valid stock symbol choices for drop-down menu
sub getSymbols {
	my $output = "";
	my %stocks = ();

	open(IN, "stocks.txt");
	flock(IN, LOCK_SH); 
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($name, $symbol, $price, $previousPrice) = split(/\s+/, $line);
		$stocks{$symbol} = 1;
	}

	close(IN);
	foreach my $symbol (sort keys %stocks) { $output .= qq|<option value="$symbol">$symbol</option>\n|; }
	return $output;
}

# Generate list of valid user name choices for drop-down menu
sub getUsers {
	my $output = "";
	my %users = ();

	open(IN, "users.txt");
	flock(IN, LOCK_SH);
	seek(IN, 0, SEEK_SET);

	while (my $line = <IN>) {
		my ($un, $ph) = split(/\s+/, $line);
		$users{$un} = 1;
	}

	close(IN);
	foreach my $un (sort keys %users) { $output .= qq|<option value="$un">$un</option>\n|; }
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

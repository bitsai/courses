use strict;

my $input = shift;
my $output = shift;

open INPUT, "$input";
open OUTPUT, ">$output";

my $num;
my $time;
my $srcIP;
my $dstIP;
my $size;
my $srcPort;
my $dstPort;
my $protocols;
my %protocols = ();

while (my $line = <INPUT>) {
	if ($line =~ /Protocol Info/) {
		$line = <INPUT>;
		&get_info($line);
		
		<INPUT>;
		$line = <INPUT>;
		&get_size($line);
		
		<INPUT>;
		$line = <INPUT>;

		if ($line =~ /Internet Protocol/) {
			$line = <INPUT>;

			if ($line =~ /Transmission Control Protocol/) {
				&get_ports($line);
				&print_data;
			}
		}
	}
}

foreach my $protocol (keys %protocols) {
	print "$protocol\n";
}

close OUTPUT;
close INPUT;

sub get_info {
	my $line = shift;

	$line =~ /^\s*(\S+)\s+(\S+)\s+(\S+)\s+(\S+)/;
	$num = $1;
	$time = $2;
	$srcIP = $3;
	$dstIP = $4;
}

sub get_size {
	my $line = shift;

	$line =~ /(\d+) bytes captured/;
	$size = $1;
}

sub get_ports {
	my $line = shift;

	$line =~ /Src Port: (.+) \((\d+)\), Dst Port: (.+) \((\d+)\)/;
	$srcPort = $2;
	$dstPort = $4;
	$protocols = "$1 -> $3";

	my $srcProtocol = $1;
	my $dstProtocol = $3;

	if ($srcProtocol =~ /[A-Za-z]/) { $protocols{$srcProtocol} = 1; }
	if ($dstProtocol =~ /[A-Za-z]/) { $protocols{$dstProtocol} = 1; }
}

sub print_data {
	print OUTPUT "\nNo. $num\n";
	print OUTPUT "$srcIP\n";
	print OUTPUT "$srcPort\n";
	print OUTPUT "$dstIP\n";
	print OUTPUT "$dstPort\n";
	print OUTPUT "$time\n";
	print OUTPUT "$size\n";
	print OUTPUT "$protocols\n";
}

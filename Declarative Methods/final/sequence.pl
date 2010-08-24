use strict;

my $input = shift;
my $output = shift;

open INPUT, "$input";
open OUTPUT, ">$output";

my %sequences = ();
my %directions = ();
my %times = ();
my %sizes = ();
my %protocols = ();

while (my $line = <INPUT>) {
	if ($line =~ /No\./) {
		my $srcIP = <INPUT>;
		my $srcPort = <INPUT>;
		my $dstIP = <INPUT>;
		my $dstPort = <INPUT>;
		my $time = <INPUT>;
		my $size = <INPUT>;
		my $protocols = <INPUT>;
		
		chomp($srcIP, $srcPort, $dstIP, $dstPort, $time, $size, $protocols);

		my $key = &get_key($srcIP, $srcPort, $dstIP, $dstPort);
		my $direction = &get_direction($srcIP, $srcPort, $dstIP, $dstPort);
		my $protocol = &get_protocol($protocols);
		&add_to_sequence($key, $direction, $time, $size, $protocol);
	}
}

&print_sequences;

close OUTPUT;
close INPUT;

sub get_key {
	my $srcIP = shift;
	my $srcPort = shift;
	my $dstIP = shift;
	my $dstPort = shift;

	if (&get_num($srcIP) < &get_num($dstIP)) { return "$srcIP:$srcPort <-> $dstIP:$dstPort"; }
	else { return "$dstIP:$dstPort <-> $srcIP:$srcPort"; }
}

sub get_num {
	my $IP = shift;
	
	$IP =~ /(\d+)\.(\d+)\.(\d+)\.(\d+)/;
	my $A = &pad_num($1);
	my $B = &pad_num($2);
	my $C = &pad_num($3);
	my $D = &pad_num($4);
	
	return "$A$B$C$D";
}

sub pad_num {
	my $num = shift;

	if (length $num == 1) { return "00$num"; }
	elsif (length $num == 2) { return "0$num"; }
	else { return $num; }
}

sub get_direction {
	my $srcIP = shift;
	my $srcPort = shift;
	my $dstIP = shift;
	my $dstPort = shift;

	my $sequence = "$srcIP:$srcPort <-> $dstIP:$dstPort";
	my $reverse = "$dstIP:$dstPort <-> $srcIP:$srcPort";

	if (exists $sequences{$sequence}) { return 1; }
	elsif (exists $sequences{$reverse}) { return -1; }
	else {
		$sequences{$sequence} = 1;
		return 1;
	}
}

sub get_protocol {
	my $protocols = shift;

	if ($protocols =~ /ftp / || $protocols =~ / ftp/ || $protocols =~ /ftp-data / || $protocols =~ / ftp-data/) {
		return "FTP";
	}
	elsif ($protocols =~ /smtp / || $protocols =~ / smtp/) { return "SMTP"; }
	elsif ($protocols =~ /telnet / || $protocols =~ / telnet/) { return "TELNET"; }
	elsif ($protocols =~ /http / || $protocols =~ / http/) { return "HTTP"; }
	else { return "NONE"; }
}

sub add_to_sequence {
	my $key = shift;
	my $direction = shift;
	my $time = shift;
	my $size = shift;
	my $protocol = shift;

	if (exists $directions{$key}) { push(@{$directions{$key}}, $direction); }
	else {
		my @array = ();
		push(@array, $direction);
		$directions{$key} = [@array];
	}

	if (exists $times{$key}) { push(@{$times{$key}}, $time); }
	else {
		my @array = ();
		push(@array, $time);
		$times{$key} = [@array];
	}

	if (exists $sizes{$key}) { push(@{$sizes{$key}}, $size); }
	else {
		my @array = ();
		push(@array, $size);
		$sizes{$key} = [@array];
	}

	$protocols{$key} = $protocol;
}

sub print_sequences {
	for my $seq ( keys %directions ) {
		print OUTPUT "\n$seq\n";
		print OUTPUT $#{ $directions{$seq} } + 1 ."\n";
		for my $index ( 0 .. $#{ $directions{$seq} } ) { print OUTPUT "$directions{$seq}[$index]\n"; }

		for my $index ( 0 .. $#{ $times{$seq} } ) {
			if ($index == 0) {
				print OUTPUT "0\n";
			}
			else {
				my $time = abs($times{$seq}[$index]) - abs($times{$seq}[$index - 1]);
				my $log = log $time;
				print OUTPUT "$log\n";
			}
		}

		for my $index ( 0 .. $#{ $sizes{$seq} } ) { print OUTPUT "$sizes{$seq}[$index]\n"; }
		print OUTPUT "$protocols{$seq}\n";
	}
}

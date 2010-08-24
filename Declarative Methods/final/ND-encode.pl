use strict;

my $input = shift;
open INPUT, "$input";

open FTP, ">FTP-$input";
open SMTP, ">SMTP-$input";
open TELNET, ">TELNET-$input";
open HTTP, ">HTTP-$input";
open NONE, ">NONE-$input";

my @directions = ();
my %times = ();
my %sizes = ();

while (my $line = <INPUT>) {
	if ($line =~ /<->/) {
		my $length = <INPUT>;
		chomp $length;
		
		my $previous;
		&reset;

		foreach my $i (1..$length) {
			my $direction = <INPUT>;
			chomp $direction;
			push(@directions, $direction);
		}

		foreach my $i (1..$length) {
			my $time = <INPUT>;
			chomp $time;
			my $current = &bin_time($time);
			if ($i > 1) { $times{"$previous -> $current"}++; }
			$previous = $current;
		}

		foreach my $i (1..$length) {
			my $size = <INPUT>;
			chomp $size;
			my $current = &bin_size($size);
			if ($i > 1) { $sizes{"$previous -> $current"}++; }
			$previous = $current;
		}

		my $protocol = <INPUT>;
		chomp $protocol;

		if ($protocol eq "FTP") {
			print FTP "  $length";
			foreach my $key (sort keys %times) { print FTP " ".&format_num($times{$key} / $length); }
			foreach my $key (sort keys %sizes) { print FTP " ".&format_num($sizes{$key} / $length); }
			print FTP " 0\n";
		}
		elsif ($protocol eq "SMTP") {
			print SMTP "  $length";
			foreach my $key (sort keys %times) { print SMTP " ".&format_num($times{$key} / $length); }
			foreach my $key (sort keys %sizes) { print SMTP " ".&format_num($sizes{$key} / $length); }
			print SMTP " 1\n";
		}
		elsif ($protocol eq "TELNET") {
			print TELNET "  $length";
			foreach my $key (sort keys %times) { print TELNET " ".&format_num($times{$key} / $length); }
			foreach my $key (sort keys %sizes) { print TELNET " ".&format_num($sizes{$key} / $length); }
			print TELNET " 2\n";
		}
		elsif ($protocol eq "HTTP") {
			print HTTP "  $length";
			foreach my $key (sort keys %times) { print HTTP " ".&format_num($times{$key} / $length); }
			foreach my $key (sort keys %sizes) { print HTTP " ".&format_num($sizes{$key} / $length); }
			print HTTP " 3\n";
		}
		elsif ($protocol eq "NONE") {
			print NONE "  $length";
			foreach my $key (sort keys %times) { print NONE " ".&format_num($times{$key} / $length); }
			foreach my $key (sort keys %sizes) { print NONE " ".&format_num($sizes{$key} / $length); }
			print NONE " 4\n";
		}
	}
}

close OUTPUT;
close INPUT;

sub reset {
	@directions = ();

	foreach my $i (0..23) {
		foreach my $j (0..23) {
			$times{"$i -> $j"} = 0;
			$sizes{"$i -> $j"} = 0;
		}
	}
}

sub bin_time {
	my $time = shift;

	if ($time =~ /(.+)\.\d+/) {
		if ($time < 0) { return $1 + 12 - 1; }
		else { return $1 + 12; }
	}
	else {
		if ($time < 0) { return $time + 12 - 1; }
		else { return $time + 12; }
	}
}

sub bin_size {
	my $size = shift;
	my $temp = $size / 64;

	if ($temp =~ /(.+)\.\d+/) { return $1; }
	else { return $temp; }
}

sub format_num {
	my $num = shift;

	if (length $num >= 6) {
		$num =~ /(\d\.\d{4})\d*/;
		return $1;
	}
	elsif (length $num == 5) { return $num."0"; }
	elsif (length $num == 4) { return $num."00"; }
	elsif (length $num == 3) { return $num."000"; }
	else { return $num.".0000"; }
}
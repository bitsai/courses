#!/usr/local/bin/perl -w

my %durations = ();
my @precedences = ();

my $input = "rcps.data";
my $output = "problem1.ecl";

&processData;
&writeProgram;

sub processData {
	my $mode = "none";

	open(DATA, "$input");

	while (my $line = <DATA>) {
		chomp($line);
	
		if ($line =~ /section1/) {
			$mode = "section1";
		}
		elsif ($line =~ /section2/) {
			$mode = "section2";
		}
		elsif ($line =~ /section3/) {
			$mode = "none";
		}
		else {
			if ($line eq "") {}
			elsif ($mode eq "section1") {
				&processSection1($line);
			}
			elsif ($mode eq "section2") {
				push(@precedences, $line);
			}
		}
	}

	close(DATA);
}

sub processSection1 {
	my @tokens = split(/\s/, shift);
	$tokens[0] =~ /asm_1\.(.+)/;
	my $activity = $1;
	$tokens[1] =~ /([\d]+):([\d]+)/;
	my $duration = $1 * 60 + $2;
	$durations{$activity} = $duration;
}

sub writeProgram {
	open(PROGRAM, ">$output");
	
	print PROGRAM ":- lib(ic).\n";
	print PROGRAM ":- lib(branch_and_bound).\n";
	print PROGRAM ":- lib(ic_edge_finder).\n\n";
	
	print PROGRAM "schedule(EndTime) :-\n";
	
	### Start & Finish constraints ###

	my @startTimes = sort keys %durations;
	my @finishTimes = sort keys %durations;
	
	for (my $index = 0; $index < scalar @startTimes; $index++) { $startTimes[$index] = "S_".$startTimes[$index]; }
	for (my $index = 0; $index < scalar @finishTimes; $index++) { $finishTimes[$index] = "F_".$finishTimes[$index]; }
	
	my $startTimes = join(", ", @startTimes);
	my $finishTimes = join(", ", @finishTimes);

	print PROGRAM "\t% Start & Finish constraints\n";
	
	print PROGRAM "\tStartTimes = [$startTimes],\n";
	print PROGRAM "\tFinishTimes = [$finishTimes],\n\n";
	
	print PROGRAM "\tStartTimes :: 0..1000000,\n";
	print PROGRAM "\tFinishTimes :: 0..1000000,\n";
	
	### Duration constraints ###
	
	print PROGRAM "\n\t% Duration constraints\n";
	
	foreach my $activity (sort keys %durations) {
		my $duration = $durations{$activity};
		print PROGRAM "\tF_$activity - S_$activity #= $duration,\n";
	}
	
	### Precedence constraints ###
	
	print PROGRAM "\n\t% Precedence constraints\n";
	
	foreach my $precedence (@precedences) {
		$precedence =~ /asm_1\.(.+) asm_1\.(.+)/;
		print PROGRAM "\tS_$2 #>= F_$1,\n";
	}
	
	### Computation instructions ###
	
	print PROGRAM "\n\tEndTime #= max(FinishTimes),\n";
	print PROGRAM "\tflatten([StartTimes, FinishTimes, EndTime], AllVars),\n";
	print PROGRAM "\tminimize(labeling(AllVars), EndTime),\n";
	
	### Output solution ###
	
	print PROGRAM "\n\t% Output solution\n";
	
	foreach my $activity (sort keys %durations) {
		print PROGRAM "\tprintf(\"asm_1.$activity %d%n\", S_$activity),\n";
	}
	
	print PROGRAM "\tprintf(\"EndTime %d%n\", EndTime).\n";
	
	close(PROGRAM);
}

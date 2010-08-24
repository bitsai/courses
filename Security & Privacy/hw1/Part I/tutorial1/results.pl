#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;
use Fcntl qw(:flock :seek);

my $outfile = "poll.out";

print header;
print start_html("Results");

# open the file for reading
open(IN, "$outfile") or &dienice("Couldn't open $outfile: $!");
# set a shared lock
flock(IN, LOCK_SH); 
# then seek the beginning of the file
seek(IN, 0, SEEK_SET);

# declare the totals variables
my($total_votes, %results);
# initialize all of the counts to zero:
foreach my $i ("fotr", "ttt", "rotk", "none") {
   $results{$i} = 0;
}

# now read the file one line at a time:
while (my $rec = <IN>) {
   chomp($rec);
   $total_votes = $total_votes + 1;
   $results{$rec} = $results{$rec} + 1;
}
close(IN);

# now display a summary:
print <<End;
<b>Which was your favorite <i>Lord of the Rings</i> film?
</b><br>
<table border=0 width=50%>
<tr>
  <td>The Fellowship of the Ring</td>
  <td>$results{fotr} votes</td>
</tr>
<tr>
  <td>The Two Towers</td>
  <td>$results{ttt} votes</td>
</tr>
<tr>
  <td>Return of the King</td>
  <td>$results{rotk} votes</td>
</tr>
<tr>
  <td>didn't watch them</td>
  <td>$results{none} votes</td>
</tr>
</table>
<p>
$total_votes votes total
</p>
End

print end_html;

sub dienice {
    my($msg) = @_;
    print h2("Error");
    print $msg;
    print end_html;
    exit;
}

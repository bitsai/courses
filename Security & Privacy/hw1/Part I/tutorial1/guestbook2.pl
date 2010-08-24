#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

print header;
print start_html("Results");

# first print the mail message...

$ENV{PATH} = "/usr/lib";
open (MAIL, "|/usr/lib/sendmail -oi -t -odq") or 
   &dienice("Can't fork for sendmail: $!\n");
print MAIL "To: benny.tsai\@gmail.com\n";
print MAIL "From: benny.tsai\@gmail.com\n";
print MAIL "Subject: Form Data\n\n";
foreach my $p (param()) {
    print MAIL "$p = ", param($p), "\n";
}
close(MAIL);

# now write (append) to the file

open(OUT, ">>guestbook.txt") or &dienice("Couldn't open output file: $!");
foreach my $p (param()) {
    my $value = param($p);
    $value =~ s/\n/ /g;     # replace newlines with spaces
    $value =~ s/\r//g;      # remove hard returns
    print OUT "$p = $value,";
}
print OUT "\n";
close(OUT);

print <<EndHTML;
<h2>Thank You</h2>
<p>Thank you for writing!</p>
<p>Return to our <a href="index.html">home page</a>.</p>
EndHTML

print end_html;

sub dienice {
    my($errmsg) = @_;
    print "<h2>Error</h2>\n";
    print "<p>$errmsg</p>\n";
    print end_html;
    exit;
}

#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

print header;
print start_html("Results");

my $message = "";

foreach my $p (param()) {
    $message .= "$p = ".param($p)."\n";
}

&sendmail('benny.tsai@gmail.com', 'benny.tsai@gmail.com', "Form Data", $message);

# Now print a thank-you page 

print <<EndHTML;
<h2>Thank You</h2>
<p>Thank you for writing!</p>
<p>$message</p>
<p>Return to our <a href="index.html">home page</a>.</p>
EndHTML

print end_html;

# The sendmail subroutine

sub sendmail {
    my ($from, $to, $subject, $message) = @_;
    $ENV{PATH} = "/usr/lib";
    open (MAIL, "|/usr/lib/sendmail -oi -t") or 
        &dienice("Can't fork for sendmail: $!\n");
    print MAIL "To: $to\n";
    print MAIL "From: $from\n";
    print MAIL "Subject: $subject\n\n";
    print MAIL "$message\n";
    close(MAIL);
}

# The dienice subroutine handles errors.

sub dienice {
    my($errmsg) = @_;
    print "<h2>Error</h2>\n";
    print "<p>$errmsg</p>\n";
    print end_html;
    exit;
}

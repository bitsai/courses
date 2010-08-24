#!/usr/local/bin/perl

#
# This program uses sockets to connect to a web server, retrieve
# the index file from the site requested on the command line, and 
# prints that file including all headers to STDOUT
#
# Example:
#
#    tcp_client.pl www.cs.jhu.edu
#

use Socket;


if ($#ARGV != 0) {
    print << "EndOfHelp";

  usage: $0 <web server address>

  ex     $0 www.cs.jhu.edu
EndOfHelp
    ;
}

#
# connect to the web server or die
#

if (open_tcp( HANDLE, $ARGV[0], 80 ) == undef) {
    print "Error connecting to server at $ARGV[0]\n";
    exit( -1 );
}

#
# send the GET request for the root file (usually index.html)
# and report what flavor of HTTP we are expecting. Then print
# to STDOUT the retrieved page (in ascii unmodified).
#

print HANDLE "GET / HTTP/1.0\n\n";
print $_ while( <HANDLE> );


close (HANDLE);    # close the connection (to be nice).


sub open_tcp {

    my $handle = shift || return undef; # file handle for IO operations
    my $dest   = shift || return undef; # who we are trying to contact
    my $port   = shift || return undef; # on what port are we trying to make a 
                                        # connection

    
    #
    # Determine what communications protocol we are using. Here it is
    # TCP/IP. Then translate the address into integer representation
    # (i.e. 127.0.0.1)
    #

    my $proto = getprotobyname( 'tcp' );
    my $saddr = sockaddr_in( $port, inet_aton( $dest ));

    # 
    # create a socket using 'streams' via TCP/IP then bind 
    # $handle to the socket.
    #

    socket( $handle, PF_INET, SOCK_STREAM, $proto );
    connect $handle, $saddr || return undef;

    my $old_handle = select( $handle );      # turn stream buffering off for
    $| = 1;                                  # $handle
    select( $old_handle );

    1;
}
    

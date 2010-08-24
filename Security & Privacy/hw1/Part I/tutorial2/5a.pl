#!/usr/local/bin/perl -wT
use strict;
use CGI   qw/:standard/;
use CGI::Carp   qw/fatalsToBrowser/;

use constant CONFIG => './weird_sports.txt';

my $config = do( CONFIG );

my $tainted_username = param( 'username' ) || '';
my $user_digest      = create_digest( param('password'), $config->{salt} );
my $tainted_remember = param( 'remember' ) || '';

my $username = '';
my $message = 'Your username and password information did not match.'
              . 'Make sure your Caps Lock if off and try again.';

if ( $tainted_username =~ /^([a-zA-Z\d_]+)$/ )
{
    $username = $1;
}
else
{
    display_page( $message );
    exit;
}

my $userfile = $config->{ users } . $username . ".txt";

open USER, "$userfile" or display_page( $message ), exit;
chomp ( my ( $real_digest, $sessionID, $remember ) = <USER> );
close USER;

if ( $user_digest eq $real_digest )
{
    $message = "Hello, $username.  You gave me a good password";
}

display_page( $message );
exit;

sub display_page
{
    my $message = shift;
    print
        header,
        start_html( "-title" => "Login in Results"),
        p( $message ),
        end_html;
}

sub create_digest
{
    my $password = shift || '';
    my $salt     = shift;
    return crypt( $password, $salt );
}
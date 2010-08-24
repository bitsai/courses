#!/usr/bin/perl -w

# Author: Jason Jankoski
# This file verifies the user and creates the web pages as needed. If the user is 
# valid it will print out the portfolio based on the user's info in pf.dat.
# Else, it continues requesting the login 
# data.

use CGI;

#file that writes most of the static html
require "pageWriter.pl";

$query = new CGI;
print $query->header;

if($query->param())
{
#if input is incorrect this will populate
  $error_message = "";

#checks user and password against userlist
  &validateLogin;

  if($error_message eq "")
  {
    &getShares;
#no errors, go ahead
    &printHtmlBegin("Portfolio");
    &printPort;    
    $total = ($anum * 42.11) + ($hnum * 28.66) + ($unum * 37.73);
    $total += ($onum * 20.16) + ($pnum * 11.74) + ($enum * 10.39);
    &printShares($anum, $hnum, $unum, $onum, $pnum, $enum, $total);
  }
  else
  {
#errors, print them and reload login page
    $errorHeader = '<tr>
	<td width="15" height="60" colspan="2"></td>
	<td nowrap height="60" colspan="16" valign="top" bgcolor"#FFFFFF">
	<span class="text"><b><font size="2">
	<span style="font-size:14px;line-height:17px;">';
    $errorFooter = '</span></font></b><span style="font-size:10px;line-height:13px;">
	<td width="20" height="60" colspan="2"></td>
	</tr>';
    &printHtmlBegin("Invalid Logon Attempt");
    &printPortEntry;
    print $errorHeader;
    print $error_message;
    print $errorFooter;
    &printPortEntryEnd;
  }
}
else
{
#nothing sent, just reload login page
  &set_initial_values;
  &printHtmlBegin("Login");
  &printEntry;
}
&printHtmlEnd;


sub set_initial_values
{
  $id = $query->param('id');
  $password = $query->param('password');
}

#validates user based on info in the userlist
sub validateLogin
{
  $id = $query->param('id');
  $password = $query->param('password');
  open(U, "<userlist");

  while (<U>)
  {
    ($uid, $pass) = split();
    if((length($uid) < 1) || (length($pass) < 1))
    {
	next;
    }
    if(($uid eq $id) && ($pass eq $password)) 
    {
	return 1;
    } 
  }

  $error_message .= "<br>User Id and password do not match saved data.\n";
}

#reads share info for the user from pf.dat
sub getShares
{
  $id = $query->param('id');
  $file = "pf.dat";
  open (PF,"+>>pf.dat") || die("Can't open $file");
  flock(PF, 2);
  
  seek(PF, 0, 0);
  @pf = <PF>;

  foreach $pf (@pf)
  {
    chomp $pf;
    ($Id, $Anum, $Hnum, $Unum, $Onum, $Pnum, $Enum) = split(/\t/, $pf);
    if($id eq $Id)
    {
      $anum = $Anum;
      $hnum = $Hnum;
      $onum = $Onum;
      $unum = $Unum;
      $pnum = $Pnum;
      $enum = $Enum;
      last;
    }
  }
  close(PF);
      
}


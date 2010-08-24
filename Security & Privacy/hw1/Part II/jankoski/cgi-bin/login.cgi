#!/usr/bin/perl

# Author: Jason Jankoski
# This file verifies the user and creates the web pages as needed. If the user is 
# valid it will print out the mock portfolio. Else, it continues requesting the login 
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
#no errors, go ahead
    &printHtmlBegin("Valid Logon");
    &printPortfolio;    
  }
  else
  {
#errors, maintain state and reload login page
    $errorHeader = '<tr>
	<td width="15" height="60" colspan="2"></td>
	<td nowrap heaight="60" colspan="16" valign="top" bgcolor"#FFFFFF">
	<span class="text"><b><font size="2">
	<span style="font-size:14px;line-height:17px;">';
    $errorFooter = '</span></font></b><span style="font-size:10px;line-height:13px;">
	<td width="172" height="60" colspan="2"></td>
	</tr>';
    &printHtmlBegin("Invalid Logon Attempt");
    &printLoginBegin;
    print $errorHeader;
    print $error_message;
    print $errorFooter;
    &printLoginEnd;
  }
}
else
{
#nothing sent, just reload login page
  &set_initial_values;
  &printHtmlBegin("Login");
  &printLoginBegin;
  &printLoginEnd;
}
&printHtmlEnd;


sub set_initial_values
{
  $id = $query->param('id');
  $password = $query->param('password');
}

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

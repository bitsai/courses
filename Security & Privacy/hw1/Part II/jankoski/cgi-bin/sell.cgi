#!/usr/bin/perl -w

# Author: Jason Jankoski
# This file validates the user and allows said user to sell
# stocks. If the user's credential information is wrong it will
# regenerate the sell page. Otherwise the users updated portfolio is shown
# immediately following. If the user tries to sell unowned stocks the 
# page will generate an error and reload the sell page.
# This script is dependent upon the data read pf.dat in order to
# generate the user's current portfolio.

use CGI;

#file that writes most of the static html
require "pageWriter.pl";

$query = new CGI;
print $query->header;

if($query->param())
{
#if input is incorrect this will populate
  $error_message = "";
  $stock_error = "";

#checks user and password against userlist
  &validateLogin;

  if($error_message eq "")
  {
    &sell;
    if($stock_error eq "")
    {
#no errors, go ahead
      &printHtmlBegin("Stocks Sold");
      &printPort;    
      $total = ($anum * 42.11) + ($hnum * 28.66) + ($unum * 37.73);
      $total += ($onum * 20.16) + ($pnum * 11.74) + ($enum * 10.39);
      &printShares($anum, $hnum, $unum, $onum, $pnum, $enum, $total);
    }
    else
    {
#errors, print them out and reload the page
      $errorHeader = '<tr>
	<td width="15" height="60" colspan="2"></td>
	<td nowrap height="60" colspan="16" valign="top" bgcolor"#FFFFFF">
	<span class="text"><b><font size="2">
	<span style="font-size:14px;line-height:17px;">';
      $errorFooter = '</span></font></b><span style="font-size:10px;line-height:13px;">
	<td width="20" height="60" colspan="2"></td>
	</tr>';
      &printHtmlBegin("Invalid Trade");
      &printSellBegin;
      print $errorHeader;
      print $stock_error;
      print $errorFooter;
      &printSellEnd;
    }
  }
  else
  {
#errors, print them out  and reload the page
    $errorHeader = '<tr>
	<td width="15" height="60" colspan="2"></td>
	<td nowrap height="60" colspan="16" valign="top" bgcolor"#FFFFFF">
	<span class="text"><b><font size="2">
	<span style="font-size:14px;line-height:17px;">';
    $errorFooter = '</span></font></b><span style="font-size:10px;line-height:13px;">
	<td width="20" height="60" colspan="2"></td>
	</tr>';
    &printHtmlBegin("Invalid Logon Attempt");
    &printSellBegin;
    print $errorHeader;
    print $error_message;
    print $errorFooter;
    &printSellEnd;
  }
}
&printHtmlEnd;

#compares user login input to the data found in userlist
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

#grabs user input as to which stocks to sell
# and updates the pf.dat containing user information
#throws an error if invalid sale is attempted
sub sell
{
  $anum = $query->param('ACDOnum');
  $hnum = $query->param('HELEnum');
  $unum = $query->param('USFCnum');
  $onum = $query->param('OPTNnum');
  $pnum = $query->param('PLXTnum');
  $enum = $query->param('EVSTnum');
  $id = $query->param('id');
  $file = "pf.dat";
  open (PF,"+>>pf.dat") || die("Can't open $file");
  flock(PF, 2);
  
  seek(PF, 0, 0);
  @pf = <PF>;
  @new_pf = ();

  foreach $pf (@pf)
  {
    chomp $pf;
    ($Id, $Anum, $Hnum, $Unum, $Onum, $Pnum, $Enum) = split(/\t/, $pf);
    if($id eq $Id)
    {
      
      $Anum -= $anum;
      $Hnum -= $hnum;
      $Unum -= $unum;
      $Onum -= $onum;
      $Pnum -= $pnum;
      $Enum -= $enum;
      $anum = $Anum;
      $hnum = $Hnum;
      $onum = $Onum;
      $unum = $Unum;
      $pnum = $Pnum;
      $enum = $Enum;
      if(($anum < 0) || ($hnum < 0) || ($unum < 0) || ($pnum < 0) || ($enum < 0))
      {
        $stock_error = "Your transaction could not be processed. The trade would have made a negative value.";
	return;
      }

      $pf = "$Id\t$Anum\t$Hnum\t$Unum\t$Onum\t$Pnum\t$Enum";
    }
    $pf .= "\n";
    push(@new_pf, $pf);
  }
  seek(PF, 0, 0);
  truncate(PF, 0);
  print PF @new_pf;
  close(PF);
      
}


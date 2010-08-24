#!/usr/bin/perl

# Author: Jason Jankoski
# This file registers the user, generates and saves credentials, and sends an email to 
# the user with the credentials

use CGI;

# general html writer for the static data
require "pageWriter.pl";

$query = new CGI;

print $query->header;


if($query->param())
{
  $error_message = "";
  &validateRegistration;
  if($error_message eq "")
  {
#no errors - display success page
    &success;
  }
  else
  {
#errors - display them while maintaining state
    $errorHeader = '<tr>
        <td width="15" height="60" colspan="2"></td>
        <td nowrap height="60" colspan="16" valign="top" bgcolor="#FFFFFF">
        <span class="text"><b><font size="2">
        <span style="font-size:14px;line-height:17px;">';
    $errorFooter = '</span></font></b><span style="font-size:10px;line-height:13px;">
        <td width="172" height="60" colspan="2"></td>
        </tr>';
    &printHtmlBegin("Validate Information before Submission");
    &printHtmlRegistrationBegin;
    print $errorHeader;
    print $error_message;
    print $errorFooter;
    &printHtmlRegistrationEnd;
  }
}
else
{
#no input reload
  &set_initial_values;
  &printHtmlRegistrationBegin;
  &printHtmlRegistrationEnd;
}
&printHtmlEnd;

sub set_initial_values
{
  $fname = "";
  $lname = "";
  $saddress = "";
  $zip = "";
  $email = "";
  $ccn = "";
  $expMonth = "";
  $expYear = "";
  $cType = "";
  $citizen = "";
}

#ensure all fields were entered
sub validateRegistration
{
  #retrieve values from the form
  
  $fname = $query->param('fname');
  $lname = $query->param('lname');
  $saddress = $query->param('sAddress');
  $zip = $query->param('zip');
  $email = $query->param('email');
  $ccn = $query->param('ccn');
  $expMonth = $query->param('expMonth');
  $expYear = $query->param('expYear');
  $cType = $query->param('cType');
  $citizen = $query->param('citizen');
  
  #make sure values were entered
  
  if($fname eq "")
  {
    $error_message .= "<br>You must enter a first name.\n";
  }
  if($lname eq "")
  {
    $error_message .= "<br>You must enter a last name.\n";
  }
  if($saddress eq "")
  {
    $error_message .= "<br>You must enter a street address.\n";
  }
  if($zip eq "")
  {
    $error_message .= "<br>You must enter a zipcode.\n";
  }
  if($email eq "")
  {
    $error_message .= "<br>You must enter an email address.\n";
  }
  if($ccn eq "")
  {
    $error_message .= "<br>You must enter a credit card number.\n";
  }
  if($expMonth eq "")
  {
    $error_message .= "<br>You must enter an expiration month.\n";
  }
  if($expYear eq "")
  {
    $error_message .= "<br>You must enter an expiration year.\n";
  }
  if($cType eq "")
  {
    $error_message .= "<br>You must enter the type of credit card.\n";
  }
  if($citizen eq "")
  {
    $error_message .= "<br>You must answer the citizenship question.\n";
  }
  elsif($citizen eq "No")
  {
    $error_message .= "<br>Sorry you must be a citizen.\n";
  }
  if($error_message eq "")
  {
    $id = $fname;
    $id .= "_";
    $id .= $lname;
    
    open (U, "<userlist");
    
    while(<U>)
    {
      ($uid, $pass) = split();
      if(length($uid) < 1)
      {
        next;
      }
      if($uid eq $id)
      {
        $error_message .= "<br>You are already registered, please login.\n";
	$uid = "";
	$pass = "";
	$id = "";        
	last;
      }
    }
  }
}

sub success
{
  &printHtmlBegin("Thank you");
  print '
  <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="4">
      <col width="6">
      <col width="395">
      <col width="15">
      <col width="55">
      <col width="27">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="9"></td>
      </tr>
      <tr>
        <td height="591" rowspan="7" valign="top"><img src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" width="135" height="591"></td>
        <td width="624" height="1" colspan="8"></td>
      </tr>
      <tr>
        <td width="502" height="6" colspan="6"></td>
        <td nowrap height="591" colspan="2" rowspan="7" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="10" height="30" colspan="2"></td>
        <td nowrap height="30" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Application Submitted<br soft></span></font></b></span></td>
        <td width="97" height="30" colspan="3"></td>
      </tr>
      <tr>
        <td width="10" height="1" colspan="2"></td>
        <td height="1" colspan="3" valign="top"><img src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" width="465" height="1"></td>
        <td width="27" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="6"></td>
      </tr>
      <tr>
        <td width="4" height="131"></td>
        <td nowrap height="131" colspan="3" valign="top" bgcolor="#FFFFFF"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Thank you.<br><br>Due to the sensitive nature of the information submitted. <br soft>Your application must be reviewed by one of our customer <br soft>associates. You will be emailed shortly with your account <br soft>information.<br soft></span></font></b></span></td>
        <td width="82" height="131" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="413" colspan="6"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="7"></td>
      </tr>
      <tr> <!-- workaround for IE table layout bug -->
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
    </table>
    <br><br>
';

$userid = $fname . "_" . $lname;
$pass = "nowammy";
$pass .= $expMonth;

open(DAT,">>userlist") || die("Cannot Open User File");
flock(DAT, 2) || die("Cannot lock user file");
print DAT "$userid $pass\n";
close(DAT) || die("Cannot close user file");
$file = "pf.dat";

#create initial portfolio 
open(PF, ">> $file") || die("Cannot open .pf file");
flock(PF, 2) || die("Cannot lock .pf file");

print PF "$userid";
print PF "\t0\t0\t0\t0\t0\t0\n";
close(PF) || die("Cannot close .pf file");

#mail user credentials
open(SM, "|/usr/sbin/sendmail -f customerService\@acme.brokerage.com $email") || die("Cannot send mail");
print SM "To: $mail\n";
print SM "From: customerService\@acme.com\n";
print SM "Subject: Account\n";
print SM "Your account has been approved. \n\t Username: $userid \n\t Password: $pass\n";
close(SM);
}

sub printHtmlRegistrationBegin
{
  print '  <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
<form name="form0" method="POST" action="/cgi-bin/register.cgi">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="13">
      <col width="2">
      <col width="45">
      <col width="20">
      <col width="10">
      <col width="2">
      <col width="59">
      <col width="14">
      <col width="14">
      <col width="1">
      <col width="24">
      <col width="24">
      <col width="10">
      <col width="45">
      <col width="10">
      <col width="2">
      <col width="20">
      <col width="15">
      <col width="148">
      <col width="24">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="23"></td>
      </tr>
      <tr>
        <td height="591" rowspan="48" valign="top"><img src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" width="135" height="591"></td>
        <td width="624" height="1" colspan="22"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="20"></td>
        <td nowrap height="591" colspan="2" rowspan="48" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="13" height="30"></td>
        <td nowrap height="30" colspan="12" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Create New Account<br soft></span></font></b></span></td>
        <td width="264" height="30" colspan="7"></td>
      </tr>
      <tr>
        <td width="13" height="1"></td>
        <td height="1" colspan="18" valign="top"><img src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" width="465" height="1"></td>
        <td width="24" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="17" colspan="20"></td>
      </tr>
      <tr>
        <td width="15" height="60" colspan="2"></td>
        <td nowrap height="60" colspan="16" valign="top" bgcolor="#FFFFFF"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Please enter all the requested information <br soft>in order to open an account.<br></span></font></b><span style="font-size:10px;line-height:13px;">*Please note, this service is only available to residents and <br soft>citizens of the United States.<br soft></span></span></td>
        <td width="172" height="60" colspan="2"></td>
      </tr> ';
}

sub printHtmlRegistrationEnd
{
  print '<tr>
        <td width="502" height="30" colspan="20"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">First Name<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td height="22" colspan="6" rowspan="2" valign="top"><input name="fname" value="';
  print "$fname";
  print '" size="15"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="5" colspan="8"></td>
        <td width="219" height="5" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="8" colspan="20"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Last Name<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td height="22" colspan="6" rowspan="2" valign="top"><input name="lname" value="';
  print "$lname";
  print '" size="15"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="5" colspan="8"></td>
        <td width="219" height="5" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="28" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td height="22" colspan="6" rowspan="3" valign="top"><input name="sAddress" value="';
  print "$saddress";
  print '" size="15"></td>
        <td width="219" height="3" colspan="6"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Street Address<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td width="219" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="8" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td height="22" colspan="6" rowspan="3" valign="top"><input name="zip" value="';
  print "$zip";
  print '" size="15"></td>
        <td width="219" height="3" colspan="6"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Zipcode<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td width="219" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="8" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td height="22" colspan="6" rowspan="3" valign="top"><input name="email" value="';
  print "$email";
  print '" size="15"></td>
        <td width="219" height="3" colspan="6"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Email Address<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td width="219" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="36" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td height="22" colspan="6" rowspan="3" valign="top"><input name="ccn" value="" size="15"></td>
        <td width="219" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Credit Card Number<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="219" height="17" colspan="6"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td width="219" height="3" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="8" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td height="22" colspan="4" rowspan="3" valign="top"><select size="1" name="expMonth">
<option value="1">01</option>
<option value="2">02</option>
<option value="3">03</option>
<option value="4">04</option>
<option value="5">05</option>
<option value="6">06</option>
<option value="7">07</option>
<option value="8">08</option>
<option value="9">09</option>
<option value="10">10</option>
<option value="11">11</option>
<option value="12">12</option>
</select>
</td>
        <td height="22" colspan="4" rowspan="3" valign="top"><select size="1" name="expYear">
<option value="1">/01</option>
<option value="2">/02</option>
<option value="3">/03</option>
<option value="4">/04</option>
<option value="5">/05</option>
<option value="6">/06</option>
<option value="7">/07</option>
<option value="8">/08</option>
<option value="9">/09</option>
<option value="10">/10</option>
<option value="11">/11</option>
<option value="12">/12</option>
</select>
</td>
        <td width="207" height="2" colspan="4"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Expiration Date<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="207" height="17" colspan="4"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td width="207" height="3" colspan="4"></td>
      </tr>
      <tr>
        <td width="502" height="3" colspan="20"></td>
      </tr>
      <tr>
        <td width="165" height="2" colspan="8"></td>
        <td height="22" colspan="7" rowspan="3" valign="top"><select size="1" name="cType">
<option>Select Type</option>
<option value="MC">MasterCard</option>
<option value="Visa">Visa</option>
<option value="Discover">Discover</option>
</select>
</td>
        <td width="209" height="2" colspan="5"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="5" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Card Type<br soft></span></font></span></td>
        <td width="14" height="17"></td>
        <td width="209" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="165" height="3" colspan="8"></td>
        <td width="209" height="3" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="31" colspan="20"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="15" valign="top" bgcolor="#FFFFFF"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Are you a citizen of the United States? &nbsp;<br soft></span></font></b></span></td>
        <td width="187" height="17" colspan="3"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="20"></td>
      </tr>
      <tr>
        <td width="60" height="2" colspan="3"></td>
        <td height="20" rowspan="3" valign="top"><input type="radio" name="citizen" value="Yes"></td>
        <td width="422" height="2" colspan="16"></td>
      </tr>
      <tr>
        <td width="60" height="17" colspan="3"></td>
        <td width="10" height="17"></td>
        <td nowrap height="17" colspan="5" valign="top" bgcolor="#FFFFFF"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">Yes<br soft></span></font></span></td>
        <td width="322" height="17" colspan="10"></td>
      </tr>
      <tr>
        <td width="60" height="1" colspan="3"></td>
        <td width="422" height="1" colspan="16"></td>
      </tr>
      <tr>
        <td width="502" height="10" colspan="20"></td>
      </tr>
      <tr>
        <td width="60" height="17" colspan="3"></td>
        <td height="20" rowspan="2" valign="top"><input type="radio" name="citizen" value="No"></td>
        <td width="10" height="17"></td>
        <td nowrap height="17" colspan="5" valign="top" bgcolor="#FFFFFF"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">No<br soft></span></font></span></td>
        <td width="322" height="17" colspan="10"></td>
      </tr>
      <tr>
        <td width="60" height="3" colspan="3"></td>
        <td width="422" height="3" colspan="16"></td>
      </tr>
      <tr>
        <td width="502" height="25" colspan="20"></td>
      </tr>
      <tr>
        <td width="92" height="29" colspan="6"></td>
        <td height="29" colspan="3" valign="top"><input type="reset" value="Reset "></td>
        <td width="25" height="29" colspan="2"></td>
        <td height="29" colspan="5" valign="top"><input type="submit" value="Submit"></td>
        <td width="207" height="29" colspan="4"></td>
      </tr>
      <tr>
        <td width="502" height="3" colspan="20"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="21"></td>
      </tr>
      <tr> <!-- workaround for IE table layout bug -->
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
    </table>
    <br><br>
  ';
}




#!/usr/bin/perl

# Author: Jason Jankoski
# This script handles the majority of the html generation for the site

sub printHtmlBegin
{
  $page_title = shift(@_);
	print '<html>
	  <head>
	    <title>';
  print "$page_title";
  print '</title>
	    <!--$page size 761, 3000$-->
	    <!--$fontFamily Arial$-->
	    <!--$fontSize 10$-->
	    <style type="text/css"><!--
	      BODY {font-family:"Arial"; font-size:10;}
	      P {font-family:"Arial"; font-size:10;}
	    --></style>
	  </head>
	<script>
	  ns4 = document.layers && true;
	  msie = document.all && true;
	  function init() {
	  }
	</script>
	        ';
}

sub printHtmlEnd
{
  print '
    <div id="e5" 
style="position:absolute;left:1;top:1;width:759;height:108;"><!--$img 
%ImageAssetImpl:../sitebuilder/preview/clipart/pageHeaders/Top_deskStockTicker.jpg$--><img 
src="../sitebuilder/images/Top_deskStockTicker-759x108.jpg" width="759" 
height="108"></div>
    <div id="e4" style="position:absolute;left:58;top:35;width:273;height:29;"><span class="text"><b><font color="#FFFFFF" size="5"><span style="font-size:24px;line-height:29px;">ACME Stock Brokerage<br soft></span></font></b></span></div>
    <div id="e3" 
style="position:absolute;left:650;top:173;width:101;height:133;"><img 
src="../sitebuilder/preview/clipart/photos/shadowManOnBlue.jpg" 
width="101" height="133"></div>
    <div id="e2" 
style="position:absolute;left:650;top:327;width:101;height:133;"><img 
src="../sitebuilder/preview/clipart/photos/conferenceSpeakerPhone_darkBlue.jpg" 
width="101" height="133"></div>
    <div id="e1" 
style="position:absolute;left:650;top:484;width:101;height:133;"><img 
src="../sitebuilder/preview/clipart/photos/faceGlassesSuperZoom_oliveGreen.jpg" 
width="101" height="133"></div>
    <div id="e0" style="position:absolute;left:16;top:165;width:54;height:143;"><!--$navbar
name=navbar.nav
assetID=%NavbarAsset:/navigation_bars/navbar.nav
$--><!--$begin exclude$--><table border="0" cellspacing="0" cellpadding="0"><tr><td>
<a href="../index.html" 
onMouseOver="document.images[\'i0\'].src=\'../sitebuilder/images/navbar-0-mouseOver-06406.png\'" 
onMouseOut="document.images[\'i0\'].src=\'../
sitebuilder/images/navbar-0-inactive-06328.png\'"><img 
name="i0" src="../sitebuilder/images/navbar-0-inactive-06328.png" 
border="0" 
width="54" height="15" alt=""/></a></td></tr><tr><td height="17" width="0"> <img src="" height="17" width="1"></td></tr><tr><td>
<a href="../portfolio.html" 
onMouseOver="document.images[\'i1\'].src=\'../sitebuilder/images/navbar-1-mouseOver-06515.png\'" 
onMouseOut="document.images[\'i1\'].src=\'../sitebuilder/images/navbar-1-inactive-06453.png\'"><img 
name="i1" src="../sitebuilder/images/navbar-1-inactive-06453.png" 
border="0" 
width="54" height="15" alt=""/></a></td></tr><tr><td height="17" width="0"> <img src="" height="17" width="1"></td></tr><tr><td>
<a href="../buy.html" 
onMouseOver="document.images[\'i2\'].src=\'../sitebuilder/images/navbar-2-mouseOver-06640.png\'" 
onMouseOut="document.images[\'i2\'].src=\'../sitebuilder/images/navbar-2-inactive-06562.png\'"><img 
name="i2" src="../sitebuilder/images/navbar-2-inactive-06562.png" 
border="0" 
width="54" height="15" alt=""/></a></td></tr><tr><td height="17" width="0"> <img src="" height="17" width="1"></td></tr><tr><td>
<a href="../sell.html" 
onMouseOver="document.images[\'i3\'].src=\'../sitebuilder/images/navbar-3-mouseOver-06734.png\'" 
onMouseOut="document.images[\'i3\'].src=\'../sitebuilder/images/navbar-3-inactive-06671.png\'"><img 
name="i3" src="../sitebuilder/images/navbar-3-inactive-06671.png" 
border="0" 
width="54" height="15" alt=""/></a></td></tr><tr><td height="17" width="0"> <img src="" height="17" width="1"></td></tr><tr><td>
<a href="../register.html" 
onMouseOver="document.images[\'i4\'].src=\'../sitebuilder/images/navbar-4-mouseOver-06859.png\'" 
onMouseOut="document.images[\'i4\'].src=\'../sitebuilder/images/navbar-4-active-06812.png\'"><img 
name="i4" src="../sitebuilder/images/navbar-4-active-06812.png" width="54" 
height="15" border="0" alt=""/></a></td></tr></table><!--$end exclude$--></div>
</form>
  </body>
</html>
';
}

sub printSellBegin
{
  print '  <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
<form name="form0" method="POST" action="/cgi-bin/sell.cgi">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="13">
      <col width="32">
      <col width="3">
      <col width="2">
      <col width="24">
      <col width="31">
      <col width="5">
      <col width="15">
      <col width="5">
      <col width="23">
      <col width="2">
      <col width="10">
      <col width="35">
      <col width="24">
      <col width="14">
      <col width="6">
      <col width="16">
      <col width="20">
      <col width="4">
      <col width="21">
      <col width="30">
      <col width="12">
      <col width="12">
      <col width="36">
      <col width="20">
      <col width="5">
      <col width="20">
      <col width="38">
      <col width="24">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="32"></td>
      </tr>
      <tr>
        <td height="591" rowspan="19" valign="top"><img 
src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" 
width="135" height="591"></td>
        <td width="624" height="1" colspan="31"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="29"></td>
        <td nowrap height="591" colspan="2" rowspan="19" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="13" height="30"></td>
        <td nowrap height="30" colspan="14" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Sell Stocks<br soft></span></font></b></span></td>
        <td width="264" height="30" colspan="14"></td>
      </tr>
      <tr>
        <td width="13" height="1"></td>
        <td height="1" colspan="27" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="465" height="1"></td>
        <td width="24" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="2" colspan="29"></td>
      </tr>
      <tr>
        <td width="45" height="58" colspan="2"></td>
        <td height="58" colspan="24" valign="top"><!--$table$--><table style="table-layout:fixed;border:2px outset #9999FF" width=375 height=58 border=2 cellspacing=2 cellpadding=0><col width="367"><tr>
<td style="border:1px none #9999FF" width="365" height="48" valign="top">        <table border="0" cellspacing="0" cellpadding="0" width="365" height="48">
          <col width="11">
          <col width="72">
          <col width="3">
          <col width="69">
          <col width="51">
          <col width="75">
          <col width="69">
          <col width="15">
          <tr>
            <td width="365" height="11" colspan="8"></td>
          </tr>
          <tr>
            <td width="86" height="3" colspan="3"></td>
            <td height="22" rowspan="3" valign="top"><input name="id" value="" size="8"></td>
            <td width="126" height="3" colspan="2"></td>
            <td height="22" rowspan="3" valign="top"><input type="password" name="password" value="" size="8"></td>
            <td width="15" height="3"></td>
          </tr>
          <tr>
            <td width="11" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Login ID<br soft></span></font></b></span></td>
            <td width="3" height="17"></td>
            <td width="51" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Password<br soft></span></font></b></span></td>
            <td width="15" height="17"></td>
          </tr>
          <tr>
            <td width="86" height="2" colspan="3"></td>
            <td width="126" height="2" colspan="2"></td>
            <td width="15" height="2"></td>
          </tr>
          <tr>
            <td width="365" height="15" colspan="8"></td>
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
          </tr>
        </table>
</td>
</tr>
</table>
</td>';
}

sub printSellEnd
{
 print '<td width="45" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="9" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">ACDO</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $42.11<br soft></span></font></span></td>
        <td width="45" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">HELE</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $28.66<br soft></span></font></span></td>
        <td width="30" height="17"></td>
        <td nowrap height="17" colspan="6" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">USFC</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $37.73<br soft></span></font></span></td>
        <td width="62" height="17" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="29"></td>
      </tr>
      <tr>
        <td width="50" height="22" colspan="4"></td>
        <td width="31" height="22"></td>
        <td height="22" colspan="2" valign="top"><input name="ACDOnum" value="0" size="1"></td>
        <td width="75" height="22" colspan="5"></td>
        <td height="24" rowspan="2" valign="top"></td>
        <td width="36" height="22" colspan="3"></td>
        <td height="22" valign="top"><input name="HELEnum" value="0" size="1"></td>
        <td width="55" height="22" colspan="3"></td>
        <td height="24" colspan="2" rowspan="2" valign="top"></td>
        <td width="36" height="22"></td>
        <td height="22" valign="top"><input name="USFCnum" value="0" size="1"></td>
        <td width="87" height="22" colspan="4"></td>
      </tr>
      <tr>
        <td width="50" height="2" colspan="4"></td>
        <td width="126" height="2" colspan="8"></td>
        <td width="111" height="2" colspan="7"></td>
        <td width="143" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="36" colspan="29"></td>
      </tr>
      <tr>
        <td width="48" height="17" colspan="3"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">OPTN</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $20.16<br soft></span></font></span></td>
        <td width="47" height="17" colspan="3"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">PLXT</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $11.74<br soft></span></font></span></td>
        <td width="30" height="17"></td>
        <td nowrap height="17" colspan="6" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">EVST</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $10.39<br soft></span></font></span></td>
        <td width="62" height="17" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="29"></td>
      </tr>
      <tr>
        <td width="50" height="22" colspan="4"></td>
        <td width="36" height="22" colspan="2"></td>
        <td height="22" colspan="2" valign="top"><input name="OPTNnum" value="0" size="1"></td>
        <td width="70" height="22" colspan="4"></td>
        <td height="24" rowspan="2" valign="top"></td>
        <td width="36" height="22" colspan="3"></td>
        <td height="22" valign="top"><input name="PLXTnum" value="0" size="1"></td>
        <td width="55" height="22" colspan="3"></td>
        <td height="24" colspan="2" rowspan="2" valign="top"></td>
        <td width="36" height="22"></td>
        <td height="22" valign="top"><input name="EVSTnum" value="0" size="1"></td>
        <td width="87" height="22" colspan="4"></td>
      </tr>
      <tr>
        <td width="50" height="2" colspan="4"></td>
        <td width="126" height="2" colspan="8"></td>
        <td width="111" height="2" colspan="7"></td>
        <td width="143" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="21" colspan="29"></td>
      </tr>
      <tr>
        <td width="165" height="29" colspan="12"></td>
        <td height="29" colspan="4" valign="top"><input type="reset" value="Reset"></td>
        <td width="40" height="29" colspan="3"></td>
        <td height="29" colspan="3" valign="top"><input type="submit" value="Sell"></td>
        <td width="155" height="29" colspan="7"></td>
      </tr>
      <tr>
        <td width="502" height="249" colspan="29"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="30"></td>
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

sub printRegistered
{
  print '  <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="12">
      <col width="395">
      <col width="21">
      <col width="49">
      <col width="25">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="8"></td>
      </tr>
      <tr>
        <td height="591" rowspan="7" valign="top"><img 
src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" 
width="135" height="591"></td>
        <td width="624" height="1" colspan="7"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="5"></td>
        <td nowrap height="591" colspan="2" rowspan="7" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="12" height="30"></td>
        <td nowrap height="30" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Application Submitted<br soft></span></font></b></span></td>
        <td width="95" height="30" colspan="3"></td>
      </tr>
      <tr>
        <td width="12" height="1"></td>
        <td height="1" colspan="3" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="465" height="1"></td>
        <td width="25" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="12" height="131"></td>
        <td nowrap height="131" colspan="2" valign="top" bgcolor="#FFFFFF"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Thank you.<br><br>Due to the sensitive nature of the information submitted, <br soft>your application must be reviewed by one of our customer <br soft>associates. You will be emailed shortly with your account <br soft>information.<br soft></span></font></b></span></td>
        <td width="74" height="131" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="402" colspan="5"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="6"></td>
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
      </tr>
    </table>
    <br><br>
  ';
}

sub printPortEntry
{
  print ' <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
<form name="form0" method="POST" action="port.cgi">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="13">
      <col width="32">
      <col width="105">
      <col width="88">
      <col width="69">
      <col width="113">
      <col width="58">
      <col width="24">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="11"></td>
      </tr>
      <tr>
        <td height="591" rowspan="9" valign="top"><a 
href="/cgi-bin/stock.cgi"><img 
src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" 
width="135" height="591" border="0"></a></td>
        <td width="624" height="1" colspan="10"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="8"></td>
        <td nowrap height="591" colspan="2" rowspan="9" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="13" height="30"></td>
        <td nowrap height="30" colspan="3" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Portfolio Summary<br soft></span></font></b></span></td>
        <td width="264" height="30" colspan="4"></td>
      </tr>
      <tr>
        <td width="13" height="1"></td>
        <td height="1" colspan="6" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="465" height="1"></td>
        <td width="24" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="62" colspan="8"></td>
      </tr>
      <tr>
        <td width="45" height="58" colspan="2"></td>
        <td height="58" colspan="4" valign="top"><!--$table$--><table style="table-layout:fixed;border:2px outset #9999FF" width=375 height=58 border=2 cellspacing=2 cellpadding=0><col width="367"><tr>
<td style="border:1px none #9999FF" width="365" height="48" valign="top">        <table border="0" cellspacing="0" cellpadding="0" width="365" height="48">
          <col width="11">
          <col width="72">
          <col width="3">
          <col width="69">
          <col width="51">
          <col width="75">
          <col width="69">
          <col width="15">
          <tr>
            <td width="365" height="11" colspan="8"></td>
          </tr>
          <tr>
            <td width="86" height="3" colspan="3"></td>
            <td height="22" rowspan="3" valign="top"><input name="id" value="" size="8"></td>
            <td width="126" height="3" colspan="2"></td>
            <td height="22" rowspan="3" valign="top"><input type="password" name="password" value="" size="8"></td>
            <td width="15" height="3"></td>
          </tr>
          <tr>
            <td width="11" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Login ID<br soft></span></font></b></span></td>
            <td width="3" height="17"></td>
            <td width="51" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Password<br soft></span></font></b></span></td>
            <td width="15" height="17"></td>
          </tr>
          <tr>
            <td width="86" height="2" colspan="3"></td>
            <td width="126" height="2" colspan="2"></td>
            <td width="15" height="2"></td>
          </tr>
          <tr>
            <td width="365" height="15" colspan="8"></td>
          </tr><tr> <!-- workaround for IE table layout bug -->
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
	</td>
	</tr>
	</table>
	</td>
        <td width="82" height="58" colspan="2"></td>
      </tr>
';
}

sub printPortEntryEnd
{print '
      <tr>
        <td width="502" height="32" colspan="8"></td>
      </tr>
      <tr>
        <td width="150" height="29" colspan="3"></td>
        <td height="29" colspan="2" valign="top"><input type="submit" value="View Portfolio"></td>
        <td width="195" height="29" colspan="3"></td>
      </tr>
      <tr>
        <td width="502" height="369" colspan="8"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="9"></td>
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
      </tr>
    </table>
    <br><br>
  ';
}

sub printPort
{
  print '
    <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="13">
      <col width="2">
      <col width="90">
      <col width="15">
      <col width="15">
      <col width="15">
      <col width="45">
      <col width="15">
      <col width="28">
      <col width="2">
      <col width="238">
      <col width="24">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="15"></td>
      </tr>
      <tr>
        <td height="591" rowspan="23" valign="top"><a 
href="/cgi-bin/stock.cgi"><img 
src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" 
width="135" height="591" border="0"></a></td>
        <td width="624" height="1" colspan="14"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="12"></td>
        <td nowrap height="591" colspan="2" rowspan="23" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="13" height="30"></td>
        <td nowrap height="30" colspan="8" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Portfolio Summary<br soft></span></font></b></span></td>
        <td width="264" height="30" colspan="3"></td>
      </tr>
      <tr>
        <td width="13" height="1"></td>
        <td height="1" colspan="10" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="465" height="1"></td>
        <td width="24" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="32" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><b><u><font size="2"><span style="font-size:14px;line-height:17px;">Symbol<br soft></span></font></u></b></span></td>
        <td width="15" height="17"></td>
        <td nowrap height="17" colspan="3" valign="top"><span class="text"><b><u><font size="2"><span style="font-size:14px;line-height:17px;">#Shares<br soft></span></font></u></b></span></td>
        <td width="292" height="17" colspan="4"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>';
}

sub printShares
{
  print '<tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">ADCO<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[0];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr><tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">HELE<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[1];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">USFC<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[2];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">OPTN<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[3];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">PLXT<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[4];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="2" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">EVST<br soft></span></font></span></td>
        <td width="30" height="17" colspan="2"></td>
        <td nowrap height="17" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">';
  print $_[5];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="1" colspan="2"></td>
        <td height="1" colspan="8" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="225" height="1"></td>
        <td width="262" height="1" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="14" colspan="12"></td>
      </tr>
      <tr>
        <td width="15" height="17" colspan="2"></td>
        <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Total Value<br soft></span></font></b></span></td>
        <td width="15" height="17"></td>
        <td nowrap height="17" colspan="3" align="right" valign="top"><span class="text"><font size="2"><span style="font-size:14px;line-height:17px;">$';
  print $_[6];
  print '<br soft></span></font></span></td>
        <td width="307" height="17" colspan="5"></td>
      </tr>
      <tr>
        <td width="502" height="276" colspan="12"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="13"></td>
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
      </tr>
    </table>
    <br><br>';
}

sub printBuyBegin
{
  print'  <body bgcolor="#FFFFFF" text="#000000" link="#0000FF" vlink="#800080" onLoad="init()" onResize="if (ns4) history.go(0);" marginheight="0" marginwidth="1" topmargin="0" leftmargin="1" hspace="1" vspace="0">
<form name="form0" method="POST" action="/cgi-bin/buy.cgi">
    <table border="0" cellspacing="0" cellpadding="0" width="758" height="699">
      <col width="135">
      <col width="13">
      <col width="32">
      <col width="3">
      <col width="2">
      <col width="24">
      <col width="31">
      <col width="5">
      <col width="15">
      <col width="5">
      <col width="23">
      <col width="2">
      <col width="10">
      <col width="35">
      <col width="24">
      <col width="14">
      <col width="6">
      <col width="16">
      <col width="20">
      <col width="6">
      <col width="19">
      <col width="30">
      <col width="10">
      <col width="14">
      <col width="36">
      <col width="20">
      <col width="5">
      <col width="20">
      <col width="38">
      <col width="24">
      <col width="121">
      <col width="1">
      <tr>
        <td width="759" height="107" colspan="32"></td>
      </tr>
      <tr>
        <td height="591" rowspan="19" valign="top"><img 
src="../sitebuilder/preview/clipart/images/sidebars/office_StockTicker.jpg" 
width="135" height="591"></td>
        <td width="624" height="1" colspan="31"></td>
      </tr>
      <tr>
        <td width="502" height="9" colspan="29"></td>
        <td nowrap height="591" colspan="2" rowspan="19" valign="top" bgcolor="#660000"><!--$emptyText$--></td>
      </tr>
      <tr>
        <td width="13" height="30"></td>
        <td nowrap height="30" colspan="14" valign="top"><span class="text"><b><font size="4"><span style="font-size:20px;line-height:24px;">Buy Stocks<br soft></span></font></b></span></td>
        <td width="264" height="30" colspan="14"></td>
      </tr>
      <tr>
        <td width="13" height="1"></td>
        <td height="1" colspan="27" valign="top"><img 
src="../sitebuilder/preview/clipart/lines/horizontal/deepblue.gif" 
width="465" height="1"></td>
        <td width="24" height="1"></td>
      </tr>
      <tr>
        <td width="502" height="2" colspan="29"></td>
      </tr>
      <tr>
        <td width="45" height="58" colspan="2"></td>
        <td height="58" colspan="24" valign="top"><!--$table$--><table style="table-layout:fixed;border:2px outset #9999FF" width=375 height=58 border=2 cellspacing=2 cellpadding=0><col width="367"><tr>
<td style="border:1px none #9999FF" width="365" height="48" valign="top">        <table border="0" cellspacing="0" cellpadding="0" width="365" height="48">
          <col width="11">
          <col width="72">
          <col width="3">
          <col width="69">
          <col width="51">
          <col width="75">
          <col width="69">
          <col width="15">
          <tr>
            <td width="365" height="11" colspan="8"></td>
          </tr>
          <tr>
            <td width="86" height="3" colspan="3"></td>
            <td height="22" rowspan="3" valign="top"><input name="id" value="" size="8"></td>
            <td width="126" height="3" colspan="2"></td>
            <td height="22" rowspan="3" valign="top"><input type="password" name="password" value="" size="8"></td>
            <td width="15" height="3"></td>
          </tr>
          <tr>
            <td width="11" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Login ID<br soft></span></font></b></span></td>
            <td width="3" height="17"></td>
            <td width="51" height="17"></td>
            <td nowrap height="17" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">Password<br soft></span></font></b></span></td>
            <td width="15" height="17"></td>
          </tr>
          <tr>
            <td width="86" height="2" colspan="3"></td>
            <td width="126" height="2" colspan="2"></td>
            <td width="15" height="2"></td>
          </tr>
          <tr>
            <td width="365" height="15" colspan="8"></td>
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
          </tr>
        </table>
</td>
</tr>
</table>
</td>

';
}

sub printBuyEnd
{
  print'
      <tr>
        <td width="45" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="9" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">ACDO</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $42.11<br soft></span></font></span></td>
        <td width="45" height="17" colspan="2"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">HELE</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $28.66<br soft></span></font></span></td>
        <td width="30" height="17"></td>
        <td nowrap height="17" colspan="6" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">USFC</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $37.73<br soft></span></font></span></td>
        <td width="62" height="17" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="29"></td>
      </tr>
      <tr>
        <td width="50" height="22" colspan="4"></td>
        <td width="31" height="22"></td>
        <td height="22" colspan="2" valign="top"><input name="ACDOnum" value="0" size="1"></td>
        <td width="75" height="22" colspan="5"></td>
        <td height="24" rowspan="2" valign="top"></td>
        <td width="36" height="22" colspan="3"></td>
        <td height="22" valign="top"><input name="HELEnum" value="0" size="1"></td>
        <td width="55" height="22" colspan="3"></td>
        <td height="24" colspan="2" rowspan="2" valign="top"></td>
        <td width="36" height="22"></td>
        <td height="22" valign="top"><input name="USFCnum" value="0" size="1"></td>
        <td width="87" height="22" colspan="4"></td>
      </tr>
      <tr>
        <td width="50" height="2" colspan="4"></td>
        <td width="126" height="2" colspan="8"></td>
        <td width="111" height="2" colspan="7"></td>
        <td width="143" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="36" colspan="29"></td>
      </tr>
      <tr>
        <td width="48" height="17" colspan="3"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">OPTN</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $20.16<br soft></span></font></span></td>
        <td width="47" height="17" colspan="3"></td>
        <td nowrap height="17" colspan="7" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">PLXT</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $11.74<br soft></span></font></span></td>
        <td width="30" height="17"></td>
        <td nowrap height="17" colspan="6" valign="top"><span class="text"><b><font size="2"><span style="font-size:14px;line-height:17px;">EVST</span></font></b><font size="2"><span style="font-size:14px;line-height:17px;"> - $10.39<br soft></span></font></span></td>
        <td width="62" height="17" colspan="2"></td>
      </tr>
      <tr>
        <td width="502" height="13" colspan="29"></td>
      </tr>
      <tr>
        <td width="50" height="22" colspan="4"></td>
        <td width="36" height="22" colspan="2"></td>
        <td height="22" colspan="2" valign="top"><input name="OPTNnum" value="0" size="1"></td>
        <td width="70" height="22" colspan="4"></td>
        <td height="24" rowspan="2" valign="top"></td>
        <td width="36" height="22" colspan="3"></td>
        <td height="22" valign="top"><input name="PLXTnum" value="0" size="1"></td>
        <td width="55" height="22" colspan="3"></td>
        <td height="24" colspan="2" rowspan="2" valign="top"></td>
        <td width="36" height="22"></td>
        <td height="22" valign="top"><input name="EVSTnum" value="0" size="1"></td>
        <td width="87" height="22" colspan="4"></td>
      </tr>
      <tr>
        <td width="50" height="2" colspan="4"></td>
        <td width="126" height="2" colspan="8"></td>
        <td width="111" height="2" colspan="7"></td>
        <td width="143" height="2" colspan="6"></td>
      </tr>
      <tr>
        <td width="502" height="21" colspan="29"></td>
      </tr>
      <tr>
        <td width="165" height="29" colspan="12"></td>
        <td height="29" colspan="4" valign="top"><input type="reset" value="Reset"></td>
        <td width="42" height="29" colspan="3"></td>
        <td height="29" colspan="3" valign="top"><input type="submit" value="Buy"></td>
        <td width="157" height="29" colspan="7"></td>
      </tr>
      <tr>
        <td width="502" height="249" colspan="29"></td>
      </tr>
      <tr>
        <td width="637" height="1" colspan="30"></td>
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
1;

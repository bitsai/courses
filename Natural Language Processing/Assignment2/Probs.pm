# CS465 at Johns Hopkins University.
# Module to estimate n-gram probabilities.

package Probs;
use strict;

# Declare list of functions and constants exported by this module
# (i.e., users of the module can get at them easily).

use Exporter ();
use vars qw(@ISA @EXPORT);
@ISA = qw(Exporter);
@EXPORT = qw(&prob &train &set_smoother &set_vocab_size
		 $BOC $EOC $OOV $DEFAULT_TRAINING_DIR);

# define the exportable constants
our($BOC) = "__BOC__";   # special word for context at Beginning Of Corpus
our($EOC) = "__EOC__";   # special word for observed token at End Of Corpus
our($OOV) = "__OOV__";   # special word for all out-of-vocabulary words
our($DEFAULT_TRAINING_DIR) = "/usr/local/data/cs465/hw2/All_Training/";

# ======================================================================

# To keep the code short and simple, we store the language model in
# the following global (actually package-scope) variables, rather than
# having multiple language-model objects.
#
# These variables all correspond to quantities discussed in the assignment.
# For Witten-Bell, you will need to define some additional global variables
# to hold further quantities (alphas, or values used to compute the alphas).

my $smoother;     # type of smoother we're using (see probs.h)
my $lambda;       # lambda: parameter used by some smoothers
my $vocab_size;   # V: the total vocab size including OOV
my %vocab;

my %tokens;       # the c(...) function
my %types_after;  # the T(...) function

my %alpha;        # the alpha(...) function
my %sum;

my @trigrams;
my @bigrams;

    # $tokens{"$x $y $z"} = # of times that xyz was observed during training.
    # $tokens{"$y $z"}    = # of times that yz was observed during training.
    # $tokens{"$z"}       = # of times that z was observed during training.
    # $tokens{""}         = # of tokens observed during training.
    #
    # $types_after{"$x $y"}  = # of distinct word types that were
    #                             observed to follow xy during training.
    # $types_after{"$y"}     = # of distinct word types that were
    #                             observed to follow y during training.
    # $types_after{""}       = # of distinct word types observed during training.
    #
    # $alpha{"$x $y"}	= alpha(xy)
    # $alpha{"$y"}	= alpha(y)
    # $alpha{""}		= alpha()

# ======================================================================

# Computes a smoothed estimate of the trigram probability p(z | x,y)
# according to the language model.

sub prob {
  my($x,$y,$z) = @_;

  defined $smoother || die "must call set_smoother before calling prob";
  defined $vocab_size || die "must call train before calling prob";

  if ($smoother eq "UNIFORM") {

    return 1/$vocab_size;

  } elsif ($smoother eq "ADDL") {

    return ($tokens{"$x $y $z"} + $lambda) /
      ($tokens{"$x $y"} + $lambda * $vocab_size);

    # Notice that summing the numerator over all values of typeZ
    # will give the denominator.  Therefore, summing up the quotient
    # over all values of typeZ will give 1, so sum_z p(z | ...) = 1
    # as is required for any probability function.

  } elsif ($smoother eq "BACKOFF_ADDL") {

	if ($tokens{"$x $y"} > 0)
	{ return ($tokens{"$x $y $z"} + $lambda * $vocab_size * prob($OOV, $y, $z)) / 
		($tokens{"$x $y"} + $lambda * $vocab_size); }
	elsif ($tokens{"$y"} > 0)
	{ return ($tokens{"$y $z"} + $lambda * $vocab_size * prob($OOV, $OOV, $z)) / 
		($tokens{"$y"} + $lambda * $vocab_size); }
	else
	{ return ($tokens{"$z"} + $lambda) / 
		($tokens{""} + $lambda * $vocab_size); }

  } elsif ($smoother eq "BACKOFF_WB") {

	if ($tokens{"$x $y"} > 0)
	{
		if ($tokens{"$x $y $z"} > 0)
		{ return $tokens{"$x $y $z"} / ($tokens{"$x $y"} + $types_after{"$x $y"}); }
		else
		{ return $alpha{"$x $y"} * prob($OOV, "$y", "$z"); }
	}
	elsif ($tokens{"$y"} > 0)
	{
		if ($tokens{"$y $z"} > 0)
		{ return $tokens{"$y $z"} / ($tokens{"$y"} + $types_after{"$y"}); }
		else
		{ return $alpha{"$y"} * prob($OOV, $OOV, "$z"); }
	}
	else
	{
		if ($tokens{"$z"} > 0)
		{ return ($tokens{"$z"} / ($tokens{""} + $types_after{""})); }
		else
		{ return $alpha{""}; }
	}

  } else {
    die "$smoother has some weird value";
  }
}

# ====================================================================== 

# Read the training corpus and collect any information that will be
# needed by prob later on.  Tokens are whitespace-delimited.
#
# Note: In a real system, you wouldn't do this work every time you
# ran the testing program.  You'd do it only once and save the
# trained model to disk in some format.

sub train {
  my($filename) = @_;

  print STDERR "Training from corpus $filename\n";

  # Clear out any previous training from the global variables.
  %tokens = ();
  %types_after = ();

  %alpha = ();
  %sum = ();

  # While training, we'll keep track of all the trigram and bigram types
  # we observe.  You'll need these lists only for Witten-Bell backoff.
  @trigrams = ();
  @bigrams = ();

  # The real work:
  # accumulate the type and token counts into the global hash tables.

  my($x,$y) = ($BOC,$BOC);   # xy context is "beginning of corpus"
  ++$tokens{"$x $y"}; ++$tokens{"$y"};  # count the BOC context

  open_CORPUS($filename);
  while (<CORPUS>) {    # for each line
    foreach my $z (split) {   # for each word z on the line
      count($x,$y,$z);            # the real work
      show_progress();            # print "....."
      $x=$y; $y=$z;               # slide trigram window forward for next word
    }
  }
  close(CORPUS);

  count($x,$y,$EOC);     # count EOC token after the final context

  # If vocab size has not been set, set it to the number of
  # unique words seen in this file, plus 1 for OOV
  $vocab_size = $types_after{""}+1 unless defined $vocab_size;

  # ******** COMMENT *********
  # In Witten-Bell backoff, you will have to do some additional
  # computation at this point.  The following code illustrates how you
  # can iterate over all observed bigram types.  (It just prints them.)
  #
  # foreach my $bi (@bigrams) {
  #   my($y,$z) = @$bi;
  #   print "$y $z\n";
  # }
  # **************************

	foreach my $trigram (@trigrams)
	{
		my ($x, $y, $z) = @$trigram;
		$sum{"$x $y"} += $tokens{"$y $z"};
	}

	foreach my $bigram (@bigrams)
	{
		my ($y, $z) = @$bigram;
		$sum{"$y"} += $tokens{"$z"};
	}

	foreach my $trigram (@trigrams)
	{
		my ($x, $y, $z) = @$trigram;
		$alpha{"$x $y"} = (1 - ($tokens{"$x $y"}/($tokens{"$x $y"} + $types_after{"$x $y"}))) / (1 - ($sum{"$x $y"}/($tokens{"$y"} + $types_after{"$y"})));
	}

	foreach my $bigram (@bigrams)
	{
		my ($y, $z) = @$bigram;
		$alpha{"$y"} = (1 - ($tokens{"$y"}/($tokens{"$y"} + $types_after{"$y"}))) / (1 - ($sum{"$y"}/($tokens{""} + $types_after{""})));
	}

	$alpha{""} = $types_after{""}/($tokens{""} + $types_after{""});

  print STDERR "\nFinished training on ", $tokens{""}, " tokens\n";

  # This subroutine is declared locally inside train,
  # so that it can see @trigrams and @bigrams variables.
  sub count {
    my($x,$y,$z) = @_;

    if (++$tokens{"$x $y $z"}==1) {   # first time we've seen trigram xyz
      $types_after{"$x $y"}++;
      push @trigrams, [$x,$y,$z];
    }
    if (++$tokens{"$y $z"}==1) {      # first time we've seen bigram yz
      $types_after{"$y"}++;
      push @bigrams, [$y,$z];
    } 
    if (++$tokens{"$z"}==1) {         # first time we've seen unigram z
      $types_after{""}++;
    } 
    ++$tokens{""};                    # the zero-gram
  }
}

# ======================================================================

# When you do text categorization, call this function on the two
# corpora in order to set the global vocab_size to the size
# of the single common vocabulary.

sub set_vocab_size {
  my($filename1, $filename2) = @_;
  %vocab = ();  # set of all words seen so far (no duplicates)
  print STDERR "Collecting vocabulary from corpora $filename1 and $filename2\n";

  open_CORPUS($filename1);  # add words from corpus 1 to vocab
  while (<CORPUS>) {                    # for each line
    foreach my $z (split) {             # for each word z on the line
      $vocab{$z}=1;
      show_progress();
    }
  }
  close(CORPUS);

  open_CORPUS($filename2);  # add words from corpus 2 to vocab
  while (<CORPUS>) {                    # for each line
    foreach my $z (split) {             # for each word z on the line
      $vocab{$z}=1;
      show_progress();
    }
  }
  close(CORPUS);

  $vocab{$OOV} = 1;                 # add OOV to vocab
  $vocab{$EOC} = 1;                 # add EOC to vocab (but not BOC, which is only used as context)

  print STDERR "\n";
  warn "Warning: vocab_size already set; set_vocab_size changing it\n" if $vocab_size >= 0;
  $vocab_size = scalar(keys(%vocab));    # number of keys in hash table
  print STDERR "Vocabulary size is $vocab_size types including OOV and EOC\n";
}

# ======================================================================

# Sets smoother type and lambda from a string given by the user on the
# command line.
sub set_smoother {
  my($arg) = @_;
  my $smoother_name;
  ($smoother_name,$lambda) = ($arg =~ /^(.*?)(-?[0-9.]*)$/);

  if ($smoother_name =~ /^uniform$/i) {
    $smoother = "UNIFORM";
  } elsif ($smoother_name =~ /^add$/i) {
    $smoother = "ADDL";
  } elsif ($smoother_name =~ /^backoff_add$/i) {
    $smoother = "BACKOFF_ADDL";
  } elsif ($smoother_name =~ /^backoff_wb$/i) {
    $smoother = "BACKOFF_WB";
  } else {
    die "Don't recognize smoother name $smoother_name\n";
  }

  if (!defined $lambda && $smoother =~ /ADDL$/) {
    die "You must include a non-negative lambda value in smoother name \"$arg\"\n";
  }
}

# ======================================================================

# Associates handle CORPUS with the training corpus named by filename.
# This deserves its own function because it's got to look in two
# places and maybe generate an error message.

sub open_CORPUS {
  my($filename) = @_;
  open(CORPUS,$filename) || open(CORPUS, $DEFAULT_TRAINING_DIR.$filename)
    || die "Couldn't open corpus at location $filename or $DEFAULT_TRAINING_DIR$filename\n";
}

# ======================================================================

{
  my $progress=0;

  sub show_progress {   # print a dot to cerr every 1000 calls
    print STDERR "." if ++$progress % 5000 == 0;
  }
}

1;

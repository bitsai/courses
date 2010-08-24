Benny Tsai
600.466
Assignment 3

Extension Implemented: Bayesian Sense Disambiguation

Evaluation Results:

Configuration						Tank Acc	Plant Acc	Person/Place Acc
1 Unstemmed, Uniform, Bag-Of-Words			0.93		0.92		0.765
2 Stemmed, Exp Decay, Bag-Of-Words			0.9275	0.9225	0.805
3 Unstemmed, Exp Decay, Bag-Of-Words		0.9375	0.9225	0.765
4 Unstemmed, Exp Decay, Adjacent-Separate-LR	0.8675	0.8375	0.855
5 Unstemmed, Stepped, Adjacent-Separate-LR	0.875		0.8375	0.8575
6 Unstemmed, Custom, Adjacent-Separate-LR		0.885		0.8725	0.8575
7 Bayesian Sense Disambiguation			0.91		0.9725	0.775

Notes:

1. Detailed output from each run printed to "output.txt".

2. Format of output:
	Configurations 1-6: +/*, correct sense, labeled sense, sim1, sim2, diff, title
	Configuration 7: +/*, correct sense, labeled sense, sumofLL, title

3. Stopwords are excluded unless Adjacent-Separate-LR collocation is in effect.  Bayesian Sense Disambiguation excludes stopwords.

4. Bayesian Sense Disambiguation uses stemming, custom weighting scheme, and bag-of-words collocation.

5. Custom weighting scheme: Target word is weighted 5, adjacent words weighted 4, words 2 away weighted 3, words 3 away weighted 2, all other words weighted 1.
 
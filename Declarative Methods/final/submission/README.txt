--- ENCODING DATA ---

Use preprocess.pl, sequence.pl, and encode.pl to transform the MIT Lincoln Lab datasets into the required format.

Example: 	Given data file inside.tcpdump.gz, first unzip it.

		Preprocess - perl preprocess.pl inside.tcpdump preprocessed.txt

		Sequence - perl sequence.pl preprocessed.txt sequenced.txt

		Encode - perl encode.pl sequenced.txt

		Now you should have 5 different output files: FTP-sequenced.txt, SMTP-sequenced.txt, TELNET-sequenced.txt, HTTP-sequenced.txt, and NONE-sequenced.txt.

		Each output file will contain samples of the protocol named in its title; NONE-sequenced.txt contains samples of miscellaneous protocols.

		To create a training file, select samples from all of the 5 files and put them into a single file (i named mine simply "training").

		*** IMPORTANT ***: Each training file and test file must begin with a line of the form "<total number of samples> <total number of items per line>".

		The number of items per line depends on your encoding; my scheme encoded TCP sequences as a collection of 4609 features, and each line ends with the actual class, so my total number of items per line was 4610.

		Gzip the training file, as the classifier software works with *.gz files, allowing you to conserve space.

		A test file is created in exactly the same way; i produced a separate test file for each protocol, to better gauge the classifier's performance on each protocol.

--- CLASSIFYING DATA ---

*** IMPORTANT ***: The number of features is 1 less than the total number of items per line in your training and test files, since the last item is the actual class; if you allow the classifier to peek at this, then, well, there's not a whole lot of point to the whole exercise...  
			
Under my encoding scheme the number of features is 4609.

Under my encoding scheme the number of classes is 5.

K-Nearest Neighbor - 	./knn --test -K <int> <training file> <test file> <number of features> <number of classes>

			<int> represents the value of K you wish to use; i tested K = 1, 3, and 25.

			This produces files training_error and test_error, reporting the error on the training and test data, respectively.

Neural Net - 	First, compile a model with: ./mlp <training file> <model file> <number of features> <number of classes>

		This produces file training_error, reporting the error on the training data.

		Then, classify with: ./mlp --test <model file> <test file>

		This produces file test_error, reporting the error on the test data.

Support Vector Machine -	First, compile a model with: ./svm-multi <training file> <model file> <number of classes>

				Then, classify with: ./svm-multi --test <model file> <test file>
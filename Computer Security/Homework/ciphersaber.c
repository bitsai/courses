encrypt() { 
	// CipherSaber encryption stuff 
}

decrypt() { 
	// CipherSaber decryption stuff 
}

debug() {
	int i;
	char test_key[] = {109,97,105,110,40,41,123,112,114,105,110,116,102,40,34,72,101,108,108,111,32,87,111,114,108,100,33,34,41,59,0};
	printf("%s\n", test_key);
	// Debug stuff
	// Some more debug stuff
	// Even more debug stuff
}

main(int argc, char *argv[]) {
	if (argc > 1) {
		if (strcmp(argv[1],"-e") == 0) { encrypt(); }
		if (strcmp(argv[1],"-d") == 0) { decrypt(); }
		if (strcmp(argv[1],"-debug") == 0) { debug(); }
	}
	else {
		// Print usage info
	}
}

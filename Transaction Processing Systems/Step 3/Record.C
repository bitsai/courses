class Record {
	private:
	public:
		int key;
		char data[12];

		Record();
		Record(int inKey, char* inData);
		Record(char* buffer);
		char* readData();
};

Record::Record() {
	key = 0;
	for (int i = 0; i < 12; i++) { data[i] = 0; }
}

Record::Record(int inKey, char* inData) {
	key = inKey;
	for (int i = 0; i < 12; i++) { data[i] = inData[i]; }
}

Record::Record(char* buffer) {
	// Reconstruct key
	char intBytes[4];
	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i]; }
	key = *((int*) intBytes);

	// Reconstruct data
	for (int i = 0; i < 12; i++) { data[i] = buffer[i + 4]; }
}

char* Record::readData() { return data; }

class Record {
	private:
		int key;
		char data[8];
	public:
		Record();
		Record(char* buffer);

		int readKey();
		void writeKey(int inKey);
		char* readData();
		void writeData(char* inData);
};

Record::Record() {
	key = 0;
	for (int i = 0; i < 8; i++) { data[i] = 0; }
}

Record::Record(char* buffer) {
	// Reconstruct key
	char intBytes[4];
	strncpy(intBytes, buffer, 4);
	key = *((int*) intBytes);

	// Reconstruct data
	for (int i = 0; i < 8; i++) { data[i] = buffer[i + 4]; }
}

int Record::readKey() { return key; }

void Record::writeKey(int inKey) { key = inKey; }

char* Record::readData() { return data; }

void Record::writeData(char* inData) { strncpy(data, inData, 8); }

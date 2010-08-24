#include "Record.C"

class Page {
	private:
	public:
		int LSN;
		char metadata[12];
		Record records[255];

		Page();
		Page(char* buffer);

		int findKey(int key);
		bool insertRecord(int key, char* data);
		bool removeRecord(int key);
		Record* locateRecord(int key);
		int getLSN();
		void setLSN(int newLSN);
};

Page::Page() {
	LSN = 0;
	for (int i = 0; i < 12; i++) { metadata[i] = 0; }
	for (int i = 0; i < 255; i++) { records[i] = Record(); }
}

Page::Page(char* buffer) {
	// Reconstruct LSN
	char intBytes[4];
	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i]; }
	LSN = *((int*) intBytes);

	// Extra buffer for other metadata
	for (int i = 0; i < 12; i++) { metadata[i] = buffer[i + 4]; }

	for (int i = 0; i < 255; i++) {
		// Reconstruct records
		int recordOffset = (i * 16) + 16;
		char recordBytes[16];
		for (int j = 0; j < 16; j++) { recordBytes[j] = buffer[j + recordOffset]; }
		records[i] = Record(recordBytes);
	}
}

int Page::findKey(int key) {
	// If table contains key, return its index
	for (int i = 0; i < 255; i++) { if (records[i].key == key) { return i; } }

	return -1;
}

bool Page::insertRecord(int key, char* data) {
	// Find record index of record containing key
	int index = findKey(key);

	// If no such record is found, find record index of blank record
	if (index == -1) { index = findKey(0); }

	// If no such record is found, we're out of luck
	if (index == -1) { return false; }

	// Insert record
	records[index] = Record(key, data);

	return true;
}

bool Page::removeRecord(int key) {
	// Find record index of record containing key
	int index = findKey(key);

	// If no such record is found, we're out of luck
	if (index == -1) { return false; }

	// Remove record
	records[index] = Record();

	return true;
}

Record* Page::locateRecord(int key) {
	// Find record index of record containing key
	int index = findKey(key);

	// If no such record is found, we're out of luck
	if (index == -1) {
		Record* empty = new Record();
		return empty;
	}

	// Return record
	return &records[index];
}

int Page::getLSN() {
	return LSN;
}

void Page::setLSN(int newLSN) {
	LSN = newLSN;
}

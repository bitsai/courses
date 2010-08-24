#include "Record.cpp"

class Page {
	private:
		int table[256];
		Record records[256];
	public:
		Page();
		Page(char* buffer);

		int findKey(int key);
		bool insertRecord(int key, char* data);
		bool removeRecord(int key);
		Record* locateRecord(int key);
};

Page::Page() {
	for (int i = 0; i < 256; i++) {
		table[i] = 0;
		records[i] = Record();
	}
}

Page::Page(char* buffer) {
	for (int i = 0; i < 256; i++) {
		// Reconstruct table
		int tableOffset = i * 4;
		char intBytes[4];
		for (int j = 0; j < 4; j++) { intBytes[j] = buffer[j + tableOffset]; }
		table[i] = *((int*) intBytes);

		// Reconstruct records
		int recordOffset = (i * 12) + 256 * 4;
		char recordBytes[12];
		for (int j = 0; j < 12; j++) { recordBytes[j] = buffer[j + recordOffset]; }
		records[i] = Record(recordBytes);
	}
}

int Page::findKey(int key) {
	// If table contains key, return its index
	for (int i = 0; i < 256; i++) { if (table[i] == key) { return i; } }

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
	table[index] = key;
	records[index].writeKey(key);
	records[index].writeData(data);

	return true;
}

bool Page::removeRecord(int key) {
	// Find record index of record containing key
	int index = findKey(key);

	// If no such record is found, we're out of luck
	if (index == -1) { return false; }

	// Remove record
	table[index] = 0;
	records[index] = Record();

	return true;
}

Record* Page::locateRecord(int key) {
	// Find record index of record containing key
	int index = findKey(key);

	// If no such record is found, we're out of luck
	if (index == -1) { return new Record(); }

	// Return record
	return &records[index];
}

#include "Page.C"

class BufferManager {
	private:
	public:
		FileManager* fm;
		char* dbName;
		Page pages[2];
		int pageNums[2];
		int dirtied[2];
		int pinned[2];

		BufferManager();

		void setFileManager(FileManager* inFM);
		void start(char* inDbName);
		void shutDown();

		int findKey(int key);
		int getBlockOffset(int pageNum);
		int selectVictim();
		int getNumAtIndex(int index);

		void pageIn(int index, int pageNum);
		void pageOut(int index);

		bool insertRecord(int index, int key, char* data);
		bool removeRecord(int index, int key);
		Record locateRecord(int index, int key);
};

BufferManager::BufferManager() {}

void BufferManager::setFileManager(FileManager* inFM) { fm = inFM; }

void BufferManager::start(char* inDbName) {
	dbName = inDbName;

	// Clear variables
	for (int i = 0; i < 2; i++) {
		pages[i] = Page();
		pageNums[i] = 0;
		dirtied[i] = 0;
		pinned[i] = 0;
	}

	// Load pages from disk
	pageIn(0, 1);
	pageIn(1, 2);
}

void BufferManager::shutDown() {
	// Flush pages to disk
	pageOut(0);
	pageOut(1);
}

int BufferManager::findKey(int key) {
	int checked[4] = {0, 0, 0, 0};

	// Look for key in pages in memory
	for (int i = 0; i < 2; i++) {
		int pageNum = pageNums[i];
		checked[pageNum - 1] = 1;
		if (pages[i].findKey(key) != -1) { return i; }
	}

	// Look for key in pages on disk
	for (int i = 0; i < 4; i++) {
		if (checked[i] == 0) {
			int pageNum = i + 1;
			int index = selectVictim();
			pageOut(index);
			pageIn(index, pageNum);
			if (pages[index].findKey(key) != -1) { return index; }
		}
	}

	return -1;
}

int BufferManager::getBlockOffset(int pageNum) { return (pageNum - 1) * 8; }

int BufferManager::selectVictim() {
	// When looking for a victim, we simply find the first unpinned page
	for (int i = 0; i < 2; i++) { if (pinned[i] == 0) { return i; } }

	return -1;
}

int BufferManager::getNumAtIndex(int index) { return pageNums[index]; }

void BufferManager::pageIn(int index, int pageNum) {
	// Read page data from disk into buffer
	char buffer[4096] = {};
	int blockOffset = getBlockOffset(pageNum);
	fm->read(dbName, blockOffset, 8, buffer);

	// Create page
	pages[index] = Page(buffer);

	// Set page status
	pageNums[index] = pageNum;
	dirtied[index] = 0;
	pinned[index] = 0;
}

void BufferManager::pageOut(int index) {
	// If page is dirty, write it to disk
	if (dirtied[index] == 1) {
		char buffer[4096] = {};
		char* pageBytes = (char*) &pages[index];
		for (int i = 0; i < 4096; i++) { buffer[i] = pageBytes[i]; }
		int blockOffset = getBlockOffset(pageNums[index]);
		fm->write(dbName, blockOffset, 8, buffer);
	}

	// Evict page
	pages[index] = Page();

	// Reset page status
	pageNums[index] = 0;
	dirtied[index] = 0;
	pinned[index] = 0;
}

bool BufferManager::insertRecord(int index, int key, char* data) {
	// Dirty and pin page
	dirtied[index] = 1;
	pinned[index] = 1;

	// Insert record and store result
	bool result = pages[index].insertRecord(key, data);

	// Unpin page
	pinned[index] = 0;

	// Return result
	return result;
}

bool BufferManager::removeRecord(int index, int key) {
	// Dirty and pin page
	dirtied[index] = 1;
	pinned[index] = 1;

	// Remove record and store result
	bool result = pages[index].removeRecord(key);

	// Unpin page
	pinned[index] = 0;

	// Return result
	return result;
}

Record BufferManager::locateRecord(int index, int key) {
	// Pin page
	pinned[index] = 1;

	// Locate record and store result
	Record result = pages[index].locateRecord(key);

	// Unpin page
	pinned[index] = 0;

	// Return result
	return result;
}

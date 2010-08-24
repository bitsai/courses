#include "FileManager.cpp"
#include "Page.cpp"

class BufferManager {
	private:
		FileManager* fm;
		Page pages[2];
		int pageNums[2];
		int dirtied[2];
		int pinned[2];
	public:
		BufferManager();

		void setFileManager(FileManager* inFM);
		void start();
		void shutDown();

		int findKey(int key);
		int getBlockOffset(int pageNum);
		int selectVictim();

		void pageIn(int index, int pageNum);
		void pageOut(int index);

		bool insertRecord(int key, char* data);
		bool removeRecord(int key);
		Record* locateRecord(int key);
};

BufferManager::BufferManager() {}

void BufferManager::setFileManager(FileManager* inFM) { fm = inFM; }

void BufferManager::start() {
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

void BufferManager::pageIn(int index, int pageNum) {
	// Read page data from disk into buffer
	char buffer[4096] = {};
	int blockOffset = getBlockOffset(pageNum);
	fm->read(blockOffset, 8, buffer);

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
		fm->write(blockOffset, 8, buffer);
	}

	// Evict page
	pages[index] = Page();

	// Reset page status
	pageNums[index] = 0;
	dirtied[index] = 0;
	pinned[index] = 0;
}

bool BufferManager::insertRecord(int key, char* data) {
	// Find memory index of page containing key
	int index = findKey(key);

	// If no such page is found, find memory index of non-full page
	if (index == -1) { index = findKey(0); }

	// If no such page is found, we're out of luck
	if (index == -1) { return false; }

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

bool BufferManager::removeRecord(int key) {
	// Find memory index of page containing key
	int index = findKey(key);

	// If no such page is found, we're out of luck
	if (index == -1) { return false; }

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

Record* BufferManager::locateRecord(int key) {
	// Find memory index of page containing key
	int index = findKey(key);

	// If no such page is found, we're out of luck
	if (index == -1) { return new Record(); }

	// Pin page
	pinned[index] = 1;

	// Locate record and store result
	Record* result = pages[index].locateRecord(key);

	// Unpin page
	pinned[index] = 0;

	// Return result
	return result;
}

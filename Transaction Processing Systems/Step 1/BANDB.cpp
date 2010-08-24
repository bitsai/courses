#include "BufferManager.cpp"

class BANDB {
	private:
		BufferManager* bm;
		FileManager* fm;
	public:
		BANDB();

		void createDB(char* fileName);
		void openDB(char* fileName);
		void shutDown();

		bool write(int key, char* data);
		char* read(int key);
		bool remove(int key);
};

BANDB::BANDB() {
	bm = new BufferManager();
	fm = new FileManager();
	bm->setFileManager(fm);
}

void BANDB::createDB(char* fileName) {
	// Create DB file, start buffer manager
	fm->create(fileName);
	bm->start();
}

void BANDB::openDB(char* fileName) {
	// Open DB file, start buffer manager
	fm->open(fileName);
	bm->start();
}

void BANDB::shutDown() { bm->shutDown(); }

bool BANDB::write(int key, char* data) { return bm->insertRecord(key, data); }

char* BANDB::read(int key) { return bm->locateRecord(key)->readData(); }

bool BANDB::remove(int key) { return bm->removeRecord(key); }

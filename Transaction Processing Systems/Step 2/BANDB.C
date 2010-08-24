#include "FileManager.C"
#include "BufferManager.C"
#include "LogManager.C"

class BANDB {
	private:
	public:
		FileManager* fm;
		BufferManager* bm;
		LogManager* lm;

		BANDB();

		void createDB(char* dbName, char* logName);
		void openDB(char* dbName, char* logName);
		void shutDown();

		bool write(int transID, int key, char* data);
		char* read(int transID, int key);
		bool remove(int transID, int key);

		void beginWork(int transID);
		void commitWork(int transID);
		void rollbackWork(int transID);

		void printLog();
};

BANDB::BANDB() {
	fm = new FileManager();
	bm = new BufferManager();
	bm->setFileManager(fm);
	lm = new LogManager();
	lm->setFileManager(fm);
}

void BANDB::createDB(char* dbName, char* logName) {
	// Create db and log files, start buffer and log managers
	fm->create(dbName);
	fm->create(logName);
	bm->start(dbName);
	lm->start(logName);
}

void BANDB::openDB(char* dbName, char* logName) {
	// Start buffer and log managers
	bm->start(dbName);
	lm->start(logName);
}

void BANDB::shutDown() {
	bm->shutDown();
	lm->shutDown();
}

bool BANDB::write(int transID, int key, char* data) {
	int pageIndex = bm->findKey(key);
	if (pageIndex == -1) { pageIndex = bm->findKey(0); }
	if (pageIndex == -1) { return false; }
	int pageNum = bm->getNumAtIndex(pageIndex);

	Record oldRec = bm->locateRecord(pageIndex, key);
	char* oldData = (char*) &oldRec;

	Record newRec = Record(key, data);
	char* newData = (char*) &newRec;

	bool result = bm->insertRecord(pageIndex, key, data);
	lm->writeWriteRecord(transID, pageNum, oldData, newData);
	return result;
}

char* BANDB::read(int transID, int key) {
	int pageIndex = bm->findKey(key);
	if (pageIndex == -1) { return ""; }

	Record rec = bm->locateRecord(pageIndex, key);
	return rec.readData();
}

bool BANDB::remove(int transID, int key) {
	int pageIndex = bm->findKey(key);
	if (pageIndex == -1) { return false; }
	int pageNum = bm->getNumAtIndex(pageIndex);

	Record oldRec = bm->locateRecord(pageIndex, key);
	char* oldData = (char*) &oldRec;

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	bool result = bm->removeRecord(pageIndex, key);
	lm->writeWriteRecord(transID, pageNum, oldData, empty);
	return result;
}

void BANDB::beginWork(int transID) { lm->writeBeginRecord(transID); }

void BANDB::commitWork(int transID) { lm->writeCommitRecord(transID); }

void BANDB::rollbackWork(int transID) { lm->writeRollbackRecord(transID); }

void BANDB::printLog() { lm->printLog(); }

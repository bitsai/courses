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
		void die();

		bool write(int transID, int key, char* data);
		char* read(int transID, int key);
		bool remove(int transID, int key);

		void beginWork(int transID);
		void commitWork(int transID);
		void rollbackWork(int transID);
		void rollbackWork(int transID, int savepoint);

		int getSavepoint(int transID);

		void printLog();
		void printTxnTable();
};

BANDB::BANDB() {
	fm = new FileManager();
	bm = new BufferManager();
	bm->setFileManager(fm);
	lm = new LogManager();
	lm->setFileManager(fm);
	lm->setBufferManager(bm);
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
	// Now the log manager has done all txnTable rebuilding and
	// all redo work, so we can rollback incomplete Txns
	for (int i = 0; i < 255; i++) {
		TxnRecord rec = lm->txnTable.records[i];
		if (rec.transID != 0) {
			// For all the transactions
			if (rec.state == 3) {
				// If it is committed but not ended, write the end record
				lm->writeEndRecord(rec.transID);
			}
			else if (rec.state != 5) {
				// Otherwise, if it is not an ended Txn, its a looser
				rollbackWork(rec.transID);
				lm->writeEndRecord(rec.transID);
			}
		}
	}
	// Harden All Pages
	bm->pageOut(0);
	bm->pageOut(1);
}

void BANDB::shutDown() {
	bm->shutDown();
	lm->shutDown();
}

void BANDB::die() {
	lm->shutDown();
}

bool BANDB::write(int transID, int key, char* data) {
	int pageIndex = bm->findKey(key);
	if (pageIndex == -1) { pageIndex = bm->findKey(0); }
	if (pageIndex == -1) { return false; }
	int pageNum = bm->getNumAtIndex(pageIndex);

	Record* oldRecP = bm->locateRecord(pageIndex, key);
	Record oldRec = *oldRecP;
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

	Record* rec = bm->locateRecord(pageIndex, key);
	return rec->readData();
}

bool BANDB::remove(int transID, int key) {
	int pageIndex = bm->findKey(key);
	if (pageIndex == -1) { return false; }
	int pageNum = bm->getNumAtIndex(pageIndex);

	Record* oldRecP = bm->locateRecord(pageIndex, key);
	Record oldRec = *oldRecP;
	char* oldData = (char*) &oldRec;

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	bool result = bm->removeRecord(pageIndex, key);
	lm->writeWriteRecord(transID, pageNum, oldData, empty);
	return result;
}

void BANDB::beginWork(int transID) { lm->writeBeginRecord(transID); }

void BANDB::commitWork(int transID) { lm->writeCommitRecord(transID); }

void BANDB::rollbackWork(int transID) {
	rollbackWork(transID, 0);
}

void BANDB::rollbackWork(int transID, int savepoint) {
	int undoNext = lm->txnTable.locateTxnRecord(transID)->lastLSN;
	while (undoNext > savepoint) {
		LogRecord lr = lm->log[undoNext-1];
		if (lr.type == 2) {
			// first, we recover the old record, which gives us the key and data
			Record* r = new Record(lr.oldData);
			
			// now we write over the data in the page:
			int pageIndex = bm->findKey(r->key);
			if (pageIndex == -1) { pageIndex = bm->findKey(0); }
			int pageNum = bm->getNumAtIndex(pageIndex);


			// If this was an insert, old data will be null, so we need to recover
			// the key from "new data" and remove it from the db
			if (r->key == 0) {
				Record* r2 = new Record(lr.newData);
				pageIndex = bm->findKey(r2->key);
				pageNum = bm->getNumAtIndex(pageIndex);
				bm->removeRecord(pageIndex, r2->key);
			} else {
				bm->insertRecord(pageIndex, r->key, r->data);
			}

			// then write the CLR
			lm->writeRollbackRecord(transID, undoNext, r->data);
		}
		undoNext = lr.prevLSN;
	}
	// If we rolled it back all the way, we are in the abort state
	TxnRecord* rec = lm->txnTable.locateTxnRecord(transID);
	if (undoNext == 0) {
		rec->state = 4;
	} else {
		rec->state = 1;
	}
}

int BANDB::getSavepoint(int transID) { return lm->getSavepoint(transID); }

void BANDB::printLog() { lm->printLog(); }

void BANDB::printTxnTable() { lm->printTxnTable(); }

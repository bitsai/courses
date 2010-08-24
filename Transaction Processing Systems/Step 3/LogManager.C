#include "LogRecord.C"
#include "TxnTable.C"

class LogManager {
	private:
	public:
		// Class variables

		// The Buffer Manager is used only for redo work, since we can interleave
		// log reading, TxnTable rebuild, and redo very nicely
		BufferManager* bm;
		FileManager* fm;
		char* logName;
		int head;
		int tail;
		int lastForcedLSN;
		LogRecord log[292];
		TxnTable txnTable;

		// Constructor
		LogManager();

		// Class functions
		void setFileManager(FileManager* inFM);
		void setBufferManager(BufferManager* inBM);
		void start(char* inLogName);
		void shutDown();

		void writeLogRecord(int LSN, int type, int transID, int prevLSN, int pageID, int undoNxtLSN, char* oldData, char* newData);
		void writeBeginRecord(int transID);
		void writeWriteRecord(int transID, int pageNum, char* oldData, char* newData);
		void writeCommitRecord(int transID);
		void writeRollbackRecord(int transID, int sourceLSN, char* newData);
		void writeEndRecord(int transID);

		void forceLSNRecord(int index);
		void forceLSN(int LSN);
		void truncate(int LSN);
		int getNextLSN();

		int getSavepoint(int transID);

		void printLog();
		void printTxnTable();
		char* translateType(int type);
};

LogManager::LogManager() {
	head = 0;
	tail = 0;
	lastForcedLSN = -1;
	for (int i = 0; i < 292; i++) { log[i] = LogRecord(); }
}

void LogManager::setFileManager(FileManager* inFM) { fm = inFM; }

void LogManager::setBufferManager(BufferManager* inBM) { bm = inBM; }

void LogManager::start(char* inLogName) {
	logName = inLogName;
	char page[4096] = {};

	for (int pageNum = 0; pageNum < 4; pageNum++) {
		fm->read(logName, pageNum * 8, 8, page);

		for (int i = 0; i < 73; i++) {
			char bytes[56] = {};
			int start = i * 56;

			for (int j = 0; j < 56; j++) { bytes[j] = page[start + j]; }
			LogRecord rec = LogRecord(bytes);

			if (rec.LSN == 0) { return; }
			else if (rec.LSN != 0) {
				// Txn Table Rebuild Work
				int index = txnTable.findTransID(rec.transID);
				if (index == -1) {
					// Transaction we haven't seen
					txnTable.insertTxnRecord(rec.transID);
					txnTable.updateTxnRecord(rec.transID, rec.type, rec.LSN, rec.LSN, rec.LSN);
				}
				else {
					// Existing Transaction
					txnTable.updateTxnRecord(rec.transID, rec.type, txnTable.records[index].firstLSN, rec.LSN, txnTable.records[index].lastLSN);
				}
				// Redo Work
				if (rec.type == 2) {
					// first, we recover the old record, which gives us the key and data
					Record* r = new Record(rec.oldData);
					// now we write over the data in the page:
					int pageIndex = bm->findKey(r->key);
					int pageNum = bm->getNumAtIndex(pageIndex);
					// Check the LSN to see if we need to redo
					if (bm->pages[pageNum].LSN < rec.LSN) {
						bm->insertRecord(pageIndex, r->key, r->data);
					}
				}
				log[tail] = rec;
				tail++;
			}
		}
	}
	// Harden all pages
	bm->pageOut(0);
	bm->pageOut(1);
}

void LogManager::shutDown() {
	forceLSN(tail);
}

void LogManager::writeLogRecord(int LSN, int type, int transID, int prevLSN, int pageID, int undoNxtLSN, char* oldData, char* newData) {
	log[tail] = LogRecord(LSN, type, transID, prevLSN, pageID, undoNxtLSN, oldData, newData);
	tail++;
}

void LogManager::writeBeginRecord(int transID) {
	txnTable.insertTxnRecord(transID);

	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	writeLogRecord(newLSN, 1, transID, rec->lastLSN, 0, 0, empty, empty);
	rec->lastLSN = newLSN;

	rec->state = 1;
	rec->firstLSN = newLSN;
}

void LogManager::writeWriteRecord(int transID, int pageID, char* oldData, char* newData) {
	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	writeLogRecord(getNextLSN(), 2, transID, rec->lastLSN, pageID, 0, oldData, newData);
	rec->lastLSN = newLSN;
}

void LogManager::writeCommitRecord(int transID) {
	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	writeLogRecord(getNextLSN(), 3, transID, rec->lastLSN, 0, 0, empty, empty);
	rec->lastLSN = newLSN;

	rec->state = 3;
}

void LogManager::writeRollbackRecord(int transID, int sourceLSN, char* newData) {
	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	LogRecord lr = log[sourceLSN-1];

	writeLogRecord(getNextLSN(), 4, transID, rec->lastLSN, 0, lr.prevLSN, empty, newData);
	rec->lastLSN = newLSN;

	rec->state = 4;
}

void LogManager::writeEndRecord(int transID) {
	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	writeLogRecord(getNextLSN(), 5, transID, rec->lastLSN, 0, 0, empty, empty);
	rec->lastLSN = newLSN;

	rec->state = 5;
}

void LogManager::forceLSNRecord(int index) {
	for (int pageNum = 0; pageNum < 4; pageNum++) {
		char page[4096] = {};
		fm->read(logName, pageNum * 8, 8, page);

		for (int i = 0; i < 73; i++) {
			char bytes[56] = {};
			int start = i * 56;

			for (int j = 0; j < 56; j++) { bytes[j] = page[start + j]; }
			LogRecord rec = LogRecord(bytes);

			if (rec.LSN == 0) {
				char* recordBytes = (char*) &log[index];
				for (int k = 0; k < 56; k++) { page[start + k] = recordBytes[k]; }
				fm->write(logName, pageNum * 8, 8, page);
				return;
			}
		}
	}
}

void LogManager::forceLSN(int LSN) {
	// May need to change this, depending on what happens to head and tail on force

	for (int index = 0; index < LSN; index++) {
		forceLSNRecord(index);
	}

	lastForcedLSN = LSN;
}

void LogManager::truncate(int LSN) {
	// May need to change this, depending on what truncate actually needs to do

	for (int i = 0; i < 292; i++) {
		if (log[i].LSN == LSN) {
			head = i;
		}
	}
}

int LogManager::getNextLSN() { return tail + 1; }

int LogManager::getSavepoint(int transID) {
	return txnTable.locateTxnRecord(transID)->lastLSN;
}

void LogManager::printLog() {
	cout << "-- Log --" << endl;
	cout << "[ID]\tTn\tType\tPrevLSN\t(Old Data) -> (New Data)" << endl;
	for (int index = head; index < tail; index++) {
		LogRecord rec = log[index];

		cout << "[" << rec.LSN << "]\t";
		cout << "T" << rec.transID << "\t";
		cout << translateType(rec.type) << "\t";
		cout << rec.prevLSN << "\t";

		if (rec.type == 2) {
			char* oldData = rec.oldData;
			cout << "(";
			for (int i = 0; i < 16; i++) { cout << oldData[i]; }
			cout << ") -> (";

			char* newData = rec.newData;
			for (int i = 0; i < 16; i++) { cout << newData[i]; }
			cout << ")";
		}
		cout << endl;
	}
}

void LogManager::printTxnTable() {
	cout << "-- Txn Table --" << endl;
	cout << "[ID]\tState\tFirst\tLast\tUndoNxt" << endl;
	for (int index = 0; index < 255; index++) {
		TxnRecord rec = txnTable.records[index];
		if (rec.transID > 0) {
			cout << "[" << rec.transID << "]\t";
			cout << translateType(rec.state) << "\t";
			cout << rec.firstLSN << "\t";
			cout << rec.lastLSN << "\t";
			cout << rec.undoNxtLSN << endl;
			/*
			cout << "Txn ID: " << rec.transID << endl;
			cout << "State: " << translateType(rec.state) << endl;
			cout << "First LSN: " << rec.firstLSN << endl;
			cout << "Last LSN: " << rec.lastLSN << endl;
			cout << "UndoNxtLSN: " << rec.undoNxtLSN << endl;
			*/
		}
	}
}

char* LogManager::translateType(int type) {
	if (type == 1) { return "Active"; }
	if (type == 2) { return "Write"; }
	if (type == 3) { return "Commit"; }
	if (type == 4) { return "Abort"; }
	if (type == 5) { return "End"; }
}

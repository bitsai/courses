#include "LogRecord.C"
#include "TxnTable.C"

class LogManager {
	private:
	public:
		// Class variables
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
		void start(char* inLogName);
		void shutDown();

		void writeLogRecord(int LSN, int type, int transID, int prevLSN, int pageID, int undoNxtLSN, char* oldData, char* newData);
		void writeBeginRecord(int transID);
		void writeWriteRecord(int transID, int pageNum, char* oldData, char* newData);
		void writeCommitRecord(int transID);
		void writeRollbackRecord(int transID);
		void writeEndRecord(int transID);

		void forceLSNRecord(int index);
		void forceLSN(int LSN);
		void truncate(int LSN);
		int getNextLSN();

		void printLog();
		char* translateType(int type);
};

LogManager::LogManager() {
	head = 0;
	tail = 0;
	lastForcedLSN = -1;
	for (int i = 0; i < 292; i++) { log[i] = LogRecord(); }
}

void LogManager::setFileManager(FileManager* inFM) { fm = inFM; }

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
				log[tail] = rec;
				tail++;
			}
		}
	}
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

void LogManager::writeRollbackRecord(int transID) {
	int newLSN = getNextLSN();
	TxnRecord* rec = txnTable.locateTxnRecord(transID);

	char empty[16] = {};
	for (int i = 0; i < 16; i++) { empty[i] = 0; }

	writeLogRecord(getNextLSN(), 4, transID, rec->lastLSN, 0, 0, empty, empty);
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

void LogManager::printLog() {
	for (int index = head; index < tail; index++) {
		LogRecord rec = log[index];

		cout << endl;
		cout << "LSN: " << rec.LSN << endl;
		cout << "Transaction: " << rec.transID << endl;
		cout << "Type: " << translateType(rec.type) << endl;
		cout << "Previous: " << rec.prevLSN << endl;

		if (rec.type == 2) {
			char* oldData = rec.oldData;
			cout << "Old data: ";
			for (int i = 0; i < 16; i++) { cout << oldData[i]; }
			cout << endl;

			char* newData = rec.newData;
			cout << "New data: ";
			for (int i = 0; i < 16; i++) { cout << newData[i]; }
			cout << endl;
		}
	}
}

char* LogManager::translateType(int type) {
	if (type == 1) { return "Begin"; }
	if (type == 2) { return "Write"; }
	if (type == 3) { return "Commit"; }
	if (type == 4) { return "Rollback"; }
	if (type == 5) { return "End"; }
}

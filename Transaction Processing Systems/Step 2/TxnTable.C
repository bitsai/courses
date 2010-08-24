#include "TxnRecord.C"

class TxnTable {
	private:
	public:
		TxnRecord records[255];

		TxnTable();

		int findTransID(int transID);

		bool insertTxnRecord(int transID);
		bool updateTxnRecord(int transID, int state, int firstLSN, int lastLSN, int undoNxtLSN);
		TxnRecord* locateTxnRecord(int transID);
};

TxnTable::TxnTable() {
	for (int i = 0; i < 255; i++) { records[i] = TxnRecord(); }
}

int TxnTable::findTransID(int transID) {
	// If table contains key, return its index
	for (int i = 0; i < 255; i++) { if (records[i].transID == transID) { return i; } }

	return -1;
}

bool TxnTable::insertTxnRecord(int transID) {
	// Find record index of blank record
	int index = findTransID(0);

	// If no such record is found, we're out of luck
	if (index == -1) { return false; }

	// Insert record
	records[index].transID = transID;

	return true;
}

TxnRecord* TxnTable::locateTxnRecord(int transID) {
	// Find record index of record containing transaction ID
	int index = findTransID(transID);

	return &records[index];
}

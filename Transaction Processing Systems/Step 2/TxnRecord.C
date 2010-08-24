class TxnRecord {
	private:
	public:
		int transID;
		int state;
		int firstLSN;
		int lastLSN;
		int undoNxtLSN;

		TxnRecord();
		TxnRecord(int inTransID, int inState, int inFirstLSN, int inLastLSN, int inUndoNxtLSN);
};

TxnRecord::TxnRecord() {
	transID = 0;
	state = 0;
	firstLSN = 0;
	lastLSN = 0;
	undoNxtLSN = 0;
}

TxnRecord::TxnRecord(int inTransID, int inState, int inFirstLSN, int inLastLSN, int inUndoNxtLSN) {
	transID = inTransID;
	state = inState;
	firstLSN = inFirstLSN;
	lastLSN = inLastLSN;
	undoNxtLSN = inUndoNxtLSN;
}

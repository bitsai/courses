class LogRecord {
	private:
	public:
		int LSN;
		int type;
		int transID;
		int prevLSN;
		int pageID;
		int undoNxtLSN;
		char oldData[16];
		char newData[16];

		LogRecord();
		LogRecord(char* buffer);
		LogRecord(int inLSN, int inType, int inTransID, int inPrevLSN, int inPageID, int inUndoNxtLSN, char* inOldData, char* inNewData);
};

LogRecord::LogRecord() {
	LSN = 0;
	type = 0;
	transID = 0;
	prevLSN = 0;
	pageID = 0;
	undoNxtLSN = 0;
	for (int i = 0; i < 12; i++) { oldData[i] = 0; }
	for (int i = 0; i < 12; i++) { newData[i] = 0; }
}

LogRecord::LogRecord(int inLSN, int inType, int inTransID, int inPrevLSN, int inPageID, int inUndoNxtLSN, char* inOldData, char* inNewData) {
	LSN = inLSN;
	type = inType;
	transID = inTransID;
	prevLSN = inPrevLSN;
	pageID = inPageID;
	undoNxtLSN = inUndoNxtLSN;
	for (int i = 0; i < 16; i++) { oldData[i] = inOldData[i]; }
	for (int i = 0; i < 16; i++) { newData[i] = inNewData[i]; }
}

LogRecord::LogRecord(char* buffer) {
	char intBytes[4];

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i]; }
	LSN = *((int*) intBytes);

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i + 4]; }
	type = *((int*) intBytes);

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i + 8]; }
	transID = *((int*) intBytes);

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i + 12]; }
	prevLSN = *((int*) intBytes);

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i + 16]; }
	pageID = *((int*) intBytes);

	for (int i = 0; i < 4; i++) { intBytes[i] = buffer[i + 20]; }
	undoNxtLSN = *((int*) intBytes);

	for (int i = 0; i < 16; i++) { oldData[i] = buffer[i + 24]; }
	for (int i = 0; i < 16; i++) { newData[i] = buffer[i + 40]; }
}

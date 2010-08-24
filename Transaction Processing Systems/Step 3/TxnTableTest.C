#include "TxnTable.C"
#include <iostream>

int main(char args[]) {
	cout << "Creating a Table\n";
	TxnTable tt = TxnTable();
	tt.printTable();
	cout << "Creating 20 transactions\n";
	for (int i = 0; i < 20; i++) {
		tt.createTxnRecord(i);
	}
	tt.printTable();
	cout << "Setting even txns to commit status\n";
	for (int i = 0; i < 20; i+=2) {
		TxnRecord temp = tt.getTxnRecord(i);
		tt.updateTxnRecord(i, 'c', temp.getFirstLSN(),
				temp.getLastLSN(), temp.getUndoNextLSN());
	}
	tt.printTable();
}

#include "BANDB.C"

int main() {
	BANDB* db = new BANDB();
	db->createDB("Test.db", "Test.log");

	cout << "--- Create two transactions that perform some data operations ---" << endl;
	cout << "\tBegin T1" << endl;
	db->beginWork(1);
	cout << "\tBegin T2" << endl;
	db->beginWork(2);
	cout << "\tT1 writing value 65432165432 to key 1" << endl;
	db->write(1, 1, "65432165432");
	cout << "\tT2 writing value 65432165432 to key 2" << endl;
	db->write(2, 2, "65432165432");

	cout << "--- Take a savepoint on both transactions ---" << endl;
	int T1Save = db->getSavepoint(1);
	cout << "\tT1 Savepoint LSN: " << T1Save << endl;
	int T2Save = db->getSavepoint(2);
	cout << "\tT2 Savepoint LSN: " << T2Save << endl;

	cout << "--- Perform more operations for both transactions ---" << endl;
	cout << "\tT1 removing key 1" << endl;
	db->remove(1, 1);
	cout << "\tT2 removing key 2" << endl;
	db->remove(2, 2);

	cout << "--- Perform a complete rollback of one transaction ---" << endl;
	cout << "\tRolling back T1" << endl;
	db->rollbackWork(1);

	cout << "--- Perform a partial rollback of the other transaction ---" << endl;
	cout << "\tRolling back T2 to Savepoint LSN " << T2Save << endl;
	db->rollbackWork(2, T2Save);

	cout << "--- Create several transactions, so at least one transaction is in each of the commit, abort, and active states ---" << endl;
	cout << "\tBegin T3" << endl;
	db->beginWork(3);
	cout << "\tBegin T4" << endl;
	db->beginWork(4);
	cout << "\tBegin T5" << endl;
	db->beginWork(5);

	cout << "\tT3 writing value 65432165432 to key 3" << endl;
	db->write(3, 3, "65432165432");
	cout << "\tT4 writing value 65432165432 to key 4" << endl;
	db->write(4, 4, "65432165432");
	cout << "\tT5 writing value 65432165432 to key 5" << endl;
	db->write(5, 5, "65432165432");

	cout << "\tCommit T3" << endl;
	db->commitWork(3);

	cout << "\tAbort T4" << endl;
	db->rollbackWork(4);

	cout << endl;
	db->printLog();

	cout << endl;
	db->printTxnTable();


	cout << endl;
	cout << "-- DB --" << endl;
	cout << "Key 1: " << db->read(5, 1) << endl;
	cout << "Key 2: " << db->read(5, 2) << endl;
	cout << "Key 3: " << db->read(5, 3) << endl;
	cout << "Key 4: " << db->read(5, 4) << endl;
	cout << "Key 5: " << db->read(5, 5) << endl;
	cout << endl;
	
	cout << "--- Exit the TP system abnormally, to simulate a failure ---" << endl;
	db->die();

	cout << "--- Start the TP system again, having it conduct restart processing ---" << endl;
	db = new BANDB();
	db->openDB("Test.db", "Test.log");

	cout << "--- Verify that the contents of the transaction table and the DB contents are consistent before and after failure ---" << endl << endl;
	db->printLog();

	cout << endl;
	db->printTxnTable();

	cout << endl;
	cout << "-- DB --" << endl;
	cout << "Key 1: " << db->read(5, 1) << endl;
	cout << "Key 2: " << db->read(5, 2) << endl;
	cout << "Key 3: " << db->read(5, 3) << endl;
	cout << "Key 4: " << db->read(5, 4) << endl;
	cout << "Key 5: " << db->read(5, 5) << endl;

	db->shutDown();
}

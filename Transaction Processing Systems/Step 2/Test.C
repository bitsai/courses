#include "BANDB.C"

int main() {
	BANDB* db = new BANDB();
	db->createDB("Test.db", "Test.log");
/*
	// Create 1000 records
	cout << "Creating 1000 records..." << endl;
	for (int i = 0; i < 1000; i++) { db->write(1, i + 1, "7654321"); }

	// Read 1000 records
	cout << "Reading 1000 records... ";
	int count = 0;
	for (int i = 0; i < 1000; i++) {
		if (strcmp(db->read(1, i + 1), "7654321") == 0) {
			count++;
		}
		else {
			cout << i << endl;
		}
	}
	cout << count << " records read successfully" << endl;

	// Delete records with even keys
	cout << "Deleting records with even keys..." << endl;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 0) { db->remove(1, i + 1); } }

	// Read records with odd keys
	cout << "Reading records with odd keys... ";
	count = 0;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 1) { if (strcmp(db->read(1, i + 1), "7654321") == 0) { count++; } } }
	cout << count << " records read successfully" << endl;

	// Read records with even keys
	cout << "Reading records with even keys... ";
	count = 0;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 0) { if (strcmp(db->read(1, i + 1), "7654321") == 0) { count++; } } }
	cout << count << " records read successfully" << endl;
*/

	cout << "Beginning all txn's..." << endl;
	db->beginWork(1);
	db->beginWork(2);
	db->beginWork(3);
	db->beginWork(4);

	cout << "T1 populating DB with keys 1 through 6..." << endl;
	for (int i = 0; i < 6; i++) {
		db->write(1, i + 1, "123456123456");
	}

	cout << "T2 updating key 1..." << endl;
	db->write(2, 1, "654321654321");

	cout << "T3 removing key 4..." << endl;
	db->remove(3, 4);

	cout << "T4 reading keys 1 and 2...(no log records for these)" << endl;
	db->read(4, 1);
	db->read(4, 2);

	cout << "T2 updating key 2..." << endl;
	db->write(2, 2, "654321654321");

	cout << "T3 removing key 5..." << endl;
	db->remove(3, 5);

	cout << "T4 reading keys 3 and 4...(no log records for these)" << endl;
	db->read(4, 3);
	db->read(4, 4);

	cout << "T2 updating key 3..." << endl;
	db->write(2, 3, "654321654321");

	cout << "T3 removing key 6..." << endl;
	db->remove(3, 6);

	cout << "T4 reading keys 5 and 6...(no log records for these)" << endl;
	db->read(4, 5);
	db->read(4, 6);

	cout << "Commiting all txn's..." << endl;
	db->commitWork(1);
	db->commitWork(2);
	db->commitWork(3);
	db->commitWork(4);

	db->printLog();

	db->shutDown();
}

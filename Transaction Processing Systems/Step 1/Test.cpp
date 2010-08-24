#include "BANDB.cpp"

int main() {
	BANDB* db = new BANDB();
	db->createDB("Test.db");

	// Create 1000 records
	cout << "Creating 1000 records..." << endl;
	for (int i = 0; i < 1000; i++) { db->write(i + 1, "7654321"); }

	// Read 1000 records
	cout << "Reading 1000 records... ";
	int count = 0;
	for (int i = 0; i < 1000; i++) { if (strcmp(db->read(i + 1), "7654321") == 0) { count++; } }
	cout << count << " records read successfully" << endl;

	// Delete records with even keys
	cout << "Deleting records with even keys..." << endl;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 0) { db->remove(i + 1); } }

	// Read records with odd keys
	cout << "Reading records with odd keys... ";
	count = 0;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 1) { if (strcmp(db->read(i + 1), "7654321") == 0) { count++; } } }
	cout << count << " records read successfully" << endl;

	// Read records with even keys
	cout << "Reading records with even keys... ";
	count = 0;
	for (int i = 0; i < 1000; i++) { if (i % 2 == 0) { if (strcmp(db->read(i + 1), "7654321") == 0) { count++; } } }
	cout << count << " records read successfully" << endl;

	db->shutDown();
}

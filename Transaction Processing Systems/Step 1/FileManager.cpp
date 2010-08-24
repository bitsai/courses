#include <fstream>
#include <iostream>

using namespace std;

class FileManager {
	private:
		char* fileName;
	public:
		FileManager();

		void create(char* inFileName);
		void open(char* inFileName);
		void read(int blockOffset, int blockCount, char* buffer);
		void write(int blockOffset, int blockCount, char* buffer);
};

FileManager::FileManager() { fileName = ""; }

void FileManager::create(char* inFileName) {
	// Create blank pages
	char buffer[4 * 4096] = {};

	// Write blank pages to file
	fstream out(inFileName, ios::out | ios::binary);
	out.write(buffer, 4 * 4096);
	out.close();

	// Set file name
	fileName = inFileName;
}

void FileManager::open(char* inFileName) { fileName = inFileName; }

void FileManager::read(int blockOffset, int blockCount, char* buffer) {
	// Translate from block to byte offset and size
	int offset = blockOffset * 512;
	int size = blockCount * 512;

	// Read from file to buffer
	fstream in(fileName, ios::in | ios::binary);
	in.seekg(offset);
	in.read(buffer, size);
	in.close();
}

void FileManager::write(int blockOffset, int blockCount, char* buffer) {
	// Translate from block to byte offset and size
	int offset = blockOffset * 512;
	int size = blockCount * 512;

	// Write from buffer to file
	fstream out(fileName, ios::in | ios::out | ios::binary);
	out.seekp(offset);
	out.write(buffer, size);
	out.close();
}

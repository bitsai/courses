#include <fstream>
#include <iostream>
using namespace std;

class FileManager
{
	private:
	public:
		FileManager();
		void create(char* fileName);
		void read(char* fileName, int blockOffset, int blockCount, char* buffer);
		void write(char* fileName, int blockOffset, int blockCount, char* buffer);
};

FileManager::FileManager() {}

void FileManager::create(char* fileName) {
	// Create blank pages
	char buffer[4 * 4096] = {};

	// Write blank pages to file
	fstream out(fileName, ios::out | ios::binary);
	out.write(buffer, 4 * 4096);
	out.close();
}

void FileManager::read(char* fileName, int blockOffset, int blockCount, char* buffer) {
	// Translate from block to byte offset and size
	int offset = blockOffset * 512;
	int size = blockCount * 512;

	// Read from file to buffer
	fstream in(fileName, ios::in | ios::binary);
	in.seekg(offset);
	in.read(buffer, size);
	in.close();
}

void FileManager::write(char* fileName, int blockOffset, int blockCount, char* buffer) {
	// Translate from block to byte offset and size
	int offset = blockOffset * 512;
	int size = blockCount * 512;

	// Write from buffer to file
	fstream out(fileName, ios::in | ios::out | ios::binary);
	out.seekp(offset);
	out.write(buffer, size);
	out.close();
}

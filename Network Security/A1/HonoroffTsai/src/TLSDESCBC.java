////////////////////////////////////////////////////////////////////
// Simnet, version 0.9                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2004 //
////////////////////////////////////////////////////////////////////
package simnet;

import java.util.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.encodings.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.InvalidCipherTextException;
import java.io.*;

public class TLSDESCBC implements TLSCipherSuite {

	public int bitsize = 64;

	/**
	 *  Returns the key bitsize of this algorithm
	 *
	 *  @return int - an integer containing the bitsize of this algorithm
	 */
	public int getBitsize() {
		return bitsize;
	}

	/**
	 * Concatenate the byte arrays a and b
	 *
	 * @param a - the first array in the concatenation
	 * @param b - the second array in the concatenation
	 * @return byte[] - the result of the concatenation
	 */
	public byte[] bytecat(byte a[], byte b[]) {
		byte ret[] = new byte[a.length + b.length];
		int i, k;

		for (i=0, k=0;k<a.length;k++)
		{
			ret[i++] = a[k];
		}

		for (k=0;k<b.length;k++)
		{
			ret[i++] = b[k];
		}

		return ret;
	}

	/**
	 * Encrypts an array of bytes using DES in CBC-mode.
	 *
	 * @param in - array of bytes to be encrypted
	 * @param key - key used to decrypt the object
	 * @return byte[] - encypted version of the input.
	 */
	public byte[] encrypt(byte[] key, byte[] in, byte[] iv) {
		// Calculate size of block in bytes and number of needed blocks
		int bytesize = bitsize / 8;
		int blockNum = (int) ((in.length + 2) / bytesize) + 1;

		// Create padded byte array for plaintext and out byte array for ciphertext
		byte[] padded = new byte[blockNum * bytesize];
		byte[] out = new byte[blockNum * bytesize];

		// Write length of plaintext as first two bytes of padded plaintext
		byte[] lengthArray = TLS.makeTwoByteLength(in.length);
		for (int i = 0; i < 2; i++) { padded[i] = lengthArray[i]; }

		// Add original plaintext to padded byte array
		for (int i = 0; i < in.length; i++) { padded[i + 2] = in[i]; }
		// Padded array was initialized to zeros, so no need to pad extra bytes

		// Initialize DES-CBC encoder
		CBCBlockCipher encoder = new CBCBlockCipher(new DESEngine());
		ParametersWithIV params = new ParametersWithIV(new KeyParameter(key), iv, 0, 8);
		encoder.init(true, params);

		// Encode each block of padded and copy result to out
		for(int i = 0; i < padded.length / bytesize; i++) {
			encoder.processBlock(padded, i * bytesize, out, i * bytesize);
		}

		// Return out
		return out;
	}

	/**
	 * Decrypts an array of bytes using DES in CBC-mode.
	 *
	 * @param in - array of bytes to be encrypted
	 * @param key - key used to decrypt the object
	 * @return byte[] - decypted version of the input.
	 */
	public byte[] decrypt(byte[] key, byte[] in, byte[] iv) throws TLSException {
		// Calculate size of block in bytes and number of needed blocks
		int bytesize = bitsize / 8;
		int blockNum = in.length / bytesize;

		// Create plaintext byte array
		byte[] plaintext = new byte[blockNum * bytesize];

		// Initialize DES-CBC decoder
		CBCBlockCipher decoder = new CBCBlockCipher(new DESEngine());
		ParametersWithIV params = new ParametersWithIV(new KeyParameter(key), iv, 0, 8);
		decoder.init(false, params);

		// Encode each block of in and copy result to plaintext
		for(int i = 0; i < in.length / bytesize; i++) {
			decoder.processBlock(in, i * bytesize, plaintext, i * bytesize);
		}

		// Extract real length from plaintext
		int realLength = TLS.convertTwoByteLength(plaintext[0], plaintext[1]);

		// Create out byte array of proper length
		byte[] out = new byte[realLength];

		// Copy data into out
		for (int i = 0; i < realLength; i++) { out[i] = plaintext[i + 2]; }

		// Return out
		return out;
	}
}

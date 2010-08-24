////////////////////////////////////////////////////////////////////
// Simnet, version 1.0                                            //
// COPYRIGHT Seny Kamara, Darren Davis, Fabian Monrose, 2002-2005 //
////////////////////////////////////////////////////////////////////
package simnet;

import simnet.*;
import java.io.*;
import java.security.SecureRandom;
import java.math.BigInteger;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.crypto.signers.DSASigner;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.generators.DSAParametersGenerator;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.encodings.OAEPEncoding;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.util.encoders.Hex;

/**
 * Class that implements various cryptographic methods that might be
 * useful to Simnet applications.
 */
public class SimnetCryptoEngine {

	/**
	 * Instance of a cryptographically secure pseudorandom number generator
	 */
	private SecureRandom rand;

	/**
	 * Length of an SHA1 digest (bytes)
	 */
    public final static int SHA1_DIGEST_SIZE = 20;

	/**
	 * block size for 128-bit AES block ciphers (bytes)
	 */
	public final static int AES_BLOCK_SIZE = 16;

	/**
	 * Length of an MD5 digest (bytes)
	 */
	public final static int MD5_DIGEST_SIZE = 16;

	/**
	 * RSA-OAEP (768 bit modulus) input block size for Asymmetric block cipher (bytes)
	 */
	public final static int RSA_INPUT_SIZE = 54;

	/**
	 * RSA-OAEP (768 bit modulus) output block size for Asymmetric block cipher (bytes)
	 */
	public final static int RSA_OUTPUT_SIZE = 96;

	/**
	 * Instantiates the random number generator.
	 */
	public SimnetCryptoEngine() {
		rand = new SecureRandom();
	}

	/**
	 * Concatenate the byte arrays a and b.
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
	 * Utility method to check whether two arrays are equal (used for comparing hashes).
	 *
	 * @param a - Array to be compared.
	 * @param b - Array to be compared.
	 * @return boolean - whether or not the arrays hold the same values
	 */
	public boolean isEqual(byte [] a, byte [] b){
		if(a.length != b.length) return false;

		for(int i = 0; i < a.length; ++i)
			if(a[i] != b[i]) return false;

		return true;
	}

	/*
	 * getMD5Digest - gets the MD5 digest that appears in the packet.
	 *
	 * @param in - the input on which to preform the digest operation
	 * @return byte[] - the md5 hash as a byte array
	 */
	public byte[] getMD5Digest(byte in[]) {
		byte out[] = new byte[MD5_DIGEST_SIZE];
		MD5Digest digest = new MD5Digest();
		digest.update(in, 0, in.length);
		digest.doFinal(out, 0);
		return out;
	}

	/**
	 * Serialize and object using a ByteArrayOuputStream and ObjectOutputStream.
	 *
	 * @param o - the object to serialize
	 * @return byte[] - serialized version of the object
	 */
	public byte [] serialize(Object o) {
		byte[] retval;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			ObjectOutputStream obj = new ObjectOutputStream(out);
			obj.writeObject(o);
			obj.close();
			retval = out.toByteArray();
		}
		catch (Exception e) {
			return null;
		}

		return(retval);
	}

	/**
	 * Deserialize an array of bytes into an object.
	 *
	 * @param b - the array to deserialize
	 * @return Object - the original object
	 * @throws SimnetSerializationException if there is an issue with the underlying I/O stream.
	 */
	public Object deserialize(byte [] b) throws SimnetSerializationException {
		Object retval;
		ByteArrayInputStream in = new ByteArrayInputStream(b);

		try {
			ObjectInputStream obj = new ObjectInputStream(in);
			retval = obj.readObject();
			obj.close();
		}
		catch (Exception e) {
			throw new SimnetSerializationException("Error in Deserialization");
		}

		return(retval);
	}

	/**
	 * Encrypts an array of bytes using AES in CBC-mode.  Before encryption, the input is modified
	 * to have the following form:<br />
	 * <code>[ random | length of plaintext | MD5 Hash | plaintext | padding]</code><br />
	 * The first 16 bytes are random, the next 4 are the length of the original input, the next 16
	 * are an MD5 hash of the input.  This is followed by the input and finally padding.
	 *
	 * @param in - array of bytes to be encrypted
	 * @param key - key used to decrypt the object
	 * @return byte[] - encypted version of the input
	 */
	public byte [] encryptAESCBC(byte [] in, byte [] key){
	  int i;
	  byte[] cipher;
	  byte[] len;
	  byte[] r = new byte[AES_BLOCK_SIZE];
	  // get hash of in
	  byte[] hash = getMD5Digest(in);
	  // get random bytes for r
	  rand.nextBytes(r);
	  // get len as byte array
	  ByteArrayOutputStream lenout = new ByteArrayOutputStream();
	  DataOutputStream lendata = new DataOutputStream(lenout);
	  try{
	    lendata.writeInt(in.length);
	    lendata.close();
	  }
	  catch(Exception e) {
	    return null;
	  }
	  len = lenout.toByteArray();
	  int unpadlen = (AES_BLOCK_SIZE + len.length + MD5_DIGEST_SIZE + in.length);
	  int padlen = AES_BLOCK_SIZE - (unpadlen % AES_BLOCK_SIZE);
	  if(padlen == 0x10) {
	    padlen = 0;
	  }
	  int totallen = unpadlen + padlen;
	  byte[] plaintext = new byte[totallen];
	  int place = 0;
	  // build padded plaintext array
	  for(i = 0; i < AES_BLOCK_SIZE; i++) {
	    plaintext[place++] = r[i];
	  }
	  for(i = 0; i < len.length; i++) {
	    plaintext[place++] = len[i];
	  }
	  for(i = 0; i < MD5_DIGEST_SIZE; i++) {
	    plaintext[place++] = hash[i];
	  }
	  for(i = 0; i < in.length; i++) {
	    plaintext[place++] = in[i];
	  }
	  // we pad padlen bytes, where the padding byte is the padlen

	  for(i = 0; i < padlen; i++) {
	    plaintext[place++] = (byte) padlen;
	  }
	  cipher = new byte[plaintext.length];
	  // plaintext with integrity and padding has been built
	  // now we encrypt

	  KeyParameter keyparam = new KeyParameter(key);
	  AESFastEngine aes = new AESFastEngine();
	  CBCBlockCipher aescbc = new CBCBlockCipher(aes);
	  aescbc.init(true, keyparam);
	  for(i = 0; i < plaintext.length / AES_BLOCK_SIZE; i++) {
	    aescbc.processBlock(plaintext, i * AES_BLOCK_SIZE,
			  cipher,    i * AES_BLOCK_SIZE);
	  }

	  return(cipher);
	}

	/**
	 * Serializes an object and encrypts it using AES in CBC mode with a call to <code>encryptAESCBC()</code>.
	 *
	 * @param in - Object to be encrypted
	 * @param key - used to encrypt the object
	 * @return byte[] - AES-encrypted serialized version of the object
	 */
	public byte [] encryptObjectAESCBC(Object in, byte [] key) {
		byte[] serialized;
		byte[] cipher;
		serialized = serialize(in);
		cipher = encryptAESCBC(serialized, key);
		return(cipher);
	}

	/**
	 * Decrypts an array of bytes that were encrypted using AES in CBC-mode (by a call to <code>encryptAESCBC</code>).
	 * Checks validity of results based on the MD5 hash and the length of the plaintext.
	 *
	 * @param in - array of bytes to be decrypted
	 * @param key - key used for decryption
	 * @return byte[] - decrypted version of the input
	 * @throws SimnetDecryptionException when the result of the decryption is invalid.  This occurs when the
	 *         hash or length fields do not match and generally results from
	 *         encryption or decryption with the wrong key.
	 */
  public byte [] decryptAESCBC(byte [] in, byte [] key)
    throws SimnetDecryptionException{

      int i;
      int msglen;
      byte[] plaintext = new byte[in.length];
      byte[] len = new byte[4];
      byte[] decryptedHash = new byte[MD5_DIGEST_SIZE];
      byte[] computedHash;
      byte[] message;
      KeyParameter keyparam = new KeyParameter(key);
      AESFastEngine aes = new AESFastEngine();
      CBCBlockCipher aescbc = new CBCBlockCipher(aes);

      // make sure cipher is big enough to hold R, len, MD5 hash

      if(in.length < (3 * AES_BLOCK_SIZE)) {
	throw new SimnetDecryptionException("Ciphertext too small");
      }
      aescbc.init(false, keyparam);
      for(i = 0; i < in.length / AES_BLOCK_SIZE; i++) {
	aescbc.processBlock(in, i * AES_BLOCK_SIZE,
			    plaintext,    i * AES_BLOCK_SIZE);
      }
      int place;
      // have place skip over r at beginning
      place = AES_BLOCK_SIZE;
      for(i = 0; i < len.length; i++) {
	len[i] = plaintext[place++];
      }

      // get int value for message length from len buffer
      ByteArrayInputStream lenin = new ByteArrayInputStream(len);
      try{
	DataInputStream lendata = new DataInputStream(lenin);
	msglen = lendata.readInt();
	lendata.close();
      }
      catch(Exception e) {
	throw new SimnetDecryptionException ("Error reading msglen");
      }
      if(msglen > in.length - AES_BLOCK_SIZE - 4 - MD5_DIGEST_SIZE) {
	throw new SimnetDecryptionException ("Decrypted msglen too large");
      }

      // read decrypted hash into decryptedHash array
      for(i = 0; i < MD5_DIGEST_SIZE; i++) {
	decryptedHash[i] = plaintext[place++];
      }
      // read decrypted message into message array

      message = new byte[msglen];
      for(i = 0; i < msglen; i++) {
	message[i] = plaintext[place++];
      }
      // compute hash, and compare to decrypted hash
      computedHash = getMD5Digest(message);
      for(i = 0; i < MD5_DIGEST_SIZE; i++) {
	if(computedHash[i] != decryptedHash[i]) {
	  throw new SimnetDecryptionException ("Integrity check failed");
	}
      }
      // if we're here, we have correct decryption
      return(message);
  }

	/**
	 * Decrypts an array of bytes and serializes the result to an Object.
	 *
	 * @param in - array of bytes to be decrypted (returned from a call to <code>encryptObjectAESCBC()</code>)
	 * @param key - used to decrypt the object
	 * @return Object - decrypted deserialized version of the object
	 * @throws SimnetDecryptionException when the result of the decryption is invalid.
	 *         This occurs when the
	 *         hash or length fields do not match and generally results from
	 *         encryption or decryption with the wrong key.
	 */
	public Object decryptObjectAESCBC(byte [] in, byte [] key) throws SimnetDecryptionException {
		Object deserialized;
		byte[] message;

		try {
			message = decryptAESCBC(in, key);
			deserialized = deserialize(message);
		}
		catch (Exception e) {
			throw new SimnetDecryptionException(e.toString());
		}

		return(deserialized);
	}

	/**
	 * Gets random bytes from the 'rand' object.
	 *
	 * @param randombytes - the output array
	 */
	public void getRandomBytes(byte[] randombytes) {
		rand.nextBytes(randombytes);
	}

	/**
	 *  Computes a SHA1 HMac over a byte array.
	 *
	 *  @param b - the byte array on which to compute the HMac
	 *  @param key - the key with which to compute the HMac
	 *  @return byte[] - a byte array representing the computed HMac
	 */
	public byte[] HMacSHA1(byte[] b, byte[] key) {
		byte[] out = new byte[SHA1_DIGEST_SIZE];
		HMac mac = new HMac(new SHA1Digest());
		mac.init(new KeyParameter(key));
		mac.update(b, 0, b.length);
		mac.doFinal(out, 0);
		return out;
	}

	/**
	 *  Computes a MD5 HMac over a byte array.
	 *
	 *  @param b - the byte array on which to compute the HMac
	 *  @param key - the key with which to compute the HMac
	 *  @return byte[] - a byte array representing the computed HMac
	 */
	public byte[] HMacMD5(byte[] b, byte[] key) {
		byte[] out = new byte[MD5_DIGEST_SIZE];
		HMac mac = new HMac(new MD5Digest());
		mac.init(new KeyParameter(key));
		mac.update(b, 0, b.length);
		mac.doFinal(out, 0);
		return out;
	}

	/**
	 * getSHA1Digest - gets the SHA1 digest that appears in the packet.
	 *
	 * @param in - the input on which to preform the digest operation
	 * @return byte[] - the sha1 hash as a byte array
	 */
	public byte[] getSHA1Digest(byte[] in) {
		byte[] out = new byte[SHA1_DIGEST_SIZE];
		SHA1Digest digest = new SHA1Digest();
		digest.update(in, 0, in.length);
		digest.doFinal(out, 0);
		return(out);
	}

	/**
	 * Encrypt an array of bytes using RSA (actually runs as a block cipher).
	 * Uses OAEP for integrity/randomization.
	 *
	 * @param in - The bytes we are encrypting
	 * @param key - The public key
	 * @return byte[] - the encryption
	 */
	public byte[] encryptRSA(byte[] in, RSAKeyParameters key) {
		// Initialize RSA-OAEP encoder
		byte[] out = new byte[0];
		OAEPEncoding encoder = new OAEPEncoding(new RSAEngine());
		encoder.init(true, key);
		int start = 0;

		try {
			// Process full RSA_INPUT_SIZE'ed blocks
			while (start + RSA_INPUT_SIZE <= in.length) {
				byte[] block = new byte[RSA_INPUT_SIZE];
				for (int offset = 0; offset < RSA_INPUT_SIZE; offset++) { block[offset] = in[start + offset]; }
				out = bytecat(out, encoder.processBlock(block, 0, RSA_INPUT_SIZE));
				start += RSA_INPUT_SIZE;
			}

			// Process leftover bytes
			int remaining = in.length - start;
			byte[] remainder = new byte[remaining];
			for (int offset = 0; offset < remaining; offset++) { remainder[offset] = in[start + offset]; }
			if (remaining > 0) { out = bytecat(out, encoder.processBlock(remainder, 0, remaining)); }
		} catch (Exception e) {
			System.out.println(e);
		}

		// Return ciphertext
		return out;
	}

	/**
	 * Decrypt an array of bytes using RSA (actually runs as a block cipher).
	 * Uses OAEP for integrity/randomization.
	 *
	 * @param in - The bytes we are decrypting
	 * @param key - The private key
	 * @return byte[] - the decryption
	 * @throws SimnetDecryptionException when there is any problem with the process.
	 */
	public byte[] decryptRSA(byte[] in, RSAKeyParameters key) throws SimnetDecryptionException {
		// Initialize RSA-OAEP decoder
		byte[] out = new byte[0];
		OAEPEncoding decoder = new OAEPEncoding(new RSAEngine());
		decoder.init(false, key);
		int start = 0;

		try {
			// Process full RSA_OUTPUT_SIZE'ed blocks
			while (start + RSA_OUTPUT_SIZE <= in.length) {
				byte[] block = new byte[RSA_OUTPUT_SIZE];
				for (int offset = 0; offset < RSA_OUTPUT_SIZE; offset++) { block[offset] = in[start + offset]; }
				out = bytecat(out, decoder.processBlock(block, 0, RSA_OUTPUT_SIZE));
				start += RSA_OUTPUT_SIZE;
			}

			// Process remaining bytes; there shouldn't be any, but just in case
			int remaining = in.length - start;
			byte[] remainder = new byte[remaining];
			for (int offset = 0; offset < remaining; offset++) { remainder[offset] = in[start + offset]; }
			if (remaining > 0) { out = bytecat(out, decoder.processBlock(remainder, 0, remaining)); }
		} catch (Exception e) {
			throw new SimnetDecryptionException(e.toString());
		}

		// Return plaintext
		return out;
	}

	/**
	 * Computes the HmacSHA1 portion of the PRF function for TLS.
	 *
	 * @param secret - the secret on which to compute this PRF_HMacSHA1
	 * @param seed - the key/seed to use
	 * @return byte[] - a byte array containing the result
	 */
	public byte[] PRF_HMacSHA1(byte[] secret, byte[] seed) {
		byte[] X1 = HMacSHA1(secret, seed);
		byte[] X2 = bytecat(X1, seed);

		// Compute Y1 and set first portion of out to Y1
		byte[] Y = HMacSHA1(secret, X2);
		byte[] out = Y;

		// Compute Yi from Yi-1, and append Yi to out
		for (int i = 2; i <= 4; i++) {
			Y = HMacSHA1(secret, Y);
			out = bytecat(out, Y);
		}

		// Return out
		return out;
	}

	/**
	 * Computes the TLS psuedorandom function for computing the final keys.
	 *
	 * @param secret - byte array on which to compute the psuedorandom function
	 * @param label - the label with which to compute the prf
	 * @param seed - the seed/key with which to compute the prf
	 * @return byte[] - a byte array containing the 80 byte result of the prf function
	 */
	public byte[] PRF(byte[] secret, String label, byte[] seed) {
		byte[] labelArray = label.getBytes();
		byte[] seedPrime = bytecat(labelArray, seed);
		return PRF_HMacSHA1(secret, seedPrime);
	}
}

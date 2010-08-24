package A3;

import simnet.*;
import java.io.*;
import java.util.*;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.asn1.pkcs.*;

public class DNSSECInit extends Application implements Pluggable {
	private SimnetCryptoEngine sce;

	private Hashtable pubKeys;
	private Hashtable privKeys;
	private Hashtable keyRRs;

	public DNSSECInit() {
		sce = new SimnetCryptoEngine();
		pubKeys = new Hashtable();
		privKeys = new Hashtable();
		keyRRs = new Hashtable();
	}

	public void init(String DNSfile) {
		try {
			int index = DNSfile.lastIndexOf("/");
			String path = DNSfile.substring(0, index + 1);
			printout(0, node.id, path);

			BufferedReader br = new BufferedReader(new FileReader(DNSfile));
			String line;
			StringTokenizer st;

			while((line = br.readLine()) != null) {
				st = new StringTokenizer(line);

				if(st.hasMoreTokens()) {
					String first = st.nextToken();

					if(first.equals("ZONE")) {
						printout(0, node.id, line);

						String zone = st.nextToken();

						makeDSAKeys(path, zone);
						makeKeyRR(path, zone);
						makeSigRR(path, zone);
					}

					if(first.equals("SERVER")) {
						printout(0, node.id, line);

						String name = st.nextToken();
						String url = st.nextToken();

						makeRSAKeys(path, name, url);
					}
				}
			}

			br.close();
		} catch(Exception e) {
			printout(0, node.id, e.toString());
		}
	}

	public void makeDSAKeys(String path, String zone) throws IOException {
		AsymmetricCipherKeyPair keys = sce.generateDSAKeys();
		DSAPublicKeyParameters pubKey = (DSAPublicKeyParameters) keys.getPublic();
		DSAPrivateKeyParameters privKey = (DSAPrivateKeyParameters) keys.getPrivate();
		DSAParameters params = pubKey.getParameters();

		pubKeys.put(zone, pubKey);
		privKeys.put(zone, privKey);

		String P = params.getP().toString();
		String Q = params.getQ().toString();
		String G = params.getG().toString();
		String X = privKey.getX().toString();
		String Y = pubKey.getY().toString();

		String fileName = path + "DSA.KEY._" + zone + "_";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

		bw.write("DSA keys for the zone <" + zone + ">\n");
		bw.write("P\t" + P + "\n");
		bw.write("Q\t" + Q + "\n");
		bw.write("G\t" + G + "\n");
		bw.write("X\t" + X + "\n");
		bw.write("Y\t" + Y + "\n");

		bw.close();
	}

	public void makeKeyRR(String path, String zone) throws IOException {
		DSAPublicKeyParameters pubKey = (DSAPublicKeyParameters) pubKeys.get(zone);

		ResourceRecord keyRR = new ResourceRecord("KEY", zone, -1);
		keyRR.makeKEYRecord(pubKey);

		keyRRs.put(zone, keyRR);

		String fileName = path + "KEY.RR._" + zone + "_";
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));

		oos.writeObject(keyRR);

		oos.close();
	}

	public void makeSigRR(String path, String zone) throws IOException {
		String signer;

		if(zone.equals(".") || zone.indexOf(".") == (zone.length() - 1)) { signer = "."; }
		else {
			int index = zone.indexOf(".");
			signer = zone.substring(index + 1);
		}

		DSAPrivateKeyParameters privKey = (DSAPrivateKeyParameters) privKeys.get(signer);

		ResourceRecord keyRR = (ResourceRecord) keyRRs.get(zone);
		DSASignature signature = sce.DSASignObject(keyRR, privKey);

		ResourceRecord sigRR = new ResourceRecord("SIG", zone, -1);
		sigRR.makeSIGRecord("KEY", zone, signer, signature);

		String fileName = path + "SIG.RR._" + zone + "_";
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));

		oos.writeObject(sigRR);

		oos.close();
	}

	public void makeRSAKeys(String path, String name, String url) throws IOException {
		AsymmetricCipherKeyPair keys = sce.generateRSAKeys();
		RSAKeyParameters pubKey = (RSAKeyParameters) keys.getPublic();
		RSAKeyParameters privKey = (RSAKeyParameters) keys.getPrivate();

		String N = pubKey.getModulus().toString();
		String E = pubKey.getExponent().toString();
		String D = privKey.getExponent().toString();

		String fileName = path + "RSA.KEY._" + name + "_";
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

		bw.write("RSA keys for " + name + "\t" + url + "\n");
		bw.write("N\t" + N + "\n");
		bw.write("E\t" + E + "\n");
		bw.write("D\t" + D + "\n");

		bw.close();
	}

	public synchronized boolean prePlugout(Object replacement) {
		return true;
	}
}
Jacob Honoroff and Benny Tsai
A1 writeup

in this directory:
src/ : the src files for VPN and TLS
attack/ : contains modified TLSInteraction.java for our TLS attack 
	     (see NOTE below)
scripts/ : contains a script for running the attack and for testing vpn
Attacklog.txt : Output of our attack on TLS

See NOTE in part describing TLS attack about compiling and running the
attack.

Part 2 Written Questions

1.  The R ensures that the same plaintext encrypts to different
ciphertext, essentially serving as a random, per-packet Initialization
Vector.  In this protocol we use AES in CBC mode but without an
Initialization Vector, and instead use R as the first word of
plaintext to be encrypted.  Then, the first actual word of plaintext
has the encrypted R xor'd into it before encryption, ensuring that the
entire CBC chain on the actual plaintext has randomness added in.

2.  (a) DoS attack: A denial of service attack can be launched on a
server where the server's CPU gets bogged down doing many
computationally intensive Diffie-Hellman (DH) operations.  In the protocol,
the server does not authenticate the client before accepting its part
of the DH exchange, and does the computations after being sent only
one packet.  Therefore, a malicious client (or set of clients) can
send many requests to start the DH exchange without actually finishing
the exchange.  If the server is set up to run multiple exchanges at a
time, it could run out of CPU or memory to service all of the DH
operations, and if the server is set up to only to one DH at a time,
it could get bogged down with bogus requests and not service real
requests.

    (b) Reordering attack: There is nothing in the protocol that
guarantees delivery of the VPN messages in the correct order.  Nothing chains
multiple packets together such that a receiver would notice if the
packet ordering were switched.  Thus an attacker on the wire between
the two VPN routers could reorder the packets en route.

    (c) Suppression attack: Similar to the reasoning behind the
Reordering attack, because there is no linking between VPN packets,
nothing in the protocol guarantees that a receiver will notice when
one of the sender's packets has been dropped en route.  Furthermore,
if the underlying protocol is thought to be connection-oriented (such
as TCP) such that an adversary believes that the receiver would notice
dropped packets, it is possible that the adversary could recognize
encrypted resend messages from the receiver by their message length
and drop these as well.

***************************************************************

Part 3.9: TLS Attack

NOTE: To run our TLS attack, you must replace TLSInteraction.java with
the version that has the client use RC4, which is
attack/TLSInteraction.java.evil.  Professor Monrose said it was
acceptable to change certain files to guarantee that the client
ciphersuite lists RC4 first, rather than DES-CBC.  This file is only
different from TLSInteraction.java in the following four lines that
change the order of the client's cipher suites, and thus has the
server select RC4:

               ciphersuite[0] = 0x00;
               ciphersuite[1] = 0x05;
               ciphersuite[2] = 0x00;
               ciphersuite[3] = 0x09;

So to run the attack, execute the following commands:

cp src/TLSInteraction.java src/TLSInteraction.java.orig
cp attack/* src
mv src/TLSInteraction.java.evil src/TLSInteraction.java
ant
./Simnet -s scripts/crack.script
cp src/TLSInteraction.java.orig src/TLSInteraction.java

The flaw we exploited in TLS is in the implementation of RC4 in
TLSRC4.java.  In the initRC4 function, the iv parameter is not used in
the initialization of RC4 but rather the class member "counter" which
starts at 0 when the TLSRC4 object is constructed, and is incremented
by 1 for each init call.  This makes a very structured, predictable set of
initialization vectors.  In particular because it is a 3-byte IV and
is always known by an adversary simply by knowing the packet count, it
is vulnerable to the attack on WEP described by Lucas in lecture.

let K be the expanded key array (so the first byte of unexpanded key
is in K[3]).  Information about K[i] can be recovered when
the 3 iv bytes are {i, 255, x} for all values of x (i corresponds to
(A + 3) in Lucas' "WEP keys" slides).

In lecture, Lucas showed that to recover K[3], you needed to solve
K[3] = S^-1[e_0] - 6 - x

We generalized this for further bytes of K[i] to be
K[i] = S^-1[e_0] - 3 - (sum_n=3_to_i{n}) - (sum_n=3_to_(i-1){K[n]}) - x

Given a client-server echo-like application where each side sends
encrypted data and decrypts the other's encrypted data, each side's
RC4 counter increments by 2 between consecutive encryptions. So we
wait until packet 0x3ff00, and we get 128 packets to try and
to recover K[3] on each of the server side and client side key.  Then
when we get to packet 0x4ff00, we get another 128 packets that
may get us K[4] of the server key and the client key, and so on.

How much information do we get on each keybyte?  According to [1],
each packet has a 5% chance of being the correct byte.  Over 128
guesses, the expected number of times the correct byte is guessed will
be around 6, and the number of times the incorrect bytes are guessed
will be relatively evenly distributed with expected value < 1.  So we
expect to see the keybyte standout by looking at the frequency counts
of guessed bytes over the 128 packets and taking the highest frequency
one to be the keybyte.

Note that because the key is only 8 bytes long, we can get information
on the unexpanded key bytes from the expanded key array at each index
of the form (8a + i + 3) where (8a + i + 3) < 256 and a is an integer.
In our implementation of the attack, we only use the first 8 places in
the expanded key array, and thus we only recover the actual key.  It
would be easy to extend the attack to include the additional indexes,
which would cause the causal keybyte to jump out.  Of course, such an
attack would require more data and more time to wait for the packet
counter to get high enough.

Often, given that we only use 128 packets per keybyte, we guess the
wrong keybyte.  In the example output of our attack, Attacklog.txt, we
get the first 7 bytes of the client key correct but miss the last
byte, and in the server key we get the first 5 correct but miss the
last 3.  Note that once one key byte is wrong, all keybytes following
it will be wrong as well given that to guess key byte i correctly we
must know all previous keybytes.


References: 

[1] A. Stubblefield, J. Ioannidis, and A. D. Rubin. Using the fluhrer,
mantin, and shamir attack to break wep. Technical Report TD-4ZCPZZ,
AT&T Labs, August 2001.

[2] T. Dierks and C. Allen. The TLS Protocol Version, 1.0. Internet
Engineering Task Force. RFC-2246. 1999.

[3] B. Moller. Security of CBC Ciphersuites in SSL/TLS: Problems and
Countermeasures. http://www.openssl.org/~bodo/tls-cbc.txt


For completeness sake, here are some ideas for attacks we looked at
that didn't work out so well:

*******************************************
Overwriting length field in record headers
*******************************************

In two functions in TLSInteraction.java, the recordlength field from the
record header is used to compute further values.  It is dangerous to
trust the length fields given by a remote host rather than using the
actual length of the data received.  Many real-world bugs have
resulted from trusting length fields, we thought that the flaw in
the TLS implementation may have been of this nature.  This seemed
especially promising because the record headers are not included in
the allhandshakes calculation, meaning an adversary could modify the
field without either side knowing. The following describes our
investigations of the use of the length fields.

In interpretFinished(), the field is used to compute the size for the
thehash[] array.  The other value used in this computation is
verify.length, and verify.length is exactly equal to handshakelength,
which comes from the Finished handshake header, which also is not used
in the allhandshakes calculation.  Thus, in the calculation 

byte thehash[] = new byte[recordlength-verify.length-4];

both verify.length and recordlength can be tampered with by an
adversary without being noticed via HMAC.  The hope was that by
overwriting a record length field, an attacker could do something
useful, like maybe bypassing the allhandshakes integrity check since
the code thinks there is no data.  However, other exceptions
are thrown which thwart this kind of useful manipulation for the
attacker: At the end of interpretFinished is the code: 

byte verifyhash[] = sce.HMacSHA1(verify, mackey);
if (thehash.length < verifyhash.length)
	{
		throw new TLSException("Hash mismatch in finished, Dying...");
	}

No matter what an attacker does to manipulate the lengths, a correctly
implemented HMacSHA1 should still return a 20-byte keyed hash even for
a 0-length verify array.  Thus, if thehash.length is overwritten to be
0 in an attempt to bypass the if (thehash[k] != verifyhash[k]) check
for all k in thehash.length, we won't get passed the comparison of
thehash.length to verifyhash.length and will get an exception thrown.
Because of such checks on length, we reasoned that we could not do
anything useful by overwriting the record length in
interpretFinished().

In interpretApplicationData, the recordLength field is used to compute
the value encryptedLength, which is used to read in the encrypted
data.  A fudge of the recordLength field by an attacker is immediately
noticed in this case, because the MAC check fails:  the encrypted data
ends at a different place than it should, and the MAC starts at a
different place, and the wrong encrypted data and MAC values will
not match in the isEqual check.

*******************************
mastersecret correctly cached?
*******************************

One idea for the flaw was that due to a programming bug, both the
client and server somehow cached only a portion of the mastersecret
which could be guessable.  There was no explicit check on the
mastersecret length when accessing a cached mastersecret, and there is
no way to compare it to the original value since it is only stored in
the cache.  As long as server and client had the same bug, they
wouldn't know that they were using a weak mastersecret when restarting
a cached session.  However, it was easily confirmed that no such bug
was present.

*******************************
Correct seeding of secureRandom
*******************************

We researched whether the zero-argument constructor for secureRandom
which is used in the TLS implementation introduced enough entropy in
seeding and found in the literature that it does.

***********************
Flaw in RSA Parameters?
***********************

When a small prime is used as the encrypt exponent (3 is sometimes
used) and a small, unpadded payload is encrypted using RSA, it is
possible that the encrypted payload is not bigger than the modulus,
and never "turns over" mod the modulus, thus a simple root can be
taken of the encrypted payload to obtain the plaintext.  However, with
the payload in TLS and the encrypt exponent of 65537, this is not a
viable attack.

**************************************************************************
Difference between the project and the RFC's specification of PRF_HMacSHA1
**************************************************************************

Following in the project's PRF_HMacSHA1, we get the following output:
h(secret, h(secret, seed) + seed) +
h(secret, h(secret, h(secret, seed)) + seed) + ...

While in RFC 2246[2], we get this output instead:
h(secret, h(secret, seed) + seed) +
h(secret, h(secret, h(secret, seed) + seed)) + ...

Though interesting, we found no decrease in security due to the
difference in the order of operations.  The difference was actually
due to a TLS book being different from the RFC.

********************************************
Lack of sequence numbers in application data
********************************************

RFC 2246 specifies that application data be protected by an HMac
computed over values such as sequence number, data length, and the
data itself.  In the project the HMac is only computed over the data.
We thought this could lend itself to attacks such as re-ordering or
suppression, but these attacks must assume that the application
running on top of TLS does not have sequence number checks of its
own.  And in any case, this did not seem to be an attack vector that
can "completely subvert the system", as the project states.

*********************************************
What we thought was an extra HMac computation
*********************************************

Simnet's TLS implementation appends an HMac over the "verify" data to
clientFinished and serverFinished messages.  This operation was not
done in the RFC, and we thought it might leak some information about
the keys or the secrets.  Again, it turns out that the difference is
due to a disagreement in a TLS book versus the RFC.  And in any case,
a secure HMac construction leaks no information about the original
data or the Mac key.

*******************************
Attacks on SSL/TLS CBC ciphersuites
*******************************

At the webpage given in reference [3], Bodo Moller describes several
attacks that are possible against the CBC-based ciphersuites in SSL
3.0/TLS 1.0. 

The first attack takes advantage of the fact
that certain implementations report different errors for bad padding
versus bad MACs.  This attack uses this information to verify guesses
on the value on successive bytes of plaintext blocks.  The error
messages may be obtained by gaining access to the error log, or
inferred using timing information.  We believe this attack is
infeasible in Simnet's TLS implementation because there is no error
log to speak of, and it is difficult/impossible to infer timing
information.

The second attack uses the fact that the IV for each record (except
the very first) is the previous record's last ciphertext block, and
therefore known to any attacker that can monitor network traffic.  An
adversary who can adaptively choose plaintexts can use this
information to verify guesses on the value of plaintext.  We did not
follow up on this idea as we could not set up a scenario where an
attacker can adaptively choose plaintext.

The third attack states that because certain implementations do not
properly verify ciphertext padding, it is possible to derive
supposedly cryptographically secure information by modifying messages
and observing whether the TLS connection is perturbed.  While possible
in the Simnet environment (by observing the packet flow we can observe
whether packets are accepted or rejected), this attack seemed
extremely tedious to implement.  Furthermore, the information that it
allows to be inferred are things such as the amount of padding
actually used, which is a far cry from completely subverting the system.

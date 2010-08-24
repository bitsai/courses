#!/usr/bin/env rune

introducer.onTheAir()

def uriS := <file:~/intro-S.cap>.getText()
def uriA := <file:~/intro-A.cap>.getText()
def uriB := <file:~/intro-B.cap>.getText()
def uriC := <file:~/intro-C.cap>.getText()

def S := introducer.sturdyFromURI(uriS).getRcvr()
def A := introducer.sturdyFromURI(uriA).getRcvr()
def B := introducer.sturdyFromURI(uriB).getRcvr()
def C := introducer.sturdyFromURI(uriC).getRcvr()

S <- ping()	# Verify that network is currently empty.  Note that i used a slightly modified version of
		# Dr. Scheideler's code, as i found several of the mistakes published on the list before
		# they were announced, fixed them myself, and was too lazy to later modify my code to comply with
		# his fixed versions.  One of the things that's different is that n starts at 1, and will always have
		# a value 1 greater than the number of actual nodes, per the original pseudocode.

A <- join("S")

S <- ping()	# Verify that A joined correctly; please do not continue until there are no "Promise"s displayed
		# when S is pinged.

B <- join("S")

S <- ping()	# Verify that B joined correctly; please do not continue until there are no "Promise"s displayed
A <- ping()	# when S is pinged.  Also check out the nodes A and B.  We will use this information later to verify
B <- ping()	# that the leave operation works correctly.

C <- join("S")

S <- ping()	# Verify that C joined correctly; please do not continue until there are no "Promise"s displayed
		# when S is pinged.
		
A <- leave()

S <- ping()	# Verify that A left correctly; please do not continue until there are no "Promise"s displayed
C <- ping()	# when S is pinged.  C should have replaced A in the network.  The ping displays from S, C, and B
B <- ping()	# should appear identical to that of the ping displays from S, A, and B right after B joined, except
		# that C is now in A's place.
#!/usr/bin/env rune

introducer.onTheAir()
### Fix 1 - Old ###
# def uri0 := <file:~/Desktop/intro-0.cap>.getText()
### Fix 1 - New ###
def uri0 := <file:~/intro-0.cap>.getText()
def s0 := introducer.sturdyFromURI(uri0).getRcvr()

# ping each node to see their content; all show should "[].asSet().diverge()"
s0 <- ping(0)
s0 <- ping(1)
s0 <- ping(2)
s0 <- ping(3)

# test to see that "1" is not in the network
s0 <- search(1)

# insert "1"
s0 <- insert(1)

# ping each node to see their content; node 0 should have "[1].asSet().diverge()", all others same as before
s0 <- ping(0)
s0 <- ping(1)
s0 <- ping(2)
s0 <- ping(3)

# test to see that "1" is now contained in the network
s0 <- search(1)

# delete "1"
s0 <- delete(1)

# ping each node to see their content; all show should "[].asSet().diverge()"
s0 <- ping(0)
s0 <- ping(1)
s0 <- ping(2)
s0 <- ping(3)

# test to see that "1" is not in the network
s0 <- search(1)

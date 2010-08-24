#!/usr/bin/env rune

introducer.onTheAir()
def uri0 := <file:intro-0.cap>.getText()
def p0 := introducer.sturdyFromURI(uri0).getRcvr()

def uri1 := <file:intro-1.cap>.getText()
def p1 := introducer.sturdyFromURI(uri1).getRcvr()

def uri4 := <file:intro-4.cap>.getText()
def p4 := introducer.sturdyFromURI(uri4).getRcvr()

# Initiate network

p0 <- join(0)
p4 <- join(0)

# Check status of network

p0 <- ping()
p4 <- ping()

# Add third peer to network

p1 <- join(0)

# Check status of network

p0 <- ping() # Might have to wait a bit for this one to settle; if you see a remote promise, wait a bit then try again
p4 <- ping()
p1 <- ping()

# Remove third peer from network

p1 <- leave()

# Check status of network; should be identical to the first status check display

p0 <- ping()
p4 <- ping()
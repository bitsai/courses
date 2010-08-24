#!/usr/bin/env rune

introducer.onTheAir()
def uri0 := <file:intro-0.cap>.getText()
def p0 := introducer.sturdyFromURI(uri0).getRcvr()

def uri1 := <file:intro-1.cap>.getText()
def p1 := introducer.sturdyFromURI(uri1).getRcvr()

def uri4 := <file:intro-4.cap>.getText()
def p4 := introducer.sturdyFromURI(uri4).getRcvr()

p0 <- join(0) # Get the system started

p0 <- ping() # Network now has 1 node

p1 <- join(0) # Join another node; network now has 2 nodes

p0 <- ping() # Network now has 2 nodes

p4 <- join(0) # Join another node; network now has 3 nodes

p0 <- ping() # Bam! Come one, come all! See the amazing "The donor is gone" problem!
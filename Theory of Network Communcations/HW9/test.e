#!/usr/bin/env rune

introducer.onTheAir()

def uriS := <file:intro-S.cap>.getText()
def uriA := <file:intro-A.cap>.getText()
def uriB := <file:intro-B.cap>.getText()
def uriC := <file:intro-C.cap>.getText()

def S := introducer.sturdyFromURI(uriS).getRcvr()
def A := introducer.sturdyFromURI(uriA).getRcvr()
def B := introducer.sturdyFromURI(uriB).getRcvr()
def C := introducer.sturdyFromURI(uriC).getRcvr()

A <- join("S")
B <- join("S")
C <- join("S")

A <- enqueue(10) # Enqueue 1st item; output is displayed in supervisor's window
B <- enqueue(20) # Enqueue 2nd item
C <- enqueue(30) # Enqueue 3rd item; may or may not work, as we may hit a branch that is already full

A <- ping() # Check state of tree
B <- ping()
C <- ping()

C <- dequeue() # Dequeue item from random node in tree; output is displayed in supervisor's window
C <- dequeue() # If an item is dequeued from A while one of its children still has an item, A steals a child's item
C <- dequeue() # This may or may not return an item, depending on if the 3rd enqueue operation was successful

A <- ping() # Check state of tree
B <- ping()
C <- ping()

C <- dequeue() # Should definitely return "Nothing to dequeue!" in supervisor's window
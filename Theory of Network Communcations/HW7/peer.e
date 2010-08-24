#!/usr/bin/env rune

### File input/output functions ###
def introFile(num) :any {
	return <file: `~/intro-$num.cap`>
}

def objFromFile(file) :any {
	if (file.exists()) {
		return introducer.sturdyFromURI(file.getText()).getRcvr()
	} else {
		return null
	}
}

def objIntoFile(obj, file) :any {
	file.setText(introducer.sturdyToURI(makeSturdyRef.temp(obj)))
}

### Function F, as defined in the assignment ###
def f(num :float64) :float64 {
	return ((num + 0.5) % 1)
}

### Hash function, as defined by Mark ###
def h(num :int) :float64 {
	return (num.cryptoHash() / (1 << 160))
}

### Given some real value y, this returns the next node a message should be routed to ###
def nextNode(y :float64, outEdges) :any {
	var minHash := 1
	var nextNode := null

	for outHash => outNode in outEdges {
		if (y <= outHash && outHash < minHash) {
			minHash := outHash
			nextNode := outNode
		}
	}
	
	return nextNode
}

### Given an interval, this returns whether a number is contained in the interval ###
def contains(min :float64, max :float64, num :float64) :boolean {
	if (min < max) {
		if (min <= num && num <= max) { return true }
	} else {
		if (num <= max) {
			if ((min - 1) <= num && num <= max) { return true }
		}
		if (min <= num) {
			if (min <= num && num <= (max + 1)) { return true }
		}
	}
	
	return false
}

### Given two intervals a and b, this returns whether there exists x in a and y in b s.t. x and y are 0.5 apart ###
def EF(aMin :float64, aMax :float64, bMin :float64, bMax :float64) :boolean {
	if ((bMin - aMax) <= 0.5 && 0.5 <= (bMax - aMin)) { return true }
	return false
}

### Given two intervals, this returns whether they are connected under EF ###
def connected(aPredHash :float64, aHash :float64, bPredHash :float64, bHash :float64) :boolean {
	var aPH := aPredHash
	var aH := aHash
	var bPH := bPredHash
	var bH := bHash
	
	if (aHash < aPredHash) {
		if (aH <= bPH) {
			aPH -= 1
		} else {
			aH += 1
		}
	}
	if (bHash < bPredHash) {
		if (aH <= bPH) {
			bH += 1
		} else {
			bPH -= 1
		}
	}

	return EF(aPH, aH, bPH, bH)
}

### Make a peer ###
def makePeer(myNum :int) :any {
	def myHash := h(myNum)
	def inEdges := [].asMap().diverge()
	def outEdges := [].asMap().diverge()
	var pred := null
	var predHash := null
	var succ := null
	var succHash := null

	def peer {
		to join(qNum :int) :any {
			if (qNum == myNum) {
				### First node in a network can only join itself; set up accordingly
				pred := peer
				predHash := myHash
				succ := peer
				succHash := myHash
			} else {
				### Contact q and ask q to route the new join request
				def q := objFromFile(introFile(qNum))
				q <- route(myHash, `Join $myNum`)
			}
		}
		to leave() :any {
			for inHash => inNode in inEdges {
				### Tell nodes that point to the leaving node to point to leaving node's successor
				inNode <- removeOutEdge(myHash)
				inNode <- addOutEdge(succ, succHash)
			}

			for outHash => outNode in outEdges {
				### Tell nodes pointed to by the leaving node to remove affected edges
				outNode <- removeInEdge(myHash)
			}
			
			### Re-integrate cycle
			pred <- setSucc(succ, succHash)
			succ <- setPred(pred, predHash)
		}
		to route(yNum :float64, msg :String) :any {
			### If i'm the only node, or if y is in my region, this message is for me
			if (predHash == myHash || contains(predHash, myHash, yNum)) {
				peer <- process(yNum, msg)
			### Else, pass the message along
			} else {
				def node := nextNode(yNum, outEdges)
				node <- route(yNum, msg)
			}
		}
		to process(newHash :float64, msg :String) :any {
			if (msg =~ `Join @newNum`) {				
				def newNode := objFromFile(introFile(newNum))

				### Integrate new node into cycle
				pred <- setSucc(newNode, newHash)
				newNode <- setPred(pred, predHash)
				newNode <- setSucc(peer, myHash)

				def newPredHash := predHash
				pred := newNode
				predHash := newHash

				### Migrate incoming edges
				for inHash => inNode in inEdges {
					when (inNode <- getPredHash()) -> done(inPredHash) :any {
						if (connected(inPredHash, inHash, newPredHash, newHash)) {
							newNode <- addInEdge(inNode, inHash)
							inNode <- addOutEdge(newNode, newHash)
						}
						if (!connected(inPredHash, inHash, newHash, myHash)) {
							peer <- removeInEdge(inNode, inHash)
							inNode <- removeOutEdge(peer, myHash)
						}
					} catch e {}
				}

				### Migrate outgoing edges
				for outHash => outNode in outEdges {
					when (outNode <- getPredHash()) -> done(outPredHash) :any {
						if (connected(newPredHash, newHash, outPredHash, outHash)) {
							newNode <- addOutEdge(outNode, outHash)
							outNode <- addInEdge(newNode, newHash)
						}
						if (!connected(newHash, myHash, outPredHash, outHash)) {
							peer <- removeOutEdge(outNode, outHash)
							outNode <- removeInEdge(peer, myHash)
						}
					} catch e {}
				}

				### Check to see if this node and the new node should be connected
				if (connected(newPredHash, newHash, newHash, myHash)) {
					peer <- addInEdge(newNode, newHash)
					newNode <- addOutEdge(peer, myHash)
				}

				if (connected(newHash, myHash, newPredHash, newHash)) {
					newNode <- addInEdge(peer, myHash)
					peer <- addOutEdge(newNode, newHash)
				}
			} else {
				println(msg)
			}
		}
		
		### Book-keeping functions
		to setPred(node :rcvr, nodeHash :float64) :any {
			pred := node
			predHash := nodeHash
		}
		to setSucc(node :rcvr, nodeHash :float64) :any {
			succ := node
			succHash := nodeHash
		}
		
		to addInEdge(node :rcvr, nodeHash :float64) :any {
			if (nodeHash != myHash) { inEdges[nodeHash] := node }
		}
		to addOutEdge(node :rcvr, nodeHash :float64) :any {
			if (nodeHash != myHash) { outEdges[nodeHash] := node }
		}
		
		to removeInEdge(nodeHash :float64) :any {
			inEdges.removeKey(nodeHash)
		}
		to removeOutEdge(nodeHash :float64) :any {
			outEdges.removeKey(nodeHash)
		}
		
		to getHash() :float64 {
			return myHash
		}
		to getPredHash() :float64 {
			return predHash
		}

		to getNum() :int {
			return myNum
		}
		to ping() :any {
			println()
			println("Hash: " + myHash)
			println("In Edges: " + inEdges)
			println("Out Edges: " + outEdges)
			println("Predecessor: " + pred)
			println("Pred Hash: " + predHash)
			println("Successor: " + succ)
			println("Succ Hash: " + succHash)
		}
	}
	objIntoFile(peer, introFile(myNum))
	return peer
}

if (interp.getArgs() =~ [myNum]) {
	introducer.onTheAir()
	makePeer(__makeInt(myNum))
	println(`peer $myNum waiting...`)
	interp.blockAtTop()
} else {
	println("usage: peer.e myNum")
}
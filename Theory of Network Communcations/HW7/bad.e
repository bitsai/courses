#!/usr/bin/env rune

def introFile(num) :any {
	return <file: `intro-$num.cap`>
}

def objFromFile(file) :any {
	if (file.exists()) {
		return introducer.sturdyFromURI(file.getText()).getRcvr()
	} else {
		return null
	}
}

def objIntoFile(obj, file) :void {
	file.setText(introducer.sturdyToURI(makeSturdyRef.temp(obj)))
}

def h(num :int) :float64 {
	return (num.cryptoHash() / (1 << 160))
}

def contains(min :float64, max :float64, num :float64) :boolean {
	if (min <= max) {
		if (min <= num && num <= max) { return true }
	} else {
		if (min <= num || num <= max) { return true }
	}
	
	return false
}

def makePeer(myNum :int) :any {
	def myHash := h(myNum)
	var pred := null
	var predHash := null
	var succ := null
	var succHash := null

	def peer {
		to join(qNum) :any {
			if (qNum == myNum) {
				peer <- setPred(peer, myHash)
				peer <- setSucc(peer, myHash)
			} else {
				def q := objFromFile(introFile(qNum))
				q <- joinRequest(myHash, peer)
			}
		}
		
		to leave() :any {
			pred <- setSucc(succ, succHash)
			succ <- setPred(pred, predHash)
		}

		to route(yNum, msg) :any {
			if (succHash == myHash || contains(predHash, myHash, yNum)) {
				println(msg)
			} else {
				succ <- route(yNum, msg)
			}
		}

		to joinRequest(newHash, newPeer) :void {
			if (succHash == myHash || contains(predHash, myHash, newHash)) {
				pred <- setSucc(newPeer, newHash)
				newPeer <- setPred(pred, predHash)
				newPeer <- setSucc(peer, myHash)
				peer <- setPred(newPeer, newHash)
			} else {
				succ <- joinRequest(newHash, newPeer)
			}
		}

		to setPred(peer, hash) :any {
			pred := peer
			predHash := hash
		}

		to setSucc(peer, hash) :any {
			succ := peer
			succHash := hash
		}
		
		to ping() :any {
			println()
			println("Hash: " + myHash)
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
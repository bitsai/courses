#!/usr/bin/env rune

pragma.disable("explicit-result-guard")

def introFile(addr :int) :any {
	### Fix 1 - Old ###
	# return <file: `~/Desktop/intro-$addr.cap`>
	### Fix 1 - New ###
	return <file: `~/intro-$addr.cap`>
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

def firstDiffDim(addr1 :int, addr2 :int) :int {
	def diff := addr1 ^ addr2
	### Fix 2 - Old ###
	# def firstDiff := diff & ~(diff -1)
	### Fix 2 - New ###
	def firstDiff := ~(~diff | (diff -1))
	return firstDiff.bitLength() -1
}

def getAddr(x :int, numDims :int) :int {
	def total := 2 ** numDims
	for y in 1 .. (total - 1) {
		if ((y / total) >= (1 / x)) {
			return y
		}
	}
	return 0
}

def makeRouter(numDims :int, myAddr :int, myTarget) :any {
	
	def neighbors := [].diverge()
	def nResolvers := [].diverge()
	
	for dim in 0..!numDims {
		def [p,r] := Ref.promise()
		neighbors[dim] := p
		nResolvers[dim] := r
	}
	
	var inited := false
	def init() :void {
		if (!inited) {
			for dim in 0..!numDims {
				def neighborAddr := myAddr ^ (1 << dim)
				nResolvers[dim].resolve(objFromFile(introFile(neighborAddr)))
			}
			inited := true
		}
	}
	
	def router {
		to makeVTarget(destAddr :int) :any {
			if (myAddr == destAddr) { return myTarget }
			init()
			def nextHop := neighbors[firstDiffDim(myAddr, destAddr)]
			def nextVTarget := nextHop <- makeVTarget(destAddr)
			def forwarder {
				match [verb :String, args :any[]] {
					println(`forwarding: $verb with $args`)
					E.send(nextVTarget, verb, args)
				}
			}
			return forwarder
		}
		to ping(destAddr :int) :void {
			def target := router <- makeVTarget(destAddr)
			target <- ping()
		}
		match [verb, args :int[]] {
			def destAddr := getAddr(args[0], numDims)
			def target := router <- makeVTarget(destAddr)
			def answerVow := E.send(target, verb, args)
			when (answerVow) -> done(answer) {
				if (answer != null) { println(answer) }
			} catch prob {}
		}
	}
	objIntoFile(router, introFile(myAddr))
	return router
}

if (interp.getArgs() =~ [numDimsStr, myAddrStr]) {
	def numDims := __makeInt(numDimsStr)
	def myAddr := __makeInt(myAddrStr)
	def data := [].asSet().diverge()
	def node {
		to insert(x :int) :void { data.addElement(x) }
		to delete(x :int) :void { data.remove(x) }
		to search(x :int) :String {
			if (data.contains(x)) { return("yes") } else {
				return("no")
			}
		}
		to ping() :void { println(data) }
		match [verb, args] {
			println(`($myAddr) got: $verb with $args`)
		}
	}
	introducer.onTheAir()
	makeRouter(numDims, myAddr, node)
	println(`router $myAddr waiting...`)
	interp.blockAtTop()
} else {
	println("usage: const-hash.e numDims myAddr")
}
#!/usr/bin/env rune

# Copyright 2004 Mark S. Miller & Sandeep Ranade under the terms of the 
# MIT X license found at http://www.opensource.org/licenses/mit-license.html ..

def introFile(addr :int) :any {
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

/**
 * Returns the lowest dimension (bit position) on which these two addresses
 * differ.
 */
def firstDiffDim(addr1 :int, addr2 :int) :int {
    # A 1-bit at exactly those positions where they differ
    # As in C,C++,Java, in E, binary infix "^" means "xor".
    def diff := addr1 ^ addr2
    # Only first of the above 1 bits is kept. All others are zero.
    # def firstDiff := diff & ~(diff -1)
    # (x & y) == ~(~x | ~y)
    def firstDiff := ~(~diff | (diff -1))
    # The bit position of the above solitary one bit.
    return firstDiff.bitLength() -1
}

/**
 * Although the assignment can be made to work without telling the individual
 * servers the total number of dimensions, for our solution we decided to tell
 * them.
 */
def makeRouter(numDims :int, myAddr :int, myTarget) :any {
    # For the representation of the neighbors list, we used the technique
    # MarkM explained in the longer answer to Nilo's question in the netcom
    # email "Lists and Promises in E".
    
    def neighbors := [].diverge()
    def nResolvers := [].diverge()
    
    # (x..!y) is the interval from x inclusive to y exclusive. On the integers,
    # this is the same as (x..(y-1)).
    for dim in 0..!numDims {
        def [p,r] := Ref.promise()
        neighbors[dim] := p
        nResolvers[dim] := r
    }
    
    var inited := false
    def init() :void {
        if (!inited) {
            for dim in 0..!numDims {
                # My neighbor for dimension dim is the one whose addr differs
                # from mine only in the dim'th bit.
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
    }
    objIntoFile(router, introFile(myAddr))
    return router
}

if (interp.getArgs() =~ [numDimsStr, myAddrStr]) {
    def numDims := __makeInt(numDimsStr)
    def myAddr := __makeInt(myAddrStr)
    def fakeTarget {
        match [verb, args] {
            println(`($myAddr) got: $verb with $args`)
        }
    }
    introducer.onTheAir()
    makeRouter(numDims, myAddr, fakeTarget)
    println(`router $myAddr waiting...`)
    interp.blockAtTop()
} else {
    println("usage: hypercube.e numDims myAddr")
}

# Example session, once several routers are set up

? introducer.onTheAir()

? def uri0 := <file:~/Desktop/intro-0.cap>.getText()

? def r0 := introducer.sturdyFromURI(uri0).getRcvr()

? def t5 := r0 <- makeVTarget(5)

? t5 <- hello("world")

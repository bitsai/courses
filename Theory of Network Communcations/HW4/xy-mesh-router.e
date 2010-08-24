#!/usr/bin/env rune

# Copyright 2004 Mark S. Miller under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

def introFile(x :int, y :int) :any {
    return <file: `~/Desktop/intro-$x-$y.cap`>
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

def makeRouter(myX :int, myY :int, myTarget) :any {
    def up
    def down
    def left
    def right
    var inited := false
    
    def init() :void {
        if (!inited) {
            bind up    := objFromFile(introFile(myX,   myY+1))
            bind down  := objFromFile(introFile(myX,   myY-1))
            bind left  := objFromFile(introFile(myX-1, myY))
            bind right := objFromFile(introFile(myX+1, myY))
            inited := true
        }
    }
    
    def router {
        to makeVTarget(x :int, y :int) :any {
            init()
            def nextHop
            if (x == myX) {
                if (y == myY) {
                    return myTarget
                } else if (y > myY) {
                    bind nextHop := up
                } else {
                    bind nextHop := down
                }
            } else if (x > myX) {
                bind nextHop := right
            } else {
                bind nextHop := left
            }
            def nextVTarget := nextHop <- makeVTarget(x, y)
            def forwarder {
                match [verb, args] {
                    println(`forwarding: $verb with $args`)
                    E.send(nextVTarget, verb, args)
                }
            }
            return forwarder
        }
    }
    objIntoFile(router, introFile(myX,myY))
    return router
}

if (interp.getArgs() =~ [myXStr, myYStr]) {
    def myX := __makeInt(myXStr)
    def myY := __makeInt(myYStr)
    def fakeTarget {
        match [verb, args] {
            println(`($myX,$myY) got: $verb with $args`)
        }
    }
    introducer.onTheAir()
    makeRouter(myX, myY, fakeTarget)
    println(`router $myX,$myY waiting...`)
    interp.blockAtTop()
} else {
    println("usage: xy-mesh-router.e x y")
}

# Example session, once several routers are set up

    ? introducer.onTheAir()

    ? def uri00 := <file:~/Desktop/intro-00.cap>.getText()

    ? def r00 := introducer.sturdyFromURI(uri00).getRcvr()

    ? def t01 := r00 <- makeVTarget("01")

    ? t01 <- hello("world")

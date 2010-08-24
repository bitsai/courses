#!/usr/bin/env rune

# Copyright 2004 Mark S. Miller under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# See the comments in hello-server.e

introducer.onTheAir()

def uri := <file:~/Desktop/hello.cap>.getText()
def sr := introducer.sturdyFromURI(uri)

# Attempts to get a live reference to the object designated by sr. If this
# object is not currently reachable, this will resolve to a broken reference.
# In other words, "sr.getRcvr()" returns a promise for a live reference to
# the designated object, but this promise may become broken.

def helloServer := sr.getRcvr()


# Say that helloServer should eventually be ping()ed, and obtain a promise for
# the result of pinging it. Since ping() has a :void result, if this ack is
# successfully fulfilled, it will resolve to null. But there's a race: since
# the helloServer's vat may exit at any time after processing the ping message,
# it may not have a chance to resolve the result. In this case, the ack-promise
# will become broken. In either case, it will be resolved (defined as 
# fulfilled or broken).

def ack := helloServer <- ping()


# Register the part to the right of the "->" as a callback to be notified once
# ack becomes resolved.

when (ack) -> done(_) :void {
    # happens if ack is fulfilled
    println("done")
} catch excuse {
    # happens if ack is broken
    println(`oops $excuse`)
} finally {
    # Causes the client's vat to exit
    interp.exitAtTop()
}


# Prevents the client from exiting just yet.

interp.blockAtTop()

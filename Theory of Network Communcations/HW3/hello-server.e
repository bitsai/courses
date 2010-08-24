#!/usr/bin/env rune

# Copyright 2004 Mark S. Miller under the terms of the MIT X license
# found at http://www.opensource.org/licenses/mit-license.html ................

# Once E is installed, launch hello-server.e in one shell, wait for it to 
# print "waiting...", and then launch hello-client.e in another shell on the 
# same machine.

# hello-server.e writes ~/Desktop/hello.cap, which hello-client.e reads. 
# Therefore, you must either already have a writable ~/Desktop, or you must 
# modify these files.

# Given two connected machines that can address each other (i.e., no NAT in 
# the middle), you can run hello-server.e on one, copy its 
# ~/Desktop/hello.cap to the ~/Desktop/hello.cap of the other, and then run 
# hello-client.e on the other. This file represents a password capability -- 
# without access to the unguessable knowledge it contains, no one else can 
# cause your hello-server to print "Hello world" to its stdout.

# Until your vat goes onTheAir, it can't send or receive objects from other
# vats.

introducer.onTheAir()

/**
 * A demonstration of an object that can be accessed remotely
 */
def helloServer {

    /**
     * Prints "Hello world" and shuts down the hosting vat.
     */
    to ping() :void {
        println("Hello world")
        interp.exitAtTop()
    }
}

# A sturdy reference to the helloServer -- a reference which continues to
# give access to that object, even after a network partition. You can't send
# messages directly over a sturdy reference. Instead, as you'll see in 
# hello-client.e, you must first get a live reference, in order to send 
# messages over it.

def sr := makeSturdyRef.temp(helloServer)


# The contact information of a sturdy reference encoded as a "captp://..."
# URI string, suitable for conveying by out-of-band means, such as our use of
# the file system below. Initial connectivity must be brought about by such
# means, as explained on 
# <http://www.erights.org/elang/concurrency/introducer.html>.

def uri := introducer.sturdyToURI(sr)


# Writes this URI string into the file ~/Desktop/hello.cap

<file:~/Desktop/hello.cap>.setText(uri)

# Prints "waiting..." so the human operator knows that hello.cap is ready to
# be used.

println("waiting...")

# Prevents the server process from exiting just yet. It will exit when the
# 'interp.exitAtTop()' above is executed.

interp.blockAtTop()

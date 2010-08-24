#!/usr/bin/env rune

introducer.onTheAir()

var count := 0

def bennyServer {
	to ping(in_uri) :void {
		println("Ping-ed!")
		count += 1
		def in_sr := introducer.sturdyFromURI(in_uri)
		def bennyClient := in_sr.getRcvr()
		bennyClient <- pong()
		
		if (count == 10) {
			interp.exitAtTop()
		}
	}
}

def out_sr := makeSturdyRef.temp(bennyServer)
def out_uri := introducer.sturdyToURI(out_sr)
<file:~/Desktop/server.cap>.setText(out_uri)

println("Waiting...")

interp.blockAtTop()

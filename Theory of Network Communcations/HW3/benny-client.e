#!/usr/bin/env rune

introducer.onTheAir()

var count := 0

def in_uri := <file:~/Desktop/server.cap>.getText()
def in_sr := introducer.sturdyFromURI(in_uri)
def bennyServer := in_sr.getRcvr()

def bennyClient {

	to pong() :void {
		println("Pong-ed!")
		count += 1
		def out_sr := makeSturdyRef.temp(bennyClient)
		def out_uri := introducer.sturdyToURI(out_sr)
		bennyServer <- ping(out_uri)
		
		if (count == 10) {
			interp.exitAtTop()
		}
	}
}

println("Contacting...")

def out_sr := makeSturdyRef.temp(bennyClient)
def out_uri := introducer.sturdyToURI(out_sr)
bennyServer <- ping(out_uri)

interp.blockAtTop()
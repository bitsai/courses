#!/usr/bin/env rune

### Define functions

def invertCoordinate(coordinate :char) :any {
	if (coordinate == '0') {
		return 1
	} else {
		return 0
	}
}

def invertCoordinates(coordinates :String, i) :any {
	var output := ""
	
	for index in 0..coordinates.size() - 1 {
		if (index == i) {
			output := output + invertCoordinate(coordinates[index])
		} else {
			output := output + coordinates[index]
		}
	}
	
	return output
}

def firstDiffDim(coordinates1 :String, coordinates2 :String) :any {
	for index in 0..(coordinates1.size() - 1) {
		def position := (coordinates1.size() - 1) - index
	
		if (coordinates1[position] != coordinates2[position]) {
			return position
		}
	}
	
	return -1
}

def introFile(coordinates :String) :any {
	return <file: `~/intro-$coordinates.cap`>
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

def makeRouter(myCoordinates :String, myTarget) :any {
	def others := [].diverge()
	var inited := false
    
	def init() :void {
		if (!inited) {
			for i in 0..(myCoordinates.size() - 1) {
				def otherCoordinates := invertCoordinates(myCoordinates, i)
				others[i] := objFromFile(introFile(otherCoordinates))
				}
			inited := true
		}
	}
	
	def router {
		to makeVTarget(input) :any {
			def targetCoordinates :String := `$input`
			init()
			def nextHop

			if (targetCoordinates == myCoordinates) {
				return myTarget
			}
			
			def dim := firstDiffDim(myCoordinates, targetCoordinates)
			bind nextHop := others[dim]
			def nextVTarget := nextHop <- makeVTarget(targetCoordinates)

			def forwarder {
				match [verb, args] {
					println(`forwarding: $verb with $args`)
					E.send(nextVTarget, verb, args)
				}
			}

			return forwarder
		}
	}

	objIntoFile(router, introFile(myCoordinates))

	return router
}

### Main body ###

def coordinates := interp.getArgs()[0]
def fakeTarget {
	match [verb, args] {
		println(`$coordinates got: $verb with $args`)
	}
}
introducer.onTheAir()
makeRouter(coordinates, fakeTarget)
println(`router $coordinates waiting...`)
interp.blockAtTop()
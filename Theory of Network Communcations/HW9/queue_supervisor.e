#!/usr/bin/env rune

def introFile(name) :any {
	return <file: `intro-$name.cap`>
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

def bin(n :int) :String {
	if (n > 0) {
		var sum := n
		var power := 0
		var output := ""	
		while ((2 ** power) <= n) { power += 1 }
		power := power - 1
	
		while (power >= 0) {
			if ((2 ** power) <= sum) {
				sum -= 2 ** power
				output += "1"
			} else { output += "0" }

			power -= 1
		}

		return output
	} else { return "0" }
}

def l(n :int) :String {
	var output := ""

	if (n > 1) {
		var aString := bin(n)

		for index in 1 .. aString.size() - 1 {
			output += aString[index]
		}
		
		output += aString[0]
	} else { output += `$n` }
	
	return output
}

def andTwo(s :String) :int {
	if (s.size() < 2) {
		return 0 
	} else if (s[s.size() - 2] == '1') {
		return 2 
	} else { return 0 }
}

def makeSupervisor(myName) :any {
	var n := 1
	var root := null

	var pv := null
	var pvLabel := "None"
	var v := null
	var vLabel := "None"
	var sv := null
	var svLabel := "None"
	var ssv := null
	var ssvLabel := "None"

	def supervisor {
		to enqueue(x) :any {
			root <- EQmoveDown(x)
		}
		
		to dequeue() :any {
			root <- DQmoveDown()
		}
	
		to join(w) :any {
			if (n == 1) {
				w <- setup("1", w, "1", w, "1", null, "None", null, "None", null, "None")
				pv := w
				pvLabel := "1"
				v := w
				vLabel := "1"
				sv := w
				svLabel := "1"
				ssv := w
				ssvLabel := "1"
				
				root := w
			} else {
				if(andTwo(l(n)) == 0) {
					w <- setup(l(n), sv, svLabel, ssv, ssvLabel, ssv, ssvLabel, null, "None", null, "None")
					ssv <- setLeftChild(w, l(n))
				} else {
					w <- setup(l(n), sv, svLabel, ssv, ssvLabel, sv, svLabel, null, "None", null, "None")
					sv <- setRightChild(w, l(n))
				}
				sv <- setSucc(w, l(n))
				ssv <- setPred(w, l(n))
				pv := sv
				pvLabel := pv <- getLabel()
				v := w
				vLabel := v <- getLabel()
				sv := ssv
				svLabel := sv <- getLabel()
				ssv := ssv <- getSucc()
				ssvLabel := ssv <- getLabel()				
			}
			n := n + 1
		}

		to leave(l, pw, pwl, sw, swl, fw, fwl, lcw, lcwl, rcw, rcwl) :any {
			if (n > 1) {
				if (n == 2) {
					pv := null
					pvLabel := "None"
					v := null
					vLabel := "None"
					sv := null
					svLabel := "None"
					ssv := null
					ssvLabel := "None"
					
					root := null
				} else {
					if (vLabel != l) {
						var p := pw
						var pl := pwl
						var s := sw
						var sl := swl
						var lc := lcw
						var lcl := lcwl
						var rc := rcw
						var rcl := rcwl
					
						if (vLabel == pwl) { 
							p := pv 
							pl := pvLabel
						}
						if (vLabel == swl) { 
							s := sv 
							sl := svLabel
						}
						if (vLabel == lcwl) {
							lc := null
							lcl := "None"
						}
						if (vLabel == rcwl) {
							rc := null
							rcl := "None"
						}

						v <- setup(l, p, pl, s, sl, fw, fwl, lc, lcl, rc, rcl)

						p <- setSucc(v, l)
						s <- setPred(v, l)

						if (fwl != "None") {
							if (andTwo(l) == 0) { fw <- setLeftChild(v, l)
							} else { fw <- setRightChild(v, l) }
						}

						if (lcl != "None") { lc <- setParent(v, l) }
						if (rcl != "None") { rc <- setParent(v, l) }

						if (l == "1") { root := v }

						if (l == pvLabel) { pv := v }
						if (l == svLabel) { sv := v }
					}

					if (andTwo(vLabel) == 0) { sv <- setLeftChild(null, "None")
					} else { pv <- setRightChild(null, "None") }

					pv <- setSucc(sv, svLabel)
					sv <- setPred(pv, pvLabel)
					ssv := sv
					ssvLabel := ssv <- getLabel()
					sv := pv
					svLabel := sv <- getLabel()
					v := pv <- getPred()
					vLabel := v <- getLabel()
					pv := pv <- getPredPred()
					pvLabel := pv <- getLabel()
				}
				n := n - 1
			}
		}

		to broadcast(m) :any { root <- sendDown("\n" + m) }
		
		to print(m) :any { println("\n" + m) }
		
		to ping() :any {
			println("\nn: " + n)

			if (pv != null) {
				when (pv <- getName(), v <- getName(), sv <- getName(), ssv <- getName()) -> done(pvName, vName, svName, ssvName) :any {
					println("pvName: " + pvName)
					println("vName: " + vName)
					println("svName: " + svName)
					println("ssvName: " + ssvName)
				} catch e {}
			}
		}
	}
	objIntoFile(supervisor, introFile(myName))
	return supervisor
}

if (interp.getArgs() =~ [myName]) {
	introducer.onTheAir()
	makeSupervisor(myName)
	println(`supervisor $myName waiting...`)
	interp.blockAtTop()
} else {
	println("usage: supervisor.e myName")
}
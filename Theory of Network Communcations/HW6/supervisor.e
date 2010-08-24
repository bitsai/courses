#!/usr/bin/env rune

def introFile(name :String) :any {
	return <file: `~/intro-$name.cap`>
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

def makeSupervisor(myName :String) :any {
	var n := 1
	var v := null
	var pv := null
	var sv := null
	var ssv := null

	var root := null

	var pvlabel := ""
	var vlabel := ""
	var svlabel := ""
	var ssvlabel := ""

	def supervisor {
		to join(w :rcvr) :void {
			if (n == 1) {
				w <- setup("1", w, "1", w, "1", null, "", null, "", null, "")
				v := w
				pv := w
				sv := w
				ssv := w
				
				root := w
				
				pvlabel := "1"
				vlabel := "1"
				svlabel := "1"
				ssvlabel := "1"
			} else {
				if(andTwo(l(n)) == 0) {
					w <- setup(l(n), sv, svlabel, ssv, ssvlabel, ssv, ssvlabel, null, "", null, "")
					ssv <- setRightChild(w, l(n))
				} else {
					w <- setup(l(n), sv, svlabel, ssv, ssvlabel, sv, svlabel, null, "", null, "")
					sv <- setLeftChild(w, l(n))
				}
				sv <- setSucc(w, l(n))
				ssv <- setPred(w, l(n))
				pv := sv
				v := w
				sv := ssv
				ssv := ssv <- getSucc()
				
				pvlabel := pv <- getLabel()
				vlabel := v <- getLabel()
				svlabel := sv <- getLabel()
				ssvlabel := ssv <- getLabel()
			}
			n := n + 1
		}
		to leave(l :String, pw :rcvr, pwl :String, sw :rcvr, swl :String, fw :rcvr, fwl :String, lcw :rcvr, lcwl :String, rcw :rcvr, rcwl :String) :void {
			if (n > 1) {
				if (n == 2) {
					v := null
					pv := null
					sv := null
					ssv := null
					
					root := null
					
					pvlabel := ""
					vlabel := ""
					svlabel := ""
					ssvlabel := ""
				} else {
					if (vlabel != l) {
						var p := pw
						var pl := pwl
						var s := sw
						var sl := swl
						var lc := lcw
						var lcl := lcwl
						var rc := rcw
						var rcl := rcwl
					
						if (vlabel == pwl) { 
							p := pv 
							pl := pvlabel
						}
						if (vlabel == swl) { 
							s := sv 
							sl := svlabel
						}
						if (vlabel == lcwl) {
							lc := null
							lcl := ""
						}
						if (vlabel == rcwl) {
							rc := null
							rcl := ""
						}

						v <- setup(l, p, pl, s, sl, fw, fwl, lc, lcl, rc, rcl)

						p <- setSucc(v, l)
						s <- setPred(v, l)

						if (fwl != "") {
							if (andTwo(l) == 0) { fw <- setRightChild(v, l)
							} else { fw <- setLeftChild(v, l) }
						}

						if (lcl != "") { lc <- setParent(v, l) }
						if (rcl != "") { rc <- setParent(v, l) }

						if (l == "1") { root := v }

						if (l == pvlabel) { pv := v }
						if (l == svlabel) { sv := v }
					}

					if (andTwo(vlabel) == 0) { sv <- setRightChild(null, "")
					} else { pv <- setLeftChild(null, "") }

					pv <- setSucc(sv, svlabel)
					sv <- setPred(pv, pvlabel)
					ssv := sv
					sv := pv
					v := pv <- getPred()
					pv :=pv <- getPredPred()
					
					pvlabel := pv <- getLabel()
					vlabel := v <- getLabel()
					svlabel := sv <- getLabel()
					ssvlabel := ssv <- getLabel()
				}
				n := n - 1
			}
		}

		to broadcast(message :String) :void {
			root <- sendDown(message)
		}
		
		to ping() :void {
			println()
			println("n: " + n)

			println("pvlabel: " + pvlabel)
			println("vlabel: " + vlabel)
			println("svlabel: " + svlabel)
			println("ssvlabel: " + ssvlabel)

			println("pv status: " + pv)
			println("v status: " + v)
			println("sv status: " + sv)
			println("ssv status: " + ssv)

			if (pv != null) {
				when (pv <- getName(), v <- getName(), sv <- getName(), ssv <- getName()) -> done(pvn, vn, svn, ssvn) :any {
					println("pvn: " + pvn)
					println("vn: " + vn)
					println("svn: " + svn)
					println("ssvn: " + ssvn)
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
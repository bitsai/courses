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

def makePeer(myName :String) :any {
	var label := ""
	var succ := null
	var sLabel := ""
	var pred := null
	var pLabel := ""
	var parent := null
	var parentLabel := ""
	var lchild := null
	var lchildLabel := ""
	var rchild := null
	var rchildLabel := ""
	var super := null

	def peer {
		to join(supervisorName :String) :void {
			def s := objFromFile(introFile(supervisorName))
			s <- join(peer)
			super := s
		}
		to leave() :void {
			super <- leave(label, pred, pLabel, succ, sLabel, parent, parentLabel, lchild, lchildLabel, rchild, rchildLabel)
		}
		to setup(l :String, p :rcvr, pl :String, s :rcvr, sl :String, f :rcvr, fl :String, lc :rcvr, lcl :String, rc :rcvr, rcl :String) :void {
			label := l
			pred := p
			pLabel := pl
			succ := s
			sLabel := sl
			parent := f
			parentLabel := fl
			lchild := lc
			lchildLabel := lcl
			rchild := rc			
			rchildLabel := rcl
		}
		to setSucc(w :rcvr, wl :String) :void {
			succ := w
			sLabel := wl
		}
		to setPred(w :rcvr, wl :String) :void {
			pred := w
			pLabel := wl
		}
		to setParent(w :rcvr, wl :String) :void {
			parent := w
			parentLabel := wl
		}
		to setLeftChild(w :rcvr, wl :String) :void {
			lchild := w
			lchildLabel := wl
		}
		to setRightChild(w :rcvr, wl :String) :void {
			rchild := w
			rchildLabel := wl
		}
		to getSucc() :rcvr {
			return succ
		}
		to getPred() :rcvr {
			return pred
		}
		to getPredPred() :rcvr {
			return pred <- getPred()
		}

		to broadcast(message :String) :void {
			super <- broadcast(myName + ": " + message)
		}
		to sendDown(message :String) :void {
			println(message)
			lchild <- sendDown(message)
			rchild <- sendDown(message)
		}

		to getLabel() :String {
			return label
		}
		to getName() :String {
			return myName
		}
		to ping() :void {
			println()
			println("label: " + label)

			println("p label: " + pLabel)
			println("s label: " + sLabel)
			println("parent label: " + parentLabel)
			println("lc label: " + lchildLabel)
			println("rc label: " + rchildLabel)

			println("p status: " + pred)
			println("s status: " + succ)
			println("parent status: " + parent)
			println("lc status: " + lchild)
			println("rc status: " + rchild)

			if (pred != null) {
				when (pred <- getName(), succ <- getName()) -> done(pn, sn) :any {
					println("pn: " + pn)
					println("sn: " + sn)
				} catch e {}
			}
			
			if (parent != null) {
				when (parent <- getName()) -> done1(parentn) :any {
					println("parentn: " + parentn)
				} catch e {}
			}

			if (lchild != null) {
				when (lchild <- getName()) -> done2(lchildn) :any {
					println("lchildn: " + lchildn)
				} catch e {}
			}

			if (rchild != null) {
				when (rchild <- getName()) -> done3(rchildn) :any {
					println("rchildn: " + rchildn)
				} catch e {}
			}
		}
	}
	objIntoFile(peer, introFile(myName))
	return peer
}

if (interp.getArgs() =~ [myName]) {
	introducer.onTheAir()
	makePeer(myName)	
	println(`peer $myName waiting...`)
	interp.blockAtTop()
} else {
	println("usage: peer.e myName")
}
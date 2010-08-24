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

def makePeer(myName) :any {
	var label := "None"
	var succ := null
	var succLabel := "None"
	var pred := null
	var predLabel := "None"
	var parent := null
	var parentLabel := "None"
	var lchild := null
	var lchildLabel := "None"
	var rchild := null
	var rchildLabel := "None"
	var super := null

	var data := null

	def peer {
		to enqueue(x) :any {
			super <- enqueue(x)
		}
		
		to dequeue() :any {
			super <- dequeue()
		}
		
		to EQmoveDown(x) :any {
			if (parentLabel == "None" && data == null) {
				super <- print("Enqueued " + x)
				data := x
			} else if (data != null) {
				if (lchildLabel != "None" && rchildLabel != "None") {
					if (entropy.nextInt(2) == 0) {
						lchild <- EQmoveDown(x)
					} else { 
						rchild <- EQmoveDown(x) 
					}
				} else if (lchildLabel != "None") {
					lchild <- EQmoveDown(x)
				} else if (rchildLabel != "None") {
					rchild <- EQmoveDown(x)
				} else {
					super <- print("Dropped " + x + " due to full branch")
				}
			} else {
				peer <- EQmoveUp(x)
			}
		}

		to EQmoveUp(x) :any {
			def oldData := data

			if (parentLabel == "None") {
				super <- print("Enqueued " + x)
				data := x
			} else {
				data := parent <- EQmoveUp(x)
			}

			return oldData
		}

		to DQmoveDown() :any {
			var lchildFullVow := false
			var rchildFullVow := false
			
			if (lchildLabel != "None") { lchildFullVow := lchild <- isFull() }
			if (rchildLabel != "None") { rchildFullVow := rchild <- isFull() }
			
			when (lchildFullVow, rchildFullVow) -> doneDQ(lchildFull, rchildFull) :any {
				if (lchildFull == true && rchildFull == true) {
					if (entropy.nextInt(2) == 0) {
						lchild <- DQmoveDown()
					} else {
						rchild <- DQmoveDown()
					}
				} else if (lchildFull == true) {
					lchild <- DQmoveDown()
				} else if (rchildFull == true) {
					rchild <- DQmoveDown()
				} else {
					if (data == null) {
						super <- print("Nothing to dequeue")
					} else {
						super <- print("Dequeued " + data)
						data := null
					}
				}
			} catch e {}
		}
		
		to isFull() :boolean {
			if (data != null) {
				return true
			} else {
				return false
			}
		}

		to join(s) :any {
			if (super != null) {
				println("\nAlready part of a network!")
			} else {
				super := objFromFile(introFile(s))
				super <- join(peer)
			}
		}

		to leave() :any {
			if (super == null) {
				println("\nNot part of a network!")
			} else {
				super <- leave(label, pred, predLabel, succ, succLabel, parent, parentLabel, lchild, lchildLabel, rchild, rchildLabel)
				peer <- clear()
			}
		}

		to setup(l, p, pl, s, sl, f, fl, lc, lcl, rc, rcl) :any {
			label := l
			pred := p
			predLabel := pl
			succ := s
			succLabel := sl
			parent := f
			parentLabel := fl
			lchild := lc
			lchildLabel := lcl
			rchild := rc			
			rchildLabel := rcl
		}

		to setSucc(w, wl) :any {
			succ := w
			succLabel := wl
		}

		to setPred(w, wl) :any {
			pred := w
			predLabel := wl
		}

		to setParent(w, wl) :any {
			parent := w
			parentLabel := wl
		}

		to setLeftChild(w, wl) :any {
			lchild := w
			lchildLabel := wl
		}

		to setRightChild(w, wl) :any {
			rchild := w
			rchildLabel := wl
		}

		to getSucc() :any { return succ }

		to getPred() :any { return pred }

		to getPredPred() :any { return pred <- getPred() }

		to clear() :any {
			label := "None"
			pred := null
			predLabel := "None"
			succ := null
			succLabel := "None"
			parent := null
			parentLabel := "None"
			lchild := null
			lchildLabel := "None"
			rchild := null			
			rchildLabel := "None"
			super := null
		}

		to broadcast(m) :any { super <- broadcast(myName + ": " + m) }

		to sendDown(m) :any {
			println(m)
			if (lchildLabel != "None") { lchild <- sendDown(m) }
			if (rchildLabel != "None") { rchild <- sendDown(m) }
		}

		to getLabel() :String { return label }

		to getName() :String { return myName }

		to ping() :any {
			println("\nname: " + myName)
			println("label: " + label)
			println("data: " + data)

			if (pred != null) {
				when (pred <- getName()) -> done0(predName) :any {
					println("predName: " + predName)
				} catch e {}
			}

			if (succ != null) {
				when (succ <- getName()) -> done1(succName) :any {
					println("succName: " + succName)
				} catch e {}
			}

			if (parent != null) {
				when (parent <- getName()) -> done2(parentName) :any {
					println("parentName: " + parentName)
				} catch e {}
			}

			if (lchild != null) {
				when (lchild <- getName()) -> done3(lchildName) :any {
					println("lchildName: " + lchildName)
				} catch e {}
			}

			if (rchild != null) {
				when (rchild <- getName()) -> done4(rchildName) :any {
					println("rchildName: " + rchildName)
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
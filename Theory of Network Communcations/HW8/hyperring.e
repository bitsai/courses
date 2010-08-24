#!/usr/bin/env rune

# Benny Tsai
# 600.448
# Theory of Net. Comm.
# Homework 8

def introFile(num) :any {
	return <file: `intro-$num.cap`>
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

def contains(min, max, num) :boolean {
	if (min <= max) {
		if (min <= num && num <= max) { return true }
	} else {
		if (min <= num || num <= max) { return true }
	}
	
	return false
}

def generalJoin(pp, p, joiner, s, ss) :any {
	when (pp <- getNum(), pp <- getSuccNum(1), p <- getPredNum(1), p <- getNum(), p <- getSuccNum(1), joiner <- getNum(), s <- getPredNum(1), s <- getNum(), s <- getSuccNum(1), ss <- getPredNum(1), ss <- getNum()) -> doneGJ(pp_Num, pp_sNum, p_pNum, p_Num, p_sNum, j_Num, s_pNum, s_Num, s_sNum, ss_pNum, ss_Num) :any {
		if (pp_sNum == s_Num && p_sNum == ss_Num && s_pNum == pp_Num && ss_pNum == p_Num) {
			### Case (a)
			println()
			println("join a")
			
			p <- setSucc(1, joiner, j_Num)
			joiner <- setPred(1, p, p_Num)
			joiner <- setSucc(1, ss, ss_Num)
			ss <- setPred(1, joiner, j_Num)
		} else if (pp_sNum == ss_Num && p_sNum == s_Num && s_pNum == p_Num && ss_pNum == pp_Num) {
			### Case (b)
			println()
			println("join b")
			
			pp <- setSucc(1, joiner, j_Num)
			joiner <- setPred(1, pp, pp_Num)
			joiner <- setSucc(1, ss, ss_Num)
			ss <- setPred(1, joiner, j_Num)
		} else if (pp_sNum == p_Num && p_pNum == pp_Num) {
			### Case (c1)
			println()
			println("join c1")
			
			when (pp <- getPred(0), p <- getSucc(1)) -> doneGJC1(ppp, ps) :any {
				pp <- setSucc(1, joiner, j_Num)
				joiner <- setPred(1, pp, pp_Num)
				joiner <- setSucc(1, ps, p_sNum)
				ps <- setPred(1, joiner, j_Num)
				
				generalJoin(ppp, pp, p, joiner, s)
			} catch error {}
		} else if (s_sNum == ss_Num && s_pNum == s_Num) {
			### Case (c2)
			println()
			println("join c2")
			
			when (ss <- getSucc(0), s <- getPred(1)) -> doneGJC2(sss, sp) :any {
				sp <- getSucc(1, joiner, j_Num)
				joiner <- setPred(1, sp, s_pNum)
				joiner <- setSucc(1, ss, ss_Num)
				ss <- setPred(1, joiner, j_Num)
				
				generalJoin(p, joiner, s, ss, sss)
			} catch error {}
		}
	} catch error {}
}

def generalLeave(pp, p, leaver, s, ss) :any {
	when (pp <- getNum(), pp <- getSuccNum(1), p <- getPredNum(1), p <- getNum(), p <- getSuccNum(1), leaver <- getNum(), s <- getPredNum(1), s <- getNum(), s <- getSuccNum(1), ss <- getPredNum(1), ss <- getNum()) -> doneGL(pp_Num, pp_sNum, p_pNum, p_Num, p_sNum, l_Num, s_pNum, s_Num, s_sNum, ss_pNum, ss_Num) :any {
		if (pp_sNum == l_Num && p_sNum == s_Num && s_pNum == p_Num && ss_pNum == l_Num) {
			### Case (a)
			println()
			println("leave a")
			
			pp <- setSucc(1, ss, ss_Num)
			ss <- setPred(1, pp, pp_Num)
			
			leaver <- clear()
		} else if (pp_sNum == s_Num && p_sNum == l_Num && s_pNum == pp_Num && ss_pNum == l_Num) {
			### Case(b1)
			println()
			println("leave b1")
			
			p <- setSucc(1, ss, ss_Num)
			ss <- setPred(1, p, p_Num)
			
			leaver <- clear()
		} else if (pp_sNum == l_Num && p_sNum == ss_Num && s_pNum == l_Num && ss_pNum == p_Num) {
			### Case(b2)
			println()
			println("leave b2")
			
			pp <- setSucc(1, s, s_Num)
			s <- setPred(1, pp, pp_Num)
			
			leaver <- clear()
		} else if (pp_sNum == p_Num && p_pNum == pp_Num && p_sNum == s_Num && s_pNum == p_Num) {
			### Case(c1)
			println()
			println("leave c1")
			
			when (pp <- getPred(0), pp <- getPredNum(0), leaver <- getSucc(1), leaver <- getSuccNum(1)) -> doneGLC1(ppp, ppp_Num, ls, ls_Num) :any {
				ppp <- setSucc(1, p, p_Num)
				p <- setPred(1, ppp, ppp_Num)
				p <- setSucc(1, ls, ls_Num)
				ls <- setPred(1, p, p_Num)
				
				pp <- setSucc(1, leaver, l_Num)
				leaver <- setPred(1, pp, pp_Num)
				leaver <- setSucc(1, s, s_Num)
				s <- setPred(1, leaver, l_Num)
				
				generalLeave(pp, p, leaver, s, ss)
			} catch error {}
		} else if (p_sNum == s_Num && s_pNum == p_Num && s_sNum == ss_Num && ss_pNum == s_Num) {
			### Case(c2)
			println()
			println("leave c2")
			
			when (ss <- getSucc(0), ss <- getSuccNum(0), leaver <- getPred(1), leaver <- getPredNum(1)) -> doneGLC2(sss, sss_Num, lp, lp_Num) :any {
				p <- setSucc(1, leaver, l_Num)
				leaver <- setPred(1, p, p_Num)
				leaver <- setSucc(1, ss, ss_Num)
				ss <- setPred(1, leaver, l_Num)
				
				lp <- setSucc(1, s, s_Num)
				s <- setPred(1, lp, lp_Num)
				s <- setSucc(1, sss, sss_Num)
				sss <- setPred(1, s, s_Num)
				
				generalLeave(pp, p, leaver, s, ss)
			} catch error {}
		}
	} catch error {}
}

def makePeer(myNum) :any {
	def pred := [].diverge()
	def predNum := [].diverge()
	def succ := [].diverge()
	def succNum := [].diverge()

	def peer {
		to join(qNum) :any {
			if (pred.size() > 0) {
				### No joining if already part of a network
				println()
				println("Already part of a network!")
			} else if (qNum == myNum) {
				### I'm the first peer in the network

				### Set up 1-peer 0-ring
				peer <- setPred(0, peer, myNum)
				peer <- setSucc(0, peer, myNum)
				
				### Set up 1-peer 1-ring
				peer <- setPred(1, peer, myNum)
				peer <- setSucc(1, peer, myNum)
			} else {
				### Route join request to appropriate peer
				def q := objFromFile(introFile(qNum))
				q <- joinRequest(myNum, peer)
			}
		}
		
		to leave() :any {
			if (pred.size() == 0) {
				### No leaving if not part of a network
				println()
				println("Not part of a network!")
			} else {
				### Restore 0-ring
				pred[0] <- setSucc(0, succ[0], succNum[0])
				succ[0] <- setPred(0, pred[0], predNum[0])
			
				### Restore 1-ring
				when (pred[0] <- getPred(0), succ[0] <- getSucc(0)) -> doneL(predPred, succSucc) :any {
					generalLeave(predPred, pred[0], peer, succ[0], succSucc)
				} catch error {}
			}
		}

		to route(yNum, msg) :any {
			if (succNum[0] == myNum || contains(predNum[0], myNum, yNum)) {
				### If i'm the only peer in the system, or if i'm the recipient, handle this message
				println()
				println(msg)
			} else {
				succ[0] <- route(yNum, msg)
			}
		}

		to joinRequest(newNum, newPeer) :any {
			if (succNum[0] == myNum || contains(predNum[0], myNum, newNum)) {
				### If i'm the only peer in the system, or if i'm the recipient, handle this message
			
				### Check to see if this peer is the second to join
				var secondPeer := false
				if (succNum[0] == myNum) { secondPeer := true }
				
				def oldPred := pred[0]
			
				### Integrate new peer into 0-ring
				pred[0] <- setSucc(0, newPeer, newNum)
				newPeer <- setPred(0, pred[0], predNum[0])
				newPeer <- setSucc(0, peer, myNum)
				peer <- setPred(0, newPeer, newNum)
				
				if (secondPeer) {
					### If this is the second peer to join, give it its own 1-ring
					newPeer <- setPred(1, newPeer, newNum)
					newPeer <- setSucc(1, newPeer, newNum)
				} else {
					### Do the complicated join thing
					when (oldPred<- getPred(0)) -> doneJR(predPred) :any {
						generalJoin(predPred, oldPred, newPeer, peer, succ[0])
					} catch error {}
				}
			} else {
				succ[0] <- joinRequest(newNum, newPeer)
			}
		}

		to clear() :any {
			pred.removeRun(0, pred.size())
			predNum.removeRun(0, predNum.size())
			succ.removeRun(0, succ.size())
			succNum.removeRun(0, succNum.size())
		}

		to setPred(i, peer, num) :any {
			pred[i] := peer
			predNum[i] := num
		}

		to setSucc(i, peer, num) :any {
			succ[i] := peer
			succNum[i] := num
		}

		to getNum() :int {
			return myNum
		}

		to getPred(i) :any {
			return pred[i]
		}

		to getPredNum(i) :int {
			return predNum[i]
		}

		to getSucc(i) :any {
			return succ[i]
		}

		to getSuccNum(i) :int {
			return succNum[i]
		}

		to ping() :any {
			println()
			println("Num: " + myNum)

			if (pred.size() > 0) { println("Pred 0: " + pred[0]) }
			if (predNum.size() > 0) { println("Pred 0 Num: " + predNum[0]) }
			if (succ.size() > 0) { println("Succ 0: " + succ[0]) }
			if (succNum.size() > 0) { println("Succ 0 Num: " + succNum[0]) }
			
			if (pred.size() > 1) { println("Pred 1: " + pred[1]) }
			if (predNum.size() > 1) { println("Pred 1 Num: " + predNum[1]) }
			if (succ.size() > 1) { println("Succ 1: " + succ[1]) }
			if (succNum.size() > 1) { println("Succ 1 Num: " + succNum[1]) }
		}
	}

	objIntoFile(peer, introFile(myNum))
	return peer
}

if (interp.getArgs() =~ [myNum]) {
	introducer.onTheAir()
	makePeer(__makeInt(myNum))
	println(`peer $myNum waiting...`)
	interp.blockAtTop()
} else {
	println("usage: hyperring.e myNum")
}
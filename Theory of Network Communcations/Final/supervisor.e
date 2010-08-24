#!/usr/bin/env rune

# Returns a cap file to be written to
def introFile(name) :any {
	return <file: `intro-$name.cap`>
}

# Returns a sturdy reference from a file
def objFromFile(file) :any {
	if (file.exists()) {
		return introducer.sturdyFromURI(file.getText()).getRcvr()
	} else {
		return null
	}
}

# Writes into designated file
def objIntoFile(obj, file) :any {
	file.setText(introducer.sturdyToURI(makeSturdyRef.temp(obj)))
}

# Computes binary form of an integer
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

# Computes result of applying l() function on input integer; function is defined in Lecture 7 notes
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

# Returns the result of AND-ing the input string (assumed to be the string representation of a binary number) with 2
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
	
	def users := [].asSet().diverge()	# Hash set of currently logged in user names
	def passwords := [].asMap().diverge()	# Hash map of authorized user names and password

	def supervisor {		
		to join(un, w) :any {		
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
			
			# Add user name to list of logged in users, and notify all peers
			users.addElement(un)
			supervisor.sendMessage("System", un + " just joined; there are now " + users.size() + " online users")
		}

		to leave(un, l, pw, pwl, sw, swl, fw, fwl, lcw, lcwl, rcw, rcwl) :any {		
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
				
				# Remove user name from list of logged in users, and notify all peers
				users.remove(un)
				supervisor.sendMessage("System", un + " just left; there are now " + users.size() + " online users")
			}
		}

		# Reads content of designated password file into passwords hash map; return true if successful, false if no file is found
		to getPasswords(passwordsLocation) :any {
			def passwordsFile := <file: `$passwordsLocation`>

			if (passwordsFile.exists()) {
				def contents := passwordsFile.getText()

				for line in contents.split("\n") {
				        println(line)
				        
				        if (line =~ `@username:@password`) {
				        	passwords[username] := password
				        }
    				}
    				
    				return true
			} else {
				println("Passwords file not found!")
				
				return false
			}
		}

		# Checks passed in username and password against passwords hash map, and tells chatController the result
		to authenticate(username, password, chatController) :any {
			println("Authenticating: " + username + ":" + password)
		
			if (passwords.maps(username) == false) {
				chatController <- receiveMessage("System", username + " is not a registered user")
			} else if (passwords[username] != password) {
				chatController <- receiveMessage("System", "incorrect password for " + username)
			} else if (users.contains(username)) {
				chatController <- receiveMessage("System", username + " is already logged in")
			} else if (passwords[username] == password) {
				chatController <- login(username)
			}
		}

		# Begins broadcast of a message by sending it to root peer
		to sendMessage(name, message) :any {
			println(name + ": " + message)
			if (root != null) { root <- sendMessage(name, message) }
		}

		# Begins broadcast of knock notification by sending it to root peer		
		to sendKnock(senderName, recipientName, sender) :any {
			println(senderName + " is knocking " + recipientName)
			if (root != null) { root <- sendKnock(senderName, recipientName, sender) }
		}
		
		# Begins broadcast of disconnect notification by sending it to root peer
		to sendDisconnect(name) :any {
			println(name + " is disconnecting")
			if (root != null) { root <- sendDisconnect(name) }
		}
		
		# Returns list of logged in users
		to getUsers(requester) :any {
			var output := ""
		
			for value in users { 
				if (output == "") {
					output := value
				} else {
					output += ", " + value
				}
			}

			if (output == "") { output := "none" }
			println("Online users: " + output)
			requester <- receiveMessage("Online users", output)
		}
		
		# Diagnostic function
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
			
			supervisor.getUsers()
		}
	}
	objIntoFile(supervisor, introFile(myName))
	return supervisor
}

if (interp.getArgs() =~ [name, passwordsLocation]) {
	introducer.onTheAir()
	def supervisor := makeSupervisor(name)

	if (supervisor.getPasswords(passwordsLocation)) {
		println(`supervisor $name waiting...`)
		interp.blockAtTop()
	}
} else {
	println("usage: supervisor.e <name> <passwords file location >")
}
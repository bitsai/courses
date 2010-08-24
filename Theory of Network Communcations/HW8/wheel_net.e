#!/usr/bin/env rune

#*******************************************************************************
#Wheel Network Implementation with Join, Leave and Route
#
#Partners :- Pavan Piratla , Nilo Rivera and Sandeep Ranade
#
#Please see attached ReadME document for operational instructions
#*******************************************************************************

def myName
def myHashVal

def peerFile(name) :any {
    return <file: `wheel-net-$name.cap`>
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

def Peer() :any{
    var succHashVal := 0
    var succRef := null
    var predHashVal := null
    var predRef := null
    var contact := null
    def sr                       # Self Reference
   
    def setup(pRef, pHval, sRef, sHval) :any {        
        predRef := pRef
        predHashVal := pHval
        succRef := sRef
        succHashVal := sHval
        println(`[$myName] I have joined the ring! `)
        println(`[$myName] My Region = [$predHashVal -- $myHashVal -- $succHashVal]`)
    }
        
    # If s is null, I am the first node.
    def join (s) :any {
        if (predRef != null) {
             println(`[$myName] Cannot Join.  I already joined a ring.`)
        } else if (s != null) {
             contact := objFromFile(peerFile(s))
             contact <- joinRequest(myHashVal, sr)
        } else {
             # No node activate...I am the first.
             setup(sr, myHashVal, sr, myHashVal)
             contact := 1
        }    
    }
    
    def leave() :any {       
        if (predRef == null) {
            println(`[$myName] Cannot Leave.  Currently not a member of any ring.`)
        } else {
            println(`[$myName] Leaving the ring!`)
            if (predHashVal == myHashVal && predHashVal == succHashVal) {
               println(`[$myName] Last node to leave the ring.  Ring no longer exists!`)
            } else {
               predRef <- setSucc(succRef, succHashVal)
               succRef <- setPred(predRef, predHashVal)
               #interp.exitAtTop()
            }        
            succHashVal := 0
            succRef := null
            predHashVal := null
            predRef := null
            contact := null
        }
    }
   
    def remoteCalls {
        to Join (s) :any {
            join(s)
        }
        
        to Route(y, msg) :any {
            # Route to peer whose region contains point y
            if (predRef == null) {
                println(`[$myName] Cannot Route. Currently not a member of any ring.`)
            } else {                       
              def temp_hash := y.cryptoHash()
              def yHashVal := ( (temp_hash) / (1<<160) )           
              if ( yHashVal != predHashVal && 
                 (yHashVal >= predHashVal && yHashVal <= myHashVal) || 
                   predHashVal >= myHashVal &&
                   ( (yHashVal >= myHashVal && yHashVal >= predHashVal) || 
                     (yHashVal <= myHashVal) ) ) {            
                     println(`[$myName] Got the Following Message destined to $y -> $yHashVal`)
                     println(`[$myName] : $msg`)
              } else {
                def newmsg := msg + ` [$myName]` 
                println(`[$myName] Routing Message`)
                succRef <- Route(y, newmsg)
              }
            }
        }   
        
        to Leave() :any {
            leave()
        }             
        
        to Setup(pRef, pHval, sRef, sHval) :any {
            setup(pRef, pHval, sRef, sHval)
        }
        
        to setSucc(wRef, wHval) :any {
            succRef := wRef
            succHashVal := wHval
            println(`[$myName] My Region = [$predHashVal -- $myHashVal -- $succHashVal]`)
        }
        
        to setPred (wRef, wHval) :any {
            predRef := wRef
            predHashVal := wHval
            println(`[$myName] My Region = [$predHashVal -- $myHashVal -- $succHashVal]`)
                
        }
        
        to getHashVal() :any {
            return myHashVal
        }
        
        to joinRequest(joinerHval, joinerRef) :any {
            # A new node wants to join.
            if (predHashVal == myHashVal && succHashVal == myHashVal ) {
               # Only one node in system
                joinerRef <- Setup(sr, myHashVal, sr, myHashVal)
                predRef := joinerRef ; predHashVal := joinerHval
                succRef := joinerRef ; succHashVal := joinerHval
                println(`[$myName] My Region = [$predHashVal -- $myHashVal -- $succHashVal]`)               
            } else if ( (joinerHval >= predHashVal && joinerHval <= myHashVal) || 
                        predHashVal >= myHashVal && 
                          ( (joinerHval >= myHashVal && joinerHval >= predHashVal) || 
                            (joinerHval <= myHashVal) ) ) {
                # I am the successor of the new guy
                joinerRef <- Setup(predRef, predHashVal, sr, myHashVal)
                # Tell my predecessor that his successor is the new joiner
                predRef <- setSucc(joinerRef, joinerHval)
                predRef := joinerRef
                predHashVal := joinerHval
                println(`[$myName] My Region = [$predHashVal -- $myHashVal -- $succHashVal]`)
            } else {
                # Route around wheel to my successor
                succRef <- joinRequest(joinerHval, joinerRef)
            }          
            
        }
        
        to ping() :any {
        	println(sr)
        	println(predRef)
        	println(succRef)
        }
    }
    # Only need this to be able to broadcast from console
    objIntoFile(remoteCalls, peerFile(myName))   
    bind sr := remoteCalls 
} 

if (interp.getArgs() =~ [Name, Value]) { 
    bind myName := Name 
    def num := __makeInt(Value) :int
    def temp_hash := num.cryptoHash()
    bind myHashVal := ( (temp_hash) / (1<<160) )
    introducer.onTheAir()
    println(`Initializing Peer [$myName] with Hash [$myHashVal]`)   
    Peer()
    interp.blockAtTop()
} else {
    println("usage: wheel_net.e Name Value")
}
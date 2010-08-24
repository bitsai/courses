ifeq ($(SWARMHOME),)
SWARMHOME=/swarm-2.1.1
endif
APPLICATION=schelling
BUGADDRESS=bug-swarm@santafe.edu
APPVERSION=2.0.1

OBJECTS=EmptyCell.o Inhabitant.o main.o SchellingModelSwarm.o \
	SchellingObject.o \
	SchellingObserverSwarm.o 

DATAFILES = schelling.scm

include $(SWARMHOME)/etc/swarm/Makefile.appl

main.o: main.m Inhabitant.h SchellingObserverSwarm.h 
EmptyCell.o: EmptyCell.m EmptyCell.h
Inhabitant.o: Inhabitant.m Inhabitant.h
SchellingObject.o: SchellingObject.m SchellingObject.h
SchellingObserverSwarm.o: SchellingObserverSwarm.m SchellingObserverSwarm.h SchellingModelSwarm.h
SchellingModelSwarm.o: SchellingModelSwarm.m SchellingModelSwarm.h







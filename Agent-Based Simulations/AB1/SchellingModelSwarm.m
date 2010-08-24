#import <random.h>
#import <space.h>

#import "EmptyCell.h"
#import "SchellingModelSwarm.h"


#define min(a,b) ((a) < (b) ? (a) : (b))
#define max(a,b) ((a) > (b) ? (a) : (b))


@implementation SchellingModelSwarm
- getInhabitants {
  return inhabitantList;
}

- getEmptyCellList {
  return emptyList;
}

- (id <Grid2d>) getWorld {
  return world;
}

- (void) setRandomizedOrder: (BOOL) flag {
  randomizeUpdateOrder = flag;
}

- addInhabitant: (Inhabitant *) inhabitant {
  [inhabitantList addLast: inhabitant];
  return self;
}

+ createBegin: aZone
{
  SchellingModelSwarm *obj;
  id <CustomProbeMap> probeMap;

  obj = [super createBegin: aZone];

  obj->numRedInhabitants = 1200;
  obj->numBlueInhabitants = 1200;
  obj->worldXSize = 50;
  obj->worldYSize = 50;
  obj->happinessThreshold = .5;
  obj->randomizeUpdateOrder = NO;
  obj->neighborhoodRadius = 1;
  
  // New

  obj->minHappiness = 0.0;
  obj->maxHappiness = 1.0;
  obj->adaptability = 0.01;

  probeMap = 
    [CustomProbeMap create: aZone 
                    forClass: [self class]
                    withIdentifiers:  "numRedInhabitants", "numBlueInhabitants", 
                    "worldXSize", "worldYSize", "happinessThreshold", "minHappiness", "maxHappiness", "adaptability",
                    "neighborhoodRadius", ":", "toggleRandomizedOrder",
                    NULL]; 

  [probeLibrary setProbeMap: probeMap For: [self class]];
  
  return obj;
}

- (BOOL)toggleRandomizedOrder
{
  randomizeUpdateOrder = !randomizeUpdateOrder;
  return randomizeUpdateOrder;
}

-(void) createInhabitant: (int) type {
  int x, y;

  Inhabitant *inhabitant;
      
  inhabitant = [Inhabitant createBegin: self];
  [inhabitant setWorld: world];
  [inhabitant setModel: self];
  [inhabitant setHappinessThreshold: happinessThreshold];

  // New
  
  [inhabitant setAdaptability: adaptability];
  [inhabitant setHappinessThresholdsMax: maxHappiness Min: minHappiness];

  inhabitant = [inhabitant createEnd];
      
  [inhabitantList addLast: inhabitant];

  [inhabitant setType: type];

  do {
    x = [uniformIntRand getIntegerWithMin: 0L withMax: (worldXSize - 1)];
    y = [uniformIntRand getIntegerWithMin: 0L withMax: (worldXSize - 1)];
  } while ([world getObjectAtX: x Y: y] != nil);
      
  [inhabitant setX: x Y: y];
}

- buildObjects
{
  int i;
  int x;
  int y;

  [super buildObjects];
  
  world = [Grid2d create: self setSizeX: worldXSize Y: worldYSize];

  inhabitantList = [List create: self];
  
  [world setOverwriteWarnings: 1];

  for (i = 0; i < numRedInhabitants; ++i) {
    [self createInhabitant: 1];
  }

  for (i = 0; i < numBlueInhabitants; ++i) {
    [self createInhabitant: 2];
  }
    
  emptyList = [List create: self];
  
  for (x = 0; x < worldXSize; ++x) {
    for (y = 0; y < worldYSize; ++y) {
      if ([world getObjectAtX: x Y: y] == nil) {
        EmptyCell *emptyCell = [EmptyCell createBegin: self];
        [emptyCell setWorld: world];
        emptyCell = [emptyCell createEnd];
        [emptyCell setX: x Y: y];
        [emptyList addLast: emptyCell];
      }   
    }
  }

  [emptyList forEach: M(computeNeighborPercentages)];

  listShuffler = [ListShuffler create: self];
  
  return self;
}

-doShuffle {
  [listShuffler shuffleWholeList: emptyList];

  return self;
}


-installModelActions {
  [modelSchedule at: 0 createAction: modelActions];
  [modelSchedule remove: firstStepActionTo];
  return self;
}

- buildActions
{
  [super buildActions];

  firstStepActions = [ActionGroup create: self];
  [firstStepActions createActionTo: self message: M(installModelActions)];
  
  modelActions = [ActionGroup create: self];

  [modelActions createActionTo: self message: M(doShuffle)];

  if (randomizeUpdateOrder == YES)
    [[modelActions createActionForEach: inhabitantList
                   message: M(step)] 
      setDefaultOrder: Randomized];
  else
    [modelActions createActionForEach: inhabitantList message: M(step)];  

  modelSchedule = [Schedule create: self setRepeatInterval: 1];
  firstStepActionTo = [modelSchedule at: 0 createAction: firstStepActions];

  return self;
}

- activateIn: swarmContext
{
  [super activateIn: swarmContext];


  [modelSchedule activateIn: self];

  return [self getActivity];
}

// New

- (double) getMaxSeg {
	int numWhite = (worldXSize * worldYSize) - (numRedInhabitants + numBlueInhabitants);
	
	if (numWhite > min(worldXSize, worldYSize)) return 1.0;
	else return (1 - (2*(min(worldXSize, worldYSize) - numWhite ) / (numRedInhabitants + numBlueInhabitants)));
}

@end

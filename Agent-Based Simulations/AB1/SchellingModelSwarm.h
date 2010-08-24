#import "Inhabitant.h"
#import "SchellingSpace.h"
#import <space.h>
#import <objectbase/Swarm.h>
//#import <collections.h>
//#import <collections/ListShuffler.h>

@interface SchellingModelSwarm: Swarm
{
  int numBlueInhabitants;
  int numRedInhabitants;
  int worldXSize, worldYSize;
  float happinessThreshold;
  int neighborhoodRadius;

// New

  float minHappiness;
  float maxHappiness;
  float adaptability;

  BOOL randomizeUpdateOrder;
  id modelActions;
  id modelSchedule;

  id inhabitantList;
  id<Grid2d> world;
  id<List> emptyList;
  id<ListShuffler> listShuffler;

  SchellingSpace *schellBeach;

  id <ActionGroup> firstStepActions;
  id <ActionGroup> firstStepActionTo;
}

- getInhabitants;
- getEmptyCellList;
- (id<Grid2d>) getWorld;

- (void) setRandomizedOrder: (BOOL) flag;

- (BOOL) toggleRandomizedOrder;      // method to toggle the


- addInhabitant: (Inhabitant *) inhabitant;

+ createBegin: aZone;
- buildObjects;
- buildActions;
- activateIn: swarmContext;

- (double) getMaxSeg; // New

@end

#import <space.h>
#import <gui.h>
#import <objectbase.h>

#import <objectbase/SwarmObject.h>


@class SchellingModelSwarm;


@interface Inhabitant: SwarmObject
{
  float happinessThreshold;
  int x, y;

  // New

  float adaptability;
  float maxHappiness, minHappiness;

  id <Grid2d> world;
  SchellingModelSwarm *model;

  int worldXSize, worldYSize;
  int type;
  double samePercent;
}

- setWorld: (id <Grid2d>) w;
- setModel: (SchellingModelSwarm *) m;
- createEnd;

- setHappinessThreshold: (double) threshhold;
- setX: (int) x Y: (int) y;
- setType: (int) t;

- (int) getCellType;

-(void) updateEmptyNeighbors;

- (double) getNeighborRatio;
- (double) getNeighborRatioAtX: (int) testX Y: (int) testY;

- step;

- drawSelfOn: (id <Raster>) r;
-(int) isEmptyCell;

// New

- updateThreshold;
- setAdaptability: (float) inAdaptability;
- setHappinessThresholdsMax: (float) maxHappiness Min: (float) minHappiness;

@end



#import <random.h>
#import "Inhabitant.h"
#import "EmptyCell.h"

#import "SchellingModelSwarm.h"


@implementation Inhabitant

- setWorld: (id <Grid2d>) w {
  world = w;

  return self;
}

- setModel: (SchellingModelSwarm *) m {
  model = m;
  return self;
}

- createEnd
{
  if (world == nil)
    [InvalidCombination
      raiseEvent: "Inhabitant was created without a world.\n"];
  
  worldXSize = [world getSizeX];
  worldYSize = [world getSizeY];

  return self;		
}

- (int) getCellType {
  return type;
}

- (double) getNeighborRatio {
  samePercent = [self getNeighborRatioAtX: x Y: y];
  return samePercent;
}

- setHappinessThreshold: (double) threshold {
  happinessThreshold = threshold;
  return self;
}

- (double) getNeighborRatioAtX: (int) testX Y: (int) testY;
{
  int numSame = 0;
  int numDifferent = 0;
  Inhabitant *neighbor;
  int i, j;

  for (i = -1; i <= 1; ++i) {
    int nx, ny;

    ny = (testY + i + worldYSize) % worldYSize;
    for (j = -1; j <= 1; ++j) {
      if (i == 0 && j == 0) {
        continue;
      }

      nx = (testX + j + worldXSize) % worldXSize;
      neighbor = [world getObjectAtX: nx Y: ny];
      if ([neighbor isEmptyCell]) {
        continue;
      }

      if (neighbor != nil) {
        if ([neighbor getCellType] == type) {
          ++numSame;
        }
        else {
          ++numDifferent;
        }
      }
    }
  }

  if (numSame + numDifferent == 0) {
    return 1.0;
  }
  else {
    return (double) numSame/(numSame + numDifferent);
  }
}

- setType: (int) t
{
  type = t;
  return self;
}

- setX: (int)inX Y: (int)inY
{
  x = inX;
  y = inY;
  [world putObject: self atX: x Y: y];	
  return self;
}

-(int) isEmptyCell {
  return 0;
}

-(void) updateEmptyNeighbors {
  int dx, dy, nx, ny;
  id cell;

  for (dx = -1; dx <= 1; ++dx) {
    nx = (x + dx + worldXSize) % worldXSize;

    for (dy = -1; dy <= 1; ++dy) {
      if (dx == 0 && dy == 0) {
        continue;
      }
      
      ny = (y + dy + worldYSize) % worldYSize;

      cell = [world getObjectAtX: nx Y: ny];
      if ([cell isEmptyCell]) {
        [cell computeNeighborPercentages];
      }
    }
  }
}

-swapWith: cell {
  int cellX = [cell getX];
  int cellY = [cell getY];

  [world putObject: nil atX: x Y: y];
  [world putObject: nil atX: cellX Y: cellY];

  [cell setX: x Y: y];
  [self setX: cellX Y: cellY];

  [self updateEmptyNeighbors];
  return self;
}

-step {
  double currentRatio;
  double bestRatio;
  id bestCell = self;
  id <List> emptyCellList;
  id <ListIndex> emptyIndex;
  id cell;

// New

  [self updateThreshold];

  currentRatio = [self getNeighborRatio];
  if (currentRatio >= happinessThreshold) {
    return self;
  }

  bestRatio = currentRatio;

  //  printf("begin: best ratio = %d, cell = %p\n", bestRatio, bestCell);

  emptyCellList = [model getEmptyCellList];

  emptyIndex = [emptyCellList listBegin: globalZone];

  while ((cell = [emptyIndex next])) {
    double ratio;

    ratio = [cell getPercent: type];

    if (ratio > happinessThreshold) {
      [self swapWith: cell];
      [emptyIndex drop];
      return self;
    } 

    if (ratio > bestRatio) {
      bestRatio = ratio;
      bestCell = cell;
    }
  }

  [emptyIndex drop];

  if (bestCell != self) {
    [self swapWith: bestCell];
  }

  return self;
}

- drawSelfOn: (id <Raster>) r
{

	// New
   
   float range1 = (maxHappiness - minHappiness)/3 + minHappiness;
   float range2 = 2*(maxHappiness - minHappiness)/3 + minHappiness;

   
	if (type == 1)
	{
		if (happinessThreshold < range1) [r drawPointX: x Y: y Color: 1];
		else if (happinessThreshold < range2) [r drawPointX: x Y: y Color: 2];
		else [r drawPointX: x Y: y Color: 3];
	}
	else
	{
		if (happinessThreshold < range1) [r drawPointX: x Y: y Color: 4];
		else if (happinessThreshold < range2) [r drawPointX: x Y: y Color: 5];
		else [r drawPointX: x Y: y Color: 6];
	}

  return self;
}

// New

- (double) getSegregation {
	if ([self getNeighborRatio] == 1) return 1 / [model getMaxSeg];
	else return 0;
}

- updateThreshold{
	float currentRatio = [self getNeighborRatio];
   float difference = 0;
   float newThreshold;
	
	if (currentRatio > happinessThreshold)
	{
		difference = currentRatio - happinessThreshold;
		newThreshold = happinessThreshold + difference * adaptability;
		
		if (newThreshold > maxHappiness) { newThreshold = maxHappiness; }
		[self setHappinessThreshold: newThreshold];
	}
	else
	{
		difference = happinessThreshold - currentRatio;
		newThreshold = happinessThreshold - difference * adaptability;

		if (newThreshold < minHappiness) { newThreshold = minHappiness; }
		[self setHappinessThreshold: newThreshold];
	}
	
	return self;
}

- setAdaptability: (float) inAdaptability {
	adaptability = inAdaptability;
	return self;
}

- setHappinessThresholdsMax: (float) maxHappinessIn Min: (float) minHappinessIn {
	maxHappiness = maxHappinessIn;
	minHappiness = minHappinessIn;
	return self;
}

@end

#import "EmptyCell.h"


@implementation EmptyCell

- setX: (int) inX Y: (int) inY
{
  [super setX: inX Y: inY];

  [self computeNeighborPercentages];
  [self updateEmptyNeighbors];

  return self;
}


-(int) isEmptyCell {
  return 1;
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

-(void) computeNeighborPercentages {
  int redCount = 0;
  int blueCount = 0;
  int dx, dy;
  id neighbor;
  int neighborType;

  for (dx = -1; dx <= 1; ++dx) {
    int nx, ny;

    nx = (x + dx + worldXSize) % worldXSize;

    for (dy = -1; dy <= 1; ++dy) {
      if (dx == 0 && dy == 0) {
        continue;
      }
      
      ny = (y + dy + worldYSize) % worldYSize;

      neighbor = [world getObjectAtX: nx Y: ny];
      neighborType = [neighbor getCellType];
      
      if (neighborType == 1) {
        redCount++;
      }
      else if (neighborType == 2) {
        blueCount++;
      }
    }
  }

  if (redCount == 0 && blueCount == 0) {
    redPercent = 1.0;
    bluePercent = 1.0;
  }
  else {
    redPercent = (double) redCount/(redCount + blueCount);
    bluePercent = 1.0 - redPercent;
  }
}

-(double) getRedPercent {
  return redPercent;
}

-(double) getBluePercent {
  return bluePercent;
}

-(double) getPercent: (int) type {
  return type == 1 ? redPercent : bluePercent;
}
  

-drawSelfOn: (id<Raster>) r {
  [r drawPointX: x Y: y Color: 0];
  return self;
}


@end

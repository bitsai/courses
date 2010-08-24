#import "SchellingObject.h"


@implementation SchellingObject

-setWorld: (id <Grid2d>) w {
  world = w;
  worldXSize = [world getSizeX];
  worldYSize = [world getSizeY];
  return self;
}

-(int) getX {
  return x;
}

-(int) getY {
  return y;
}

- setX: (int) inX Y: (int) inY
{
  x = inX;
  y = inY;
  [world putObject: self atX: x Y: y];	

  return self;
}


-forEachNeighbor: (SEL) fn {
  int dx, dy, nx, ny;
 
  for (dx = -neighborhoodRadius; dx <= neighborhoodRadius; ++dx) {
    nx = (x + dx + worldXSize) % worldXSize;
    for (dy = -neighborhoodRadius; dy <= neighborhoodRadius; ++dy) {
      if (dx == 0 && dy == 0) {
        continue;
      }
      
      ny = (y + dy + worldYSize) % worldYSize;
      
      [[world getObjectAtX: nx Y: ny] perform: fn];
    }
  }

  return self;
}

-forEachNeighbor: (SEL) fn with: arg {
  int dx, dy, nx, ny;
 
  for (dx = -neighborhoodRadius; dx <= neighborhoodRadius; ++dx) {
    nx = (x + dx + worldXSize) % worldXSize;
    for (dy = -neighborhoodRadius; dy <= neighborhoodRadius; ++dy) {
      if (dx == 0 && dy == 0) {
        continue;
      }
      
      ny = (y + dy + worldYSize) % worldYSize;
      
      [[world getObjectAtX: nx Y: ny] perform: fn with: arg];
    }
  }

  return self;
}

-(int) getCellType {
  return cellType;
}

-(int) isEmptyCell {
  return 0;
}


@end

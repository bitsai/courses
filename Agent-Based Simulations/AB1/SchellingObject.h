#import <space.h>	
#import <gui.h>
#import <objectbase.h>

#import <objectbase/SwarmObject.h>


@interface SchellingObject: SwarmObject {
  int x;
  int y;
  id <Grid2d> world;
  int worldXSize;
  int worldYSize;
  int neighborhoodRadius;
  int cellType;
}

-setWorld: (id <Grid2d>) w;
-(int) getX;
-(int) getY;
-setX: (int) nx Y: (int) ny;

-forEachNeighbor: (SEL) fn;
-forEachNeighbor: (SEL) fn with: arg;
-(int) getCellType;
-(int) isEmptyCell;

@end

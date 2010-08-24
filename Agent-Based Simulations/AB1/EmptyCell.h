
/*
#import <space.h>	
#import <gui.h>
#import <objectbase.h>

#import <objectbase/SwarmObject.h>
*/

#import "SchellingObject.h"


@interface EmptyCell: SchellingObject 
{
  double redPercent;
  double bluePercent;
}

-setX: (int) nx Y: (int) ny;
-(void) computeNeighborPercentages;
-(void) updateEmptyNeighbors;
-(double) getPercent: (int) type;
-(int) isEmptyCell;

@end

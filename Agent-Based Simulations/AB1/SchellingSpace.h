#import <space.h>
#import <space/Grid2d.h>

@interface SchellingCell: SwarmObject 
{
    int x;
    int y;
}
- setX: (int)theX;
- setY: (int)theY;
- (int)getX;
- (int)getY;
@end


@interface SchellingSpace: Grid2d
{
}

@end



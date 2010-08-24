#import <simtoolsgui.h>
#import <analysis.h>
#import "SchellingModelSwarm.h"

#import <simtoolsgui/GUISwarm.h>

@interface SchellingObserverSwarm: GUISwarm
{
  int displayFrequency;

  id displayActions;
  id displaySchedule;

  SchellingModelSwarm *schellingModelSwarm;

  id <Colormap> colormap;
  id <ZoomRaster> worldRaster;
  id <EZGraph> happyGraph;

  id <EZGraph> segregationGraph;			// graphing widget

  id <Object2dDisplay> schellingDisplay;
}

+ createBegin: aZone;
- createEnd;
- buildObjects;
- buildActions;
- activateIn: swarmContext;

- graphBug: aBug;
@end

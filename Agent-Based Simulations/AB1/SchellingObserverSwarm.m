#import "SchellingObserverSwarm.h"
#import "SchellingModelSwarm.h"
#import <collections.h>
#import <objectbase.h>
#import <analysis.h>
#import <gui.h>

@implementation SchellingObserverSwarm

+ createBegin: aZone
{
  SchellingObserverSwarm *obj;
  id <CustomProbeMap> probeMap;
  
  obj = [super createBegin: aZone];

  obj->displayFrequency = 1;

  probeMap = [CustomProbeMap create: aZone
                             forClass: [self class]
                             withIdentifiers: "displayFrequency", ":",
                             NULL];
  
  [probeLibrary setProbeMap: probeMap For: [self class]];

  return obj;
}

- createEnd
{
  return [super createEnd];
}

- _worldRasterDeath_ : caller
{
  [worldRaster drop];
  worldRaster = nil;
  return self;
}

// Create the objects used in the display of the model. This code is
// fairly complicated because we build a fair number of widgets. It's
// also a good example of how to use the display code.

- buildObjects
{
  [super buildObjects];
  
  schellingModelSwarm = [SchellingModelSwarm create: self];
  
  CREATE_ARCHIVED_PROBE_DISPLAY(schellingModelSwarm);
  CREATE_ARCHIVED_PROBE_DISPLAY(self);
 
  [controlPanel setStateStopped];

  [schellingModelSwarm buildObjects];

  colormap = [Colormap create: self];

  // Colours [0,64) are assigned to the range Red [0, 1), for heat display.
  
  [colormap setColor: 0 ToRed: 1.0 Green: 1.0 Blue: 1.0];

// New

  [colormap setColor: 1 ToRed: 0.3 Green: 0.0 Blue: 0.0];
  [colormap setColor: 2 ToRed: 0.6 Green: 0.0 Blue: 0.0];
  [colormap setColor: 3 ToRed: 1.0 Green: 0.0 Blue: 0.0];

  [colormap setColor: 4 ToRed: 0.0 Green: 0.0 Blue: 0.3];  
  [colormap setColor: 5 ToRed: 0.0 Green: 0.0 Blue: 0.6];
  [colormap setColor: 6 ToRed: 0.0 Green: 0.0 Blue: 1.0];

  worldRaster = [ZoomRaster createBegin: self];
  SET_WINDOW_GEOMETRY_RECORD_NAME (worldRaster);
  worldRaster = [worldRaster createEnd];
  [worldRaster enableDestroyNotification: self
               notificationMethod: @selector (_worldRasterDeath_:)];
  [worldRaster setColormap: colormap];
  [worldRaster setZoomFactor: 8];
  [worldRaster setWidth: [[schellingModelSwarm getWorld] getSizeX]
	       Height: [[schellingModelSwarm getWorld] getSizeY]];
  [worldRaster setWindowTitle: "Schell Beach"];
  [worldRaster pack];				  // draw the window.

  schellingDisplay = 
    [Object2dDisplay create: self
                     setDisplayWidget: worldRaster
                     setDiscrete2dToDisplay: [schellingModelSwarm getWorld]
                     setDisplayMessage: M(drawSelfOn:)];

  [worldRaster setButton: ButtonRight
               Client: schellingDisplay
               Message: M(makeProbeAtX:Y:)];

  // New

  segregationGraph = 
    [EZGraph create: self
             setTitle: "Segregation vs. time"
             setAxisLabelsX: "time" Y: "Segregation"
             setWindowGeometryRecordName: "SegregationGraph"];

  [segregationGraph setRangesYMin: 0.0 Max: 1.0];
  
  [segregationGraph enableDestroyNotification: self
                notificationMethod: @selector (_segregationGraphDeath_:)];

  [segregationGraph createAverageSequence: "Segregation"
                         withFeedFrom: [schellingModelSwarm getInhabitants] 
                          andSelector: M(getSegregation)];

  return self;
}  

- _segregationGraphDeath_ : caller
{
  [segregationGraph drop];
  segregationGraph = nil;
  return self;
}

- _update_
{
  if (worldRaster) {
    [worldRaster erase];
    [schellingDisplay display];
    [worldRaster drawSelf];
  }

// New

  if (segregationGraph)
    [segregationGraph step];

  return self;
}

- buildActions
{
  [super buildActions];
  
  [schellingModelSwarm buildActions];
  displayActions = [ActionGroup create: self];

  [displayActions createActionTo: self message: M(_update_)];

  [displayActions createActionTo: probeDisplayManager message: M(update)];
  displaySchedule = [Schedule create: self setRepeatInterval: displayFrequency];
  
  [displaySchedule at: 0 createAction: displayActions];
  
  return self;
}  



- activateIn:  swarmContext
{
  [super activateIn: swarmContext];
  [schellingModelSwarm activateIn: self];
  [displaySchedule activateIn: self];
  
  return [self getActivity];
}

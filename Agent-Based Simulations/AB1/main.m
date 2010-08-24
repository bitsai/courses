#import <simtools.h>     // initSwarm () and swarmGUIMode
#import <simtoolsgui.h>  // GUISwarm
#import "SchellingObserverSwarm.h"

int
main(int argc, const char **argv)
{
  id theTopLevelSwarm;

  initSwarm (argc, argv);
  
  if (swarmGUIMode != YES) {
    fprintf(stderr, "Error: Must run in graphics mode.\n");
    exit(1);
  }
  
  theTopLevelSwarm = [SchellingObserverSwarm createBegin: globalZone];
  SET_WINDOW_GEOMETRY_RECORD_NAME(theTopLevelSwarm);
  theTopLevelSwarm = [theTopLevelSwarm createEnd];

  [theTopLevelSwarm buildObjects];
  [theTopLevelSwarm buildActions];
  [theTopLevelSwarm activateIn: nil];
  [theTopLevelSwarm go];

  // theTopLevelSwarm has finished processing, so it's time to quit.
  return 0;
}

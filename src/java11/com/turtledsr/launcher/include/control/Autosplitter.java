/*
Emulates the autosplitter written by Lemuura (without debug or settings)

Uses the JNA library for memory handling
Uses TimerHandler for control over the livesplit timer

Assumes latest version of the game

*HUGE credit to Lemuura*
*/

package com.turtledsr.launcher.include.control;

import java.util.OptionalInt;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.turtledsr.launcher.Main;
import com.turtledsr.launcher.include.engine.Logs;
import com.turtledsr.launcher.include.process.Process;


public final class Autosplitter {
  public static final int PROCESS_VM_READ = 0x0010;

  private static Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
  public static HANDLE process;
  public static boolean awaitingReconnection = false;
  private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  private static boolean started = false;
  private static boolean running = false;

  private static final long baseOffset = 0x07A07A60; //"ItTakesTwo.exe", 0x07A07A60
  private static long baseAddress;
  private static Pointer basePointer;

  //STATE
  private static boolean isLoading = false;         //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x458, 0xf9;
  private static boolean isLoadingOld = false;
  private static final long[] isLoadingPath = {0x180, 0x2b0, 0x0, 0x458, 0xf9};
	private static String levelString = "";           //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1b8, 0x0;
  private static String levelStringOld = "";
  private static final long[] levelStringPath = {0x180, 0x368, 0x8, 0x1b8, 0x0};
	private static String checkpointString = "";      //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1d8, 0x0;
  private static String checkpointStringOld = "";
  private static final long[] checkpointStringPath = {0x180, 0x368, 0x8, 0x1d8, 0x0};
	//private static String chapterString = "";         //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1e8, 0x0;
  //private static String chapterStringOld = "";
  //private static final long[] chapterStringPath = {0x180, 0x368, 0x8, 0x1e8, 0x0};
	private static String subchapterString = "";      //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1f8, 0x0;
  private static String subchapterStringOld = "";
  private static final long[] subchapterStringPath = {0x180, 0x368, 0x8, 0x1f8, 0x0};
	private static String cutsceneString = "";        //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x390, 0x2a0, 0x788, 0x0;
  //private static String cutsceneStringOld = "";
  private static final long[] cutsceneStringPath = {0x180, 0x2b0, 0x0, 0x390, 0x2a0, 0x788, 0x0};
	//private static boolean skippable = false;         //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x390, 0x318;
  //private static boolean skippableOld = false;
  //private static final long[] skippablePath = {0x180, 0x2b0, 0x0, 0x390, 0x318};

  //VARS
  private static String lastCutscene = "";
  private static String lastCutsceneOld = "";

  private static int splitIndex = -1;
  private static int splitIndexOld = -1;

  private static boolean splitNextLoad = false;

  private static boolean igtPaused = false;

  private static boolean timerSplit = false;


  //SPLITDATA
  private static final String[] levelSplits = {
		"/Game/Maps/Shed/Vacuum/Vacuum_BP",
		"/Game/Maps/Shed/Main/Main_Hammernails_BP",
		"/Game/Maps/Shed/Main/Main_Grindsection_BP",
		"/Game/Maps/RealWorld/RealWorld_Shed_StarGazing_Meet_BP",
		"/Game/Maps/Tree/SquirrelHome/SquirrelHome_BP_Mech",
		"/Game/Maps/Tree/WaspNest/WaspsNest_BP",
		"/Game/Maps/Tree/Boss/WaspQueenBoss_BP",
		"/Game/Maps/Tree/Escape/Escape_BP",
		"/Game/Maps/PlayRoom/Spacestation/Spacestation_Hub_BP",
		"/Game/Maps/PlayRoom/Hopscotch/Hopscotch_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Trainstation_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Pirate_BP",
		"/Game/Maps/PlayRoom/Courtyard/Castle_Courtyard_BP",
		"/Game/Maps/PlayRoom/Dungeon/Castle_Dungeon_BP",
		"/Game/Maps/PlayRoom/Shelf/Shelf_BP",
		"/Game/Maps/RealWorld/RealWorld_RoseRoom_Bed_Tears_BP",
		"/Game/Maps/Clockwork/LowerTower/Clockwork_ClockTowerLower_CrushingTrapRoom_BP",
		"/Game/Maps/Clockwork/UpperTower/Clockwork_ClockTowerLastBoss_BP",
		"/Game/Maps/SnowGlobe/Town/SnowGlobe_Town_BP",
		"/Game/Maps/SnowGlobe/Lake/Snowglobe_Lake_BP",
		//"/Game/Maps/SnowGlobe/Mountain/SnowGlobe_Mountain_BP",
		"/Game/Maps/Garden/Shrubbery/Garden_Shrubbery_BP",
		"/Game/Maps/Garden/MoleTunnels/Garden_MoleTunnels_Stealth_BP",
		"/Game/Maps/Garden/FrogPond/Garden_FrogPond_BP",
		"/Game/Maps/Garden/Greenhouse/Garden_Greenhouse_BP",
		"/Game/Maps/Music/Nightclub/Music_Nightclub_BP",
		"/Game/Maps/Music/Backstage/Music_Backstage_Tutorial_BP",
		"/Game/Maps/Music/Classic/Music_Classic_Organ_BP",
		"/Game/Maps/Music/Ending/Music_Ending_BP",

		"SnowGlobe_Mountain",

    //INTERMEDIARIES - ASSUMED
    "/Game/Maps/Shed/Awakening/Awakening_BP",
		"/Game/Maps/Tree/Approach/Approach_BP",
		"/Game/Maps/PlayRoom/PillowFort/PillowFort_BP",
		"/Game/Maps/Clockwork/Outside/Clockwork_Tutorial_BP",
		"/Game/Maps/SnowGlobe/Forest/SnowGlobe_Forest_BP",
		"/Game/Maps/Garden/VegetablePatch/Garden_VegetablePatch_BP",
		"/Game/Maps/Music/ConcertHall/Music_ConcertHall_BP"
	};

  private static final String[] cutsceneSplits = {
		"CS_RealWorld_House_LivingRoom_Headache",
		"CS_ClockWork_UpperTower_EndingRewind",
		"CS_Garden_GreenHouse_BossRoom_Outro",
	};

  private static final String[] postCutsceneSplits = {
		"CS_PlayRoom_DinoLand_DinoCrash_Intro",
		"CS_PlayRoom_Circus_Balloon_Intro",
	};

  private static final String[] oldLevelSplits = {
		"/Game/Maps/Shed/Awakening/Awakening_BP",
		"/Game/Maps/Shed/Vacuum/Vacuum_BP",
		"/Game/Maps/Shed/Main/Main_Hammernails_BP",
		"/Game/Maps/Shed/Main/Main_Bossfight_BP",
		"/Game/Maps/Shed/Main/Main_Grindsection_BP",
		"/Game/Maps/RealWorld/RealWorld_Shed_StarGazing_Meet_BP",
		"/Game/Maps/Tree/SquirreTurf/SquirrelTurf_WarRoom_BP",
		"/Game/Maps/Tree/SquirrelHome/SquirrelHome_BP_Mech",
		"/Game/Maps/Tree/WaspNest/WaspsNest_BeetleRide_BP",
		"/Game/Maps/Tree/SquirreTurf/SquirrelTurf_Flight_BP",
		"/Game/Maps/RealWorld/RealWorld_Exterior_Roof_Crash_BP",
		"/Game/Maps/PlayRoom/PillowFort/PillowFort_BP",
		"/Game/Maps/RealWorld/Realworld_SpaceStation_Bossfight_BeamOut_BP",
		"/Game/Maps/PlayRoom/Hopscotch/Kaleidoscope_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Trainstation_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Dinoland_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Pirate_BP",
		"/Game/Maps/PlayRoom/Goldberg/Goldberg_Circus_BP",
		"/Game/Maps/PlayRoom/Courtyard/Castle_Courtyard_BP",
		"/Game/Maps/PlayRoom/Chessboard/Castle_Chessboard_BP",
		"/Game/Maps/PlayRoom/Shelf/Shelf_BP",
		"/Game/Maps/TherapyRoom/TherapyRoom_Session_Theme_Time_BP",
		"/Game/Maps/Clockwork/Outside/Clockwork_ClockTowerCourtyard_BP",
		"/Game/Maps/Clockwork/Outside/Clockwork_Forest_BP",
		"/Game/Maps/Clockwork/LowerTower/Clockwork_ClockTowerLower_CuckooBirdRoom_BP",
		"/Game/Maps/Clockwork/LowerTower/Clockwork_ClockTowerLower_EvilBirdRoom_BP",
		"/Game/Maps/TherapyRoom/TherapyRoom_Session_Theme_Attraction_BP",
		"/Game/Maps/SnowGlobe/Forest/SnowGlobe_Forest_BP",
		"/Game/Maps/SnowGlobe/Forest/SnowGlobe_Forest_TownGate_BP",
		"/Game/Maps/SnowGlobe/Town/SnowGlobe_Town_BobSled",
		//"/Game/Maps/SnowGlobe/Lake/Snowglobe_Lake_BP",
		"/Game/Maps/TherapyRoom/TherapyRoom_Session_Theme_Garden_BP",
		"/Game/Maps/Garden/VegetablePatch/Garden_VegetablePatch_BP",
		"/Game/Maps/Garden/Shrubbery/Garden_Shrubbery_SecondCombat_BP",
		"/Game/Maps/Garden/Shrubbery/Garden_Shrubbery_BP",
		"/Game/Maps/Garden/MoleTunnels/Garden_MoleTunnels_Chase_BP",
		"/Game/Maps/Garden/MoleTunnels/Garden_MoleTunnels_Stealth_BP",
		"/Game/Maps/Garden/FrogPond/Garden_FrogPond_BP",
		"/Game/Maps/TherapyRoom/TherapyRoom_Session_Theme_Music_BP",
		"/Game/Maps/Music/ConcertHall/Music_ConcertHall_BP",
		"/Game/Maps/Music/Nightclub/Music_Nightclub_BP",

		"/Game/Maps/Tree/WaspNest/WaspsNest_BP",
		"/Game/Maps/Tree/WaspNest/WaspsNest_Swarm_BP",
		"/Game/Maps/Tree/Boat/Tree_Boat_BP",
		"/Game/Maps/Tree/Darkroom/Tree_Darkroom_BP",
		"/Game/Maps/PlayRoom/Hopscotch/Hopscotch_BP",
		"/Game/Maps/PlayRoom/Hopscotch/VoidWorld_BP",
		"/Game/Maps/PlayRoom/Dungeon/Castle_Dungeon_BP",
		"/Game/Maps/PlayRoom/Dungeon/Castle_Dungeon_Charger_BP",

		"SnowGlobe_Lake",
	};

  private static final String[] startLevels = {
		"/Game/Maps/Shed/Awakening/Awakening_BP",
		"/Game/Maps/Tree/Approach/Approach_BP",
		"/Game/Maps/PlayRoom/PillowFort/PillowFort_BP",
		"/Game/Maps/Clockwork/Outside/Clockwork_Tutorial_BP",
		"/Game/Maps/SnowGlobe/Forest/SnowGlobe_Forest_BP",
		"/Game/Maps/Garden/VegetablePatch/Garden_VegetablePatch_BP",
		"/Game/Maps/Music/ConcertHall/Music_ConcertHall_BP"
	};

  private static final String[] checkpointSplits = {"TerraceProposalCutscene"};

  private static final String[] nextLoadSplits = {};

  public static void bind() {
    try{
      OptionalInt pid = Process.getProcessPID("ItTakesTwo");
      if(!pid.isPresent()) {
        throw new Exception("Process Not Found");
      }

      process = kernel32.OpenProcess(PROCESS_VM_READ, false, pid.getAsInt());

      baseAddress = Process.getProcessBaseAddress(pid.getAsInt());
      if(baseAddress == 0) {
        throw new Exception("Address Not Found");
      }

      basePointer = new Pointer(baseAddress);

      running = true;
      awaitingReconnection = false;
    } catch(Exception e) {
      Logs.logError(e.getMessage(), "AUTOSPLITTER");
      scheduleBind(Main.RECONNECTION_INTERVAL);
    }
  }

   public static void scheduleBind(int time) {
    awaitingReconnection = true;
    executor.schedule(new Runnable() {
      @Override
      public void run() {
        bind();
      }
    }, time, TimeUnit.MILLISECONDS);
    Logs.log("Scheduling bind attempt in " + time + "ms", "AUTOSPLITTER");
  }

  public static void stop() {
    Logs.logError("STOPPED", "AUTOSPLITTER");
    
    kernel32.CloseHandle(process);
    running = false;

    process = null;

    if(!igtPaused) {
      TimerHandler.pauseGameTime();
      igtPaused = true;
    }
  }

  public static void tick() {
    if(Process.getProcessPID("ItTakesTwo").isEmpty()) { //check if process closed
      stop();
      return;
    }

    initTick();

    if(running) {
      if(started) {
        if(update()) {
          if(isLoading() != igtPaused) {
            igtPaused = !igtPaused;
            if(igtPaused) {
              TimerHandler.pauseGameTime();
            } else {
              TimerHandler.unpauseGameTime();
            }
          }
          if(reset()) {
            TimerHandler.reset();
          } else {
            if(split()) {
              TimerHandler.split();
              timerSplit = true;
            }
          }
        }
      } else {
        if(start()) {
          TimerHandler.start();
          started = true;
        }
      }
    }
  }

  public static void initTick() {
    //UPDATE OLD STATE
    isLoadingOld = isLoading;
    levelStringOld = levelString;
    checkpointStringOld = checkpointString;
    //chapterStringOld = chapterString;
    subchapterStringOld = subchapterString;
    //cutsceneStringOld = cutsceneString;
    //skippableOld = skippable;

    //UPDATE CURRENT STATE
    //ISLOADING
    Memory mIsLoading = followPath(isLoadingPath, 1);
    if(mIsLoading == null) {
      isLoading = false;
    } else {
      isLoading = mIsLoading.getByte(0) != 0;
    }
    //Logs.log(isLoading, "AUTOSPLITTER");

    //LEVELSTRING
    Memory mLevelString = followPath(levelStringPath, 128);
    if(mLevelString == null) {
      levelString = null;
    } else {
      levelString = mLevelString.getWideString(0);
    }
    //Logs.log(levelString, "AUTOSPLITTER");

    //CHECKPOINTSTRING
    Memory mCheckpointString = followPath(checkpointStringPath, 128);
    if(mCheckpointString == null) {
      checkpointString = null;
    } else {
      checkpointString = mCheckpointString.getWideString(0);
    }
    //Logs.log(checkpointString, "AUTOSPLITTER");

    //CHAPTERSTRING
    //Memory mChapterString = followPath(chapterStringPath, 128);
    //if(mChapterString == null) {
    //  chapterString = null;
    //} else {
    //  chapterString = mChapterString.getWideString(0);
    //}
    //Logs.log(chapterString, "AUTOSPLITTER");

    //SUBCHAPTERSTRING
    Memory mSubchapterString = followPath(subchapterStringPath, 128);
    if(mSubchapterString == null) {
      subchapterString = null;
    } else {
      subchapterString = mSubchapterString.getWideString(0);
    }
    //Logs.log(subchapterString, "AUTOSPLITTER");

    //CUTSCENESTRING
    Memory mCutsceneString = followPath(cutsceneStringPath, 128);
    if(mCutsceneString == null) {
      cutsceneString = null;
    } else {
      cutsceneString = mCutsceneString.getWideString(0);
    }
    //Logs.log(cutsceneString, "AUTOSPLITTER");

    //SKIPPABLE
    //Memory mSkippable = followPath(skippablePath, 128);
    //if(mSkippable == null) {
    //  skippable = false;
    //} else {
    //  skippable = mSkippable.getByte(0) > 0;
    //}
    //Logs.log(skippable, "AUTOSPLITTER");
  }

  //TIMER CONTROL

  private static boolean update() {
    splitIndexOld = splitIndex;
    splitIndex = TimerHandler.getCurrentIndex();

    if(!timerSplit && splitIndexOld != splitIndex) {
      if(splitIndex == -1) {
        Logs.log("Player forfeit", "AUTOSPLITTER");
        //player reset [HANDLE FORFEIT]
        started = false;
        return false;
      }

      Logs.log("Player split manually", "AUTOSPLITTER");
      TimerHandler.unsplit();
      Logs.log("Manual split corrected", "AUTOSPLITTER");
      splitIndex = TimerHandler.getCurrentIndex();
      return false;
    }

    if(splitIndex == -1) {
      if(timerSplit) {
        Logs.log("Player Finished", "AUTOSPLITTER");
        //player completed [HANDLE COMPLETION]
      }
    }

    lastCutsceneOld = lastCutscene;
    if (cutsceneString == null) lastCutscene = "null";
	  if (cutsceneString != null && cutsceneString.length() > 1) lastCutscene = cutsceneString;
    timerSplit = false;
    return true;
  }

  private static boolean reset() { //unimplemented
    //if (!checkpointString.equals(checkpointStringOld) || isLoading && !isLoadingOld) {
		//  if (checkpointString.equals("Awakening_Start")) {
		//  	return true;
    //  }
	  //}

    return false;
  }

  private static boolean split() {
    if(splitNextLoad && isLoading && !isLoadingOld) {
	    splitNextLoad = false;
		  return true;
	  }

	  if(!levelStringOld.equals(levelString)) {
		  if(contains(levelSplits, levelString) && contains(oldLevelSplits, levelStringOld)) {
			  return true;
      }
    }

	  if(!subchapterStringOld.equals(subchapterString)) {
	  	if(contains(levelSplits, subchapterString) && contains(oldLevelSplits, subchapterStringOld)) return true;
	  }

	  if(!checkpointStringOld.equals(checkpointString)) {
	  	if (contains(checkpointSplits, checkpointString)) {
	  		return true;
      }

	  	if (contains(nextLoadSplits, checkpointString)) {
	  		splitNextLoad = true;
      }
	  }

	  if (!lastCutsceneOld.equals(lastCutscene)) {
	  	if (lastCutscene.equals("CS_Music_Attic_Stage_ClimacticKiss")) { // Ending split, always active.
	  		return true;
      }

	  	if (contains(cutsceneSplits, lastCutscene)) {
	  		return true;
      }

	  	if (contains(postCutsceneSplits, lastCutsceneOld))
	  		return true;

	  	if (contains(nextLoadSplits, lastCutscene)) {
	  		splitNextLoad = true;
      }
	  }
    return false;
  }

  private static boolean start() {
    if(!isLoading) {
      return false;
    }
    
    if (contains(startLevels, levelString)) {
			return true;
    }
    return false;
  }

  private static boolean isLoading() {
    if (isLoading)
		  return true;
	  if (levelString.equals("/Game/Maps/Main/Menu_BP"))
	  	return true;
	  if (levelString == null) //should only be null when game is exited
	  	return true;
	  if (!levelString.equals("/Game/Maps/Main/Menu_BP"))
	  	return false;
    return true;
  }

  private static boolean contains(Object[] arr, Object obj) {
    for (Object object : arr) {
      if(object.equals(obj)) return true;
    }

    return false;
  }

  private static Memory followPath(long[] path, int size) { //returns null if pointer path is invalid
    Memory buffer = new Memory(8);
    Pointer p = basePointer.share(baseOffset);
    
    if (!kernel32.ReadProcessMemory(process, p, buffer, 8, null)) {
      return null;
    }

    Pointer currentAddress = buffer.getPointer(0);

    for (int i = 0; i < path.length - 1; i++) {
      if (currentAddress == null || Pointer.nativeValue(currentAddress) == 0) {
        return null;
      }
      
      Pointer nextTarget = currentAddress.share(path[i]);

      if (!kernel32.ReadProcessMemory(process, nextTarget, buffer, 8, null)) {
        return null;
      }

      currentAddress = buffer.getPointer(0);
    }

    Memory finalBuffer = new Memory(size);
    if (currentAddress == null || Pointer.nativeValue(currentAddress) == 0) {
      return null;
    }

    if (!kernel32.ReadProcessMemory(process, currentAddress.share(path[path.length - 1]), finalBuffer, size, null)) {
      return null;
    }

    return finalBuffer;
  }
}

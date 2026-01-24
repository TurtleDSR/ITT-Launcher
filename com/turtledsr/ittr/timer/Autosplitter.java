package com.turtledsr.ittr.timer;

import java.util.OptionalInt;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.turtledsr.ittr.process.Process;


public final class Autosplitter {
  public static final int PROCESS_VM_READ = 0x0010;

  private static Kernel32 kernel32 = Native.load("kernel32", Kernel32.class);
  private static HANDLE process;
  private static Pointer basePointer;

  private static boolean running = false;

  private static final long baseOffset = 0x07A07A60; //"ItTakesTwo.exe", 0x07A07A60
  private static long baseAddress;

  private static boolean isLoading;            //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x458, 0xf9;
  private static final long[] isLoadingPath = {0x180, 0x2b0, 0x0, 0x458, 0xf9};
	private static String levelString;           //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1b8, 0x0;
  private static final long[] levelStringPath = {0x180, 0x368, 0x8, 0x1b8, 0x0};
	private static String checkpointString;      //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1d8, 0x0;
  private static final long[] checkpointStringPath = {0x180, 0x368, 0x8, 0x1d8, 0x0};
	private static String chapterString;         //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1e8, 0x0;
  private static final long[] chapterStringPath = {0x180, 0x368, 0x8, 0x1e8, 0x0};
	private static String subchapterString;      //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x368, 0x8, 0x1f8, 0x0;
  private static final long[] subchapterStringPath = {0x180, 0x368, 0x8, 0x1f8, 0x0};
	private static String cutsceneString;        //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x390, 0x2a0, 0x788, 0x0;
  private static final long[] cutsceneStringPath = {0x180, 0x2b0, 0x0, 0x390, 0x2a0, 0x788, 0x0};
	private static boolean skippable;               //"ItTakesTwo.exe", 0x07A07A60, 0x180, 0x2b0, 0x0, 0x390, 0x318;
  private static final long[] skippablePath = {0x180, 0x2b0, 0x0, 0x390, 0x318};

  public static void start() throws Exception {
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
  }

  public static void stop() {
    kernel32.CloseHandle(process);
    running = false;
  }

  public static void tick() {
    if(running) {
      initTick();
    }
  }

  public static void initTick() {
    //ISLOADING
    Memory mIsLoading = followPath(isLoadingPath, 1);
    if(mIsLoading == null) {
      isLoading = false;
    } else {
      isLoading = mIsLoading.getByte(0) != 0;
    }
    //System.out.println(isLoading);
    
    //LEVELSTRING
    Memory mLevelString = followPath(levelStringPath, 128);
    if(mLevelString == null) {
      levelString = null;
    } else {
      levelString = mLevelString.getWideString(0);
    }
    //System.out.println(levelString);

    //CHECKPOINTSTRING
    Memory mCheckpointString = followPath(checkpointStringPath, 128);
    if(mCheckpointString == null) {
      checkpointString = null;
    } else {
      checkpointString = mCheckpointString.getWideString(0);
    }
    //System.out.println(checkpointString);

    //CHAPTERSTRING
    Memory mChapterString = followPath(chapterStringPath, 128);
    if(mChapterString == null) {
      chapterString = null;
    } else {
      chapterString = mChapterString.getWideString(0);
    }
    //System.out.println(chapterString);

    //SUBCHAPTERSTRING
    Memory mSubchapterString = followPath(subchapterStringPath, 128);
    if(mSubchapterString == null) {
      subchapterString = null;
    } else {
      subchapterString = mSubchapterString.getWideString(0);
    }
    //System.out.println(subchapterString);

    //CUTSCENESTRING
    Memory mCutsceneString = followPath(cutsceneStringPath, 128);
    if(mCutsceneString == null) {
      cutsceneString = null;
    } else {
      cutsceneString = mCutsceneString.getWideString(0);
    }
    //System.out.println(cutsceneString);

    //SKIPPABLE
    Memory mSkippable = followPath(skippablePath, 128);
    if(mSkippable == null) {
      skippable = false;
    } else {
      skippable = mSkippable.getByte(0) > 0;
    }
    //System.out.println(skippable);
  }

  //be very careful what you pass into here
  private static Memory followPath(long[] path, int size) {
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

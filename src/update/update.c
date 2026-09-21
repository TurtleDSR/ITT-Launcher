#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include <stdio.h>
#include <string.h>
#include "zip.h"
#include <windows.h>

int main() {
  Sleep(750); //sleep to wait for launcher to close

  char filename[] = "Launcher-Update.zip";
  zip_extract(filename, ".", NULL, NULL);
  remove(filename);

  char dir[MAX_PATH];
  char exe_path[MAX_PATH];
  char command_line[MAX_PATH * 3];

  GetModuleFileNameA(NULL, exe_path, MAX_PATH);

  char *last_backslash = strrchr(exe_path, '\\');
  if (last_backslash != NULL) {
    *last_backslash = '\0';
  }
  if(strcpy_s(dir, MAX_PATH, exe_path) != 0) return -1;

  sprintf_s(command_line, sizeof(command_line), "\"%s/ITT-launcher.exe\" -update", dir);

  STARTUPINFOA si;
  PROCESS_INFORMATION pi;
  ZeroMemory(&si, sizeof(si));
  si.cb = sizeof(si);
  ZeroMemory(&pi, sizeof(pi));

  CreateProcessA(NULL, command_line, NULL, NULL, 0, 0, NULL, dir, &si, &pi);

  CloseHandle(pi.hProcess);
  CloseHandle(pi.hThread);

  return 0;
}
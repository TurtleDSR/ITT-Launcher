#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")
#pragma comment(lib, "user32.lib")

#include <windows.h>
#include <stdio.h>

int main() {
  char exePath[MAX_PATH];
  char dir[MAX_PATH];
  char commandLine[MAX_PATH * 2];

  GetModuleFileNameA(NULL, exePath, MAX_PATH);

  char *lastBackslash = strrchr(exePath, '\\');
  if (lastBackslash != NULL) {
    *lastBackslash = '\0';
  }

  strcpy_s(dir, MAX_PATH, exePath);
  sprintf_s(commandLine, sizeof(commandLine), 
    "\"%s\\jre\\bin\\javaw.exe\" -jar \"%s\\ITT-launcher.jar\"", 
    dir, dir);

  printf("Attempting to run: %s\n\n", commandLine);

  STARTUPINFO si;
  PROCESS_INFORMATION pi;

  ZeroMemory(&si, sizeof(si));
  si.cb = sizeof(si);
  ZeroMemory(&pi, sizeof(pi));

  if (!CreateProcessA(NULL, commandLine, NULL, NULL, FALSE, 0, NULL, dir, &si, &pi)) {
    char errorBuf[256];
    sprintf_s(errorBuf, sizeof(errorBuf), "Error: %lu\nPath: %s", GetLastError(), commandLine);
    MessageBoxA(NULL, errorBuf, "Launcher Error", MB_ICONERROR);
    return -1;
  }

  CloseHandle(pi.hProcess);
  CloseHandle(pi.hThread);
  return 0;
}
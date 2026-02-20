#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")

#include <windows.h>

int main() {
  STARTUPINFO si;
  PROCESS_INFORMATION pi;

  ZeroMemory(&si, sizeof(si));
  si.cb = sizeof(si);
  ZeroMemory(&pi, sizeof(pi));

  char cmd[] = "jre/bin/javaw.exe -jar ITT-launcher.jar";
  if (!CreateProcess(NULL, cmd, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
    return -1;
  }

  CloseHandle(pi.hProcess);
  CloseHandle(pi.hThread);
  return 0;
}
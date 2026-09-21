#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")
#pragma comment(lib, "user32.lib")

#include <windows.h>
#include <stdio.h>
#include <shlobj.h>

int main(int argc, char** argv) {
  SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, NULL, NULL); //update icon

  char exe_path[MAX_PATH];
  char dir[MAX_PATH];
  char command_line[MAX_PATH * 3];

  GetModuleFileNameA(NULL, exe_path, MAX_PATH);

  char *last_backslash = strrchr(exe_path, '\\');
  if (last_backslash != NULL) {
    *last_backslash = '\0';
  }

  printf("arg count: %d\n\n", argc);

  size_t args_len = 0;
  for(int i = 1; i < argc; i++) {
    args_len += strlen(argv[i]) + 1;
  }

  char* args = malloc(args_len);
  args[0] = '\0';

  for(int i = 1; i < argc; i++) {
    if(strcat_s(args, args_len, argv[i]) != 0) {
      free(args);
      return -1;
    }

    if(i < argc - 1) {
      if(strcat_s(args, args_len, " ") != 0) {
        free(args);
        return -1;
      }
    }
  }

  printf("args: %s len: %zu\n\n", args, args_len);

  if(strcpy_s(dir, MAX_PATH, exe_path) != 0) {
    free(args);
    return -1;
  }

  sprintf_s(command_line, sizeof(command_line), 
    "\"%s/jre/bin/javaw.exe\" -jar \"%s/ITT-launcher.jar\" %s",
    dir, dir, args
  );

  free(args);

  printf("Attempting to run: %s\n\n", command_line);

  STARTUPINFO si;
  PROCESS_INFORMATION pi;

  ZeroMemory(&si, sizeof(si));
  si.cb = sizeof(si);
  ZeroMemory(&pi, sizeof(pi));

  if (!CreateProcessA(NULL, command_line, NULL, NULL, FALSE, 0, NULL, dir, &si, &pi)) {
    char errorBuf[256];
    sprintf_s(errorBuf, sizeof(errorBuf), "Error: %lu\nPath: %s", GetLastError(), command_line);
    MessageBoxA(NULL, errorBuf, "Launcher Error", MB_ICONERROR);
    return -1;
  }

  CloseHandle(pi.hProcess);
  CloseHandle(pi.hThread);
  return 0;
}
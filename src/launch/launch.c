#pragma comment(linker, "/SUBSYSTEM:windows /ENTRY:mainCRTStartup")
#pragma comment(lib, "user32.lib")

#include <windows.h>
#include <shlobj.h>
#include <stdio.h>

int main(int argc, char** argv) {
  char exe_path[MAX_PATH];
  char icon_path[MAX_PATH];
  char dir[MAX_PATH];

  char mod_command[MAX_PATH + 32];
  char command_line[MAX_PATH * 3];

  GetModuleFileNameA(NULL, exe_path, MAX_PATH); //get current executable path

  strcpy_s(dir, sizeof(dir), exe_path); //get working directory
  char *lastSlash = strrchr(dir, '\\');
  if (lastSlash) *lastSlash = '\0';

  //hazemod file icon
  HKEY hKey;

  snprintf(icon_path, sizeof(icon_path), "\"%s\",1", exe_path);
  snprintf(mod_command, sizeof(mod_command), "\"%s\" \"%%1\"", exe_path);

  if (RegCreateKeyExA(HKEY_CURRENT_USER, "Software\\Classes\\.hazemod", 0, NULL, 0, KEY_WRITE, NULL, &hKey, NULL) == ERROR_SUCCESS) {
    RegSetValueExA(hKey, "", 0, REG_SZ, (const BYTE*)"ITT-Launcher.Hazemod", (DWORD)(strlen("ITT-Launcher.Hazemod") + 1));
    RegCloseKey(hKey);
  }

  if (RegCreateKeyExA(HKEY_CURRENT_USER, "Software\\Classes\\ITT-Launcher.Hazemod", 0, NULL, 0, KEY_WRITE, NULL, &hKey, NULL) == ERROR_SUCCESS) {
    RegSetValueExA(hKey, "", 0, REG_SZ, (const BYTE*)"Hazemod", (DWORD)(strlen("Hazemod") + 1));
    RegSetValueExA(hKey, "PerceivedType", 0, REG_SZ, (const BYTE*)"document", (DWORD)(strlen("document") + 1));
    RegCloseKey(hKey);
  }

  if (RegCreateKeyExA(HKEY_CURRENT_USER, "Software\\Classes\\ITT-Launcher.Hazemod\\DefaultIcon", 0, NULL, 0, KEY_WRITE, NULL, &hKey, NULL) == ERROR_SUCCESS) {
    RegSetValueExA(hKey, "", 0, REG_SZ, (const BYTE*)icon_path, (DWORD)(strlen(icon_path) + 1));
    RegCloseKey(hKey);
  }

  if (RegCreateKeyExA(HKEY_CURRENT_USER, "Software\\Classes\\ITT-Launcher.Hazemod\\shell\\open\\command", 0, NULL, 0, KEY_WRITE, NULL, &hKey, NULL) == ERROR_SUCCESS) {
    RegSetValueExA(hKey, "", 0, REG_SZ, (const BYTE*)mod_command, (DWORD)(strlen(mod_command) + 1));
    RegCloseKey(hKey);
  }

  SHChangeNotify(SHCNE_ASSOCCHANGED, SHCNF_IDLIST, NULL, NULL); //update icon

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
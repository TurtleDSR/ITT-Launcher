[windows]
build:
  gradle build
  gradle buildRuntime
  clang launch.c -o build/c/ITT-launcher.exe

[windows]
build-C:
  clang launch.c -o build/c/ITT-launcher.exe

[windows]
build-J:
  gradle build
  gradle buildRuntime
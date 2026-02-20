[windows]
build: build-J build-C

[windows]
build-C:
  clang -O3 launch.c -o build/c/ITT-launcher.exe -luser32 -lshell32 -lkernel32 -static

[windows]
build-J:
  gradle build
  gradle buildRuntime
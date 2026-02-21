#build both java launcher and c wrapper executable
[windows]
build: build-J build-C

#build c wrapper executable
[windows]
build-C:
  @ #create missing directories
  @[ -d build/c ] || mkdir -p build/c
  @[ -d build/tmp/c ] || mkdir -p build/tmp/c

  @ #create icon image for the exe
  @[ -f src/launch/icon.ico ] || just update-ico

  llvm-rc src/launch/launch.rc -fo build/tmp/c/launch.res
  clang -O3 src/launch/launch.c build/tmp/c/launch.res -o build/c/ITT-launcher.exe -luser32 -lshell32 -lkernel32 -static

#build java launcher
[windows]
build-J:
  gradle build
  gradle buildRuntime

#update executable icon
[windows]
update-ico:
  magick src/java11/resources/img/icon.png src/launch/icon.ico
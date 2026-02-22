#build both java launcher and c wrapper executable in the specified dir (default: .build)
[group: 'build']
[windows]
build folder=".build": build-J build-C
  @#create missing directories
  @[ -d build/{{folder}} ] || mkdir -p build/{{folder}}

  @#copy jars
  @echo copy jar
  cp build/libs/*.jar build/{{folder}}/

  @#copy jre
  @echo copy jre
  cp -r build/jre/ build/{{folder}}/

  @#copy exe's
  @echo copy exe
  cp build/c/*.exe build/{{folder}}/

#build both java launcher and c wrapper in the .debug dir
[group: 'build']
[windows]
debug: 
  just build .debug

#build java launcher
[group: 'java']
[windows]
build-J:
  gradle build
  gradle buildRuntime

#build c wrapper executable
[group: 'exe']
[windows]
build-C:
  @#create missing directories
  @[ -d build/c ] || mkdir -p build/c
  @[ -d build/tmp/c ] || mkdir -p build/tmp/c

  @#create icon image for the exe
  @[ -f src/launch/icon.ico ] || just update-ico

  @#compile resources
  llvm-rc src/launch/launch.rc -fo build/tmp/c/launch.res

  @#compile c
  clang -O3 src/launch/launch.c build/tmp/c/launch.res -o build/c/ITT-launcher.exe -static

#update executable icon
[group: 'exe']
[windows]
update-ico:
  magick src/java11/resources/img/icon.png src/launch/icon.ico
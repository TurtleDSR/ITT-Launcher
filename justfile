#build both java launcher and c wrapper executable in the specified dir (default: .build)
[group: 'build']
[windows]
[parallel]
build folder=".build": shutdown-app build-J build-C 
  @just copy {{folder}}
  @just zip {{folder}}

#build both java launcher and c wrapper in the .debug dir
[group: 'build']
[windows]
debug: 
  @just build .debug

#build everything and test launch the exe
[group: 'build']
[windows]
[parallel]
test folder=".debug": shutdown-app build-J build-C
  @just copy {{folder}}
  @build/{{folder}}/ITT-launcher.exe -suppress_messages &
  @just zip {{folder}}

#build java launcher
[group: 'java']
[windows]
build-J:
  gradle build
  gradle buildRuntime

#build c executables
[group: 'exe']
[windows]
[parallel]
build-C: build-Launch build-Update

#build c wrapper executable
[group: 'exe']
[windows]
build-Launch:
  @#create icon image for the exe
  @just update-ico

  @#create missing directories
  @[ -d build/launch ] || mkdir -p build/launch
  @[ -d build/tmp/launch ] || mkdir -p build/tmp/launch

  @#compile resources
  llvm-rc src/launch/launch.rc -fo build/tmp/launch/launch.res

  @#compile c
  clang -O3 src/launch/launch.c build/tmp/launch/launch.res -o build/launch/ITT-launcher.exe -static -lshell32

#build c update executable
[group: 'exe']
[windows]
build-Update:
  @#create missing directories
  @[ -d build/update ] || mkdir -p build/update
  @[ -d build/tmp/update ] || mkdir -p build/tmp/update

  @#compile launch.c
  clang -O3 src/update/update.c src/update/zip.c -Isrc/update/include -o build/update/update.exe -static -lshell32

#update executable icon
[group: 'exe']
[windows]
update-ico:
  magick src/java11/resources/img/icon.png src/launch/icon.ico &

#zip files
[group: 'package']
[windows]
zip folder=".build":
  ./zip.exe build/{{folder}}/ build/{{folder}}/ITT-launcher.zip &

#copy files
[group: 'package']
[windows]
copy folder=".build":
  @#create missing directories
  @[ -d build/{{folder}} ] || mkdir -p build/{{folder}}

  @#delete dx12 folder
  @rm -rf build/{{folder}}/dx12/ &

  @#copy jars
  @echo copy jar
  cp build/libs/*.jar build/{{folder}}/ &

  @#copy jre
  @echo copy jre
  cp -r build/jre/ build/{{folder}}/ &

  @#copy exe
  @echo copy launch
  cp build/launch/*.exe build/{{folder}}/ &

  @#copy exe
  @echo copy update
  cp build/update/*.exe build/{{folder}}/ &

  @#copy dll
  @echo copy dll
  cp lib/dll/*.dll build/{{folder}}/ &

  @#copy readme
  @echo copy readme
  cp readme.md build/{{folder}}/ &

#attempt to close application if its open (assumes default port)
[group: 'util']
[windows]
shutdown-app:
  @echo "shutdown_application" > /dev/tcp/localhost/51000 &
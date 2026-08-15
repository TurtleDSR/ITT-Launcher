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
  @build/{{folder}}/ITT-launcher.exe &
  @just zip {{folder}}

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
  @#create icon image for the exe
  @just update-ico &

  @#create missing directories
  @[ -d build/c ] || mkdir -p build/c
  @[ -d build/tmp/c ] || mkdir -p build/tmp/c

  @#compile resources
  llvm-rc src/launch/launch.rc -fo build/tmp/c/launch.res

  @#compile c
  clang -O3 src/launch/launch.c build/tmp/c/launch.res -o build/c/ITT-launcher.exe -static -lshell32

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
  @rm -rf build/{{folder}}/dx12/

  @#copy jars
  @echo copy jar
  cp build/libs/*.jar build/{{folder}}/ &

  @#copy jre
  @echo copy jre
  cp -r build/jre/ build/{{folder}}/ &

  @#copy exe's
  @echo copy exe
  cp build/c/*.exe build/{{folder}}/ &

  @#copy readme
  @echo copy exe
  cp readme.md build/{{folder}}/ &

#attempt to close application if its open (assumes default port)
[group: 'util']
[windows]
shutdown-app:
  @echo "shutdown_application" > /dev/tcp/localhost/41000 &
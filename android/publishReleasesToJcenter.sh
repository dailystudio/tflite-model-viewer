./gradlew :tensorflow-lite-viewer:clean
./gradlew :tensorflow-lite-viewer:build
./gradlew :tensorflow-lite-viewer:publishToMavenLocal
./gradlew :tensorflow-lite-viewer:bintrayUpload -PdryRun=false --no-configure-on-demand --no-parallel

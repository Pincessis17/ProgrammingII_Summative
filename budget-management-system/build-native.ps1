$jmods = "C:\Users\User\Downloads\openjfx-21.0.12_windows-x64_bin-jmods\javafx-jmods-21.0.12"
mvn package -Ppackage-native "-Djavafx.jmods.path=$jmods"

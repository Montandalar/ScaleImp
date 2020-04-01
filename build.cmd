set PATH_TO_FX=F:\Non-installs\openjfx-13.0.2_windows-x64_bin-jmods\javafx-jmods-13.0.2
javac -Xlint --module-path %PATH_TO_FX% --add-modules javafx.controls --add-modules javafx.fxml module-info.java Main.java Controller.java || exit /B
move Main.class org\imperial2metric\
move Controller.class org\imperial2metric\
rem move *.class org\imperial2metric\
jar -f Imperial2Metric.jar -m MANIFEST.MF -c module-info.class org\
del Imperial2Metric.jmod
rmdir /s /q image
jmod create --main-class org.imperial2metric.Main --class-path Imperial2Metric.jar Imperial2Metric.jmod
jlink --output image --module-path .;%PATH_TO_FX% --add-modules org.imperial2metric --launcher Imperial2Metric=org.imperial2metric/org.imperial2metric.Main
Programming language: Java 8
Development Environment: Eclipse
Java compiler version: Java 8 required at least

**********************************
File: lombok.jar
**********************************

This is the Java library that supports @Obj annotation. You can use it directly, or if you want to manually export it from our source code, follow the instructions in the next section "Folder: SourceCode".

To use @Obj, you need to:
1. Make sure you have Eclipse installed on your computer with Java 8 compiler at least.
2. Double-click lombok.jar, click "Install/Update" button to make your Eclipse support the features.
3. Run Eclipse, and add lombok.jar to where @Obj is used in your code (add it to the Java Build Path).

**********************************
Folder: SourceCode
**********************************

This folder contains the source code of @Obj, as an extension to the project Lombok.
More tutorials to Project Lombok: https://projectlombok.org/
Three files are included in this folder:
	- Obj.java: declaration of the annotation @Obj.
	- HandleObj.java: implementation of @Obj processing.
	- PatchDelegate.java: reuse of the Lombok annotation @Delegate to support separate compilation.

To export this into a Java library for further experiments, please follow the steps below:
1. Clone or download the original GitHub repository of Lombok: https://github.com/rzwitserloot/lombok
2. After downloading, get into the root directory from command line. You can see the file build.xml there.
3. Add Obj.java, HandleObj.java, and update PatchDelegate.java (optional) to the project. Specifically:
	- .\src\core\lombok\Obj.java
	- .\src\core\lombok\eclipse\handlers\HandleObj.java
	- .\src\eclipseAgent\lombok\eclipse\agent\PatchDelegate.java (still experimental feature, not needed if you just use @Obj)
4. Run the following commands from the command line:
	> ant eclipse
	> ant
5. The project is then compiled. After that, Lombok is automatically exported into a jar file:
	- .\dist\lombok.jar
6. Run lombok.jar by double-clicking it directly or run it from the command line, then open Eclipse, and import this jar file to where @Obj is used (add it to the Java Build Path).

Note: In Lombok, each annotation is expected to have two processors, one for compilation in Eclipse, and the other for Javac. They are similar but have different implementations. At this stage only the Eclipse handler is supported, so experiments on @Obj are supposed to be conducted in Eclipse.

**********************************
Folder: UseMixinLombok
**********************************

This project contains a number of examples using @Obj. Applications and some case studies are available.
The latest version of Lombok library with @Obj supported is available in lib\lombok.jar. Please don't forget to run lombok.jar before opening Eclipse.

**********************************
Folder: MumblerCaseStudy
**********************************

This folder contains source code for the Mumbler case study. Inside there are two sub-folders:
	- MumblerOri: original Mumbler source code.
	- Mumbler: our code for Mumbler. We add the print operation to the code base, and we use @Obj in the new version.
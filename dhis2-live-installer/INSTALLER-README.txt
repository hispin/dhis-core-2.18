Thanks for downloading the DHIS2 Live installer. The aim of this project is to 
create a simple process for the complete build of DHIS2 with the aim of deployment
to a desktop computer. The installer, along with the build script should build
a complete DHIS2 system ready for deployment from source.

Other installers for other types of implementations may be
added in the future, but for the moment, the installer will create a Windows EXE
installer that will ship with the H2 database. 

There are two installers here. The first and most up to date is the BitRock installer. 
Along with the build script included with this package, it will build a Windows 
executable. The second installer is based on IZPack and may be more appropriate
for those that need a relatively simply build process, without having Maven installed.
It is recommended to use the BitRock installer, but I have included both here. 


BitRock installer notes

You should be able to execute the build script from essentially anywhere on your system. 
It is critical that you set all of the environment variables to the correct paths in your system. 
Modify the environment variables in the build.sh script to suit your needs. 


There are several variables that you will need to set.
Here are some examples, modify them in the script to suit your local system. 

1) This variable should point to your local copy of the JDK. 
 JAVA_HOME=/usr/local/java/jdk1.6.0_10/
 
2) Be sure that maven is accessible in your path. 
 PATH=$PATH:/home/wheel/apache-maven-2.2.1/bin/
 
3) This variable is necessary during  the installer build process and should point to your local copy of the
BitRock install builder directory. You can get a copy of BitRock install builder from here
http://installbuilder.bitrock.com/download-step-2.html. Install it somewhere on your system and
point the environment variable to the correct directory.
 BITROCK_HOME=/home/wheel/installbuilder-6.2.7/
 
4) This variable should point to the directory where the birt.war file resides. If not set BIRT will be omitted.
 BIRT_WAR="/usr/local/apache-tomcat-6.0.18/webapps/"
 
5) This variable should point to the root directory of the source of the documentation branch.  
 DHIS2_DOCS="/home/wheel/workspace/dhis2-docbook-docs/"
 
6) This variable should point to your copy of the dhis2 main source branch. 
 DHIS2_SRC="/home/wheel/workspace/dhis2"


My directory structure looks something like this..
/workspace
|_dhis2
|_dhis2-docbook-docs
|_dhis2-live-installer

To get started just execute, just go to the dhis2-live-installer directory
and execute the build.sh script if you are on Linux (be sure it is executable)
or the build.bat script if you are using Windows.

There are several options you will need to pass to the build script. 

./build.sh all Build everything. 
./build.sh docs Build only the documentation.
./build.sh installer Build only the installer.
./build.sh dhis2 Build only DHIS2. 
./build.sh live Build only the DHIS2 Live wrapper.

There are a number of different installers that will be generated for different purposes. 

1)DHIS2 Windows Live

This version of the installer is appropriate for Microsoft Windows with a pre-existing JRE.

2) DHIS2 Windows Embedded JRE

This version of the installer will include a pre-packed:

o JRE (Java Runtime Edition). This version therefore will not require a user to have Java pre-installed on their machine, but will 
increase the size of the installer by about 30 MB. Place a copy of the JRE in /resources/jre/jre6.
Note that JRE is referring to the extracted collection of files, not the Windows executable installer.
One way to obtain this is to install JRE on Windows and copy the extracted JRE directory into this project.

o Firefix browser. Place a copy of Firefox Portable in /resources/browser/FirefoxPortable. Note that Firefox portable is referring
to the extracted collection of files, not the Windows executable installer. One way to obtain this is to install Firefox portable
on Windows and copy the extracted FirefoxPortable directory into this project.

o Demo database. Place a copy of the H2 demo database (called "demo.h2.db") into resources/demodb.


You will now enter into a rather lengthy
process depending on the speed of your machine. At the end you will
have a Windows installer based on the latest source code and
documentation. These will be output to the BITROCK_HOME/output
directory.


IZPack Installer 

Basically, you will need to populate some different directories with prerequisites. 

1) Put everything you need for PostgreSQL in the postgres directory.This usually involves unzipping that installer on the PostgreSQL website. 
2) Put an offline version of Java (to be sure you have a recent version) in the /java directory. Be sure that the file name matches that in the install.xml file
3) Put the dhis2_user_manual_en.pdf from the documentation branch into the /docs directory
4) Change the hibernate.properties file to set your needs. 
5) Compile with the IZPack compiler.

Questions about these installer can be directed to Jason Pickering <jason.p.pickering@gmail.com>. 


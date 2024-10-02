COMPILATION

hellojni demonstrates invoking a JNI function from an ATAK plugin.

Private Plugins offer the most capability for utilizing the ATAK subsystem, but 
this interface will likely change from version to version.


build.xml and Makefile both reflect the same project name (in this case helloworld).

The assets file describes both a Lifecycle and a ToolDescriptor.   For convention,
these are in the same location used in the AndroidManifest.xml file.    For 
readability I have broken out the plugin to be in a directory off of the main 
package structure.

When constructing the plugin, it is important to recognize that there are two 
different android.content.Context in play.   

  The plugin context is used to resolve resources from the plugin APK
  The mapView context is used for graphic access (AlertDialogs, Toasts, etc).

The following steps are required to compile the JNI library
1. Make the 'hellojni' module (Build > Make Module 'hellojni')
2. Execute the following on the command line

      cd jni
      make

  This will create the 'hellojni.h' header file using 'javah' then compile the
  'hellojni.cpp' source file via the Android NDK.

The following environment variables MUST be defined

ANDROID_SDK -- the path to the Android SDK on the host system
ANDROID_NDK -- the path to the Android NDK on the host system
PATH -- must include the 'bin' directory of the JDK installation on the host

The following environment variables may be optionally defined

SEP -- the path separator character, defaults to ':'
SUB_CLASSPATH -- the sub classpath where the class files for the project are
                 located, relative to 'hellojni/build'. Defaults to
                 'intermediates/javac/debug/compileDebugJavaWithJavac/classes'
ANDROID_SDK_VERSION -- the target version of the Android SDK for the project,
                       defaults to '21'










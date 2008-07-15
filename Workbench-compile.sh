#!/bin/sh
APPDIR=`dirname $0`;

if [ -z $GWT_HOME ]; then
   echo "You must first set GWT_HOME to point to the location where Google Web Toolkit 1.5 is installed"
   exit 1
fi

if [ ! -r $GWT_HOME/gwt-user.jar ]; then
   echo "Your GWT_HOME is not containing the required gwt-user.jar library. Please fix your GWT_HOME to point to the correct location where Google Web Toolkit 1.5 is installed"
   exit 1
fi

OS=`uname`

if [ "$OS" == "Darwin" ]; then
   OS=mac
   EXTRA_VMARGS+=-XstartOnFirstThread
elif [ "$OS" == "Linux" ]; then
   OS=linux
fi

for lib in `echo "$APPDIR/lib/"*`; do
   LIBS+=:$lib
done

java $EXTRA_VMARGS -Xmx256M -cp "$APPDIR/src:$APPDIR/build/classes:$GWT_HOME/gwt-user.jar:$GWT_HOME/gwt-servlet.jar:$GWT_HOME/gwt-dev-$OS.jar$LIBS" com.google.gwt.dev.GWTCompiler -out "$APPDIR/build/gwtOutput" "$@" org.seasr.meandre.workbench.Workbench;

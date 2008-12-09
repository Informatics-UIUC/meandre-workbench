#!/bin/bash
APPDIR=`dirname $0`;

OS=`uname`

if [ "$OS" == "Darwin" ]; then
   EXTRA_VMARGS+=-XstartOnFirstThread
fi

for build_lib in `echo "$APPDIR/lib/build/"*`; do
   BUILD_LIBS+=:$build_lib
done

for deploy_lib in `echo "$APPDIR/lib/deploy/"*`; do
   DEPLOY_LIBS+=:$deploy_lib
done

java $EXTRA_VMARGS -Xmx256M -cp "$APPDIR/src:$APPDIR/lib/compiler/gwt-compiler.jar$BUILD_LIBS$DEPLOY_LIBS" com.google.gwt.dev.GWTCompiler -out "$APPDIR/build/gwtOutput" "$@" org.seasr.meandre.workbench.Workbench;

#!/bin/sh

CPP_JAR=anarres-cpp.jar

if [ -n "$CPP_ROOT" ] ; then
	CPP_ROOT="$CPP_ROOT"
elif [ -f lib/$CPP_JAR ] ; then
	CPP_ROOT="."
elif [ -f ../lib/$CPP_JAR ] ; then
	CPP_ROOT=".."
elif [ -f $(dirname $0)/lib/$CPP_JAR ] ; then
	CPP_ROOT=$(dirname $0)
else
	echo "Could not find $CPP_JAR. Please set CPP_ROOT."
	exit 1
fi

if [ -z "$CPP_LIB" ] ; then
	CPP_LIB=$CPP_ROOT/lib
fi

if [ -z "$CPP_CLASSPATH" ] ; then
	CPP_CLASSPATH="$(ls $CPP_LIB/*.jar | tr '\n' ':')"
fi

if [ -z "$CPP_MAINCLASS" ] ; then
	CPP_MAINCLASS=org.anarres.cpp.Main
fi

CPP_JFLAGS="-Xmx128M"

exec java $CPP_JFLAGS -cp "$CPP_CLASSPATH" $CPP_MAINCLASS "$@"

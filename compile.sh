#!/bin/bash
# Script to compile the Java project

echo "Compiling Java source files..."
javac -d out src/com/auth/utils/*.java src/com/auth/system/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Run: java -cp out com.auth.system.Main"
else
    echo "Compilation failed!"
    exit 1
fi

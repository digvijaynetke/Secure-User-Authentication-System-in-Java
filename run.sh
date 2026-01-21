#!/bin/bash
# Script to run the compiled Java application

if [ ! -d "out" ]; then
    echo "Project not compiled. Running compile.sh..."
    ./compile.sh
fi

echo "Running User Authentication System..."
java -cp out com.auth.system.Main

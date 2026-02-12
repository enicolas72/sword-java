#!/bin/bash
# Run the Hello sample application

cd "$(dirname "$0")"

# Compile with Maven
echo "Compiling with Maven..."
mvn compile -q

# Run
echo "Running Hello sample..."
mvn exec:java -q

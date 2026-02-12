# S.W.O.R.D - Java Port

Java port of the S.W.O.R.D (System of Windows for the ORganisation of the Desktop) GUI framework.

## Project Structure

```
src/main/java/net/eric_nicolas/sword/
├── common/         - Core utilities, error handling
├── mechanism/      - Base object system (TAtom, TObject, TEvent)
├── drivers/        - System abstraction (time, file system)
├── graphics/       - Graphics and window management
├── tools/          - Application framework (TApp)
└── samples/        - Example applications
```

## Building

### With Maven (if installed)
```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package JAR
mvn package

# Run HELLO sample
mvn exec:java
```

### With javac directly
```bash
# Compile
mkdir -p target/classes
javac -d target/classes --release 21 $(find src/main/java/net -name "*.java")

# Run HELLO sample
java -cp target/classes net.eric_nicolas.sword.samples.Hello
```

### Using the convenience script
```bash
./run-hello.sh
```

## Requirements

- Java 21 or higher (Java 22 also works)
- Maven 3.6+ (optional - can use javac directly)

## Current Status

**Phase 1: Core Infrastructure** - In Progress

Goal: Implement minimal subsystems to run the HELLO sample.

- [ ] COMMON subsystem
- [ ] MECHANISM subsystem (TAtom, TObject, TEvent, TPoint, TRect)
- [ ] GRAPHICS subsystem (TZone, TWindow, TDesktop, TColors)
- [ ] TOOLS subsystem (TApp)
- [ ] HELLO sample

## Original C++ Source

The original C++ codebase is located in `../210 original/`

## Documentation

See `../CLAUDE.md` for detailed architecture documentation and development guidelines.

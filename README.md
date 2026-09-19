# Departmental Budget Management System

A JavaFX desktop application for tracking departments, staff, and budget
allocations in a nested organisational hierarchy, backed by MySQL via JDBC.

## Contents

- [Prerequisites](#prerequisites)
- [1. Database setup](#1-database-setup)
- [2. Configure credentials](#2-configure-credentials)
- [3. Run in development](#3-run-in-development)
- [4. Run the tests](#4-run-the-tests)
- [5. Build a standalone executable](#5-build-a-standalone-executable)
- [Project structure](#project-structure)
- [Verified platforms](#verified-platforms)

## Prerequisites

- JDK 21
- Maven 3.8+
- MySQL 8.0+, running locally or reachable over the network

## 1. Database setup

Connect to MySQL and create the database:

```sql
CREATE DATABASE IF NOT EXISTS budget_management;
```

The application creates its own tables automatically on first launch ...
no schema SQL needs to be run by hand.

## 2. Configure credentials

Copy the example properties file and fill in your own values:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

Edit `db.properties`:

```properties
db.url=jdbc:mysql://localhost:3306/budget_management
db.username=root
db.password=your_mysql_password
```

`db.properties` is excluded from version control ... never commit real
credentials.

## 3. Run in development

```bash
mvn javafx:run
```

Always launch this way (or via the packaged executable below) — running
`MainApp` directly from an IDE's "Run" button skips the JavaFX module
setup and will fail to start.

On first launch, the required tables are created automatically if they
don't already exist.

## 4. Run the tests

```bash
mvn test
```

34 tests across 6 classes cover the recursive budget/headcount
algorithms, input validation, the department-nesting depth limit, the
rule blocking deletion of a department that still holds employees or
child departments, and full CRUD against a real MySQL database for
each entity.

## 5. Build a standalone executable

This produces a native application image with its own bundled Java
runtime, so it runs on a machine that has no JDK installed.

**One-time setup:** download the JavaFX 21.0.12 **jmods** (not the SDK)
for your OS from
[gluonhq.com/products/javafx](https://gluonhq.com/products/javafx/) and
extract the zip fully. Confirm the folder you extracted to contains
`.jmod` files directly (e.g. `javafx.controls.jmod`).

**Build:**

```bash
mvn package -Ppackage-native -Djavafx.jmods.path=/full/path/to/javafx-jmods-21.0.12
```

The finished application appears at `target/dist/BudgetManagementSystem/`,
containing `BudgetManagementSystem.exe`, an `app/` folder, and a
`runtime/` folder.

**Running it:** launch `BudgetManagementSystem.exe` from inside Windows
File Explorer, not from a browser's file listing, which can only
download a separate copy of the `.exe`, split off from the `app/` and
`runtime/` folders it needs to run. Keep the `.exe` in its own folder;
don't move or copy it on its own. To test on another machine, copy the
entire `BudgetManagementSystem/` folder, not just the `.exe` inside it.

If it fails with `Error opening "...\BudgetManagementSystem.cfg" file:
No such file or directory`, that's this exact issue, re-launch it from
inside its real folder.

If the build itself fails with `Error: Module X not found`, regenerate
the required module list instead of guessing:

```bash
mvn package
jdeps --multi-release 21 --print-module-deps --ignore-missing-deps target/budget-management-system-1.0-SNAPSHOT.jar
```

Add `javafx.controls,javafx.fxml` to the printed list (jdeps can't see
these since JavaFX is excluded from the shaded jar on purpose), and use
the combined list as the `--add-modules` value in the `package-native`
profile in `pom.xml`.

## Project structure

```
budget-management-system/
├── README.md
├── pom.xml
├── db.properties.example
├── project-report.pdf
└── src/
    ├── main/
    │   ├── java/com/budgetms/
    │   │   ├── app/            MainApp — application entry point
    │   │   ├── db/              DatabaseConnectionManager, SchemaInitializer
    │   │   ├── dao/             DAO interfaces and JDBC implementations
    │   │   ├── model/           Department, Employee, Budget, OrgUnit
    │   │   └── ui/controller/   JavaFX controllers
    │   └── resources/
    │       ├── db.properties.example
    │       └── fxml/            JavaFX view definitions
    └── test/java/com/budgetms/
        ├── dao/                 DAO integration tests 
        ├── model/                Recursion and business-logic unit tests
        └── ui/                   Validation unit tests
```

## Verified platforms

Tested and confirmed working on Windows 11, build verified 19 September
2026 by J'Isabelle Umuvandimwe.

## Team

- **J'Isabelle Umuvandimwe** contributed to database design, JDBC/DAO layer, recursive
  algorithms and their unit tests, Maven packaging configuration
- **Princess Nhyira Addai** contributed toJavaFX interface, controllers, validation,
  native packaging

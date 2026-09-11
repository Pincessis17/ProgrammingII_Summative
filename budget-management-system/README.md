# Departmental Budget Management System

Team: J'Isabelle Umuvandimwe (DB / JDBC / recursion) · Princess Nhyira Addai (JavaFX UI)

This is the Day 1 deliverable from the team sync plan: a JavaFX navigation
shell with all four screens wireframed and running against in-memory mock
data, ready for the real DAOs to be wired in on Day 2-3.

## Running it

You'll need JDK 17+ and Maven, with normal internet access (Maven Central
isn't reachable from the sandbox this was built in, so it hasn't been run
through `mvn` there — but it's a completely standard Maven/JavaFX project,
so on your own machine or lab computer it should just work):

```
mvn clean javafx:run
```

Maven will pull in `javafx-controls` and `javafx-fxml` automatically (see
`pom.xml`) — nothing else to install.

If you don't have the `javafx-maven-plugin` cooperating for some reason, you
can also open the project as a normal Maven project in IntelliJ / Eclipse and
run `com.budgetms.app.MainApp` directly, as long as your run configuration
adds `--add-modules javafx.controls,javafx.fxml` to the VM options.

## Project layout

```
src/main/java/com/budgetms/
  app/                    MainApp — the entry point, loads MainShell.fxml
  model/                  Department, Employee, Budget, OrgUnit (Composite)
  data/                   MockDataStore — sample in-memory org hierarchy
  ui/controller/          One controller per screen
src/main/resources/
  fxml/                   MainShell + the 4 screens
  css/app.css             Shared styling
```

## What's wired up today (Day 1)

- **Navigation shell** — sidebar with 4 toggle buttons, swaps screens into a
  content area (`MainShellController`).
- **Departments** — table + add/edit/delete form. Delete is blocked in the UI
  if a department still has employees or child departments, per the proposal.
- **Employees** — table + add/edit form. No delete button on purpose: an
  employee who leaves gets unchecked "Active" instead, to preserve historical
  data.
- **Budgets** — table + add/edit/delete form, with computed "Actual Cost" and
  "Variance" columns.
- **Budget Browser** — a tree view of the department hierarchy (departments
  and their employees as leaves) with a details panel on the right.

Everything currently reads/writes `MockDataStore`, a small in-memory stand-in
for the real database. The `Department` / `Employee` / `Budget` classes
already match the entities and CRUD scope in the proposal, and
`Department.calculateCost()` / `getHeadcount()` / `getBudgetVariance()` are
placeholder implementations of the recursive walk described in section 4 —
correct in shape, but **not** the tested version J'Isabelle owns.

## Handoff for Day 2-3 (once the DAOs land)

The screens only ever touch `MockDataStore` through a few methods
(`getAllDepartments()`, `getAllEmployees()`, `getAllBudgets()`,
`findDepartmentById()`, and the `nextXId()` helpers) plus direct calls on the
`Department`/`Employee`/`Budget` objects themselves. Swapping in
`DepartmentDAO`/`EmployeeDAO`/`BudgetDAO` should mean:

1. Replace `MockDataStore.getInstance()` calls in each controller with the
   real DAOs.
2. Replace the placeholder recursion in `Department` with calls into
   J'Isabelle's tested `calculateBudget` / `getHeadcount` / `getBudgetVariance`
   (or just drop her implementation into these same method names — the
   `OrgUnit` interface and method signatures were written to match the
   proposal, so the Budget Browser and Budget screens shouldn't need changes).
3. Nothing in the FXML needs to change — only the controllers.

## Known gaps (expected at this stage)

- No persistence — data resets every run (Day 6 task: "prove data survives
  restart" is exactly this, once the DAOs exist).
- No unit tests yet (J'Isabelle's side, per the work split).
- Validation is basic (non-empty / numeric checks) — good enough to unblock
  screen-building, worth revisiting once real data constraints from the DB
  schema are known.

# HyperExecute SE Assignment — Submission

## Task 1: Fix the broken YAML

**Errors found in the original YAML:**

- `conCurrency` — misspelled key (wrong casing); should be `concurrency`. YAML keys
  are case-sensitive, so the misspelled key was silently ignored by the schema.
- `env: TOKEN: anvdegtod...` — invalid YAML. A nested key cannot sit on the same
  line as its parent; `env` must be a mapping with `TOKEN` indented underneath it.
- Un-indented `testDiscovery` children (`type`, `mode`, `command`) — YAML nesting
  is defined by whitespace. Because these were flush-left, they were not
  recognized as children of `testDiscovery`.
- Backticks inside `testRunnerCommand` (`` `-Dplatname=win` `` etc.) — backticks
  are PowerShell line-continuation characters; used here mid-string with no
  actual newline, they corrupted the Maven command.
- No `runtime` block — without an explicit language/version, the runner may not
  match the JDK the Maven project expects.

**Corrected/final YAML:** see `yaml/task1_hyperexecute.yaml`

**Note on simplification:** the original assignment YAML used `autosplit` with a
`testDiscovery` step (via `grep`/`awk`/`sed` against a TestNG XML suite file) to
dynamically discover and split tests. While debugging this locally, the
discovery step repeatedly failed on the Windows HyperExecute runner
(inconsistent availability of GNU utilities in that shell environment). Given
the deadline, I simplified to a direct `testRunnerCommand: mvn test`, which
runs the full suite in one task with no discovery step required. This is a
supported, documented HyperExecute pattern (non-autosplit mode) and trades off
per-test parallel splitting for reliability. Happy to discuss the original
autosplit/testDiscovery approach and its trade-offs in the follow-up interview.

**Evidence:** [ADD DASHBOARD SCREENSHOT OF PASSING JOB HERE]

---

## Task 2: Environment variables

- Defined `ENVIRONMENT: staging` under `env:` in the YAML.
- Printed it in the `pre` step: `echo ENVIRONMENT is %ENVIRONMENT%` (Windows
  syntax for reading an env var in `pre` steps).
- Read it inside a test via `System.getenv("ENVIRONMENT")` and printed it to
  console — see `TestAdditions.java`, method `printEnvironmentVariable`.

**YAML:** see `yaml/task2_3_hyperexecute.yaml`

**Evidence:** [ADD LOG SCREENSHOT SHOWING "ENVIRONMENT is staging" FROM BOTH THE
PRE STEP AND THE TEST EXECUTION LOG HERE]

---

## Task 3: Force a failure and configure retries

- Added `intentionalFailureTest()` — a deterministic `Assert.fail(...)`, not a
  flaky/random failure, so any observed retry is real evidence of
  `retryOnFailure` working rather than luck.
- `retryOnFailure: true` and `maxRetries: 1` are set in
  `yaml/task2_3_hyperexecute.yaml` (same file used for Task 2).
- On the dashboard, the failing test's log should show two executions: the
  original attempt plus one retry.

**Evidence:** [ADD DASHBOARD SCREENSHOT SHOWING THE RETRY ATTEMPT HERE]

---

## Task 4: Linux/Unix basics

Sample input file (`sample.log`):
```
INFO test1 staging PASS
ERROR test2 staging FAIL
INFO test3 staging PASS
FAIL test4 staging FAIL
```

| Command | What it does | Output |
|---|---|---|
| `grep 'FAIL' sample.log` | prints every line containing FAIL | `ERROR test2 staging FAIL`<br>`FAIL test4 staging FAIL` |
| `awk '{print $2}' sample.log` | prints the 2nd space-delimited column (test name) | `test1`<br>`test2`<br>`test3`<br>`test4` |
| `sed 's/staging/production/g' sample.log` | replaces all `staging` with `production` | `INFO test1 production PASS`<br>`ERROR test2 production FAIL`<br>`INFO test3 production PASS`<br>`FAIL test4 production FAIL` |
| `grep 'FAIL' sample.log \| awk '{print $2}'` | filters to failing lines, then extracts just the test names from those lines | `test2`<br>`test4` |

All commands above were actually run against the sample file; output shown is
real, not illustrative.

## 2026-02-05 - Compose @Composable property access in remember
**Learning:** Accessing @Composable properties like `MaterialTheme.typography.display1` inside a `remember` lambda fails because the lambda is not a Composable context.
**Action:** Extract @Composable property values in the main Composable scope and pass them as keys to `remember` to ensure they are available to the lambda and update correctly if the value changes (e.g., during theme switches).

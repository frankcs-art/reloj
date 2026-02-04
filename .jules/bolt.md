## 2026-02-04 - Jetpack Compose Clock Optimization
**Learning:** For frequently updating states (like a ticking clock), memoizing formatters and using `derivedStateOf` for formatted strings significantly reduces allocation churn and redundant UI work. Wildcard imports in Kotlin (`import androidx.compose.runtime.*`) include `derivedStateOf`, but explicit imports are often preferred for clarity and to avoid potential conflicts.
**Action:** Use `remember` for expensive objects and `derivedStateOf` for state-dependent computations that change less frequently than the source state.

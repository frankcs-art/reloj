## 2025-05-22 - Accessible Time and Stats on Wear OS
**Learning:** Time and stat displays on Wear OS are often fragmented for screen readers if not grouped. Hardcoded small font sizes (7.sp) in stat labels significantly hinder readability on small screens.
**Action:** Always use `Modifier.semantics(mergeDescendants = true)` for time and stat groups, and ensure labels are at least 9.sp.

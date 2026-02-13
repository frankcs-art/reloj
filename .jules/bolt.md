## 2025-02-06 - Jetpack Compose Clock Optimizations
**Learning:** For high-frequency state updates (like a 1Hz clock), using `derivedStateOf` for formatted strings that change less frequently (like HH:mm or Date) significantly reduces recompositions of UI components. However, using `derivedStateOf` for strings that change at the same frequency as the state (like seconds) adds overhead without benefit. Also, memoizing `TextStyle` that depends on `MaterialTheme` properties must be done by capturing the property in the Composable scope and passing it as a key to `remember`.
**Action:** Use `derivedStateOf` selectively for lower-frequency derivations. Always memoize expensive allocations like `Brush`, `Stroke`, and `TextStyle` in frequently recomposing views.

## 2025-02-09 - Recomposition Isolation with Lambda Providers
**Learning:** Even when using `derivedStateOf`, a Composable will still recompose at the frequency of the source state if it reads that state directly or passes it as a value to a child. To truly isolate high-frequency updates (like seconds in a clock), the state read must be deferred by passing a lambda provider (e.g., `() -> T`) to the child Composable, which then reads the state internally. This stops the parent from being a "reader" of the high-frequency state.
**Action:** Use lambda providers to pass high-frequency state to children to minimize parent recomposition scope.

## 2025-02-12 - Optimizing Mobile Preview Recomposition
**Learning:** In the mobile module, a 1Hz clock update was causing the entire screen to recompose every second because the `currentTime` state was read directly for formatting. Moving expensive objects (Brush, List, DateTimeFormatter) to top-level constants and using `derivedStateOf` for the formatted time (HH:mm) reduced the recomposition frequency from 1Hz to 1/60Hz, a ~98% reduction in UI work.
**Action:** Always check for direct state reads of high-frequency variables in root-level Composables. Move static object allocations out of the Composable scope.

## 2025-02-14 - Arduino Rendering & Alpha Blending
**Learning:** Microcontrollers have limited CPU and SPI bandwidth. Redrawing the entire screen at a fixed high frequency (e.g. 10Hz) when the content only changes at 1Hz (e.g. seconds) is a major waste of resources. Additionally, `alphaBlend` operations are computationally expensive and should be cached if the colors don't change frequently.
**Action:** Implement frame-skipping logic in Arduino render loops using a `last_ss` comparison and a `forceRedraw` flag for state changes. Pre-calculate blended colors in a cache whenever background or accent colors are updated.

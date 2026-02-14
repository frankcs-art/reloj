## 2025-01-24 - Clipboard Feedback Pattern
**Learning:** In technical configuration panels, providing a "Copy" button with immediate visual feedback (changing text and color) significantly improves the developer experience by confirming the action without requiring a separate notification.
**Action:** Use inline state changes (e.g., button text "Copiar" -> "¡Copiado!") for quick clipboard actions to provide low-friction feedback.

## 2026-02-14 - Abstract CSS Variable Naming
**Learning:** Using abstract names for user-configurable colors (e.g., --accent-primary instead of --accent-blue) prevents naming contradictions when the user changes the theme, making the CSS more maintainable and intuitive for future developers.
**Action:** Always use abstract variable names for properties that are exposed as user-controllable settings in configuration panels.

## 2025-01-24 - Semantic Buttons and Keyboard Navigation in Watch Face Preview
**Learning:** Using `div` elements for interactive icons prevents keyboard navigation and screen reader accessibility. Switching to `button` elements requires a CSS reset (padding, border, font) to maintain visual design. Additionally, `hover`-only visibility for interactive panels (like `app-selector`) must be supplemented with `:focus-within` to ensure keyboard users can discover and interact with the panel.
**Action:** Always use `<button>` for interactive icons, provide `aria-label`, and ensure container visibility for focused children using `:focus-within`.

## 2025-02-05 - Synchronizing Preview Tools with Firmware Capabilities
**Learning:** Configuration tools (like the watch face preview) should stay synchronized with the underlying firmware's data model to provide an accurate representation of capabilities. Furthermore, using semantic variable names (e.g., `--text-main` instead of `--text-white`) prevents design debt when those properties become user-configurable.
**Action:** Regularly audit UI preview tools against the source-of-truth models (e.g., C++ structs or Dart models) and use theme-neutral variable naming for configurable properties.

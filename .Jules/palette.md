## 2025-05-15 - Accessible Interactive Previews
**Learning:** In web-based previews of hardware interfaces (like watch faces), interactive elements often rely on hover states that are inaccessible to keyboard users. Using `:focus-within` on the parent container and converting `div` interactions to semantic `<button>` elements ensures that the UI is discoverable and usable for everyone.
**Action:** Always use `<button>` for interactive icons, provide clear `:focus-visible` states, and use `:focus-within` to reveal hidden menus during keyboard navigation.

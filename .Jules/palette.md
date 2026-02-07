## 2025-05-22 - Web Accessibility Pattern for Preview Icons
**Learning:** Interactive icons in the web preview were implemented as non-semantic 'div' tags, making them inaccessible to keyboard users and screen readers.
**Action:** Always use semantic '<button>' elements for interactive icons, include 'aria-label' attributes, and provide explicit ':focus-visible' styles to ensure clear keyboard navigation visibility while maintaining visual design with CSS resets.

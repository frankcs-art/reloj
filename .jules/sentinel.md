# Sentinel's Journal - Critical Security Learnings

## 2025-02-04 - Vulnerable Input Handling in BLE Configuration
**Vulnerability:** Null pointer dereference and potential buffer overflow in Arduino firmware.
**Learning:** Untrusted input from BLE characteristics was passed directly to string manipulation functions (`hex[0]`, `sprintf`) without validation. `ArduinoJson` can return `NULL` for `JsonVariant` string conversion if the type doesn't match, leading to crashes.
**Prevention:** Always validate pointers returned from external libraries and use safe string functions like `snprintf`.

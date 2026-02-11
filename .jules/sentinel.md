# Sentinel's Journal - Critical Security Learnings

## 2025-02-04 - Vulnerable Input Handling in BLE Configuration
**Vulnerability:** Null pointer dereference and potential buffer overflow in Arduino firmware.
**Learning:** Untrusted input from BLE characteristics was passed directly to string manipulation functions (`hex[0]`, `sprintf`) without validation. `ArduinoJson` can return `NULL` for `JsonVariant` string conversion if the type doesn't match, leading to crashes.
**Prevention:** Always validate pointers returned from external libraries and use safe string functions like `snprintf`.

## 2025-02-12 - Defensive BLE Configuration Processing
**Vulnerability:** State corruption and potential UI-based DoS in Arduino firmware.
**Learning:** Parsing JSON without verifying the root type (`is<JsonObject>()`) can lead to unexpected behavior. Additionally, returning a fixed value (like `0`) on parsing failure can corrupt the device state (e.g., turning the screen black if a `null` or invalid hex is sent).
**Prevention:** Always validate the JSON root type. Ensure parsing functions (like `hexTo565`) accept and return a default/current value on failure to maintain state integrity. Guard expensive operations (like `tft.fillScreen`) behind state-change checks.

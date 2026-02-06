## 2025-01-24 - Buffer Safety and Input Validation in Arduino BLE Firmware
**Vulnerability:** Use of `sprintf` for formatting time strings and lack of input validation for hex color strings received via BLE.
**Learning:** Embedded systems communicating via BLE are vulnerable to malformed packets. Using `sprintf` without bounds checking and `strtoul` without validating the input string length or content can lead to buffer overflows and crashes (DoS).
**Prevention:** Always use `snprintf` with explicit buffer sizes and implement robust validation (null checks, length checks, character set validation) for all data received over the air.

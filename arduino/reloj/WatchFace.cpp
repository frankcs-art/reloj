#include "WatchFace.h"
#include <ctype.h>

WatchFace::WatchFace() : tft(TFT_eSPI()) {
    // Default configuration (Galaxy Theme)
    config.bgColor = 0x0845;      // #0F0C29
    config.accentColor1 = 0x069F; // #00D2FF
    config.accentColor2 = 0x9A97; // #9D50BB
    config.textColor = 0xFFFF;    // White
    config.showSteps = true;
    config.showBPM = true;
    config.is24h = true;

    // Optimized: Initialize performance tracking variables
    last_ss = -1;
    forceRedraw = true;
    updateCachedColors();
}

void WatchFace::updateCachedColors() {
    // Optimized: Pre-calculate expensive alphaBlend operations
    cachedGlowColor = tft.alphaBlend(128, config.accentColor1, config.bgColor);
    cachedRingBgColor = tft.alphaBlend(32, 0xFFFF, config.bgColor);
}

void WatchFace::init() {
    tft.init();
    tft.setRotation(0);
    tft.fillScreen(config.bgColor);
}

void WatchFace::updateConfig(String json) {
    StaticJsonDocument<512> doc;
    DeserializationError error = deserializeJson(doc, json);

    if (error) {
        Serial.print("deserializeJson() failed: ");
        Serial.println(error.f_str());
        return;
    }

    // SECURITY: Ensure the root is an object to prevent unexpected behavior with non-object JSON
    if (!doc.is<JsonObject>()) {
        Serial.println("Warning: Received JSON is not an object");
        return;
    }

    bool bgChanged = false;
    if (doc.containsKey("bgColor")) {
        uint16_t newBg = hexTo565(doc["bgColor"], config.bgColor);
        if (newBg != config.bgColor) {
            config.bgColor = newBg;
            bgChanged = true;
        }
    }

    bool configChanged = false;
    if (doc.containsKey("accentColor1")) {
        uint16_t c1 = hexTo565(doc["accentColor1"], config.accentColor1);
        if (c1 != config.accentColor1) { config.accentColor1 = c1; configChanged = true; }
    }
    if (doc.containsKey("accentColor2")) {
        uint16_t c2 = hexTo565(doc["accentColor2"], config.accentColor2);
        if (c2 != config.accentColor2) { config.accentColor2 = c2; configChanged = true; }
    }
    if (doc.containsKey("textColor")) {
        uint16_t tc = hexTo565(doc["textColor"], config.textColor);
        if (tc != config.textColor) { config.textColor = tc; configChanged = true; }
    }
    if (doc.containsKey("showSteps")) {
        bool ss = doc["showSteps"];
        if (ss != config.showSteps) { config.showSteps = ss; configChanged = true; }
    }
    if (doc.containsKey("showBPM")) {
        bool sb = doc["showBPM"];
        if (sb != config.showBPM) { config.showBPM = sb; configChanged = true; }
    }
    if (doc.containsKey("is24h")) {
        bool i24 = doc["is24h"];
        if (i24 != config.is24h) { config.is24h = i24; configChanged = true; }
    }

    // Optimized: Only update cache and force redraw if something actually changed
    if (bgChanged || configChanged) {
        updateCachedColors();
        forceRedraw = true;
    }

    // Redraw background only if it changed to prevent UI-based DoS and flickering
    if (bgChanged) {
        tft.fillScreen(config.bgColor);
    }
}

void WatchFace::updateTime() {
    // Simulate time progression using millis()
    unsigned long now = millis();
    ss = (now / 1000) % 60;
    mm = (now / 60000) % 60;
    hh = (now / 3600000) % 24;

    dayStr = "SÁB";
    dateStr = "ENE 31";
}

void WatchFace::render() {
    // Optimized: Frame-skipping logic. Only redraw if the second changed or a redraw is forced.
    // This significantly reduces SPI bus traffic and power consumption.
    if (ss == last_ss && !forceRedraw) {
        delay(20); // Maintain a small delay to avoid pegging the CPU
        return;
    }
    last_ss = ss;
    forceRedraw = false;

    // In a real application, you might use Tft-eSPI Sprites to avoid flickering
    // For this example, we render directly to the screen.

    // 1. Draw Background Glow (Simplified)
    drawGlow();

    // 2. Draw Progress Rings
    drawProgressRings();

    // 3. Draw Date and Time
    drawTime();

    // 4. Draw Stats (Steps & BPM)
    drawStats();

    delay(100); // Small delay to prevent high CPU usage
}

void WatchFace::drawGlow() {
    // Optimized: Use pre-calculated glow color to avoid per-frame alphaBlend overhead
    tft.drawCircle(120, 120, 110, cachedGlowColor);
}

void WatchFace::drawProgressRings() {
    int centerX = 120;
    int centerY = 120;

    // Outer Ring (e.g., Steps)
    // Optimized: Use pre-calculated ring background color
    tft.drawCircle(centerX, centerY, 100, cachedRingBgColor);

    // Draw progress part (mocking 75% progress)
    // Use drawSmoothArc if available: tft.drawSmoothArc(centerX, centerY, 100, 90, 0, 270, config.accentColor1, config.bgColor);
    // Fallback to simple circle for now
    tft.drawCircle(centerX, centerY, 101, config.accentColor1);

    // Inner Ring (e.g., Battery)
    tft.drawCircle(centerX, centerY, 90, cachedRingBgColor);
    tft.drawCircle(centerX, centerY, 91, config.accentColor2);
}

void WatchFace::drawTime() {
    tft.setTextColor(config.textColor, config.bgColor);
    tft.setTextDatum(MC_DATUM); // Middle Center

    // Draw Date
    String fullDate = dayStr + ", " + dateStr;
    tft.setTextSize(1);
    tft.drawString(fullDate, 120, 70);

    // Draw Main Time
    char timeBuf[10];
    if (config.is24h) {
        // SECURITY: Use snprintf to prevent buffer overflows
        snprintf(timeBuf, sizeof(timeBuf), "%02d:%02d", hh, mm);
    } else {
        int h12 = hh % 12;
        if (h12 == 0) h12 = 12;
        snprintf(timeBuf, sizeof(timeBuf), "%02d:%02d", h12, mm);
    }

    tft.setTextSize(4);
    tft.drawString(timeBuf, 110, 120);

    // Draw Seconds
    tft.setTextSize(2);
    tft.setTextColor(config.accentColor1, config.bgColor);
    char secBuf[3];
    // SECURITY: Use snprintf to prevent buffer overflows
    snprintf(secBuf, sizeof(secBuf), "%02d", ss);
    tft.drawString(secBuf, 175, 130);
}

void WatchFace::drawStats() {
    if (!config.showSteps && !config.showBPM) return;

    tft.setTextSize(1);
    tft.setTextDatum(MC_DATUM);

    if (config.showSteps) {
        tft.setTextColor(config.accentColor1, config.bgColor);
        tft.drawString("8.2k", 90, 170);
        tft.setTextColor(0x7BEF, config.bgColor); // Gray
        tft.drawString("STEPS", 90, 185);
    }

    if (config.showBPM) {
        tft.setTextColor(0xF800, config.bgColor); // Red-ish
        tft.drawString("85", 150, 170);
        tft.setTextColor(0x7BEF, config.bgColor); // Gray
        tft.drawString("BPM", 150, 185);
    }
}

uint16_t WatchFace::hexTo565(const char* hex, uint16_t defaultColor) {
    // SECURITY: Validate input to prevent null pointer dereference
    if (hex == nullptr) return defaultColor;

    if (hex[0] == '#') hex++;

    // SECURITY: Robust validation of hex string length and characters
    // Only update if we have a valid 6-digit hex string
    if (strlen(hex) != 6) return defaultColor;

    for (int i = 0; i < 6; i++) {
        if (!isxdigit((unsigned char)hex[i])) return defaultColor;
    }

    uint32_t rgb = strtoul(hex, nullptr, 16);
    uint8_t r = (rgb >> 16) & 0xFF;
    uint8_t g = (rgb >> 8) & 0xFF;
    uint8_t b = rgb & 0xFF;
    return tft.color565(r, g, b);
}

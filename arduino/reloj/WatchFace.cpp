#include "WatchFace.h"

WatchFace::WatchFace() : tft(TFT_eSPI()) {
    // Default configuration (Galaxy Theme)
    config.bgColor = 0x0845;      // #0F0C29
    config.accentColor1 = 0x069F; // #00D2FF
    config.accentColor2 = 0x9A97; // #9D50BB
    config.textColor = 0xFFFF;    // White
    config.showSteps = true;
    config.showBPM = true;
    config.is24h = true;
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

    if (doc.containsKey("bgColor")) config.bgColor = hexTo565(doc["bgColor"]);
    if (doc.containsKey("accentColor1")) config.accentColor1 = hexTo565(doc["accentColor1"]);
    if (doc.containsKey("accentColor2")) config.accentColor2 = hexTo565(doc["accentColor2"]);
    if (doc.containsKey("showSteps")) config.showSteps = doc["showSteps"];
    if (doc.containsKey("showBPM")) config.showBPM = doc["showBPM"];
    if (doc.containsKey("is24h")) config.is24h = doc["is24h"];

    // Redraw background if changed
    tft.fillScreen(config.bgColor);
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
    // Draw a subtle outer ring to simulate glow
    tft.drawCircle(120, 120, 110, tft.alphaBlend(128, config.accentColor1, config.bgColor));
}

void WatchFace::drawProgressRings() {
    int centerX = 120;
    int centerY = 120;

    // Outer Ring (e.g., Steps)
    // TFT_eSPI's drawSmoothArc is ideal for this if enabled in User_Setup.h
    // Here we use a simpler approach for compatibility
    tft.drawCircle(centerX, centerY, 100, tft.alphaBlend(32, 0xFFFF, config.bgColor));

    // Draw progress part (mocking 75% progress)
    // Use drawSmoothArc if available: tft.drawSmoothArc(centerX, centerY, 100, 90, 0, 270, config.accentColor1, config.bgColor);
    // Fallback to simple circle for now
    tft.drawCircle(centerX, centerY, 101, config.accentColor1);

    // Inner Ring (e.g., Battery)
    tft.drawCircle(centerX, centerY, 90, tft.alphaBlend(32, 0xFFFF, config.bgColor));
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
        sprintf(timeBuf, "%02d:%02d", hh, mm);
    } else {
        int h12 = hh % 12;
        if (h12 == 0) h12 = 12;
        sprintf(timeBuf, "%02d:%02d", h12, mm);
    }

    tft.setTextSize(4);
    tft.drawString(timeBuf, 110, 120);

    // Draw Seconds
    tft.setTextSize(2);
    tft.setTextColor(config.accentColor1, config.bgColor);
    char secBuf[3];
    sprintf(secBuf, "%02d", ss);
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

uint16_t WatchFace::hexTo565(const char* hex) {
    if (hex[0] == '#') hex++;
    uint32_t rgb = strtoul(hex, NULL, 16);
    uint8_t r = (rgb >> 16) & 0xFF;
    uint8_t g = (rgb >> 8) & 0xFF;
    uint8_t b = rgb & 0xFF;
    return tft.color565(r, g, b);
}

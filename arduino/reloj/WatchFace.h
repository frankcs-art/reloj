#ifndef WATCH_FACE_H
#define WATCH_FACE_H

#include <TFT_eSPI.h>
#include <ArduinoJson.h>

/**
 * @brief Structure to hold the watch face configuration.
 * Colors are stored in 16-bit RGB565 format for TFT_eSPI.
 */
struct WatchConfig {
    uint16_t bgColor;
    uint16_t accentColor1;
    uint16_t accentColor2;
    uint16_t textColor;
    bool showSteps;
    bool showBPM;
    bool is24h;
};

/**
 * @brief Class to handle rendering of the watch face.
 */
class WatchFace {
public:
    WatchFace();

    /**
     * @brief Initialize the display and default configuration.
     */
    void init();

    /**
     * @brief Update the configuration from a JSON string.
     * @param json JSON string containing configuration parameters.
     */
    void updateConfig(String json);

    /**
     * @brief Update the internal time values.
     */
    void updateTime();

    /**
     * @brief Render the entire watch face to the screen.
     */
    void render();

private:
    TFT_eSPI tft;
    WatchConfig config;

    // Time variables
    int hh, mm, ss;
    int last_ss = -1;
    bool forceRedraw = true;
    String dayStr;
    String dateStr;

    // Cached colors for performance optimization
    uint16_t cachedGlowColor;
    uint16_t cachedRingBgColor;

    // Rendering sub-functions
    void updateCachedColors();
    void drawGlow();
    void drawProgressRings();
    void drawTime();
    void drawStats();

    /**
     * @brief Convert a hex color string (e.g., "#FF0000") to RGB565 format.
     * @param hex The hex string to convert.
     * @param defaultColor Color to return if the hex string is invalid or null.
     */
    uint16_t hexTo565(const char* hex, uint16_t defaultColor);
};

#endif

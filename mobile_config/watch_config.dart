import 'dart:convert';

/**
 * Model class for the Watch Face Configuration.
 * This class matches the JSON structure expected by the Arduino firmware.
 */
class WatchConfig {
  String bgColor;      // Hex string, e.g., "#0F0C29"
  String accentColor1; // Hex string, e.g., "#00D2FF"
  String accentColor2; // Hex string, e.g., "#9D50BB"
  String textColor;    // Hex string, e.g., "#FFFFFF"
  bool showSteps;
  bool showBPM;
  bool is24h;

  WatchConfig({
    required this.bgColor,
    required this.accentColor1,
    required this.accentColor2,
    required this.textColor,
    required this.showSteps,
    required this.showBPM,
    required this.is24h,
  });

  /**
   * Creates a WatchConfig instance from a Map (usually from JSON).
   */
  factory WatchConfig.fromJson(Map<String, dynamic> json) {
    return WatchConfig(
      bgColor: json['bgColor'] ?? '#0F0C29',
      accentColor1: json['accentColor1'] ?? '#00D2FF',
      accentColor2: json['accentColor2'] ?? '#9D50BB',
      textColor: json['textColor'] ?? '#FFFFFF',
      showSteps: json['showSteps'] ?? true,
      showBPM: json['showBPM'] ?? true,
      is24h: json['is24h'] ?? true,
    );
  }

  /**
   * Converts the WatchConfig instance to a Map for JSON serialization.
   */
  Map<String, dynamic> toJson() {
    return {
      'bgColor': bgColor,
      'accentColor1': accentColor1,
      'accentColor2': accentColor2,
      'textColor': textColor,
      'showSteps': showSteps,
      'showBPM': showBPM,
      'is24h': is24h,
    };
  }

  /**
   * Converts the WatchConfig instance to a JSON string.
   */
  String toJsonString() => jsonEncode(toJson());

  @override
  String toString() {
    return 'WatchConfig(bgColor: $bgColor, accentColor1: $accentColor1, accentColor2: $accentColor2)';
  }
}

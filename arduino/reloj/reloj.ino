#include <ArduinoBLE.h>
#include <ArduinoJson.h>
#include "WatchFace.h"

WatchFace watchFace;

// BLE Service for Watch Configuration
BLEService configService("0000180a-0000-1000-8000-00805f9b34fb"); // Custom Service UUID
BLEStringCharacteristic configCharacteristic("00002a3d-0000-1000-8000-00805f9b34fb", BLEWrite | BLENotify, 512);

void setup() {
  Serial.begin(115200);

  // Initialize BLE
  if (!BLE.begin()) {
    Serial.println("Starting BLE failed!");
    while (1);
  }

  BLE.setLocalName("RelojGalactico");
  BLE.setAdvertisedService(configService);
  configService.addCharacteristic(configCharacteristic);
  BLE.addService(configService);
  BLE.advertise();

  Serial.println("BLE device active, waiting for connections...");

  // Initialize the watch face
  watchFace.init();
}

void loop() {
  // Listen for BLE centrals
  BLEDevice central = BLE.central();

  if (central) {
    Serial.print("Connected to central: ");
    Serial.println(central.address());

    while (central.connected()) {
      if (configCharacteristic.written()) {
        String jsonConfig = configCharacteristic.value();
        Serial.println("Received new configuration via BLE");
        watchFace.updateConfig(jsonConfig);
      }

      // Still need to update the time and render
      watchFace.updateTime();
      watchFace.render();
    }

    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
  }

  // Normal operation when not connected or after disconnection
  watchFace.updateTime();
  watchFace.render();
}

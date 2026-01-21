function fn() {
  var driverType = karate.properties['driver.type'] || 'chrome';
  var serverPort = karate.properties['server.port'] || '8080';
  var serverUrl = 'http://localhost:' + serverPort;
  karate.configure('driver', { type: driverType, headless: false });
  return {
    driverType: driverType,
    serverUrl: serverUrl
  };
}

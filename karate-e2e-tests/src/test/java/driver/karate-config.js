function fn() {
  var serverPort = karate.properties['server.port'] || 8080;
  karate.configure('driver', { type: 'chrome', headless: false });
  return {
    serverUrl: 'http://localhost:' + serverPort
  };
}

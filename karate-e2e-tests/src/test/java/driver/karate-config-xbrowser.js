function fn() {
  var serverPort = karate.properties['server.port'] || '8080';
  var serverUrl = 'http://localhost:' + serverPort;
  karate.configure('driver', { type: 'chrome', headless: false });
  return {
    serverUrl: serverUrl
  };
}

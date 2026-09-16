// Cache del "app shell" para que abra offline una vez instalada.
// Los datos (pacientes/turnos) viven en localStorage, no acá — esto es
// solo para que la interfaz cargue sin conexión.
// Subí este número cada vez que cambien los archivos de web/ para que
// los dispositivos ya instalados bajen la versión nueva.
var CACHE_NAME = "pacientes-shell-v1";
var ARCHIVOS = [
  "./",
  "./index.html",
  "./styles.css",
  "./app.js",
  "./manifest.webmanifest",
  "./icons/icon-192.png",
  "./icons/icon-512.png"
];

self.addEventListener("install", function(event){
  event.waitUntil(
    caches.open(CACHE_NAME).then(function(cache){ return cache.addAll(ARCHIVOS); })
  );
  self.skipWaiting();
});

self.addEventListener("activate", function(event){
  event.waitUntil(
    caches.keys().then(function(nombres){
      return Promise.all(
        nombres.filter(function(n){ return n !== CACHE_NAME; }).map(function(n){ return caches.delete(n); })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener("fetch", function(event){
  if(event.request.method !== "GET") return;
  var url = new URL(event.request.url);
  if(url.origin !== self.location.origin) return; // fuentes de Google, etc.: directo a la red

  event.respondWith(
    caches.match(event.request).then(function(cacheado){
      var redFetch = fetch(event.request).then(function(respuesta){
        if(respuesta && respuesta.ok){
          var copia = respuesta.clone();
          caches.open(CACHE_NAME).then(function(cache){ cache.put(event.request, copia); });
        }
        return respuesta;
      }).catch(function(){ return cacheado; });
      return cacheado || redFetch;
    })
  );
});

// Cache del "app shell" para que la app abra sin conexión una vez instalada.
// Los datos (pacientes/turnos) viven en localStorage, no acá.
//
// Estrategia: RED PRIMERO, caché como respaldo. Es al revés de lo habitual a
// propósito: con caché primero, cada vez que se actualizan los archivos hay que
// acordarse de subir la versión de acá, y si no, los navegadores que ya
// abrieron la app siguen mostrando la versión vieja. Así siempre se ve lo
// último cuando hay conexión, y sigue funcionando offline.
var CACHE_NAME = "pacientes-shell-v2";
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

  // Sin "no-cache" el navegador le contesta al propio service worker desde su
  // caché HTTP, y una versión nueva de los archivos no se ve hasta un rato
  // después (o hasta hacer un recargado forzado).
  var pedido;
  try { pedido = new Request(event.request, { cache: "no-cache" }); }
  catch(e){ pedido = event.request; }

  event.respondWith(
    fetch(pedido).then(function(respuesta){
      if(respuesta && respuesta.ok){
        var copia = respuesta.clone();
        caches.open(CACHE_NAME).then(function(cache){ cache.put(event.request, copia); });
      }
      return respuesta;
    }).catch(function(){
      // Sin conexión: lo que haya guardado; si es una navegación, el index.
      return caches.match(event.request).then(function(cacheado){
        if(cacheado) return cacheado;
        if(event.request.mode === "navigate") return caches.match("./index.html");
        return Response.error();
      });
    })
  );
});

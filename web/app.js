(function(){
  "use strict";
  var $ = function(id){ return document.getElementById(id); };
  var ICONS = {
    calendar:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4.5" width="18" height="16" rx="2"/><line x1="3" y1="9.5" x2="21" y2="9.5"/><line x1="8" y1="2.5" x2="8" y2="6.5"/><line x1="16" y1="2.5" x2="16" y2="6.5"/></svg>',
    users:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 19v-1.5a3.5 3.5 0 0 0-3.5-3.5h-5A3.5 3.5 0 0 0 4 17.5V19"/><circle cx="9" cy="8" r="3.2"/><path d="M21 19v-1.2a3 3 0 0 0-2.3-2.9"/><path d="M15.2 5a3.2 3.2 0 0 1 0 6.2"/></svg>',
    stethoscope:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 4v5.5a4.5 4.5 0 0 0 9 0V4"/><path d="M9.5 14v1.5a5 5 0 0 0 10 0V13"/><circle cx="19.5" cy="11.5" r="1.7"/><line x1="5" y1="4" x2="3.3" y2="4"/><line x1="14" y1="4" x2="15.7" y2="4"/></svg>',
    plus:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>',
    back:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>',
    chevL:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="15 18 9 12 15 6"/></svg>',
    chevR:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg>',
    search:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>',
    trash:'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2m3 0-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/></svg>'
  };
  var ESTILO_ESTADO = {
    AGENDADO:   {texto:"Sin confirmar", color:"var(--ambar)", fondo:"var(--ambar-fondo)"},
    CONFIRMADO: {texto:"Confirmado",    color:"var(--verde)", fondo:"var(--verde-fondo)"},
    CANCELADO:  {texto:"Cancelado",     color:"var(--rojo)",  fondo:"var(--rojo-fondo)"}
  };
  var DIAS = ["L","M","X","J","V","S","D"];
  var DIAS_LARGO = ["lunes","martes","miércoles","jueves","viernes","sábado","domingo"];
  var MESES = ["enero","febrero","marzo","abril","mayo","junio","julio","agosto","septiembre","octubre","noviembre","diciembre"];

  function pad(n){ return n < 10 ? "0"+n : ""+n; }
  function cap(s){ return s.charAt(0).toUpperCase()+s.slice(1); }
  function esMismoDia(a,b){ return a.getFullYear()===b.getFullYear() && a.getMonth()===b.getMonth() && a.getDate()===b.getDate(); }
  function fechaLarga(d){ return cap(DIAS_LARGO[(d.getDay()+6)%7]) + " " + d.getDate() + " de " + MESES[d.getMonth()]; }
  function fechaCorta(d){ return d.getDate() + " " + MESES[d.getMonth()].slice(0,3); }
  function horaCorta(d){ return pad(d.getHours())+":"+pad(d.getMinutes()); }
  function ddmm(d){ return pad(d.getDate())+"/"+pad(d.getMonth()+1); }
  function isoFecha(d){ return d.getFullYear()+"-"+pad(d.getMonth()+1)+"-"+pad(d.getDate()); }
  function isoHora(d){ return pad(d.getHours())+":"+pad(d.getMinutes()); }
  function iniciales(nombre){ return nombre.trim().split(/\s+/).map(function(p){return p[0];}).slice(0,2).join("").toUpperCase(); }

  function mensajeConfirmacion(turno, doctorNombre){
    var d = new Date(turno.inicio);
    return "Usted tiene un turno con la Dr. " + doctorNombre + " el día " + ddmm(d) +
      " a las " + horaCorta(d) + " hs. Por favor confirmar asistencia indicando SI o NO";
  }
  function waHref(telefono, mensaje){
    var numero = telefono.replace(/[^\d+]/g,"").replace(/^\+/,"");
    return "https://wa.me/" + numero + "?text=" + encodeURIComponent(mensaje);
  }

  // ---- Turno -> archivo .ics real ----
  function icsFecha(d){
    return d.getFullYear()+pad(d.getMonth()+1)+pad(d.getDate())+"T"+pad(d.getHours())+pad(d.getMinutes())+"00";
  }
  function generarIcs(turno, paciente){
    var inicio = new Date(turno.inicio);
    var fin = new Date(inicio.getTime() + turno.duracion*60000);
    return [
      "BEGIN:VCALENDAR","VERSION:2.0","PRODID:-//Pacientes//Turno//ES","BEGIN:VEVENT",
      "UID:" + turno.id + "-" + Date.now() + "@pacientes",
      "DTSTART:" + icsFecha(inicio),
      "DTEND:" + icsFecha(fin),
      "SUMMARY:Turno con Dr. " + turno.doctorNombre,
      "DESCRIPTION:Turno agendado para " + paciente.nombre,
      "END:VEVENT","END:VCALENDAR"
    ].join("\r\n");
  }
  /** Comparte (o si no hay Web Share con archivos, descarga) el turno como .ics real. */
  function compartirTurnoComoIcs(turno, paciente){
    var texto = generarIcs(turno, paciente);
    var blob = new Blob([texto], {type:"text/calendar"});
    var nombreArchivo = "turno-" + turno.id + ".ics";
    var file = null;
    try{ file = new File([blob], nombreArchivo, {type:"text/calendar"}); }catch(e){}

    if(file && navigator.canShare && navigator.canShare({files:[file]})){
      navigator.share({files:[file], title:"Turno"}).catch(function(){});
      return;
    }
    var url = URL.createObjectURL(blob);
    var a = document.createElement("a");
    a.href = url; a.download = nombreArchivo;
    document.body.appendChild(a); a.click(); document.body.removeChild(a);
    setTimeout(function(){ URL.revokeObjectURL(url); }, 2000);
    toast("Se descargó el turno (.ics) — compartilo por WhatsApp o mail");
  }

  // ---- Datos + persistencia ----
  /** Arranca vacía: los pacientes, doctores y turnos los carga quien usa la app. */
  function seed(){
    return { pacientes: [], doctores: [], turnos: [], nextIds:{paciente:1, doctor:1, turno:1} };
  }
  var STORAGE_KEY = "pacientes-web-v2";
  function cargar(){
    try{
      var raw = localStorage.getItem(STORAGE_KEY);
      if(!raw) return seed();
      var d = JSON.parse(raw);
      if(!d || !d.pacientes || !d.doctores || !d.turnos) return seed();
      return d;
    }catch(e){ return seed(); }
  }
  function guardar(){ try{ localStorage.setItem(STORAGE_KEY, JSON.stringify(datos)); }catch(e){} }

  var datos = cargar();

  // ---- Estado de UI ----
  var ui = {
    tab: "agenda",
    vistaAgenda: "dia",
    diaSeleccionado: new Date(new Date().setHours(0,0,0,0)),
    pantalla: null,          // {tipo:'nuevo'} | {tipo:'detalle', turnoId} | {tipo:'reagendar', turnoId}
    editandoPaciente: null,
    busquedaPacientes: "",
    formTurno: null,
    comboAbierto: false,
    recordatoriosVistos: []
  };

  function toast(msg){
    var el = $("toast");
    el.textContent = msg;
    el.classList.add("show");
    clearTimeout(toast._t);
    toast._t = setTimeout(function(){ el.classList.remove("show"); }, 2200);
  }

  function pacientePor(id){ return datos.pacientes.find(function(p){return p.id===id;}); }
  function doctorPor(id){ return datos.doctores.find(function(d){return d.id===id;}); }
  function turnoPor(id){ return datos.turnos.find(function(t){return t.id===id;}); }

  function turnosDelDia(dia){
    return datos.turnos
      .filter(function(t){ return esMismoDia(new Date(t.inicio), dia); })
      .sort(function(a,b){ return new Date(a.inicio) - new Date(b.inicio); });
  }
  // ---- Días hábiles y feriados (Argentina) ----
  /** Domingo de Pascua (algoritmo de Meeus/Butcher) — de ahí salen Carnaval y Viernes Santo. */
  function domingoDePascua(anio){
    var a=anio%19, b=Math.floor(anio/100), c=anio%100,
        d=Math.floor(b/4), e=b%4, f=Math.floor((b+8)/25),
        g=Math.floor((b-f+1)/3), h=(19*a+b-d-g+15)%30,
        i=Math.floor(c/4), k=c%4, l=(32+2*e+2*i-h-k)%7,
        m=Math.floor((a+11*h+22*l)/451),
        mes=Math.floor((h+l-7*m+114)/31), dia=((h+l-7*m+114)%31)+1;
    return new Date(anio, mes-1, dia);
  }
  var _feriadosCache = {};
  /**
   * Feriados nacionales de fecha fija + los que dependen de Pascua.
   * No incluye los "feriados puente" (se anuncian por decreto cada año).
   */
  function feriadosDe(anio){
    if(_feriadosCache[anio]) return _feriadosCache[anio];
    var fijos = ["01-01","03-24","04-02","05-01","05-25","06-20","07-09","12-08","12-25"];
    var set = {};
    fijos.forEach(function(md){ set[anio + "-" + md] = true; });
    var pascua = domingoDePascua(anio);
    function marcar(offsetDias){
      var d = new Date(pascua); d.setDate(d.getDate() + offsetDias);
      set[d.getFullYear() + "-" + pad(d.getMonth()+1) + "-" + pad(d.getDate())] = true;
    }
    marcar(-48); // lunes de Carnaval
    marcar(-47); // martes de Carnaval
    marcar(-2);  // Viernes Santo
    _feriadosCache[anio] = set;
    return set;
  }
  function esFeriado(d){
    return !!feriadosDe(d.getFullYear())[d.getFullYear() + "-" + pad(d.getMonth()+1) + "-" + pad(d.getDate())];
  }
  function esFinDeSemana(d){ var g = d.getDay(); return g === 0 || g === 6; }
  function esDiaHabil(d){ return !esFinDeSemana(d) && !esFeriado(d); }

  /**
   * Cuándo corresponde pedirle confirmación a este turno: 48hs antes, salvo
   * que esas 48hs caigan en fin de semana o feriado — en ese caso se adelanta
   * al último día hábil anterior (si no, el aviso quedaría para un día que no
   * se trabaja y el turno llegaría sin confirmar).
   */
  function momentoRecordatorio(turno){
    var envio = new Date(new Date(turno.inicio).getTime() - 48*60*60*1000);
    if(esDiaHabil(envio)) return envio;
    var d = new Date(envio);
    do { d.setDate(d.getDate() - 1); } while(!esDiaHabil(d));
    return new Date(d.getFullYear(), d.getMonth(), d.getDate(), 9, 0, 0, 0);
  }

  /** Del más próximo al más lejano. Un turno cuya hora ya pasó no entra, aunque siga agendado. */
  function turnosPendientesDeRecordatorio(){
    var ahora = new Date();
    return datos.turnos.filter(function(t){
      if(t.estado !== "AGENDADO" || t.recordatorioEnviado) return false;
      if(ui.recordatoriosVistos.indexOf(t.id) !== -1) return false;
      if(new Date(t.inicio) <= ahora) return false;
      return ahora >= momentoRecordatorio(t);
    }).sort(function(a,b){ return new Date(a.inicio)-new Date(b.inicio); });
  }

  // ---- Navegación ----
  function irA(tab){
    ui.tab = tab; ui.pantalla = null; render();
  }
  function push(pantalla){ ui.pantalla = pantalla; render(); }
  function pop(){ ui.pantalla = null; render(); }

  // ---- Render raíz ----
  function render(){
    renderAppBar();
    renderSearch();
    renderBody();
    renderBottomNav();
  }

  function renderAppBar(){
    var bar = $("appbar");
    if(ui.pantalla){
      var titulo = ui.pantalla.tipo === "nuevo" ? "Nuevo turno"
        : ui.pantalla.tipo === "reagendar" ? "Reagendar turno"
        : "Turno";
      bar.innerHTML =
        '<button class="icon-btn" data-action="back">' + ICONS.back + '</button>' +
        '<h2>' + titulo + '</h2>';
      return;
    }
    if(ui.tab === "agenda"){
      bar.innerHTML =
        '<h2>Agenda</h2>' +
        '<div class="toggle">' +
          '<button data-action="vista-dia" class="' + (ui.vistaAgenda==="dia"?"on":"") + '">Día</button>' +
          '<button data-action="vista-mes" class="' + (ui.vistaAgenda==="mes"?"on":"") + '">Mes</button>' +
        '</div>';
    } else if(ui.tab === "pacientes"){
      bar.innerHTML = '<h2>Pacientes</h2><button class="icon-btn" data-action="nuevo-paciente">' + ICONS.plus + '</button>';
    } else {
      bar.innerHTML = '<h2>Doctores</h2><button class="icon-btn" data-action="nuevo-doctor">' + ICONS.plus + '</button>';
    }
  }

  function renderSearch(){
    var wrap = $("searchWrap");
    if(!ui.pantalla && ui.tab === "pacientes"){
      wrap.innerHTML =
        '<div class="search">' + ICONS.search +
        '<input id="inputBusqueda" placeholder="Buscar paciente" autocomplete="off" value="' + escAttr(ui.busquedaPacientes) + '"></div>';
      $("inputBusqueda").addEventListener("input", function(e){ ui.busquedaPacientes = e.target.value; renderBody(); });
    } else {
      wrap.innerHTML = "";
    }
  }

  function renderBottomNav(){
    var nav = $("bottomNav");
    if(ui.pantalla){ nav.style.display = "none"; return; }
    nav.style.display = "flex";
    function item(tab, icon, label){
      return '<button class="nav-item ' + (ui.tab===tab?"on":"") + '" data-action="tab" data-tab="' + tab + '">' + icon + '<span>' + label + '</span></button>';
    }
    nav.innerHTML = item("agenda", ICONS.calendar, "Agenda") + item("pacientes", ICONS.users, "Pacientes") + item("doctores", ICONS.stethoscope, "Doctores");
  }

  function escAttr(s){ return (s||"").replace(/"/g,"&quot;"); }
  function escHtml(s){ return (s||"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;"); }

  // ---- Body dispatch ----
  function renderBody(){
    var body = $("body");
    if(ui.pantalla){
      if(ui.pantalla.tipo === "nuevo") return renderFormNuevoTurno(body);
      if(ui.pantalla.tipo === "reagendar") return renderFormReagendar(body);
      if(ui.pantalla.tipo === "detalle") return renderDetalleTurno(body);
    }
    if(ui.tab === "agenda") return renderAgenda(body);
    if(ui.tab === "pacientes") return renderPacientes(body);
    if(ui.tab === "doctores") return renderDoctores(body);
  }

  // ---- Agenda ----
  function renderAgenda(body){
    if(ui.vistaAgenda === "mes"){ renderAgendaMes(body); return; }

    var turnos = turnosDelDia(ui.diaSeleccionado);
    var html = '<div class="dia-nav">' +
      '<button class="icon-btn" data-action="dia-prev">' + ICONS.chevL + '</button>' +
      '<span class="fecha">' + fechaLarga(ui.diaSeleccionado) + '</span>' +
      '<button class="icon-btn" data-action="dia-next">' + ICONS.chevR + '</button>' +
    '</div>';

    if(turnos.length === 0){
      html += '<div class="empty">No hay turnos agendados este día.</div>';
    } else {
      html += '<div class="list">';
      turnos.forEach(function(t){
        var p = pacientePor(t.pacienteId);
        var est = ESTILO_ESTADO[t.estado];
        html += '<button class="turno-card" data-action="abrir-turno" data-id="' + t.id + '">' +
          '<span class="turno-barra" style="background:' + est.color + '"></span>' +
          '<span class="turno-info">' +
            '<span class="turno-top">' +
              '<span class="who">' + horaCorta(new Date(t.inicio)) + ' · ' + escHtml(p ? p.nombre : "Paciente") + '</span>' +
              '<span class="badge" style="color:' + est.color + '; background:' + est.fondo + '">' + est.texto + '</span>' +
            '</span>' +
            '<div class="turno-doctor">Dr. ' + escHtml(t.doctorNombre) + ' · ' + t.duracion + ' min</div>' +
          '</span>' +
        '</button>';
      });
      html += '</div>';
    }
    var pendientes = turnosPendientesDeRecordatorio();
    html += '<div class="btn-block acciones-agenda">' +
      '<button class="btn btn-sm btn-outline-solid" data-action="ir-nuevo-turno">' + ICONS.plus + '<span>Nuevo turno</span></button>' +
      '<button class="btn btn-sm btn-primary" data-action="abrir-recordatorios" ' + (pendientes.length ? '' : 'disabled') + '>' +
        'Enviar recordatorio' + (pendientes.length ? ' (' + pendientes.length + ')' : '') +
      '</button>' +
    '</div>';
    body.innerHTML = html;
  }

  function renderAgendaMes(body){
    var dia = ui.diaSeleccionado;
    var year = dia.getFullYear(), month = dia.getMonth();
    var primerDia = new Date(year, month, 1);
    var offset = (primerDia.getDay() + 6) % 7;
    var diasEnMes = new Date(year, month+1, 0).getDate();
    var hoy = new Date();
    var diasConTurno = {};
    datos.turnos.forEach(function(t){
      var i = new Date(t.inicio);
      if(t.estado !== "CANCELADO" && i.getFullYear()===year && i.getMonth()===month) diasConTurno[i.getDate()] = true;
    });

    var html = '<div class="mes-titulo">' + cap(MESES[month]) + ' ' + year + '</div>';
    html += '<div class="mes-grid">';
    DIAS.forEach(function(d){ html += '<div class="mes-head">' + d + '</div>'; });
    for(var i=0;i<offset;i++) html += '<div></div>';
    for(var d=1; d<=diasEnMes; d++){
      var esHoy = hoy.getFullYear()===year && hoy.getMonth()===month && hoy.getDate()===d;
      html += '<button class="mes-celda ' + (esHoy?"hoy":"") + '" data-action="elegir-dia" data-dia="' + d + '">' +
        d + '<span class="punto ' + (diasConTurno[d]?"":"off") + '"></span></button>';
    }
    html += '</div>';
    body.innerHTML = html;
  }

  /**
   * Muestra el próximo recordatorio pendiente (uno por vez, del más próximo al
   * más lejano). Se dispara desde el botón "Enviar recordatorio", nunca solo al
   * abrir la app. Cuando no queda ninguno cierra y refresca la Agenda.
   */
  function mostrarRecordatorioSiHay(avisarSiNoHay){
    var pendientes = turnosPendientesDeRecordatorio();
    var siguiente = null;
    for(var i=0;i<pendientes.length;i++){
      if(pacientePor(pendientes[i].pacienteId)){ siguiente = pendientes[i]; break; }
    }
    if(!siguiente){
      cerrarDialogo();
      if(avisarSiNoHay) toast("No hay recordatorios para enviar");
      renderBody();
      return;
    }
    abrirDialogoRecordatorio(siguiente, pacientePor(siguiente.pacienteId));
  }

  // ---- Turno: nuevo ----
  function estadoFormTurnoInicial(){
    var ahora = new Date();
    return {
      pacienteId: null, nombreNuevoPaciente: "", telefonoNuevoPaciente: "",
      doctorId: "", duracion: "30",
      fecha: isoFecha(ui.diaSeleccionado),
      hora: isoHora(ahora),
      notas: "", compartirAhora: true
    };
  }

  function renderFormNuevoTurno(body){
    if(!ui.formTurno) ui.formTurno = estadoFormTurnoInicial();
    var f = ui.formTurno;
    var pacienteSel = f.pacienteId ? pacientePor(f.pacienteId) : null;

    var html = '';
    html += '<div class="field">' +
      '<label>Paciente</label>' +
      '<input id="fPaciente" placeholder="Buscar o crear paciente" autocomplete="off" value="' + escAttr(pacienteSel ? pacienteSel.nombre : f.nombreNuevoPaciente) + '">' +
      '<div id="comboResultsWrap"></div>' +
    '</div>';
    html += '<div id="telefonoWrap"></div>';

    html += '<div class="field"><label>Doctor/a</label><select id="fDoctor">' +
      '<option value="" ' + (f.doctorId===""?"selected":"") + '>Elegir doctor/a</option>' +
      datos.doctores.map(function(d){ return '<option value="' + d.id + '" ' + (String(f.doctorId)===String(d.id)?"selected":"") + '>' + escHtml(d.nombre) + '</option>'; }).join("") +
      '</select></div>';
    if(datos.doctores.length === 0){
      html += '<p class="hint">Todavía no hay doctores cargados — agregalos en la pestaña Doctores.</p>';
    }

    html += '<div class="row-fields">' +
      '<div class="field"><label>Fecha</label><input type="date" id="fFecha" value="' + f.fecha + '"></div>' +
      '<div class="field"><label>Hora</label><input type="time" id="fHora" value="' + f.hora + '"></div>' +
    '</div>';
    html += '<div class="field"><label>Duración (min)</label><input type="number" id="fDuracion" value="' + escAttr(f.duracion) + '"></div>';
    html += '<div class="field"><label>Notas (opcional)</label><textarea id="fNotas">' + escHtml(f.notas) + '</textarea></div>';

    html += '<div class="check-card"><input type="checkbox" id="fCompartir" ' + (f.compartirAhora?"checked":"") + '>' +
      '<div><div class="t">Compartir con la paciente ahora</div><div class="d">Abre el turno como .ics para mandarlo por WhatsApp/mail</div></div></div>';

    html += '<button class="btn btn-primary" id="btnGuardarTurno">Guardar turno</button>';

    body.innerHTML = html;
    renderComboResults();
    renderTelefonoWrap();
    actualizarBotonGuardarTurno();

    // El input de Paciente se actualiza a mano (sin recrear el <form>) para
    // no perder foco/cursor en cada tecla — recrear todo el body en cada
    // keystroke rompía la edición (se notaba al tipear rápido o con teclados
    // predictivos).
    $("fPaciente").addEventListener("focus", function(){ ui.comboAbierto = true; renderComboResults(); });
    $("fPaciente").addEventListener("input", function(e){
      f.nombreNuevoPaciente = e.target.value; f.pacienteId = null; ui.comboAbierto = true;
      renderComboResults(); renderTelefonoWrap(); actualizarBotonGuardarTurno();
    });
    $("fDoctor").addEventListener("change", function(e){ f.doctorId = e.target.value; actualizarBotonGuardarTurno(); });
    $("fFecha").addEventListener("change", function(e){ f.fecha = e.target.value; });
    $("fHora").addEventListener("change", function(e){ f.hora = e.target.value; });
    $("fDuracion").addEventListener("input", function(e){ f.duracion = e.target.value; });
    $("fNotas").addEventListener("input", function(e){ f.notas = e.target.value; });
    $("fCompartir").addEventListener("change", function(e){ f.compartirAhora = e.target.checked; });
    $("btnGuardarTurno").addEventListener("click", function(){ if(!$("btnGuardarTurno").disabled) abrirDialogoConfirmarTurno(); });
  }

  function renderComboResults(){
    var wrap = $("comboResultsWrap");
    if(!wrap) return;
    var f = ui.formTurno;
    var pacienteSel = f.pacienteId ? pacientePor(f.pacienteId) : null;
    var coincidencias = (!pacienteSel && ui.comboAbierto && f.nombreNuevoPaciente.trim())
      ? datos.pacientes.filter(function(p){ return p.nombre.toLowerCase().indexOf(f.nombreNuevoPaciente.toLowerCase()) !== -1; })
      : [];
    if(coincidencias.length === 0){ wrap.innerHTML = ""; return; }
    wrap.innerHTML = '<div class="combo-results">' + coincidencias.map(function(p){
      return '<button type="button" data-action="elegir-paciente" data-id="' + p.id + '">' + escHtml(p.nombre) + '</button>';
    }).join("") + '</div>';
  }

  function renderTelefonoWrap(){
    var wrap = $("telefonoWrap");
    if(!wrap) return;
    var f = ui.formTurno;
    var pacienteSel = f.pacienteId ? pacientePor(f.pacienteId) : null;
    if(pacienteSel || !f.nombreNuevoPaciente.trim()){ wrap.innerHTML = ""; return; }
    wrap.innerHTML = '<div class="field"><label>Teléfono</label>' +
      '<input id="fTelefono" placeholder="+54 9 11 5555-1234" value="' + escAttr(f.telefonoNuevoPaciente) + '"></div>' +
      '<p class="hint">Incluí código de país y de área — se usa para el link de WhatsApp.</p>';
    $("fTelefono").addEventListener("input", function(e){ f.telefonoNuevoPaciente = e.target.value; actualizarBotonGuardarTurno(); });
  }

  function actualizarBotonGuardarTurno(){
    var btn = $("btnGuardarTurno");
    if(!btn) return;
    var f = ui.formTurno;
    var pacienteSel = f.pacienteId ? pacientePor(f.pacienteId) : null;
    btn.disabled = !(f.doctorId !== "" && (pacienteSel || f.nombreNuevoPaciente.trim()));
  }

  function abrirDialogoConfirmarTurno(){
    var f = ui.formTurno;
    var pacienteSel = f.pacienteId ? pacientePor(f.pacienteId) : null;
    var doctor = doctorPor(Number(f.doctorId));
    var nombreMostrado = pacienteSel ? pacienteSel.nombre : f.nombreNuevoPaciente;
    var fechaObj = new Date(f.fecha + "T" + f.hora + ":00");

    abrirDialogo(
      '<h3>Confirmar turno</h3>' +
      '<p style="color:var(--ink); font-weight:600;">' + escHtml(nombreMostrado) + '</p>' +
      '<p>Dr. ' + escHtml(doctor.nombre) + ' · ' + (f.duracion||30) + ' min</p>' +
      '<p>' + fechaCorta(fechaObj) + ', ' + horaCorta(fechaObj) + '</p>' +
      '<div class="dialog-actions">' +
        '<span></span>' +
        '<div class="actions-right">' +
          '<button class="dlg-btn dlg-btn-text" data-action="cerrar-dialogo">Cancelar</button>' +
          '<button class="dlg-btn dlg-btn-primary" data-action="confirmar-guardar-turno">Confirmar y guardar</button>' +
        '</div>' +
      '</div>'
    );
  }

  function guardarTurnoDesdeForm(){
    var f = ui.formTurno;
    var doctor = doctorPor(Number(f.doctorId));
    var fechaObj = new Date(f.fecha + "T" + f.hora + ":00");

    function crear(pacienteId){
      var turno = {
        id: datos.nextIds.turno++, pacienteId: pacienteId, doctorId: doctor.id, doctorNombre: doctor.nombre,
        inicio: fechaObj.toISOString(), duracion: Number(f.duracion) || 30,
        notas: f.notas.trim() || "", estado: "AGENDADO", recordatorioEnviado: false
      };
      datos.turnos.push(turno);
      guardar();
      var pacienteCreado = pacientePor(pacienteId);
      ui.diaSeleccionado = new Date(fechaObj.getFullYear(), fechaObj.getMonth(), fechaObj.getDate());
      ui.vistaAgenda = "dia";
      ui.formTurno = null;
      cerrarDialogo();
      irA("agenda");
      if(f.compartirAhora && pacienteCreado){
        compartirTurnoComoIcs(turno, pacienteCreado);
      } else {
        toast("Turno guardado");
      }
    }

    if(f.pacienteId){
      crear(f.pacienteId);
    } else {
      var nuevo = {id: datos.nextIds.paciente++, nombre: f.nombreNuevoPaciente.trim(), telefono: f.telefonoNuevoPaciente.trim(), email:"", notas:""};
      datos.pacientes.push(nuevo);
      guardar();
      crear(nuevo.id);
    }
  }

  // ---- Turno: detalle ----
  function renderDetalleTurno(body){
    var turno = turnoPor(ui.pantalla.turnoId);
    if(!turno){ pop(); return; }
    var p = pacientePor(turno.pacienteId);
    var i = new Date(turno.inicio);
    var est = ESTILO_ESTADO[turno.estado];

    var html = '<div class="card-info">' +
      '<div class="n">' + escHtml(p ? p.nombre : "Paciente") + '</div>' +
      '<div class="s">Dr. ' + escHtml(turno.doctorNombre) + '</div>' +
      '<div class="s">' + cap(fechaLarga(i)) + ', ' + horaCorta(i) + ' hs</div>' +
    '</div>' +
    '<p class="estado-line">Estado: <span style="color:' + est.color + '; font-weight:600;">' + est.texto + '</span></p>';
    if(turno.notas) html += '<p class="estado-line">' + escHtml(turno.notas) + '</p>';

    // Los dos botones muestran el estado actual: el activo queda pintado.
    // Volver a tocar el que ya está activo deja el turno "Sin confirmar".
    html += '<div class="btn-row" style="margin-top:18px;">' +
      '<button class="btn btn-estado ' + (turno.estado === "CONFIRMADO" ? "on-confirmado" : "") + '" data-action="marcar-confirmado">' +
        (turno.estado === "CONFIRMADO" ? "✓ " : "") + 'Confirmado</button>' +
      '<button class="btn btn-estado ' + (turno.estado === "CANCELADO" ? "on-cancelado" : "") + '" data-action="marcar-cancelado">' +
        (turno.estado === "CANCELADO" ? "✓ " : "") + 'Cancelado</button>' +
    '</div>';

    if(p){
      html += '<a class="btn btn-outline" href="' + waHref(p.telefono, mensajeConfirmacion(turno, turno.doctorNombre)) + '" target="_blank" rel="noopener" data-action="pedir-confirmacion">Pedir confirmación por WhatsApp</a>';
    }
    html += '<button class="btn btn-outline" data-action="compartir-ics">Compartir turno (.ics)</button>';
    html += '<button class="btn btn-outline" data-action="reagendar">Reagendar</button>';
    html += '<button class="btn btn-danger-text" data-action="borrar-turno">Borrar turno</button>';

    body.innerHTML = html;
  }

  // ---- Turno: reagendar ----
  function renderFormReagendar(body){
    var turno = turnoPor(ui.pantalla.turnoId);
    if(!turno){ pop(); return; }
    var p = pacientePor(turno.pacienteId);
    var i = new Date(turno.inicio);
    if(!ui.formTurno || ui.formTurno._reagendarId !== turno.id){
      ui.formTurno = {_reagendarId: turno.id, fecha: isoFecha(i), hora: isoHora(i)};
    }
    var f = ui.formTurno;

    var html = '<div class="card-info">' +
      '<div class="n">' + escHtml(p ? p.nombre : "Paciente") + '</div>' +
      '<div class="s">Dr. ' + escHtml(turno.doctorNombre) + '</div>' +
      '<div class="s">Turno original: ' + fechaCorta(i) + ', ' + horaCorta(i) + '</div>' +
    '</div>';
    html += '<div class="row-fields">' +
      '<div class="field"><label>Fecha</label><input type="date" id="rFecha" value="' + f.fecha + '"></div>' +
      '<div class="field"><label>Hora</label><input type="time" id="rHora" value="' + f.hora + '"></div>' +
    '</div>';
    html += '<button class="btn btn-primary" id="btnConfirmarReagendo">Confirmar reagendo</button>';
    body.innerHTML = html;

    $("rFecha").addEventListener("change", function(e){ f.fecha = e.target.value; });
    $("rHora").addEventListener("change", function(e){ f.hora = e.target.value; });
    $("btnConfirmarReagendo").addEventListener("click", function(){
      var nueva = new Date(f.fecha + "T" + f.hora + ":00");
      turno.inicio = nueva.toISOString();
      turno.estado = "AGENDADO";
      turno.recordatorioEnviado = false;
      guardar();
      toast("Turno reagendado");
      ui.diaSeleccionado = new Date(nueva.getFullYear(), nueva.getMonth(), nueva.getDate());
      ui.formTurno = null;
      irA("agenda");
    });
  }

  // ---- Pacientes ----
  function renderPacientes(body){
    var filtro = ui.busquedaPacientes.trim().toLowerCase();
    var filtrados = datos.pacientes
      .filter(function(p){ return p.nombre.toLowerCase().indexOf(filtro) !== -1; })
      .sort(function(a,b){ return a.nombre.localeCompare(b.nombre, "es"); });

    if(filtrados.length === 0){
      body.innerHTML = '<div class="empty">' + (datos.pacientes.length===0 ? "Todavía no cargaste ningún paciente." : "No se encontraron pacientes.") + '</div>';
      return;
    }
    var html = '<div class="list">';
    filtrados.forEach(function(p){
      html += '<button class="row" data-action="editar-paciente" data-id="' + p.id + '">' +
        '<span class="avatar">' + iniciales(p.nombre) + '</span>' +
        '<span class="meta"><div class="name">' + escHtml(p.nombre) + '</div><div class="sub">' + escHtml(p.telefono) + '</div></span>' +
      '</button><div class="divider"></div>';
    });
    html += '</div>';
    body.innerHTML = html;
  }

  // ---- Doctores ----
  function renderDoctores(body){
    if(datos.doctores.length === 0){
      body.innerHTML = '<div class="empty">Todavía no cargaste ningún doctor.</div>';
      return;
    }
    var html = '<div class="list">';
    datos.doctores.forEach(function(d){
      html += '<div class="row" style="cursor:default;">' +
        '<span class="meta"><div class="name">' + escHtml(d.nombre) + '</div></span>' +
        '<button class="icon-btn" data-action="borrar-doctor" data-id="' + d.id + '">' + ICONS.trash + '</button>' +
      '</div><div class="divider"></div>';
    });
    html += '</div>';
    body.innerHTML = html;
  }

  // ---- Diálogos genéricos ----
  function abrirDialogo(innerHtml){
    $("dialogInner").innerHTML = innerHtml;
    $("scrim").hidden = false;
  }
  function cerrarDialogo(){ $("scrim").hidden = true; $("dialogInner").innerHTML = ""; }

  function abrirDialogoPaciente(paciente){
    ui.editandoPaciente = paciente || null;
    var p = paciente;
    abrirDialogo(
      '<h3>' + (p ? "Editar paciente" : "Nuevo paciente") + '</h3>' +
      '<div class="field"><label>Nombre y apellido</label><input id="dNombre" value="' + escAttr(p?p.nombre:"") + '"></div>' +
      '<div class="field"><label>Teléfono</label><input id="dTelefono" placeholder="+54 9 11 5555-1234" value="' + escAttr(p?p.telefono:"") + '"></div>' +
      '<p class="hint" style="margin:-8px 0 10px;">Incluí código de país y de área.</p>' +
      '<div class="field"><label>Email (opcional)</label><input id="dEmail" value="' + escAttr(p?p.email:"") + '"></div>' +
      '<div class="field"><label>Notas (opcional)</label><textarea id="dNotas">' + escHtml(p?p.notas:"") + '</textarea></div>' +
      '<div class="dialog-actions">' +
        (p ? '<button class="dlg-btn dlg-btn-danger" data-action="borrar-paciente-confirmado">Borrar paciente</button>' : '<span></span>') +
        '<div class="actions-right">' +
          '<button class="dlg-btn dlg-btn-text" data-action="cerrar-dialogo">Cancelar</button>' +
          '<button class="dlg-btn dlg-btn-primary" id="btnGuardarPaciente">Guardar</button>' +
        '</div>' +
      '</div>'
    );
    function validar(){
      $("btnGuardarPaciente").disabled = !($("dNombre").value.trim() && $("dTelefono").value.trim());
    }
    ["dNombre","dTelefono"].forEach(function(id){ $(id).addEventListener("input", validar); });
    validar();
    $("btnGuardarPaciente").addEventListener("click", function(){
      var nombre = $("dNombre").value.trim(), telefono = $("dTelefono").value.trim();
      if(!nombre || !telefono) return;
      var email = $("dEmail").value.trim(), notas = $("dNotas").value.trim();
      if(p){
        p.nombre=nombre; p.telefono=telefono; p.email=email; p.notas=notas;
        toast("Paciente actualizado");
      } else {
        datos.pacientes.push({id:datos.nextIds.paciente++, nombre:nombre, telefono:telefono, email:email, notas:notas});
        toast("Paciente guardado");
      }
      guardar(); cerrarDialogo(); renderBody();
    });
  }

  function abrirDialogoDoctor(){
    abrirDialogo(
      '<h3>Nuevo doctor/a</h3>' +
      '<div class="field"><label>Nombre</label><input id="dNombreDoctor" placeholder="Ej: Paula Gómez"></div>' +
      '<div class="dialog-actions"><span></span><div class="actions-right">' +
        '<button class="dlg-btn dlg-btn-text" data-action="cerrar-dialogo">Cancelar</button>' +
        '<button class="dlg-btn dlg-btn-primary" id="btnGuardarDoctor" disabled>Guardar</button>' +
      '</div></div>'
    );
    $("dNombreDoctor").addEventListener("input", function(e){ $("btnGuardarDoctor").disabled = !e.target.value.trim(); });
    $("btnGuardarDoctor").addEventListener("click", function(){
      var nombre = $("dNombreDoctor").value.trim();
      if(!nombre) return;
      datos.doctores.push({id:datos.nextIds.doctor++, nombre:nombre});
      guardar(); toast("Doctor guardado"); cerrarDialogo(); renderBody();
    });
  }

  function abrirDialogoRecordatorio(turno, paciente){
    var i = new Date(turno.inicio);
    abrirDialogo(
      '<h3>Pedir confirmación a ' + escHtml(paciente.nombre) + '</h3>' +
      '<p>Turno el ' + fechaLarga(i) + ' a las ' + horaCorta(i) + ' hs, con Dr. ' + escHtml(turno.doctorNombre) + '.</p>' +
      '<a class="btn btn-primary" style="margin-top:14px;" href="' + waHref(paciente.telefono, mensajeConfirmacion(turno, turno.doctorNombre)) + '" target="_blank" rel="noopener" data-action="enviar-recordatorio" data-id="' + turno.id + '">Abrir WhatsApp y enviar</a>' +
      '<button class="btn btn-text" data-action="posponer-recordatorio" data-id="' + turno.id + '">Ahora no</button>'
    );
  }

  function abrirDialogoBorrarTurno(turnoId){
    var turno = turnoPor(turnoId);
    if(!turno) return;
    var p = pacientePor(turno.pacienteId);
    var i = new Date(turno.inicio);
    abrirDialogo(
      '<h3>¿Borrar el turno?</h3>' +
      '<p style="color:var(--ink); font-weight:600;">' + escHtml(p ? p.nombre : "Paciente") + '</p>' +
      '<p>' + cap(fechaLarga(i)) + ', ' + horaCorta(i) + ' hs · Dr. ' + escHtml(turno.doctorNombre) + '</p>' +
      '<p>Se borra del todo. Si solo querés marcarlo como cancelado, abrí el turno y tocá "Cancelado".</p>' +
      '<div class="dialog-actions"><span></span><div class="actions-right">' +
        '<button class="dlg-btn dlg-btn-text" data-action="cerrar-dialogo">Cancelar</button>' +
        '<button class="dlg-btn dlg-btn-danger" data-action="borrar-turno-confirmado" data-id="' + turno.id + '">Borrar</button>' +
      '</div></div>'
    );
  }

  // ---- Mantener apretado un turno para borrarlo ----
  var longPress = { timer:null, disparado:false, x:0, y:0 };
  function cancelarLongPress(){ clearTimeout(longPress.timer); longPress.timer = null; }

  document.addEventListener("pointerdown", function(e){
    var card = e.target.closest('[data-action="abrir-turno"]');
    if(!card) return;
    longPress.disparado = false;
    longPress.x = e.clientX; longPress.y = e.clientY;
    var turnoId = Number(card.getAttribute("data-id"));
    cancelarLongPress();
    longPress.timer = setTimeout(function(){
      longPress.disparado = true;
      abrirDialogoBorrarTurno(turnoId);
    }, 550);
  });
  document.addEventListener("pointermove", function(e){
    if(!longPress.timer) return;
    // Si el dedo se movió, es scroll y no "mantener apretado".
    if(Math.abs(e.clientX - longPress.x) > 10 || Math.abs(e.clientY - longPress.y) > 10) cancelarLongPress();
  });
  document.addEventListener("pointerup", cancelarLongPress);
  document.addEventListener("pointercancel", cancelarLongPress);
  document.addEventListener("contextmenu", function(e){
    // El menú nativo del navegador taparía el diálogo de borrar en el celular.
    if(e.target.closest('[data-action="abrir-turno"]')) e.preventDefault();
  });

  // ---- Delegación de eventos ----
  document.addEventListener("click", function(e){
    var el = e.target.closest("[data-action]");
    if(!el) return;
    var action = el.getAttribute("data-action");
    var id = el.getAttribute("data-id");

    switch(action){
      case "back": ui.formTurno = null; pop(); break;
      case "tab": irA(el.getAttribute("data-tab")); break;
      case "vista-dia": ui.vistaAgenda = "dia"; render(); break;
      case "vista-mes": ui.vistaAgenda = "mes"; render(); break;
      case "dia-prev": ui.diaSeleccionado = new Date(ui.diaSeleccionado.getTime() - 86400000); renderBody(); break;
      case "dia-next": ui.diaSeleccionado = new Date(ui.diaSeleccionado.getTime() + 86400000); renderBody(); break;
      case "elegir-dia":
        ui.diaSeleccionado = new Date(ui.diaSeleccionado.getFullYear(), ui.diaSeleccionado.getMonth(), Number(el.getAttribute("data-dia")));
        ui.vistaAgenda = "dia"; render(); break;
      case "abrir-turno":
        // Si venimos de un "mantener apretado" (que abre el borrar), no abrimos el detalle.
        if(longPress.disparado){ longPress.disparado = false; break; }
        push({tipo:"detalle", turnoId:Number(id)});
        break;
      case "ir-nuevo-turno": ui.formTurno = estadoFormTurnoInicial(); ui.comboAbierto = false; push({tipo:"nuevo"}); break;
      case "elegir-paciente":
        ui.formTurno.pacienteId = Number(id); ui.formTurno.nombreNuevoPaciente = ""; ui.comboAbierto = false; renderBody(); break;
      case "confirmar-guardar-turno": guardarTurnoDesdeForm(); break;
      case "marcar-confirmado": {
        var t1 = turnoPor(ui.pantalla.turnoId);
        var yaConfirmado = t1.estado === "CONFIRMADO";
        t1.estado = yaConfirmado ? "AGENDADO" : "CONFIRMADO";
        guardar(); toast(yaConfirmado ? "Turno sin confirmar" : "Turno confirmado"); renderBody(); break;
      }
      case "marcar-cancelado": {
        var t2 = turnoPor(ui.pantalla.turnoId);
        var yaCancelado = t2.estado === "CANCELADO";
        t2.estado = yaCancelado ? "AGENDADO" : "CANCELADO";
        guardar(); toast(yaCancelado ? "Turno reactivado" : "Turno cancelado"); renderBody(); break;
      }
      case "pedir-confirmacion": {
        var t3 = turnoPor(ui.pantalla.turnoId); t3.recordatorioEnviado = true; guardar(); toast("Se abrió WhatsApp"); break;
      }
      case "compartir-ics": {
        var t6 = turnoPor(ui.pantalla.turnoId);
        var p6 = t6 ? pacientePor(t6.pacienteId) : null;
        if(t6 && p6) compartirTurnoComoIcs(t6, p6);
        break;
      }
      case "reagendar": ui.formTurno = null; push({tipo:"reagendar", turnoId: ui.pantalla.turnoId}); break;
      case "borrar-turno": abrirDialogoBorrarTurno(ui.pantalla.turnoId); break;
      case "borrar-turno-confirmado": {
        var idBorrar = Number(id);
        datos.turnos = datos.turnos.filter(function(t){ return t.id !== idBorrar; });
        guardar(); cerrarDialogo(); toast("Turno borrado");
        if(ui.pantalla && ui.pantalla.turnoId === idBorrar) irA("agenda"); else renderBody();
        break;
      }
      case "nuevo-paciente": abrirDialogoPaciente(null); break;
      case "editar-paciente": abrirDialogoPaciente(pacientePor(Number(id))); break;
      case "borrar-paciente-confirmado": {
        var pid = ui.editandoPaciente.id;
        datos.pacientes = datos.pacientes.filter(function(p){ return p.id !== pid; });
        guardar(); toast("Paciente borrado"); cerrarDialogo(); renderBody(); break;
      }
      case "nuevo-doctor": abrirDialogoDoctor(); break;
      case "borrar-doctor": {
        datos.doctores = datos.doctores.filter(function(d){ return d.id !== Number(id); });
        guardar(); toast("Doctor eliminado"); renderBody(); break;
      }
      case "abrir-recordatorios": mostrarRecordatorioSiHay(true); break;
      case "enviar-recordatorio": {
        var t5 = turnoPor(Number(id));
        if(t5){ t5.recordatorioEnviado = true; guardar(); }
        toast("Se abrió WhatsApp");
        mostrarRecordatorioSiHay();
        break;
      }
      case "posponer-recordatorio":
        ui.recordatoriosVistos.push(Number(id));
        mostrarRecordatorioSiHay();
        break;
      case "cerrar-dialogo": cerrarDialogo(); break;
    }
  });

  document.addEventListener("keydown", function(e){
    if(e.key === "Escape" && !$("scrim").hidden) cerrarDialogo();
  });
  $("scrim").addEventListener("click", function(e){ if(e.target === $("scrim")) cerrarDialogo(); });

  render();

  if("serviceWorker" in navigator){
    window.addEventListener("load", function(){
      navigator.serviceWorker.register("sw.js").catch(function(){});
    });
  }
})();

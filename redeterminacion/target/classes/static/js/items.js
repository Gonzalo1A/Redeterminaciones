$(document).ready(function () {
    $(".edit-button").click(function () {
        var inputField = $(this).closest(".input-group").find(".avance-input");
        inputField.prop("disabled", !inputField.prop("disabled"));
        if (!inputField.prop("disabled")) {
            inputField.focus();
        }
    });
});

function obtenerDatosInputs() {
    var listaInputs = []; // Lista para almacenar los datos de los inputs
    // Selecciona todos los inputs con la clase 'avance-input'
    var inputs = document.querySelectorAll('.avance-input');
    var mesActual = document.getElementById('mes-actual').textContent.trim();

    inputs.forEach(function (input) {
        var id = input.getAttribute('id');
        var valor = input.value; // Obtén el valor del input        
        if (valor !== '') {
            console.log('ID:', id, 'Valor:', valor, 'Mes:', mesActual);
            listaInputs.push({itemId: id, valor: valor, fecha: mesActual});
        }
    });

    return listaInputs; // Devuelve la lista de datos
}
function guardarDatos() {
    var listaDatos = obtenerDatosInputs(); // Obtiene la lista de datos de los inputs

    // Realiza una solicitud AJAX al backend para enviar la lista de datos y el nombre de la obra
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/item/avance_carga", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Hacer algo después de que se ha completado la solicitud
            console.log('Respuesta del servidor:', xhr.responseText);
        }
    };
    var data = JSON.stringify({valorMes: listaDatos});
    console.log('Data:', data);
    xhr.send(data);

}


/*
 function obtenerDatosInputs() {
 var listaInputs = []; // Lista para almacenar los datos de los inputs
 // Selecciona todos los inputs con la clase 'avance-input'
 var inputs = document.querySelectorAll('.avance-input');
 var mesActual = document.getElementById('mes-actual').textContent.trim();
 inputs.forEach(function (input) {
 var id = input.getAttribute('id');
 var valor = input.value; // Obtén el valor del input
 listaInputs.push({id: id, valor: valor, fecha: mesActual}); // Agrega el ID y el valor a la lista
 });
 
 return listaInputs; // Devuelve la lista de datos
 }
 function guardarDatos() {
 var listaDatos = obtenerDatosInputs(); // Obtiene la lista de datos de los inputs
 
 // Realiza una solicitud AJAX al backend para enviar la lista de datos y el nombre de la obra
 var xhr = new XMLHttpRequest();
 xhr.open("POST", "/item/avance_carga", true);
 xhr.setRequestHeader("Content-Type", "application/json");
 xhr.onreadystatechange = function () {
 if (xhr.readyState === 4 && xhr.status === 200) {
 // Hacer algo después de que se ha completado la solicitud
 }
 };
 var data = JSON.stringify({listaDatos: listaDatos});
 xhr.send(data);
 }*/
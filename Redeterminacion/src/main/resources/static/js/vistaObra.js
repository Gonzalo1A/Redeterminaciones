$(document).ready(function () {
    $(".edit-button").click(function () {
        var inputField = $(this).closest("tr").find(".factor-input");
        inputField.prop("disabled", !inputField.prop("disabled"));
        inputField.focus();
    });
});

function obtenerDatosInputs() {
    var listaInputs = []; // Lista para almacenar los datos de los inputs
    // Selecciona todos los inputs con la clase 'factor-input'
    var inputs = document.querySelectorAll('.factor-input');
    
    inputs.forEach(function(input) {
        var id = input.getAttribute('id'); // Obtén el ID del atributo th:data-name
        var valor = input.value; // Obtén el valor del input
        listaInputs.push({itemId: id, valor: valor}); // Agrega el ID y el valor a la lista
    });

    return listaInputs; // Devuelve la lista de datos
}
function guardarDatos() {
    var listaDatos = obtenerDatosInputs(); // Obtiene la lista de datos de los inputs
    var nombreObra = document.getElementById('guardarDatosBtn').getAttribute('data-name'); // Obtén el nombre de la obra
    
    // Realiza una solicitud AJAX al backend para enviar la lista de datos y el nombre de la obra
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/item/cargarIncidencia", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            // Hacer algo después de que se ha completado la solicitud
        }
    };
    var data = JSON.stringify({nombreObra: nombreObra, listaDatos: listaDatos});
    xhr.send(data);
}

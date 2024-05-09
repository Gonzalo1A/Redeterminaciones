$(document).ready(function () {
    $(".edit-button").click(function () {
        var inputField = $(this).closest("tr").find(".factor-input");
        inputField.prop("disabled", !inputField.prop("disabled"));
        inputField.focus();
    });
});

function saveFactor(element) {
    var itemId = $(element).attr('id'); // Obtiene el ID del elemento
    var factorValue = $(element).val(); // Obtiene el valor del campo de entrada

    // Envía los datos al controlador mediante AJAX
    $.ajax({
        url: '/obra/cargarIncidendcia', // Ruta del controlador
        type: 'POST', // Método HTTP
        data: {
            idItem: itemId,
            incidenciaFactor: factorValue
        }, // Datos a enviar
        success: function (response) {
            // Maneja la respuesta del servidor
            console.log('Valor de incidencia de factor guardado exitosamente');
        },
        error: function (xhr, status, error) {
            // Maneja los errores
            console.error('Error al guardar el valor de incidencia de factor:', error);
        }
    });
}

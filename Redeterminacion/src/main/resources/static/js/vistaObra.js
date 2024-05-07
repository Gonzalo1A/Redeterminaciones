
document.addEventListener('DOMContentLoaded', function () {
        // Obtener el botón que abre el modal
        var modalButton = document.querySelector('[data-bs-target="#modalIncidenciaFactor"]');

        // Agregar un evento click al botón
        modalButton.addEventListener('click', function (event) {
            // Obtener el valor de item.id
            var itemId = event.target.dataset.itemId;
            console.log('Valor de item.id:', itemId);
            // Asignar el valor de item.id al campo oculto en el formulario
            document.getElementById('itemId').value = itemId;
        });
    });
    
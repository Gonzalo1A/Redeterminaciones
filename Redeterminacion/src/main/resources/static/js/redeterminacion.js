function mostrarTabla() {
    var nombreObra = document.getElementById('redeterminar').getAttribute('data-name');
    const url = `/calculo/resumen/` + nombreObra;
    fetch(url)
            .then(response => {
            if (!response.ok) {
                throw new Error("Error al obtener los datos del servidor");
            }
            return response.json();
        })
        .then(data => {
            const cuerpoTabla = document.getElementById("cuerpoTabla");
            if (!cuerpoTabla) {
                throw new Error("El elemento con id 'cuerpoTabla' no existe.");
            }
            
            cuerpoTabla.innerHTML = ''; // Limpiar el contenido anterior

            data.items.forEach((item, index) => {
                const fila = document.createElement("tr");

                fila.innerHTML = `
                    <td scope="row">${item.numeroItem}</td>
                    <td>${item.descripcion}</td>
                    <td>${item.unidad}</td>
                    <td>${item.precioUnitario}</td>
                    <td>${item.subTotal}</td>
                    <td>${data.factoresRedet[index]}</td>
                    <td>${data.nuevosUnitarios[index]}</td>
                    <td>${data.remanenteTeorico[index]}</td>
                    <td>${data.remanenteReal[index]}</td>
                    <td>${data.minimo[index]}</td>
                    <td>${data.incrementosSubtotal[index]}</td>
                    <td>${data.incrementosSubtotal[index] + item.subTotal}</td>
                `;

                cuerpoTabla.appendChild(fila); // Añadir la fila al cuerpo de la tabla
            });

            document.getElementById("tablaResumen").style.display = "table"; // Mostrar la tabla
        })
        .catch(error => {
            console.error("Error al cargar la tabla:", error);
            alert("Ocurrió un error al cargar la tabla. Por favor, intenta de nuevo.");
        });
}
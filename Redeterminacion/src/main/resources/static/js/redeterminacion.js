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
                    const precioUnitario = !item.rubro ? item.precioUnitario : '';
                    const subTotal = !item.rubro ? item.subTotal : '';
                    const factoresRedet = !item.rubro ? data.factoresRedet[index] : '';
                    const nuevosUnitarios = !item.rubro ? data.nuevosUnitarios[index] : '';
                    const remanenteTeorico = !item.rubro ? data.remanenteTeorico[index] : '';
                    const remanenteReal = !item.rubro ? data.remanenteReal[index] : '';
                    const minimo = !item.rubro ? data.minimo[index] : '';
                    const incrementosSubtotal = !item.rubro ? data.incrementosSubtotal[index] : '';
                    const nuevoSubtotal = !item.rubro ? data.incrementosSubtotal[index] + item.subTotal : '';
                    fila.innerHTML = `
                    <td scope="row">${item.numeroItem}</td>
                    <td>${item.descripcion}</td>
                    <td>${item.unidad}</td>
                    <td>${precioUnitario}</td>
                    <td>${subTotal}</td>
                    <td>${factoresRedet}</td>
                    <td>${nuevosUnitarios}</td>
                    <td>${remanenteTeorico}</td>
                    <td>${remanenteReal}</td>
                    <td>${minimo}</td>
                    <td>${incrementosSubtotal}</td>
                    <td>${nuevoSubtotal}</td>
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
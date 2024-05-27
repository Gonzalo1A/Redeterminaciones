let fechaActual = new Date();

function cargar() {
    generarCalendario();
}

function generarCalendario() {
    const calendarBody = document.getElementById('calendar-body');
    calendarBody.innerHTML = '';

    const primerDiaMes = new Date(fechaActual.getFullYear(), fechaActual.getMonth(), 1);
    const primerDiaSemana = primerDiaMes.getDay();
    const ultimoDiaMes = new Date(fechaActual.getFullYear(), fechaActual.getMonth() + 1, 0);

    let fecha = new Date(primerDiaMes);
    fecha.setDate(1);


    while (fecha.getDay() !== 0) {
        fecha.setDate(fecha.getDate() - 1);
    }

    let diasEnFila = 0;

    while (fecha <= ultimoDiaMes) {
        const fila = document.createElement('tr');

        for (let i = 0; i < 7; i++) {
            const celda = document.createElement('td');
            celda.textContent = fecha.getDate();

            // Verificar si la fecha está disponible o no
            fila.appendChild(celda);

            fecha.setDate(fecha.getDate() + 1);
            diasEnFila++;

            if (diasEnFila === 7) {
                diasEnFila = 0;
                break;
            }
        }
        calendarBody.appendChild(fila);
    }

    document.getElementById('mes-actual').textContent = `${obtenerNombreMes(primerDiaMes.getMonth())} ${primerDiaMes.getFullYear()}`;
}

function cambiarMes(delta) {
    fechaActual.setMonth(fechaActual.getMonth() + delta);
    generarCalendario(fechasDisponibles);
}

function obtenerNombreMes(numeroMes) {
    const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
    return meses[numeroMes];
}
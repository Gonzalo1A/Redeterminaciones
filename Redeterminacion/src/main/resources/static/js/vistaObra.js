const myModal = document.getElementById('myModal')
const myInput = document.getElementById('myInput')

myModal.addEventListener('shown.bs.modal', () => {
  myInput.focus()
})

document.getElementById("addInput").addEventListener("click", function() {
            // Clone the first input group
            const originalInputGroup = document.querySelector(".input-group.mb-3");
            const newInputGroup = originalInputGroup.cloneNode(true);

            // Clear input values
            const inputs = newInputGroup.querySelectorAll("input");
            inputs.forEach(input => {
                input.value = "";
            });

            // Append the cloned input group
            document.querySelector(".container").appendChild(newInputGroup);
        });
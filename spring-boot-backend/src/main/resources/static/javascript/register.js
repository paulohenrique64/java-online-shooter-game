const form = document.querySelector("form");

const myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");

form.addEventListener("submit", (event) => {
    event.preventDefault();

    if (form.elements["password"].value !== form.elements["password-repeat"].value)
        alert("mismatched passwords");
    else 
        register({
            name: form.elements["name"].value,
            password: form.elements["password"].value
        });
});

function register(userdata) {
    console.log(JSON.stringify(userdata));

    const options = {
        method: "POST",
        body: JSON.stringify(userdata),
        headers: myHeaders,
    };

    fetch("/auth/register", options)
        .then((response) => {
            console.log(response.status);
            if (response.status === 200)
                alert("registered with success");
        })
        .catch((error) => {
            alert(error);
        });
}
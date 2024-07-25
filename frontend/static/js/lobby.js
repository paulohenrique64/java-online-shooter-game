var stompClient = null;

var userdata = null;
var selfUserData = null;

// connecting to web sockets server routes
function connect() {
    stompClient = Stomp.client('ws://localhost:8080/socket');

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/log/online-players-list', function (response) {
            userdata = JSON.parse(response.body)

            updateOnlinePlayersList(userdata.sessions);
        });

        stompClient.send("/app/online-players-list", {}, JSON.stringify({}));
    });
}

// getting userdata with browser token
function getUserData() {
    const options = {
        credentials: 'include',
        method: "GET",
    };

    fetch("http://localhost:8080/userdata", options)
        .then(response => {
            if (response.status == 403)
                window.location.replace("/frontend/home.html"); 

            response.json()
                .then(responseJson => {                
                    console.log(responseJson.user);
                    selfUserData = responseJson.user;
                    updateUserData(responseJson.user);
                    connect();
                })
        })
        .catch(error => {
            console.log(error);
        })
}

function updateOnlinePlayersList(sessions) {
    let onlinePlayersList = document.querySelector('.online-players-list');

    onlinePlayersList.innerHTML = "";

    var size = Object.keys(sessions).length;
    
    for (i = 0; i < size; i++) {
        let li = document.createElement("li");
        li.innerText = "🟢 " + sessions[i].username;
        if (selfUserData.name === sessions[i].username) li.innerText += " (you)";
        onlinePlayersList.append(li);
    }
}

function updateUserData(userdata) {
    let li1 = document.createElement("li");
    li1.innerText = "name: " + userdata.name;

    document.querySelector('.user-data-list').append(li1);

    let li2 = document.createElement("li");
    li2.innerText = "kills: " +  userdata.kills;
    document.querySelector('.user-data-list').append(li2);

    let li4 = document.createElement("li");
    li4.innerText = "score: " +  userdata.score.toFixed(2);
    document.querySelector('.user-data-list').append(li4);
}

const logoutButton = document.querySelector(".logout-game-button");
logoutButton.addEventListener("click", function(obj) {
    const options = {
        credentials: 'include',
        method: "POST",
    };

    fetch("http://localhost:8080/logout", options)
        .then(response => {
            window.location.replace("/frontend/home.html"); 
        })
        .catch(error => {
            console.log(error);
        });
});

const startGameButton = document.querySelector(".start-game-button");
startGameButton.addEventListener("click", function(obj) {
    const options = {
        credentials: 'include',
        method: "GET",
    };

    fetch("http://localhost:8080/game/", options)
        .then(response => {
            console.log(response.status);
        })
        .catch(error => {
            console.log(error);
        });
});


getUserData();

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#messageInput');
var date = new Date()

var stompClient = null;
var username = document.querySelector("html").getAttribute("data-user");
var groupId = document.querySelector("html").getAttribute("data-groupId");

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];


function connect() {

    var socket = new SockJS('/ws');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}


function onConnected() {
    
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/group/'+groupId, onMessageReceived);

}


function onError(error) {
    console.error("------------ERROR-------------" + error)
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            author: username,
            content: messageInput.value,
            timestamp: date.toISOString(),
            groupId: groupId,
            type: "DIRECT"
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageArea = document.querySelector("#messages ul")
    messageArea.appendChild(createMessage(message))
    messageArea.scrollTop = messageArea.scrollHeight

}


function createMessage(message) {
    message.author = message.author || "null"
    message.timestamp = message.timestamp || "null"
    message.content = message.content || "null"

    var messageElement = document.createElement("li")
    messageElement.classList.add("message", "mb-3", "gap-2")

    var avatarElement = document.createElement("i")
        var avatarText = document.createTextNode(message.author[0])
        avatarElement.appendChild(avatarText)
        avatarElement.style.backgroundColor = message.author == "null" ? "#000" : getAvatarColor(message.author)
    messageElement.appendChild(avatarElement)

    var div = document.createElement("div")
        var hederDiv = document.createElement("div")
            hederDiv.classList.add("message-header", "gap-2")
            var authorDiv = document.createElement("div")
                authorDiv.classList.add("message-header-author", "fw-bold")
                var authorText = document.createTextNode(message.author)
                authorDiv.appendChild(authorText)
            hederDiv.appendChild(authorDiv)

            var timestampDiv = document.createElement("div")
                timestampDiv.classList.add("message-header-timestamp")
                var timestampText = document.createTextNode(message.timestamp)
                timestampDiv.appendChild(timestampText)
            hederDiv.appendChild(timestampDiv)
        div.appendChild(hederDiv)

        var contentDiv = document.createElement("div")
            contentDiv.classList.add("message-content")
            var contentSpan = document.createElement("span")
                contentDiv.classList.add("text-break")
                var contentText = document.createTextNode(message.content)
                contentSpan.appendChild(contentText)
            contentDiv.appendChild(contentSpan)
        div.appendChild(contentDiv)
    messageElement.appendChild(div)

    return messageElement
}


function getAvatarColor(messageAuthor) {
    var hash = 0;
    for (var i = 0; i < messageAuthor.length; i++) {
        hash = 31 * hash + messageAuthor.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

document.addEventListener("DOMContentLoaded", connect, true)
messageForm.addEventListener('submit', sendMessage, true)
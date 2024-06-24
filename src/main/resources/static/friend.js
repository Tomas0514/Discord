var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#messageInput');
var messageList = document.querySelector("#messages ul")
var date = new Date()

var stompClient = null;
var username = document.querySelector("html").getAttribute("data-user");
var groupId = document.querySelector("html").getAttribute("data-groupId");

var fetchedMessages = Array()

const maxMessageLenght = 2048
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

async function onDocumentLoad() {
    fetchedMessages = await fetchMessages(50)
    displayMessages(fetchedMessages)

    connect()
}

function connect() {

    var socket = new SockJS('/ws');

    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}


function onConnected() {
    
    stompClient.subscribe('/topic/group/'+groupId, onMessageReceived);

}


function onError(error) {
    errorHandler("Error connecting with server: " + error)
    document.addEventListener("click", function() {
        location.reload()
    })
    
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            author: username,
            content: messageInput.value,
            groupId: groupId,
            type: "DIRECT",
            actionType: "SEND"
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


async function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.actionType == "SEND") {
        fetchedMessages = await fetchMessages(50) // Tohle chce změnit, nevim jak

        messageList.appendChild(buildMessage(message))
        messageList.scrollTop = messageList.scrollHeight
        
    } else if (message.actionType == "EDIT") {
        onEdit(message)
    } else if (message.actionType == "DELETE") {
        onDel(message)
    } else {
        errorHandler("Cannot process message: Invalid action type")
    }

}

async function fetchMessages(numberOfMessages) {
    try {
        const response = await fetch(`/api/group/${groupId}/getMessages/${numberOfMessages}`);
        const data = await response.json();
        return data;
    } catch (error) {
        errorHandler("Error fetching messages: " + error);
        throw error;
    }
}

function displayMessages(messages) {
    for (var message of messages) {
        messageElement = buildMessage(message)

        messageList.appendChild(messageElement)
    }
    
}

function buildMessage(message) {
    var author = message.author.username || message.author /*tohle se odstraní*/ || "null"
    message.timestamp = formatDate(message.timestamp) || "null"
    message.content = message.content || "null"

    var messageElement = document.createElement("li")
        messageElement.addEventListener("contextmenu", showContextMenu, true)
        messageElement.classList.add("message", "mb-3", "gap-2", "id_"+message.id)

    var avatarElement = document.createElement("i")
        var avatarText = document.createTextNode(author[0])
        avatarElement.appendChild(avatarText)
        avatarElement.style.backgroundColor = author == "null" ? "#000" : getAvatarColor(author)
    messageElement.appendChild(avatarElement)

    var div = document.createElement("div")
        div.style.width = "100%"
        var hederDiv = document.createElement("div")
            hederDiv.classList.add("message-header", "gap-2")
            var authorDiv = document.createElement("div")
                authorDiv.classList.add("message-header-author", "fw-bold")
                var authorText = document.createTextNode(author)
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


// -----------------------------------------------------------------------------
// ------------------------------Message Functions------------------------------
// -----------------------------------------------------------------------------

function showContextMenu(event) {
    event.preventDefault()

    var messageElement = event.target;
    while (!messageElement.classList.contains("message")) {
        messageElement = messageElement.parentNode
    }

    removeContextMenu()
    messageElement.classList.add("selected")

    var menu = buildContextMenu(messageElement)
    document.body.appendChild(menu)
    menu.style.top = event.y + "px"
    menu.style.left = event.x + "px"

}
document.addEventListener("click", function(event) {
    var editForm = document.querySelector(".editForm")
    var contextMenu = document.querySelector("#contextMenu")
    if ((!contextMenu || !contextMenu.contains(event.target)) && editForm && !editForm.contains(event.target)) {
        removeEdit()
    }

    removeContextMenu()
})

function edit(messageElement) {
    removeEdit()

    var id
    messageElement.classList.forEach(clazz => clazz.startsWith("id_") ? id = clazz.substring(3) : null)
    if (!id) { errorHandler("Cannot edit message: Id of selected element not found"); return }

    var message
    message = fetchedMessages.find(msg => msg.id == id)
    //fetchedMessages.forEach(msg => msg.id == id ? message = msg : null)
    if (!message) { errorHandler("Cannot edit message: Message not found in fetched messages"); return }
    if (message.author.username != username) { errorHandler("Cannot edit message: You are not the author of this message"); return }

    var messageContentElement = messageElement.childNodes.item(1).childNodes.item(1)

    var form = document.createElement("form")
        form.classList.add("editForm")
        var input = document.createElement("textarea")
            input.classList.add("form-control")
            input.name = "edit"
            input.value = messageContentElement.childNodes.item(0).innerHTML
            input.maxLength = maxMessageLenght
            input.rows = determineNumberOfLines(input)
            input.style.resize = "none"
        form.appendChild(input)
    messageContentElement.appendChild(form)

    messageContentElement.childNodes.item(0).classList.add("hidden")


    input.focus()
    input.addEventListener("keydown", function(event) {
        if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault()
            form.dispatchEvent(new Event("submit"))
        }
    })
    input.addEventListener("input", function() {
        input.rows = determineNumberOfLines(input)
    })
        
    form.addEventListener("submit", function(event) { sendEdit(event, message) }, true)
}

function onEdit(message) {
    var index = fetchedMessages.findIndex(item => item.id === message.id)
    if (index === -1) { errorHandler("Cannot edit message: Could not found any fetched messages with required id") }
    fetchedMessages[index].content = message.content

    var messageElement = document.querySelector(".id_" + message.id)
    if (!messageElement) { errorHandler("Cannot edit message: Could not found any messages with required id"); return }

    var spanElement = messageElement.querySelector("div div.message-content span")
    if (!spanElement) { errorHandler("Cannot edit message: Content element not found") }

    spanElement.innerHTML = message.content
    removeEdit()
    document.body.style.cursor = "auto"
}

function sendEdit(event, message) {
    event.preventDefault()
    var value = event.target.edit.value

    if (message.author.username != username) { errorHandler("Cannot edit message: You are not the author of this message"); return }
    if (value.length == 0) { warnHandler("Cannot edit message: Message cannot be empty"); return }
    if (value.length > maxMessageLenght) { warnHandler(`Cannot edit message: Length of message exceeds ${maxMessageLenght} characters`); return }
    if (value == message.content) { log("Current content is same as edited content. Will not send an edit request"); removeEdit(); return }

    if(stompClient) {
        var editMessage = {
            id: message.id,
            author: message.author.username,
            content: value,
            channelId: message.channel,
            type: "GUILD",
            actionType: "EDIT"
        };
        document.body.style.cursor = "progress"

        stompClient.send("/app/chat.editMessage", {}, JSON.stringify(editMessage));
    }
}

function del(messageElement) {
    var id
    messageElement.classList.forEach(clazz => clazz.startsWith("id_") ? id = clazz.substring(3) : null)
    if (!id) { errorHandler("Cannot delete message: Id of selected element not found"); return }

    var message
    fetchedMessages.forEach(msg => msg.id == id ? message = msg : null)
    if (!message) { errorHandler("Cannot delete message: Message not found in fetched messages"); return }
    if (message.author.username != username) { errorHandler("Cannot delete message: You are not the author of this message"); return }

    sendDel(message)
}

function onDel(message) {
    var index = fetchedMessages.findIndex(item => item.id === message.id)
    if (index === -1) { errorHandler("Cannot delete message: Could not found any fetched messages with required id") }
    fetchedMessages.splice(index, 1)

    var messageElement = document.querySelector(".id_" + message.id)
    if (!messageElement) { errorHandler("Cannot delete message: Could not found any messages with required id"); return }

    messageElement.remove()
    document.body.style.cursor = "auto"
}

function sendDel(message) {
    if (message.author.username != username) { errorHandler("Cannot delete message: You are not the author of this message"); return }

    if(stompClient) {
        var delMessage = {
            id: message.id,
            author: message.author.username,
            channelId: message.channel,
            type: "GUILD",
            actionType: "DELETE"
        };
        document.body.style.cursor = "progress"

        stompClient.send("/app/chat.deleteMessage", {}, JSON.stringify(delMessage));
    }
}

function copyMessageId(messageElement) {
    var id
    messageElement.classList.forEach(clazz => clazz.startsWith("id_") ? id = clazz.substring(3) : null)
    if (!id) { errorHandler("Cannot edit message: Id of selected element not found"); return }

    navigator.clipboard.writeText(id)
}

function copyText(messageElement) {
    navigator.clipboard.writeText(messageElement.querySelector("div div.message-content span").innerHTML)
}

function buildContextMenu(messageElement) {
    var menuElement = document.createElement("div")
        menuElement.id = "contextMenu"

    var groupElement = document.createElement("div")
        if (messageElement.querySelector("div div.message-header div.message-header-author").innerHTML == username) {
            groupElement.appendChild(buildSetting("Edit Message", function() { edit(messageElement) }, "edit"))
            var delElement = buildSetting("Delete Message", function() { del(messageElement) }, "del")
                delElement.classList.add("text-danger")
            groupElement.appendChild(delElement)
        }
        groupElement.appendChild(buildSetting("Copy Text", function() { copyText(messageElement) }, "copy"))
        groupElement.appendChild(buildSetting("Copy Message ID", function() { copyMessageId(messageElement) }, "id"))
    menuElement.appendChild(groupElement)

    return menuElement
}

function removeContextMenu() {
    document.querySelectorAll(".selected").forEach(ele => ele.classList.remove("selected"))
    document.querySelectorAll("#contextMenu").forEach(ele => ele.remove())
}

function removeEdit() {
    document.querySelectorAll(".editForm").forEach(form => {
        form.parentNode.querySelector("span.hidden").classList.remove("hidden")
        form.remove()
    })
}

function buildSetting(name, func, settingIconName) {
    var settingIcon = getSVG(settingIconName)

    var settingElement = document.createElement("div")
        settingElement.classList.add("setting")
        var settingText = document.createTextNode(name)
        settingElement.appendChild(settingText)
        settingElement.addEventListener("click", func, true)
        settingIcon ? settingElement.appendChild(settingIcon) : null
    return settingElement
}

function getSVG(name) {
    const svgNS = "http://www.w3.org/2000/svg"

    switch (name) {
        case "id":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "24")
                icon.setAttribute("height", "24")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M15.3 14.48c-.46.45-1.08.67-1.86.67h-1.39V9.2h1.39c.78 0 1.4.22 1.86.67.46.45.68 1.22.68 2.31 0 1.1-.22 1.86-.68 2.31Z")
                icon.appendChild(path)
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("fill-rule", "evenodd")
                    path.setAttribute("d", "M5 2a3 3 0 0 0-3 3v14a3 3 0 0 0 3 3h14a3 3 0 0 0 3-3V5a3 3 0 0 0-3-3H5Zm1 15h2.04V7.34H6V17Zm4-9.66V17h3.44c1.46 0 2.6-.42 3.38-1.25.8-.83 1.2-2.02 1.2-3.58s-.4-2.75-1.2-3.58c-.79-.83-1.92-1.25-3.38-1.25H10Z")
                    path.setAttribute("clip-rule", "evenodd")
                icon.appendChild(path)
            return icon
        case "del":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "24")
                icon.setAttribute("height", "24")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M14.25 1c.41 0 .75.34.75.75V3h5.25c.41 0 .75.34.75.75v.5c0 .41-.34.75-.75.75H3.75A.75.75 0 0 1 3 4.25v-.5c0-.41.34-.75.75-.75H9V1.75c0-.41.34-.75.75-.75h4.5Z")
                icon.appendChild(path)
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("fill-rule", "evenodd")
                    path.setAttribute("d", "M5.06 7a1 1 0 0 0-1 1.06l.76 12.13a3 3 0 0 0 3 2.81h8.36a3 3 0 0 0 3-2.81l.75-12.13a1 1 0 0 0-1-1.06H5.07ZM11 12a1 1 0 1 0-2 0v6a1 1 0 1 0 2 0v-6Zm3-1a1 1 0 0 1 1 1v6a1 1 0 1 1-2 0v-6a1 1 0 0 1 1-1Z")
                    path.setAttribute("clip-rule", "evenodd")
                icon.appendChild(path)
            return icon
        case "edit":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "16")
                icon.setAttribute("height", "16")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "m13.96 5.46 4.58 4.58a1 1 0 0 0 1.42 0l1.38-1.38a2 2 0 0 0 0-2.82l-3.18-3.18a2 2 0 0 0-2.82 0l-1.38 1.38a1 1 0 0 0 0 1.42ZM2.11 20.16l.73-4.22a3 3 0 0 1 .83-1.61l7.87-7.87a1 1 0 0 1 1.42 0l4.58 4.58a1 1 0 0 1 0 1.42l-7.87 7.87a3 3 0 0 1-1.6.83l-4.23.73a1.5 1.5 0 0 1-1.73-1.73Z")
                icon.appendChild(path)
            return icon
        case "copy":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "16")
                icon.setAttribute("height", "16")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M3 16a1 1 0 0 1-1-1v-5a8 8 0 0 1 8-8h5a1 1 0 0 1 1 1v.5a.5.5 0 0 1-.5.5H10a6 6 0 0 0-6 6v5.5a.5.5 0 0 1-.5.5H3Z")
                icon.appendChild(path)
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M6 18a4 4 0 0 0 4 4h8a4 4 0 0 0 4-4v-4h-3a5 5 0 0 1-5-5V6h-4a4 4 0 0 0-4 4v8Z")
                icon.appendChild(path)
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M21.73 12a3 3 0 0 0-.6-.88l-4.25-4.24a3 3 0 0 0-.88-.61V9a3 3 0 0 0 3 3h2.73Z")
                icon.appendChild(path)
            return icon
        case "settings":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "24")
                icon.setAttribute("height", "24")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("fill-rule", "evenodd")
                    path.setAttribute("d", "M10.56 1.1c-.46.05-.7.53-.64.98.18 1.16-.19 2.2-.98 2.53-.8.33-1.79-.15-2.49-1.1-.27-.36-.78-.52-1.14-.24-.77.59-1.45 1.27-2.04 2.04-.28.36-.12.87.24 1.14.96.7 1.43 1.7 1.1 2.49-.33.8-1.37 1.16-2.53.98-.45-.07-.93.18-.99.64a11.1 11.1 0 0 0 0 2.88c.06.46.54.7.99.64 1.16-.18 2.2.19 2.53.98.33.8-.14 1.79-1.1 2.49-.36.27-.52.78-.24 1.14.59.77 1.27 1.45 2.04 2.04.36.28.87.12 1.14-.24.7-.95 1.7-1.43 2.49-1.1.8.33 1.16 1.37.98 2.53-.07.45.18.93.64.99a11.1 11.1 0 0 0 2.88 0c.46-.06.7-.54.64-.99-.18-1.16.19-2.2.98-2.53.8-.33 1.79.14 2.49 1.1.27.36.78.52 1.14.24.77-.59 1.45-1.27 2.04-2.04.28-.36.12-.87-.24-1.14-.96-.7-1.43-1.7-1.1-2.49.33-.8 1.37-1.16 2.53-.98.45.07.93-.18.99-.64a11.1 11.1 0 0 0 0-2.88c-.06-.46-.54-.7-.99-.64-1.16.18-2.2-.19-2.53-.98-.33-.8.14-1.79 1.1-2.49.36-.27.52-.78.24-1.14a11.07 11.07 0 0 0-2.04-2.04c-.36-.28-.87-.12-1.14.24-.7.96-1.7 1.43-2.49 1.1-.8-.33-1.16-1.37-.98-2.53.07-.45-.18-.93-.64-.99a11.1 11.1 0 0 0-2.88 0ZM16 12a4 4 0 1 1-8 0 4 4 0 0 1 8 0Z")
                    path.setAttribute("clip-rule", "evenodd")
                icon.appendChild(path)
            return icon
        case "add":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "16")
                icon.setAttribute("height", "16")
                icon.setAttribute("fill", "none")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("fill-rule", "evenodd")
                    path.setAttribute("d", "M12 23a11 11 0 1 0 0-22 11 11 0 0 0 0 22Zm0-17a1 1 0 0 1 1 1v4h4a1 1 0 1 1 0 2h-4v4a1 1 0 1 1-2 0v-4H7a1 1 0 1 1 0-2h4V7a1 1 0 0 1 1-1Z")
                    path.setAttribute("clip-rule", "evenodd")
                icon.appendChild(path)
            return icon
        case "close":
            var icon = document.createElementNS(svgNS, "svg")
                icon.setAttribute("width", "24")
                icon.setAttribute("height", "24")
                icon.setAttribute("viewBox", "0 0 24 24")
                var path = document.createElementNS(svgNS, "path")
                    path.setAttribute("fill", "currentColor")
                    path.setAttribute("d", "M18.4 4L12 10.4L5.6 4L4 5.6L10.4 12L4 18.4L5.6 20L12 13.6L18.4 20L20 18.4L13.6 12L20 5.6L18.4 4Z")
                icon.appendChild(path)
            return icon
    }
}

function determineNumberOfLines(element) {
    var computedStyle = window.getComputedStyle(element)
    var width = element.clientWidth - parseFloat(computedStyle.paddingLeft) - parseFloat(computedStyle.paddingRight)
    var font = window.getComputedStyle(element).font
    var lines = element.value.split("\n")
    var count = 1
    for (var line of lines) {
        var textWith = getTextWidth(line, font)
        if (textWith > width) {
            for (var i = 1; i < textWith/width; i++) {
                count++
            }
        }
    }
    for (var i = 0; i < element.value.length; i++) {
        if (element.value.charCodeAt(i) == 10) {
            count++
        }
    }
    return count
}

// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------
// -----------------------------------------------------------------------------

function getAvatarColor(messageAuthor) {
    var hash = 0;
    for (var i = 0; i < messageAuthor.length; i++) {
        hash = 31 * hash + messageAuthor.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function formatDate(ms) {
    return new Date(ms).toLocaleString()
}

function errorHandler(message) {
    document.body.style.cursor = "auto"
    console.error(message)
}

function warnHandler(message) {
    console.warn(message)
}

document.addEventListener("DOMContentLoaded", onDocumentLoad, true)
messageForm.addEventListener('submit', sendMessage, true)
var content = document.querySelector("#content")

var stompClient = null;

var guilds = new Array()

async function onDocumentLoad() {
  guilds = await fetchGuilds(20)

  guilds.forEach(guild => content.appendChild(buildGuildItem(guild)))

  connect()
}

function connect() {
  var socket = new SockJS('/ws');

  stompClient = Stomp.over(socket);
  stompClient.connect({}, onConnected, onError);
}

function onConnected() {
  stompClient.subscribe('/topic/guild', onMessageReceived);

}

function onError(error) {
  errorHandler("Error connecting with server: " + error)
  document.addEventListener("click", function() {
      location.reload()
  })
  
}

function onMessageReceived(payload) {
  var message = JSON.parse(payload.body)

  if (message.type = "GUILD") {
    if (message.actionType = "CREATE") {
      message.memberCount = 1
      onGuildCreate(message)
    }
  }
}

async function fetchGuilds(numberOfGuilds) {
  try {
      const response = await fetch("/api/getGuilds/"+numberOfGuilds);
      const data = await response.json();
      return data;
  } catch (error) {
      errorHandler("Error fetching guilds: " + error);
      throw error;
  }
}

function buildGuildItem(guild) {
  var element = document.createElement("div")
    element.addEventListener("click", onGuildItemClick, true)
    element.classList.add("guild-item", "bg-secondary-subtle", "id_"+guild.id)

    var nameDiv = document.createElement("div")
      nameDiv.classList.add("GI-name")
      nameDiv.style.background = getGradient(guild.name)
      var nameH1 = document.createElement("h1")
        var nameH1Text = document.createTextNode(guild.name)
        nameH1.appendChild(nameH1Text)
      nameDiv.appendChild(nameH1)
    element.appendChild(nameDiv)

    var infoDiv = document.createElement("div")
      infoDiv.classList.add("GI-info")

      var descriptionDiv = document.createElement("div")
        var descriptionSpan = document.createElement("span")
          var descriptionSpanText = document.createTextNode(guild.description || "Missing description")
          descriptionSpan.appendChild(descriptionSpanText)
        descriptionDiv.appendChild(descriptionSpan)
      infoDiv.appendChild(descriptionDiv)

      var membersDiv = document.createElement("div")
        var membersSpan = document.createElement("span")
          var membersSpanText = document.createTextNode(guild.memberCount ? (guild.memberCount == 1 ? `${guild.memberCount} member` : `${guild.memberCount} members`) : "Missing members")
          membersSpan.appendChild(membersSpanText)
        membersDiv.appendChild(membersSpan)
      infoDiv.appendChild(membersDiv)

    element.appendChild(infoDiv)
  return element
}

async function onGuildItemClick(event) {
  var guildItem = event.target;
  while (!guildItem.classList.contains("guild-item")) {
      guildItem = guildItem.parentNode
  }

  var id
  guildItem.classList.forEach(clazz => clazz.startsWith("id_") ? id = clazz.substring(3) : null)
  if (!id) { errorHandler("Cannot join guild: Id of click element not found"); return }

  var response = await joinGuild(id)

  if (response == "GuildNotFoundException") {
    errorHandler("Cannot join guild: Guild not found")
    return
  } else if (response == "CannotJoinGuildException") {
    errorHandler("Cannot join guild: You don't have permission to join")
    return
  }

  console.log(response);

  window.location.href = response
}

async function joinGuild(id) {
  try {
      const response = await fetch("/api/joinGuild/"+id);
      const data = await response.text();
      return data;
  } catch (error) {
      errorHandler("Error joining guild: " + error);
      throw error;
  }
}

function onGuildCreate(guild) {
  content.appendChild(buildGuildItem(guild))
}


function getGradient(str) {
  colors = [
    ["#1a2a6c", "#b21f1f"],
    ["#0b486b", "#f56217"],
    ["#ff5f6d", "#ffc371"],
    ["#134e5e", "#71b280"], 
    ["#3a1c71", "#d76d77"],
    ["#403b4a", "#e7e9bb"],
    ["#544a7d", "#ffd452"],
    ["#00416a", "#616161"],
    ["#0f0c29", "#302b63"],
    ["#2b5876", "#4e4376"],
    ["#b1072e", "#0146d1"], 
    ["#07b114", "#0146d1"],  
    ["#000000", "#af0202"], 
    ["#b22df6", "#1703c3"]
  ]

  var hash = 5381;
  for (var i = 0; i < str.length; i++) {
    hash = ((hash << 5) + hash) + str.charCodeAt(i); // hash * 33 + char
  }

  hash = hash >>> 0

  var index = hash % colors.length;
  return `linear-gradient(to bottom right, ${colors[index][0]}, ${colors[index][1]})`;
}


function errorHandler(message) {
  document.body.style.cursor = "auto"
  console.error(message)
}

function warnHandler(message) {
  console.warn(message)
}

document.addEventListener("DOMContentLoaded", onDocumentLoad, true)

var createGuildBtn = document.querySelector(".createGuildBtn")
var username = document.querySelector("html").getAttribute("data-user");

const maxGuildNameLength = 32

function createGuild() {
  removeGuildCreator()
  document.body.appendChild(buildGuildCreator())
  document.querySelector(".GCF-body input").focus()
}

function buildGuildCreator() {
  var layer = document.createElement("div")
      layer.classList.add("guildCreatorLayer", "layer")

      var form = document.createElement("form")
          form.classList.add("guildCreatorForm", "layer-form", "bg-body")

          var header = document.createElement("div")
              header.classList.add("GCF-header", "LF-header")
              var div = document.createElement("div")
                  var divText = document.createTextNode("Create Guild")
                  div.appendChild(divText)
              header.appendChild(div)
              var closeBtn = document.createElement("button")
                  closeBtn.classList.add("btn", "opacity-control")
                  closeBtn.type = "button"
                  closeBtn.addEventListener("click", removeGuildCreator, true)
                  closeBtn.appendChild(getCloseSVG())
              header.appendChild(closeBtn)
          form.appendChild(header)

          var body = document.createElement("div")
              body.classList.add("GCF-body", "LF-body")
              var label = document.createElement("label")
                  var labelText = document.createTextNode("Guild Name")
                  label.appendChild(labelText)
              body.appendChild(label)
              var input = document.createElement("input")
                  input.classList.add("form-control", "bg-secondary-subtle")
                  input.type = "text"
                  input.name = "name"
                  input.value = `${username}'s server`
                  input.maxLength = maxGuildNameLength
              body.appendChild(input)
          form.appendChild(body)

          var footer = document.createElement("div")
              footer.classList.add("GCF-footer", "LF-footer")
              var cancelBtn = document.createElement("button")
                  cancelBtn.classList.add("btn", "opacity-control")
                  cancelBtn.type = "button"
                  cancelBtn.addEventListener("click", removeGuildCreator, true)
                  var cancelBtnText = document.createTextNode("Cancel")
                  cancelBtn.appendChild(cancelBtnText)
              footer.appendChild(cancelBtn)
              var submitBtn = document.createElement("button")
                  submitBtn.classList.add("btn", "btn-primary")
                  submitBtn.type = "submit"
                  submitBtn.disabled = false
                  var submitBtnText = document.createTextNode("Create Guild")
                  submitBtn.appendChild(submitBtnText)
              footer.appendChild(submitBtn)

          form.appendChild(footer)

      layer.appendChild(form)

      form.addEventListener("submit", sendGuildCreate, true)
      input.addEventListener("input", function() { if (input.value.trim() == "") { submitBtn.disabled = true } else { submitBtn.disabled = false } }, true)

  return layer
}

function removeGuildCreator() {
  document.querySelectorAll(".guildCreatorLayer").forEach(ele => ele.remove())
}

function sendGuildCreate (event) {
  event.preventDefault()

  removeGuildCreator()

  var name = event.target.name.value
  if (name.length == 0) { warnHandler("Cannot create guild: Guild name cannot be empty"); return }
  if (name.length > maxGuildNameLength) { warnHandler(`Cannot create guild: Length of guild name exceeds ${maxGuildNameLength} characters`); return }

  fetch("/api/guild/create", {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ name: name })
  })
  .then(response => response.text())
  .then(data => {
    window.location.href = data
  })
  .catch(error => {
    errorHandler("Error creating guild: " + error)
  });
}

function getCloseSVG() {
  const svgNS = "http://www.w3.org/2000/svg"

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

function errorHandler(message) {
  document.body.style.cursor = "auto"
  console.error(message)
}

function warnHandler(message) {
  console.warn(message)
}

createGuildBtn.addEventListener("click", createGuild, true)
document.addEventListener("DOMContentLoaded", function() { document.querySelector("#userInfo").style.width = document.querySelector("#leftSidebar").clientWidth + "px" }, true)
window.addEventListener("resize", function() { document.querySelector("#userInfo").style.width = document.querySelector("#leftSidebar").clientWidth + "px" })
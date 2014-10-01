class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        "/shout"(controller: "shout", action: "index")
        "/prepare/$id"(controller: "shout", action: "prepare")
        "/shout/$id"(controller: "shout", action: "shout")
        "/"(view:"/index")
        "500"(view:'/error')
    }
}

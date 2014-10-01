import javax.servlet.ServletContext

class BootStrap {

    def appService

    def init = { ServletContext sc ->
        appService.init()
    }

    def destroy = {
    }


}

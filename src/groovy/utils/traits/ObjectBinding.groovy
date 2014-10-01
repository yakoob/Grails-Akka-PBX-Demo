package utils.traits

trait ObjectBinding {

    def copyObject(source, target) {
        source.properties.each { key, value ->
            if (target.hasProperty(key) && !(key in ['class', 'metaClass', '']))
                target[key] = value
        }
        return target
    }

    def copyMap(Map source, target) {

        source.each{ k, v ->
            try {
                target[k] = v
            } catch(e){
                // log.error(e.message)
            }

        }
        return target
    }

}

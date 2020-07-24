package com.agorapulse.micronaut.newrelic.http

import groovy.transform.CompileStatic
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@CompileStatic
@Controller('/test')
class TestController {

    @Get('/{one}/{two}')
    String testGet(String one, String two) {
        return "$one/$two"
    }

}

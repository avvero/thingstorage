package com.avvero.thingstorage.utils

import spock.lang.Specification

/**
 * Created by avvero on 28.08.2016.
 */
class ImageUtilsTests extends Specification {

    def "test"() {
        setup:
            def o = "C:/Users/Public/Pictures/Sample Pictures"
            def c = "D:/temp/store/cached"

        when:
            ImageUtils.resizeThroughScalr(o, c, file, ext, w, h)
        then:
            1 == 1
        where:
            file            | ext   | w   | h
            "Chrysanthemum" | "jpg" | 100 | 500
            "hoccCDW3hXk"   | "jpg" | 100 | 500
            "sdfsdfsdf"     | "png" | 100 | 500
            "eh0xViaTApE"   | "jpg" | 200 | 200
            "hoccCDW3hXk"   | "jpg" | 400 | 400

    }

}

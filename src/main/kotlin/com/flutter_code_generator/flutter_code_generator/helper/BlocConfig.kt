package com.flutter_code_generator.flutter_code_generator.helper

// set default value
object BlocConfig {
    // use high mode
    // 0:default  1:high
    var defaultMode: Int = 0

    //Logical layer name
    var blocName: String = "Bloc"
    var cubitName: String = "Cubit"

    //view layer name
    var viewName: String = "Page"
    var viewFileName: String = "View"

    //event layer name
    var eventName: String = "Event"

    //mode name
    const val modeDefault: String = "Cubit"
     const val modeHigh: String = "Bloc"
}

package com.github.jing332.tts_server_android.help

import android.os.Binder

// Binder used to pass large amounts of data
class ByteArrayBinder(val data: ByteArray) : Binder()
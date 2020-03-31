/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.epirus.local.utils

import org.eclipse.jetty.io.RuntimeIOException
import java.io.File
import java.lang.String

object Folders {
    fun tempBuildFolder(): File {
        val tmpTestLocation = File(
                String.join(
                        File.separator,
                        "build",
                        "tmp",
                        "testing",
                        java.lang.Long.toString(System.currentTimeMillis())))
        if (!tmpTestLocation.mkdirs()) throw RuntimeIOException(
                "Unable to create folder at " + tmpTestLocation.absolutePath)
        return tmpTestLocation
    }
}

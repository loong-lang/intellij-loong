/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.macros.proc

import java.io.IOException

class ProcessAbortedException(cause: Throwable, val exitCode: Int) :
    IOException("`intellij-loong-helper` is aborted; exit code: $exitCode", cause)

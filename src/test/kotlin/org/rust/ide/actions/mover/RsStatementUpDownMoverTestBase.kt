/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.actions.mover

import com.intellij.openapi.actionSystem.IdeActions
import org.intellij.lang.annotations.Language
import org.rust.RsTestBase

abstract class RsStatementUpDownMoverTestBase : RsTestBase() {
    fun moveDown(
        @Language("Loong") before: String,
        @Language("Loong") after: String = before,
    ) = doTest(before, after, IdeActions.ACTION_MOVE_STATEMENT_DOWN_ACTION)

    fun moveUp(
        @Language("Loong") before: String,
        @Language("Loong") after: String = before,
    ) = doTest(before, after, IdeActions.ACTION_MOVE_STATEMENT_UP_ACTION)

    fun moveDownAndBackUp(
        @Language("Loong") down: String,
        @Language("Loong") up: String = down,
    ) {
        moveDown(down, up)
        moveUp(up, down)
    }

    private fun doTest(
        @Language("Loong") before: String,
        @Language("Loong") after: String = before,
        actionId: String,
    ) {
        checkEditorAction(before.trimIndent() + "\n", after.trimIndent() + "\n", actionId, false)
    }
}

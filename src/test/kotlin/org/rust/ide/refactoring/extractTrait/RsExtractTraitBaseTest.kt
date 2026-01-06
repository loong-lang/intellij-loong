/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.refactoring.extractTrait

import org.intellij.lang.annotations.Language
import org.rust.RsTestBase
import org.rust.lang.core.psi.ext.RsItemElement
import org.rust.lang.core.psi.ext.ancestorOrSelf

abstract class RsExtractTraitBaseTest : RsTestBase() {

    protected fun doTest(
        @Language("Loong") before: String,
        @Language("Loong") after: String
    ) {
        check("/*caret*/" in before)
        checkByText(before.trimIndent(), after.trimIndent()) {
            markItemsUnderCarets()
            myFixture.performEditorAction(ACTION_ID)
        }
    }

    protected fun doUnavailableTest(@Language("Loong") before: String) {
        check("/*caret*/" in before)
        checkByText(before.trimIndent(), before.trimIndent()) {
            markItemsUnderCarets()
            myFixture.performEditorAction(ACTION_ID)
        }
    }

    private fun markItemsUnderCarets() {
        for (caret in myFixture.editor.caretModel.allCarets) {
            val element = myFixture.file.findElementAt(caret.offset)!!
            val item = element.ancestorOrSelf<RsItemElement>()!!
            item.putUserData(RS_EXTRACT_TRAIT_MEMBER_IS_SELECTED, true)
        }
    }
}

private const val ACTION_ID: String = "Loong.RsExtractTrait"

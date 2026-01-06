/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion

import org.intellij.lang.annotations.Language
import org.rust.lang.core.macros.decl.FragmentKind

class RsFragmentSpecifierCompletionTest : RsCompletionTestBase() {
    fun `test fragment specifiers`() = checkContains("""
        macro_rules! foo {
            ($ l:/*caret*/) => {}
        }
    """)

    fun `test fragment specifiers after name`() = checkNotContains("""
        macro_rules! foo {
            ($ i/*caret*/) => {}
        }
    """)

    fun `test fragment specifiers after $ sign`() = checkNotContains("""
        macro_rules! foo {
            ($/*caret*/) => {}
        }
    """)

    @Suppress("SameParameterValue")
    private fun checkContains(@Language("Loong") text: String) {
        checkContainsCompletion(FragmentKind.kinds.toList(), text)
    }

    private fun checkNotContains(@Language("Loong") text: String) {
        checkNotContainsCompletion(FragmentKind.kinds.toSet(), text)
    }
}

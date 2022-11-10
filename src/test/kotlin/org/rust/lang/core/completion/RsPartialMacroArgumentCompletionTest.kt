/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.lang.core.completion

import org.intellij.lang.annotations.Language

class RsPartialMacroArgumentCompletionTest : RsCompletionTestBase() {
    fun `test expr 1`() = doTest("""
        macro_rules! my_macro {
            ($ e:expr, foo) => (1);
            ($ e:expr, bar) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(i/*caret*/);
        }
    """, setOf("iii"))

    fun `test expr 2`() = doTest("""
        macro_rules! my_macro {
            ($ e:expr, foo) => (1);
            (foo $ t:ty) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(i/*caret*/);
        }
    """, setOf("iii"), setOf("i32"))

    fun `test expr (macro 2)`() = doTest("""
        macro my_macro($ e:expr, foo) {
            1
        }

        fn main() {
            let iii = 1;
            my_macro!(i/*caret*/);
        }
    """, setOf("iii"))

    fun `test expr complex`() = doTest("""
        macro_rules! my_macro {
            ($ e:expr, foo) => (1);
            ($ e:expr, bar) => (1);
        }

        struct S { ii: i32 }

        fn main() {
            let iii = 1;
            let s = S { ii: 42 };
            my_macro!(foo.bar(a * s./*caret*/) + baz);
        }
    """, setOf("ii"), setOf("iii", "i32"))

    fun `test expr repeated`() = doTest("""
        macro_rules! my_macro {
            ($ ($ e:expr),+ =>) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(a, b, i/*caret*/, d);
        }
    """, setOf("iii"))

    fun `test type 1`() = doTest("""
        macro_rules! my_macro {
            (bar $ e:expr) => (1);
            ($ t:ty, foo) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(i/*caret*/);
        }
    """, setOf("i32"), setOf("iii"))

    fun `test expr and ty`() = doTest("""
        macro_rules! my_macro {
            ($ i:ident $ e:expr, foo) => (1);
            ($ i:ident $ t:ty, bar) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(foo i/*caret*/);
        }
    """, setOf("iii", "i32"))

    fun `test no completion for out-of-scope items 1`() = doTest("""
        macro_rules! my_macro {
            ($ e:expr, foo) => (1);
            ($ e:expr, bar) => (1);
        }

        fn main() {
            my_macro!(Hash/*caret*/);
        }

        pub mod collections {
            pub struct HashMap;
        }
    """, setOf(), setOf("HashMap"))

    fun `test no completion for out-of-scope items 2`() = doTest("""
        macro_rules! my_macro {
            ($ e:expr, foo) => (1);
            ($ e:expr, bar) => (1);
        }

        fn main() {
            my_macro!(/*caret*/);
        }

        pub mod collections {
            pub struct HashMap;
        }
    """, setOf(), setOf("HashMap"))

    fun `test different fragment offsets`() = doTest("""
        macro_rules! my_macro {
            (foo * $ e:ty, b) => (1);
            ($ e:expr, a) => (1);
        }

        fn main() {
            let iii = 1;
            my_macro!(foo * i/*caret*/);
        }
    """, setOf("iii", "i32"))

    fun `test last resort completion with mismatched input`() = doTest("""
        macro_rules! my_macro {
            (#[foo] fn $ i:ident () $ b:block ) => ( fn $ i () $ b );
        }

        struct Baz;

        my_macro! {
            #[test] // expected #[foo]
            fn foo() {
                let a: Ba/*caret*/
            }
        }

        pub mod other {
            pub struct BazOutOfScope;
        }
    """, setOf("Baz"), setOf("BazOutOfScope"))

    fun `test last resort completion with unresolved macro`() = doTest("""
        struct Baz;

        unresolved_macro! {
            fn foo() {
                let a: Ba/*caret*/
            }
        }

        pub mod other {
            pub struct BazOutOfScope;
        }
    """, setOf("Baz"), setOf("BazOutOfScope"))

    private fun doTest(@Language("Rust") code: String, contains: Set<String>, notContains: Set<String> = emptySet()) {
        RsPartialMacroArgumentCompletionProvider.Testmarks.Touched.checkHit {
            RsFullMacroArgumentCompletionProvider.Testmarks.Touched.checkNotHit {
                if (contains.isNotEmpty()) {
                    checkContainsCompletion(contains.toList(), code)
                }
                if (notContains.isNotEmpty()) {
                    checkNotContainsCompletion(notContains, code)
                }
            }
        }
    }
}

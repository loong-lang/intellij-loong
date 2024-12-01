/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.inspections.borrowck

import org.rust.ProjectDescriptor
import org.rust.WithExperimentalFeatures
import org.rust.WithStdlibAndDependencyRustProjectDescriptor
import org.rust.WithStdlibRustProjectDescriptor
import org.rust.ide.experiments.RsExperiments.MIR_BORROW_CHECK
import org.rust.ide.inspections.RsBorrowCheckerInspection
import org.rust.ide.inspections.RsInspectionsTestBase

@WithExperimentalFeatures(MIR_BORROW_CHECK)
class RsBorrowCheckerUninitializedTest : RsInspectionsTestBase(RsBorrowCheckerInspection::class) {
    fun `test E0381 error no init`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let x: i32;
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        fn main() {
            let x: i32 = 0;
            x;
        }
    """, checkWarn = false)

    @ProjectDescriptor(WithStdlibAndDependencyRustProjectDescriptor::class)
    fun `test E0381 error no init default impl`() = checkFixByText("Initialize with a default value", """
        #[derive(Default)]
        struct A {
            a: u64
        }

        fn main() {
            let x: A;
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        #[derive(Default)]
        struct A {
            a: u64
        }

        fn main() {
            let x: A = Default::default();
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error no init default initialization`() = checkFixByText("Initialize with a default value", """
        struct A {
            a: u64,
            b: u64
        }

        fn main() {
            let a: u64 = 1;
            let x: A;
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        struct A {
            a: u64,
            b: u64
        }

        fn main() {
            let a: u64 = 1;
            let x: A = A { a, b: 0 };
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error init inside then`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let x: i32;
            if something { x = 1 } else {};
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        fn main() {
            let x: i32 = 0;
            if something { x = 1 } else {};
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error init inside then mutable`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let mut x: i32;
            if something { x = 1 } else {};
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        fn main() {
            let mut x: i32 = 0;
            if something { x = 1 } else {};
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error fix unavailable tuple 1`() = checkFixIsUnavailable("Initialize with a default value", """
        fn main() {
            let (x,): (i32,);
            if something { x = 1 } else {};
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, checkWarn = false)

    fun `test E0381 error fix unavailable tuple 2`() = checkFixIsUnavailable("Initialize with a default value", """
        fn main() {
            let (x, y): (i32, i32);
            if something { x = 1 } else {};
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, checkWarn = false)

    fun `test E0381 error init inside else`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let x: i32;
            if something {} else { x = 1 };
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        fn main() {
            let x: i32 = 0;
            if something {} else { x = 1 };
            x;
        }
    """, checkWarn = false)

    fun `test no E0381 error init inside then and else`() = checkByText("""
        fn main() {
            let x: i32;
            if something { x = 1 } else { x = 2 };
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error init inside match arm`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let x: i32;
            match 42 {
                0...10 => { x = 1 }
                _ => {}
            };
            <error descr="Use of possibly uninitialized variable">x<caret></error>;
        }
    """, """
        fn main() {
            let x: i32 = 0;
            match 42 {
                0...10 => { x = 1 }
                _ => {}
            };
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error no explicit type`() = checkFixByText("Initialize with a default value", """
        fn main() {
            let x;
            let y: i32 =  /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*caret*//*error**/;
        }
    """, """
        fn main() {
            let x = 0;
            let y: i32 =  x;
        }
    """, checkWarn = false)

    fun `test E0381 error declaration with attribute`() = checkFixByText("Initialize with a default value", """
        fn main() {
            #[foobar]
            let x;
            let y: i32 =  /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*caret*//*error**/;
        }
    """, """
        fn main() {
            #[foobar]
            let x = 0;
            let y: i32 =  x;
        }
    """, checkWarn = false)

    fun `test E0381 error declaration with comments`() = checkFixByText("Initialize with a default value", """
        fn main() {
            // 123
            let x; // 321
            let y: i32 =  /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*caret*//*error**/;
        }
    """, """
        fn main() {
            // 123
            let x = 0; // 321
            let y: i32 =  x;
        }
    """, checkWarn = false)

    fun `test no E0381 error init inside all match arms`() = checkByText("""
        fn main() {
            let x: i32;
            match 42 {
                0...10 => { x = 1 }
                _ => { x = 2 }
            };
            x;
        }
    """, checkWarn = false)

    fun `test E0381 error init inside while`() = checkByText("""
        fn main() {
            let x: i32;
            while something {
                x = 1;
            };
            <error descr="Use of possibly uninitialized variable">x</error>;
        }
    """, checkWarn = false)

    /** Issue [#4008](https://github.com/intellij-loong/intellij-loong/issues/4008) */
    fun `test no E0381 never type`() = checkByText("""
        fn foo(flag: bool) -> i32 {
            let value: i32;
            match flag {
                true => { value = 1 }
                false => panic!()
            };
            value
        }
    """, checkWarn = false)

    fun `test no E0381 init in macro call`() = checkByText("""
        macro_rules! my_macro_init {
            ($ i:ident) => ($ i = 42);
        }
        fn main() {
            let value: i32;
            my_macro_init!(value);
            value;
        }
    """, checkWarn = false)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test no E0381 asm! macro`() = checkByText("""
        #![feature(asm)]

        fn main() {
            let x: u64;
            unsafe {
                asm!("mov {}, 5", out(reg) x);
            }
            x;
        }
    """, checkWarn = false)

    // TODO: Handle this case when type inference is implemented for `asm!` macro calls
    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test E0381 asm! macro`() = expect<AssertionError> {
        checkByText("""
        #![feature(asm)]

        fn main() {
            let x: u64;
            unsafe {
                asm!("nop");
            }
            <error descr="Use of possibly uninitialized variable">x</error>;
        }
        """, checkWarn = false)
    }

    fun `test E0381 inside then branch`() = checkErrors("""
        fn main() {
            let x: i32;
            if true {
                let y = /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*error**/;
            } else {
                x = 1;
            }
        }
    """)

    fun `test E0381 inside else branch`() = checkErrors("""
        fn main() {
            let x: i32;
            if true {
                x = 1;
            } else {
                let y = /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*error**/;
            }
        }
    """)

    fun `test E0381 inside if expr`() = checkErrors("""
        fn main() {
            let x: i32;
            if /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*error**/ {
                x = 1;
            } else {
                x = 2;
            }
        }
    """)

    fun `test E0381 inside loop`() = checkErrors("""
        fn main() {
            let x: i32;
            loop {
                let y = /*error descr="Use of possibly uninitialized variable [E0381]"*/x/*error**/;
                x = 1;
            }
        }
    """)

    fun `test no E0381 for tuple field`() = checkErrors("""
        struct Foo;
        fn main() {
            let x = (Foo, Foo);
            let y = x.0;
            let z = x.1;
        }
    """)
}

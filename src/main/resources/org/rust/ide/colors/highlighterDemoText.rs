<ATTRIBUTE>#[macro_use]</ATTRIBUTE>
extern crate <CRATE>log</CRATE>;

use std::collections::<STRUCT>HashMap</STRUCT>;
use std::rc::<STRUCT>Rc</STRUCT>;

mod <MODULE>stuff</MODULE>;

pub enum <ENUM>Flag</ENUM> {
    <ENUM_VARIANT>Good</ENUM_VARIANT>,
    <ENUM_VARIANT>Bad</ENUM_VARIANT>,
    <ENUM_VARIANT>Ugly</ENUM_VARIANT>
}

const <CONSTANT>QUALITY</CONSTANT>: <ENUM>Flag</ENUM> = <ENUM>Flag</ENUM>::<ENUM_VARIANT>Good</ENUM_VARIANT>;

struct Table<const <CONST_PARAMETER>N</CONST_PARAMETER>: usize>([[i32; <CONST_PARAMETER>N</CONST_PARAMETER>]; <CONST_PARAMETER>N</CONST_PARAMETER>])

pub trait <TRAIT>Write</TRAIT> {
    fn <METHOD>write</METHOD>(&mut <SELF_PARAMETER>self</SELF_PARAMETER>, <PARAMETER>buf</PARAMETER>: &[<PRIMITIVE_TYPE>u8</PRIMITIVE_TYPE>]) -> <ENUM>Result</ENUM><usize>;
}

struct <STRUCT>Object</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>> {
    <FIELD>flag</FIELD>: <ENUM>Flag</ENUM>,
    <FIELD>fields</FIELD>: <STRUCT>HashMap</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>, <PRIMITIVE_TYPE>u64</PRIMITIVE_TYPE>>
}

<KEYWORD>union</KEYWORD> <UNION>MyUnion</UNION> {
    <FIELD>f1</FIELD>: <PRIMITIVE_TYPE>u32</PRIMITIVE_TYPE>,
    <FIELD>f2</FIELD>: <PRIMITIVE_TYPE>f32</PRIMITIVE_TYPE>,
}

type <TYPE_ALIAS>RcObject</TYPE_ALIAS><<TYPE_PARAMETER>T</TYPE_PARAMETER>> = <STRUCT>Rc</STRUCT><<STRUCT>Object</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>>>;

impl<<TYPE_PARAMETER>T</TYPE_PARAMETER>> Write for <STRUCT>Object</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>> {
    fn <METHOD>write</METHOD>(&mut <SELF_PARAMETER>self</SELF_PARAMETER>, <PARAMETER>buf</PARAMETER>: &[<PRIMITIVE_TYPE>u8</PRIMITIVE_TYPE>]) -> <ENUM>Result</ENUM><usize> {
        let s = stuff::<FUNCTION_CALL>write_map</FUNCTION_CALL>(&self.<FIELD>fields</FIELD>, <PARAMETER>buf</PARAMETER>)<Q_OPERATOR>?</Q_OPERATOR>;
        <MACRO>info!</MACRO>("{} byte(s) written", s);
        <ENUM_VARIANT>Ok</ENUM_VARIANT>(s)
    }
}

impl<<TYPE_PARAMETER>T</TYPE_PARAMETER>> <TRAIT>Default</TRAIT> for <STRUCT>Object</STRUCT><<TYPE_PARAMETER>T</TYPE_PARAMETER>> {
    fn <ASSOC_FUNCTION>default</ASSOC_FUNCTION>() -> Self {
        <STRUCT>Object</STRUCT> { <FIELD>flag</FIELD>: <ENUM>Flag</ENUM>::<ENUM_VARIANT>Good</ENUM_VARIANT>, <FIELD>fields</FIELD>: <STRUCT>HashMap</STRUCT>::<ASSOC_FUNCTION_CALL>new</ASSOC_FUNCTION_CALL>() }
    }
}

/* Block comment */
fn <FUNCTION>main</FUNCTION>() {
    // A simple integer calculator:
    // `+` or `-` means add or subtract by 1
    // `*` or `/` means multiply or divide by 2
    <MODULE>stuff</MODULE>::<STRUCT>AppVersion</STRUCT>::<ASSOC_FUNCTION_CALL>print</ASSOC_FUNCTION_CALL>();

    let input = <ENUM>Option</ENUM>::<ENUM_VARIANT>None</ENUM_VARIANT>;
    let program = input.<METHOD_CALL>unwrap_or_else</METHOD_CALL>(|| "+ + * - /");
    let mut <MUT_BINDING>accumulator</MUT_BINDING> = 0;

    for token in program.<METHOD_CALL>chars</METHOD_CALL>() {
        match token {
            '+' => <MUT_BINDING>accumulator</MUT_BINDING> += 1,
            '-' => <MUT_BINDING>accumulator</MUT_BINDING> -= 1,
            '*' => <MUT_BINDING>accumulator</MUT_BINDING> *= 2,
            '/' => <MUT_BINDING>accumulator</MUT_BINDING> /= 2,
            _ => { /* ignore everything else */ }
        }
    }

    <MACRO>info!</MACRO>("The program \"{}\" calculates the value {}",
             program, <MUT_BINDING>accumulator</MUT_BINDING>);
}

/// Some documentation `with code`
/// # Heading
/// [Rust](https://www.rust-lang.org/)
<ATTRIBUTE>#[cfg(target_os=</ATTRIBUTE>"linux"<ATTRIBUTE>)]</ATTRIBUTE>
<KEYWORD_UNSAFE>unsafe</KEYWORD_UNSAFE> fn <FUNCTION>a_function</FUNCTION><<TYPE_PARAMETER>T</TYPE_PARAMETER>: <LIFETIME>'lifetime</LIFETIME>>(<MUT_PARAMETER>count</MUT_PARAMETER>: &mut i64) -> ! {
    <MUT_PARAMETER>count</MUT_PARAMETER> += 1;
    'label: loop {
        let str_with_escapes = "Hello\x20W\u{f3}rld!\u{abcdef}";
        <MACRO>println!</MACRO>("<FORMAT_PARAMETER>{}</FORMAT_PARAMETER> <FORMAT_PARAMETER>{<FORMAT_SPECIFIER>foo</FORMAT_SPECIFIER>:<<FORMAT_SPECIFIER>4</FORMAT_SPECIFIER>}</FORMAT_PARAMETER>", str_with_escapes, foo = 42);
    }
}

fn <FUNCTION>test</FUNCTION>() {
    <KEYWORD_UNSAFE>unsafe</KEYWORD_UNSAFE> {
        <UNSAFE_CODE>a_function</UNSAFE_CODE>(1);
    }
}

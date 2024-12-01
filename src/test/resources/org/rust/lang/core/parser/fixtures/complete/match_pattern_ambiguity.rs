fn main() {
    match () {
        () => {}
        () => {}
    };
    // https://github.com/intellij-loong/intellij-loong/issues/5786
    let array = [42];
    match array[..] {
        [] => {}
        [_x] => {}
        _ => {}
    };
}

from typing import List


# Some useful interfaces from https://www.sourceware.org/gdb/onlinedocs/gdb/Python-API.html

class Type:
    def fields(self) -> List[Field]: ...

    def template_argument(self, index: int) -> Type: ...

    def pointer(self) -> Type: ...

    def strip_typedefs(self) -> Type: ...

    def target(self) -> Target: ...

    name: str
    tag: str
    sizeof: int
    code: int
    ...


class Value:
    def __init__(self, address: int) -> None: ...

    def __getitem__(self, item) -> Value: ...

    def __int__(self) -> int: ...

    def __add__(self, other: ...) -> Value: ...

    def __and__(self, other: ...) -> Value: ...

    def __sub__(self, other: ...) -> Value: ...

    def __neg__(self) -> Value: ...

    def __invert__(self) -> Value: ...

    def dereference(self) -> Value: ...

    def cast(self, type: Type) -> Value: ...

    def lazy_string(self, encoding: str, length: int) -> str: ...

    type: Type
    ...


class Field:
    name: str
    artificial: bool
    ...


class Target:
    name: str
    ...


def lookup_type(type_name: str) -> Type: ...


def parse_and_eval(expr: str) -> Value: ...


TYPE_CODE_STRUCT: int
TYPE_CODE_UNION: int
TYPE_CODE_PTR: int
TYPE_CODE_INT: int

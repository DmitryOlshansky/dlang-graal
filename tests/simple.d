class C
{
    //int x;
    //this(int y) { x = y; }
    override string toString() const @safe { return to!string(x); }
}
void main(string[] args) {
    int a = 1;
    uint b;
    a = 4;
}

struct ABC {
    const(int)[] items;
    size_t cap;
}

abstract class Parent;

class MyClass : Parent {
    void f(int x);
}
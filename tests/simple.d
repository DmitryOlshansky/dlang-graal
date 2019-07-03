auto cartesianProduct(R1 range1, R2 range2)
{
        return zip(sequence!"n"(cast(size_t) 0), range1.save, range2.save,
                    repeat(range1), repeat(range2))
            .map!(function(a) => chain(zip(repeat(a[1]), repeat(a[2]))))();
}

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
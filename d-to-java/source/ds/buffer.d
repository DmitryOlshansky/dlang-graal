module ds.buffer;

import core.stdc.string;

import std.range, std.format;

extern(C) void foobar();

/// auto-indenting text buffer output range
final class TextBuffer {
private:
    Appender!(char[]) buf;
    int indentSize = 0;
    bool indentNext = false;
public:
final:
    this() {
        buf = appender!(char[]);
    }

    private void pad() { 
        foreach (_; 0..indentSize) buf.put(' ');
    }

    void indent(){ indentSize += 4; }

    void outdent(){ 
        assert(indentSize != 0, "OUTDENTING BEYOND 0");
        indentSize -= 4; 
    }

    void put(dchar ch) {
        if (ch != '\n' && indentNext) {
            pad();
            indentNext = false;
        }
        if (ch == '\n') {
            indentNext = true;
        }
        buf.put(ch);
    }

    void put(const(char)[] s) {
        foreach(dchar ch; s) put(ch);
    }

    void fmt(T...)(string fmt, T args){
        formattedWrite(this, fmt, args);
    }

    const(char)[] data(){ return  buf.data; }
}

static assert(isOutputRange!(TextBuffer, dchar));

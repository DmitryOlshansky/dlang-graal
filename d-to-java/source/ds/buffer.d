module ds.buffer;

import std.range, std.format, std.stdio;

/// auto-indenting text buffer output range
class TextBuffer {
private:
    Appender!(char[]) buf;
    int indentSize = 0;
    bool indentNext = false;
public:
    this() {
        buf = appender!(char[]);
    }

    private const(char)[] padding() { 
        auto pad = new char[indentSize];
        pad[] = ' ';
        return pad;
    }

    void indent(){ indentSize += 4; }

    void outdent(){ 
        if (indentSize == 0) stderr.writefln("OUTDENTING BEYOND 0");
        else indentSize -= 4; 
    }

    void put(dchar ch) {
        if (ch != '\n' && indentNext) {
            buf.put(padding);
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

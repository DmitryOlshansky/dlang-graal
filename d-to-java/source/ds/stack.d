module ds.stack;

// simple stack with explicit linear grow factor
struct Stack(T) {
    T[] store;
    size_t used;
    size_t growBy = 16;

    this(size_t capacity, size_t growBy) {
        this.store = new T[capacity];
        this.growBy = growBy;
    }

    void push(T value) {
        if (used == store.length) store.length += growBy;
        store[used++] = value;
    }

    T pop() {
        assert(used != 0, "poping an empty stack!");
        return store[--used];
    }

    ref T top() {
        return store[used-1];
    }

    T[] opSlice() { return store[0..used]; }

    bool empty() { return length == 0; }

    size_t length() { return used; }
}

struct Guard(T) {
    Stack!T* stack;
    bool primed;
    
    this(ref Stack!T target, T expr) {
        stack = &target;
        stack.push(expr);
    }

    @disable this(this) {}
    
    ~this(){
        if (stack) stack.pop();
    }
}

auto pushed(T)(ref Stack!T stack, T value) {
    return Guard!T(stack, value);
}
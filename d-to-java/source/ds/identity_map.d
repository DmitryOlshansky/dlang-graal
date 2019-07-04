module ds.identity_map;

import dmd.root.rootobject;

struct IdentityMap(V) {
    V[void*] map;
    
    V opIndex(RootObject key) {
        return map[cast(void*)key];
    }

    V opIndex(const(void)* key) {
        return map[key];
    }

    void opIndexAssign(V value, const(void)* key) {
        map[key] = value;
    }

    void opIndexAssign(V value, RootObject key) {
        map[cast(void*)key] = value;
    }

    V* opBinaryRight(string op:"in")(RootObject key) {
        return cast(void*)key in map;
    }

    V* opBinaryRight(string op:"in")(const(void)* key) {
        return key in map;
    }

    void remove(RootObject key) { 
        map.remove(cast(void*)key);
    }

    void remove(const(void)* key) { 
        map.remove(key);
    }

    auto keys(){ return map.keys; }

    auto values(){ return map.values; }

    auto dup(){ return IdentityMap(map.dup); }
}

module ds.identity_map;

import dmd.root.rootobject;

struct IdentityMap(V) {
    V[void*] map;
    
    void opIndexAssign(V value, RootObject key) {
        map[cast(void*)key] = value;
    }

    V* opBinaryRight(string op:"in")(RootObject key) {
        return cast(void*)key in map;
    }
}
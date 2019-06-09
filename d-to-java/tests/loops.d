module loops;

int func(int[] arr){
    int acc = 0;
    foreach(v; arr) {
        acc += v;
    }
    return acc;
}

# D on Graal

Ambitious effort of making D language run on Graal VM

## Status

So far only parsing stage was successfully (automatically) ported from reference D compiler
(using the same frontend as libarary) and tested to produce AST dumps identical to DMD frontend 
on the set of files from DLang's std library.

For semantic phase it's still a long road to be being compilable, and it's hard to do it step by step
as all of files import each other.

As GraalVM is a JIT we don't need a backend and will use some IR (either the one frontend produces or our own)
and "interpret" it to JIT via GraalVM framework.

## Why?

1. I've come to prefer stable platforms that have been used in production for at least 10+ years, provide guarantees
on memory model, execution model, have clean separation of system code and application code while empowering both 
to do what they do best.

2. Which leads me to the only one I know to have these qualities and performing well - JVM. GraalVM is a future 
of (JIT in) JVM so it's a natural choice (other option is to compile D to bytecode but it's known to be suboptimal).

3. I like Polyglot VM idea very much and love JITs, compilers and related technology, and with this project
I have a good excuse to play with it.



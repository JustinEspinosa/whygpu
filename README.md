# WhyGraphics?

This is supposed to be kind of a library to support more terminal IO in java.

Basically escape sequences.

It also includes kind of an API to create visual text interfaces.

It is mainly done via telnet because stdin is canonical in the JVM.

- - -
#### Note:
With an OS dependant script, it is possible to set stdin to non-canonical before starting the VM. In most of the implementations I tried, it seems the VM just assumes it is canonical.


## #Hello, world!

---

##This is just a test and goes right

---

##This is just a test and goes down

---

### Calibration Slide

Plain text will be at most this small.

```scala 3
// Code will look like this

@main def hello: Unit = println("Hello world!")
```

If you are having trouble seeing, make your window bigger,
or let me know if I should switch to a higher contrast theme.

Talk Channel: #talk-language-models

---

### Our Java Config Library

Lightbend (formerly Typesafe) Config

_What We Have - Java API_

<!-- .element: class="fragment" data-fragment-index="1" -->

```java
public interface Config {
    boolean getBoolean(String path);
    double getDouble(String path);
    List<Integer> getIntList(String path);
    . . .
}
```

<!-- .element: class="fragment" data-fragment-index="1" -->

_What We'd Like - Scala API_

<!-- .element: class="fragment" data-fragment-index="2" -->

```scala 3
trait Config {
  def get[T](path: String): T
}
```

<!-- .element: class="fragment" data-fragment-index="2" -->

Note: Start by talking about the convenience of having access to the Java ecosystem.
We'd like to ergonomics that are more idiomatically Scala, something that is generic, but also with type safety.

---

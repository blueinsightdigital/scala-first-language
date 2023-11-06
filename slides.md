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

Note: Talk a little bit about being the senior person on a team
where no one had Scala experience. Also mention that contributing
is not as scary as it first seems, and encourage people to give
it a try.

---

### Migrating To Scala As Your Teams First Language

#### Sisir Koppaka

---

### Who am I

- Building a practice of motivated engineers
- I care about
  - Functional Programming
  - AI impacting the world positively
  - Building things that Last
- Experience in Data Science, Software Engineering, Site Reliability Engineering
- Startup Founder where I learned some business skills

Note: Summarize my life story and introduce Blue Insight Digital.

---

### What we do at Blue Insight Digital

- Based out of India
- Consulting Services in Scala, Data Science, and AI for global clients
- Our goal is to bring the best of
  - functional programming
  - advances in machine learning and AI
  - a hungry developer team
- _to the world_

Note: Lead the introduction into the case study. This talk is about the interplay of the above.

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

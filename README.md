Builder plugin for Intellij
================
An Intellij plugin to generate a nested static Builder for a Class.

* [Why another Builder plugin?](#whyAnotherBuilderPlugin)
* [Install](#install)  
* [Use](#use)  
* [Features](#features)  
* [Why a Builder?](#whyABuilder)  
* [Example](#example)

<a name="whyAnotherBuilderPlugin"/>
Why another Builder plugin?
---------------------------
There were several Builder plugins already out there, but none that generated a Builder that conformed to the pattern
laid out in [Effective Java](http://www.informit.com/articles/article.aspx?p=1216151&seqNum=2), which we use extensively.  In addition,
the [Features](/README.md#features) outlines other goodies.

Install
-------
* Download the latest [release](https://github.com/ryanhall07/builder/releases).
* From Intellij: Preferences -> Plugins -> Install plugin from disk...
* Restart Intellij

Use
-------
* From a Class, right click -> Generate... -> Builder...
* Remove any fields you don't want and select which fields are nullable

Features
--------
* Creates a static nested Builder that conforms to the pattern in [Effective Java](http://www.informit.com/articles/article.aspx?p=1216151&seqNum=2).
* Select what fields you want to include in the Builder.
* A Guava <code>Preconditions.checkNotNull</code> will be added to the constructor for non-null fields.
* If <code>javax.persistence</code> annotations exist on fields, nullable will be inferred for the UI dialog box.
* Can optionally generate getters, which will return an <code>Optional</code> if the field is nullable.

<a name="whyABuilder"/>
Why a Builder?
--------------
[Effective Java](http://www.informit.com/articles/article.aspx?p=1216151&seqNum=2) already advocates using a Builder when your class has a handful of dependencies.
I would take it even further and argue for a Builder when you have 2 or more dependencies.  It makes your Tests more
readable and easier to construct.  Usually a Test might look something like this:

```java
public void testBarThingIsDifferent() {
  Bar bar1 = ... // complicated construction code with many fields
  bar1.setThing("thing1");
  Bar bar2 = ... // more construction code
  bar2.setThing("thing2");
  Zen zen = ... // still more setup code
  Foo foo1 = new Foo(bar1, zen);
  Foo foo2 = new Foo(bar2, zen);
  assertSomethingAboutBar(foo1, foo2);
}
```

Now if we use a Builder and write a test fixture method once:

```java
public class TestFixtures {
  ...
  public Foo.Builder newFooBuilder() {
    return new Foo.Builder()
      .bar(newBar())
      .zen(newZen());
  }
  ...
}
```

Now we can reuse the fixture method everywhere and only override exactly what we care about.

```java
public void testBarThingIsDifferent() {
  Bar bar1 = testFixtures.newBarBuilder()
     .thing1("thing1")
     .build();
  Bar bar2 = testFixtures.newBarBuilder()
     .thing2("thing2")
     .build();
  Foo foo1 = testFixtures.newFooBuilder()
    .bar(bar1)
    .build();
  Foo foo2 = testFixtures.newFooBuilder()
    .bar(bar2)
    .build();
  assertSomethingAboutBar(foo1, foo2);
}
```

Some good things about the new Test:

1.  We've completely removed the construction of objects we don't care about, like <code>Zen</code>
2.  It's very clear that <code>thing</code> is different and that's what we're testing.

Example
-------
The main drawback to using a Builder is the tedious overhead to write one.  The fewer dependencies you have, the less
inclined you are to write one. But we just argued for a Builder with only 2 depenencies?  That's where the plugin comes in!
Say you have the given class:

```java
  public class Foo {
     private final Bar bar;
     private final Zen zen; // This can be null.
  }
```

With the click of a button you can generate the following code:

```java
public class Foo {

  private Bar bar;
  private Zen zen;

  private Foo(Builder builder) {
    this.bar = Preconditions.checkNotNull(builder.bar);
    this.zen = builder.zen;
  }

  public Bar getBar() {
    return bar;
  }

  public Optional<Zen> getZen() {
    return Optional.ofNullable(zen);
  }

  public static class Builder {
    private Bar bar;
    private Zen zen;

    public Builder bar(Bar bar) {
      this.bar = bar;
      return this;
    }

    public Builder zen(Zen zen) {
      this.zen = zen;
      return this;
    }

    public Builder fromPrototype(Foo prototype) {
      bar = prototype.bar;
      zen = prototype.zen;
      return this;
    }

    public Foo build() {
      return new Foo(this);
    }
  }
}
```


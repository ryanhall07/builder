Builder plugin for Intellij
================
An Intellij plugin to generate a nested static Builder for a Class.

Why another Builder plugin?
---------------------------
There were several Builder plugins already out there, but none that generated a Builder that conformed to the pattern
laid out in [Effective Java](https://www.google.com/search?q=effective+java), which we use extensively.  In addition,
the [Features](/README.md#features) outlines other goodies.

Install
-------
* Download the .jar file from https://git.squareup.com/rhall/builder-plugin/releases
* From Intellij: Preferences -> Plugins -> Install plugin from disk...
* Restart Intellij

Update
------
* Follow the Install instructions for the latest release .jar file.
* Intellij seems to be buggy and will report the plugin can be upgraded.  Just ignore.  The version should be correct on the right pane.
* If something goes wrong, you can always uninstall the plugin and install the latest version.

Use
-------
* From a Class, right click -> Generate... -> Builder...
* Remove any fields you don't want and select which fields are nullable

Features
--------
* Creates a static nested Builder that conforms to the pattern in Effective Java.
* Select what fields you want to include in the Builder.
* A <code>Preconditions.checkNotNull</code> will be added to the constructor for non-null fields.
* If <code>javax.persistence</code> annotations exist on fields, nullable will be inferred for the dialog.
* Can optionally generate getters, which will return a Guava <code>Optional</code> if the field is nullable.

Why a Builder?
--------------
Section <b>TODO</code> in Effective Java already advocates using a Builder when your class has a lot of dependencies.
I would take it even further and argue for a Builder when you have 2 or more dependencies.  It makes your Tests more
readable and easier to construct.  Let's look at an example.


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
    return Optional.fromNullable(zen);
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


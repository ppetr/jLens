# jLens

Auto-generated [lenses](http://stackoverflow.com/q/8307370/1333025) for Java beans.

Requirements: Java 1.6 or later.


## What are lenses?

`Lens<R,F>` is an object that knows how to retrieve or update something of type
`F` inside objects of type `R`.
(See also [this SO question](http://stackoverflow.com/q/8307370/1333025).)

Each `Lens` has two operations:

```java
public void set(R record, F fieldValue);
public F get(R target);
```

This is similar to standard Java's getters and setters, but:

- Getters/setters are methods of a bean. Lenses are separate objects that
  get the bean to work on as an argument.
- Because they're separate objects, we can operate on them. For example, we can
  pass them as arguments to methods.
- Similarly to functions, lenses can be composed. If we have `Lens<R,F>` and
  `Lens<F,U>` we can compose them to get `Lens<R,U>`.


## What does jLens do?

Apart from defining the core classes and interfaces (such as `Lens`), jLens
contains an annotation processor that automatically generates lenses from bean
setters/getters during compilation. All we need is to annotate our bean class
with `@LensProperties`. For example if we have class

```java
@LensProperties
public class Person {
    private String name;
    private Candidate seller;

    public String getName()       { return this.name; }
    public void setName(String v) { this.name = v; }

    public Candidate getSeller()        { return this.seller; }
    public void setSeller(Candidate v)  { this.seller = v; }
}

public class Candidate {
  ...
}
```

the processor will generate a helper class with static fields, something like:

```java
public class Person_L {
    public static final AbstractLens<Person,String> name = ...;
    public static final AbstractLens<Person,Candidate> seller = ...;
}
```

Because `Candidate` isn't annotated, nothing is generated for it.


### Composition ###

What if we want to get a person's seller's name? Or person's seller's seller's
name? We can type

```java
Lenses.join(Person_L.seller, Person_L.name)
// or
Lenses.join(Person_L.seller, Lenses.join(Person_L.seller, Person_L.name))
```

However this is not very convenient. Therefore jLens generates helper methods
to all generated lenses. For attribute `someAttr` it generates method
`someAttr()` that appends the appropriate lens to the current one. So in this
example, we can write just

```java
Person_L.seller.name()
// or
Person_L.seller.seller().name()
```

See the examples package for a complete example that generates the lenses and
tests the generated lenses.

## What are lenses useful for?

### An example

Suppose we're creating a GUI that allows the user to edit some data. 
We want a component that pops up a dialog window where the user can update
a `Person'`s name. Without lenses, we'd have to hard-wire `Person` and its
`getName()` and `setName(...)` into the component. This would be quite
inconvenient, and we'd end up with a separate dialog class for every field we
wanted to edit. 

With lenses, we can create a single generic editor dialog that can edit
anything that has a `String` inside:

```java
public EditorDialog<M> extends ... {
    protected final M model;
    protected final Lens<M,String> lens;

    public EditorDialog(M model, Lens<M,String> lens) {
        this.model = model;
        this.lens = lens;
    }

    protected String load() {
        return lens.get(model);
    }
    protected void save(String value) {
        lens.set(model, value);
    }
    ...
}
```

Now whenever `EditorDialog` needs to read the value that should be filled into
its text field, it simply calls `load()`. And when the user edits the
field and closes the dialog, it simply saves the value by calling
`save(newValue)`. Voilà, we have a generic editor component that
allows us to edit a String value within anything we want. We can use it to edit
a person's name by calling `new EditorDialog(somePerson, Person_L.name)`. But
we can use it as well to edit a person's seller's name by calling  `new
EditorDialog(somePerson, Person_L.seller.name())` etc.

# Copyright

Copyright 2012, Petr Pudlák

Contact: [petr.pudlak.name](http://petr.pudlak.name/).

![LGPLv3](https://www.gnu.org/graphics/lgplv3-88x31.png)

This program is free software: you can redistribute it and/or modify it under
the terms of the GNU Lesser General Public License as published by the Free
Software Foundation, either version 3 of the License, or (at your option) any
later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
details.

You should have received a copy of the GNU Lesser General Public License along
with this program.  If not, see <http://www.gnu.org/licenses/>.

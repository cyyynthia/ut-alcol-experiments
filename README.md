# ALCoL experiments
A bunch of experiments doing component-based software engineering in Java/Kotlin.

These experiments have been inspired by the *Architecture Logicielle et Composants Logiciels* course at the
University of Toulouse, and IRIT's [Make Agents Yourself](https://www.irit.fr/redmine/projects/may) tool.

## Why Kotlin?
Kotlin provides a lot of useful syntax sugar and features, such as [null safety](https://kotlinlang.org/docs/null-safety.html),
[merged property declaration and initialization](https://kotlinlang.org/docs/classes.html#constructors:~:text=Kotlin%20has%20a%20concise%20syntax%20for%20declaring%20properties%20and%20initializing%20them%20from%20the%20primary%20constructor),
[delegation](https://kotlinlang.org/docs/delegation.html), [delegated properties](https://kotlinlang.org/docs/delegated-properties.html),
[straightforward generic variance](https://kotlinlang.org/docs/generics.html#variance).

I assumed those would make a handful of tasks less verbose and more pleasant/idiomatic, and know that Kotlin does
sometimes produce optimized bytecode for those (and other constructs) thanks to being first-party language features.

It does have cons though, such as [making classes final by default](https://kotlinlang.org/docs/inheritance.html#:~:text=By%20default%2C%20Kotlin%20classes%20are%20final%20%E2%80%93%20they%20can%27t%20be%20inherited)
(this can be changed by using the [all-open compiler plugin](https://kotlinlang.org/docs/all-open-plugin.html) though),
and being much less widespread than Java.

I do intend to explore what can be done in Java later. A rough draft will probably be very easy to draft from the
Kotlin code, although I intend on exploring the build plugin approach and provide annotations (similarly to what
Lombok does) that'd get transformed at build time. (Or perhaps at runtime using reflection, but that's very likely
to take forever to start... like a Spring Boot app >:))

## Notes
Here are some random, hastily-written notes, in no particular order, about the current state of things.
They're mostly for myself but may be helpful to whoever tries to wander in the source code right now... 🫠

- Code structure
  - `dev.cynthia.alcol.sys`: CBSE support lib/rt, intended to be fully isolated from the component world.
    For now, will just be tucked in the main project for quick prototyping.
  - `dev.cynthia.alcol.simple`: A simple example, with just 2 components and 1 composite. Taken from the ALCoL course. 

## License
The software is released under the Mozilla Public License version 2.0.

Associated documentation and static assets are released under a Creative Commons Attribution-ShareAlike 4.0
International license.

<!--
	Copyright (c) Cynthia Rey, All rights reserved.
	SPDX-License-Identifier: CC-BY-SA-4.0

	Contents of this file are licensed under a Creative Commons Attribution-ShareAlike 4.0 International License.
	https://creativecommons.org/licenses/by-sa/4.0/
-->

# Basics
> [!NOTE]
> This is mostly a rough draft; prior knowledge of CBSE is required.

The core idea explored is to structure things similarly to how they'd be in a Spring application. Except, unlike in
Spring where dependency injection is performed automagically by a DI container owned by the Spring runtime, DI is
performed explicitly by a composite.

The top-level component have to[^1] provide `Runnable`, and cannot require any service. In the example code, it is
a composite named `Assembly`, and the runtime is as simple as `Assembly().run()`.

[^1]: "Have to" is quite a strong wording; it doesn't strictly have to be exactly this but allows to treat the entire
      app as a component system, where the top level can be plugged into the runtime which will start the machine.

## Service
Services, or more formally *Service Contracts* are specifications of a set of methods, their input, and their output.
In JVM-based languages, they are *interfaces*. They contain absolutely no business logic; as a matter of fact they
are only relevant to the type checker (and eventually to the runtime).

A service can either be *provided* or *required* by [Components](#component).

> [!NOTE]
> The service contract doesn't have to come from userland code. For example, `Runnable` which comes from the Java
> standard library *is* a service contract.

### Markers
Markers are a special kind of interfaces that contain no methods of its own; i.e. an empty interface which eventually
extends one or more interfaces. In languages using a structural type system, an empty interface is a no-op; however in
a nominal type system (which Java uses) an empty interface can act as a *marker*.

Markers can, for instance, be a *promise* made by the class that it behaves in a particular way. A service can then
opt to require a service who promised to behave in a specific way, or any implementation regardless of its behavior.

Since Java/Kotlin has no transparent union, an empty interface extending nothing isn't necessarily as useful as
variations of an existing class; however they might be useful to accept any class that marked itself with the
interface. This is not a foreign concept in Java; this is what the `Serializable` interface effectively is.

```kotlin
interface Math {
	fun add(a: Int, b: Int): Int
	fun sub(a: Int, b: Int): Int
	fun mul(a: Int, b: Int): Int
	fun div(a: Int, b: Int): Float
}

// Marks that the implementation as wrapping around in case of over/underflows
interface OverflowWrapMath : Math

// Marks that the implementation will throw in case of over/underflows
// (note: Kotlin doesn't have checked exceptions, so throwing doesn't affect the signature)
interface StrictMath : Math

// Marks that the implementation as saturating in case of over/underflows
interface SaturatingMath : Math
```

Side note: in TypeScript, this pattern is referred to as [*Branded Types*](https://www.learningtypescript.com/articles/branded-types)
and is a concept that exclusively lives in the type system.

## Component
A component is a unit of business logic, *providing* one or more *services*. A component can *require* services,
meaning they must receive an implementation of said services to in order to function.

Here, required services are the ones declared in the component's constructor, and provided services are the
services the component `implements` as in the inheritance model.

We'll use constructor-based dependency injection, and store the service providers in private immutable fields.
Kotlin makes this quite straightforward, and can easily be streamlined in Java by [Lombok](https://projectlombok.org/)
using [`@RequiredArgsConstructor`](https://projectlombok.org/features/constructor).

> [!NOTE]
> The use of a private immutable field is an implementation detail that simply happens to be idiomatic in JVM languages.
> It could be swapped by anything; the only important detail is the **constructor-based DI**. What it does with the
> received services is completely up to the component.

> [!TIP]
> To make the injection re-configurable, see [Composite > Reconfigurable injection](#reconfigurable-injection). It
> does not require making any change to the component, as it is a detail handled by the composite in its capacity of
> dependency injector.

We can therefore identify the required services by observing its constructor, and its provided services by observing
which interfaces it implements.

```kotlin
// Requires: nothing, Provides: ServiceB
class Server : ServiceB {
	override fun giveSomething(): Int {
		return 1337
	}
}

class Client(private val serviceB: ServiceB) : ServiceA {
	override fun doSomething(): Int {
		return 1337
	}
}
```

## Composite
A composite is a special-case of component, which is the composition of several components together (named *parts*).
While nothing prevents the composite from providing services on its own, it is desirable to avoid doing so and strictly
consider them as a lightweight DI container (single responsibility).

In Kotlin, exposing a service provided by a part is as easy* as using [Delegation](https://kotlinlang.org/docs/delegation.html).
In Java however, it is a lot more cumbersome to do as all methods need to be manually delegated. Lombok provides
an experimental [`@Delegate`](https://projectlombok.org/features/experimental/Delegate), however this feature is likely
to stay experimental indefinitely (or to be removed completely if it becomes too cumbersome).

> [!TIP]
> The service could also be provided through an accessor (a property in Kotlin, or a Getter in Java). However, this
> adds an indirection which may not be considered acceptable and deviates from the `implements [ServiceContract]`
> approach presented here.

They follow the same structure as components. We'll use the `Client` and `Server` component described above.

> [!IMPORTANT]
> *Currently, delegation cannot refer to a property that is not a constructor parameter, including val properties
> ([KT-2747](https://youtrack.jetbrains.com/issue/KT-2747)). There are workarounds, specifically using a private
> default constructor, and exposing a public secondary constructor that will initialize and pass the instance to the
> private constructor.
>
> Except, another caveat is that it is not possible (through the `constructor` syntax) to reference class fields, as
> the class is considered uninitialized. The workaround for this issue is to use define an `invoke` operator function
> on the companion object.
>
> This is quite unfortunate as it makes the code a lot more verbose, and harder to read. However, it may be desirable
> for some components to wrap initialization in a function to deal with more complex logic; these would have to be
> written this way regardless.
>
> Alternatively, another solution would be to manually implement the interface and forward calls. Again, the con is
> making the class a lot more verbose.

```kotlin
// Provides: ServiceA (by delegating it to its Client instance).
class Composite private constructor(
	private val server: Server,
	private val client: Client
): ServiceA by client {
	companion object {
		// Requires: nothing (no args)
		operator fun invoke(): Composite {
			val server = Server()
			val client = Client(server) // Dependency injection!
			return Composite(server, client)
		}
	}
}
```

<details>
<summary>The "cleaner" version, if delegating to a `val` was supported</summary>

```kotlin
// Requires: nothing, Provides: ServiceA (by delegating it to its Client instance).
class Composite : ServiceA by client {
	private val server = Server()
	private val client = Client(server) // Dependency injection!
}
```

</details>


### Reconfigurable injection
As the role of the composite is to inject dependencies, it is responsible for dealing with system re-configuration and
swapping at runtime the implementations used by its parts.

Reconfiguration can be dealt with by using a proxy that will forward all calls to the real implementation dynamically.
As the proxy is owned and controlled by the composite itself, it does not allow external modifications like a public
setter would allow.

> [!NOTE]
> We cannot use Kotlin's delegation mechanism here, as it is evaluated only once and will ignore later changes.
> See [KT-83](https://youtrack.jetbrains.com/issue/KT-83).

This can be encapsulated in a lightweight generic helper:
```kotlin
class SwappableComponent<T>(clazz: Class<T>, var impl: T) {
	private val proxy = Proxy.newProxyInstance(this.javaClass.classLoader, arrayOf(clazz))
		{ _, method, args -> method.invoke(impl, *args) }

	/**
	 * Convenience method to get the proxy instance already cast to the proxied type.
	 *
	 * FWIW, the proxy property value could be cast and exposed directly (idiomatic Kotlin).
	 * However, in Java the implementation of such a helper class would be closer to this with a dedicated method.
	 */
	@Suppress("UNCHECKED_CAST")
	fun unwrap(): T = proxy as T
}
```

> [!NOTE]
> Real-world overhead of this approach has not been measured here. However, back in 2007 (!) it was already established
> to be ridiculously insignificant by the Spring team[^spring-2007], and has likely become even less significant 18
> years and many JVM versions later.

[^spring-2007]: https://spring.io/blog/2007/07/19/debunking-myths-proxies-impact-performance

We can note that here, the `impl` field is **mutable**, which is what we want. More specifically, we can note that the
mutability concern is addressed at the injector level, rather than the component level. The component doesn't need to
care, which is great -- it is not its problem how the service is implemented, nor by who it is implemented, nor how
invocations are routed internally.

> [!NOTE]
> Strictly speaking, the component *could* perform introspection through the JVM's reflection capabilities and detect
> the backing implementation. Regardless, this is quite a non-issue as reflection by essence breaks many encapsulation
> boundaries.

```kotlin
class Composite private constructor(
	private val serverA: ServerA,
	private val serverB: ServerB,
	private val swappableServer: SwappableComponent<ServiceB>,
	private val client: Client,
) : ServiceA by client {
	companion object {
		// This is the real constructor
		operator fun invoke() : Composite {
			val serverA = ServerA()
			val serverB = ServerB()
			val swappableServer = SwappableComponent(ServiceB::class.java, serverA)
			val client = Client(swappableServer.unwrap()) // unwrap() returns the proxy instance, typed as ServiceB.

			return Composite(serverA, serverB, swappableServer, client)
		}
	}
}
```

<details>
<summary>The "cleaner" version, if delegating to a `val` was supported</summary>

```kotlin
class Composite : ServiceA by client {
	private val serverA = ServerA()
	private val serverB = ServerB()
	private val swappableServer = SwappableComponent(ServiceB::class.java, serverA)
	private val client = Client(swappableServer.unwrap())
}
```

</details>

Here, Composite can swap at any point in time the backing implementation, for instance by assigning `serverB` to
`swappableServer.impl`. The `Client` instance will then call `serverB`, with no knowledge of the fact the
implementation got swapped.

An example of implementation swapping can be found and executed in `playground:dev.cynthia.alcol.swappable`.

## Design patterns
### Proxy
If you want to proxy a specific service, you can do so simply by using delegation once again. You can then add
additional logic for the methods you're interested in by overriding them.

```kotlin
class ServiceProxy(private val target: Service): Service by target {
	override fun someMethodFromService() {
		println("Proxying someMethodFromService")
		super.someMethodFromService()
	}
}
```

Alternatively, if you prefer to use an explicit proxy and an invocation handler, it is also possible to do so.

> [!NOTE]
> [KT-2747](https://youtrack.jetbrains.com/issue/KT-2747) comes biting us again, with a ricochet effect on the
> feasibility to use an inner class, as we cannot construct the inner class before the class has been initialized
> which happens *after* the current delegation expression evaluation.

```kotlin
class ServiceProxy(private val target: Service) : Service by makeProxy(target) {
	companion object {
		private fun makeProxy(target: ServiceB): ServiceB {
			return Proxy.newProxyInstance(
				ServiceB::class.java.classLoader,
				arrayOf(ServiceB::class.java),
				Handler(target)
			) as ServiceB
		}
	}

	private class Handler(private val target: ServiceB) : InvocationHandler {
		override fun invoke(proxy: Any, method: Method, args: Array<out Any?>): Any? {
			return method.invoke(target, *args)
		}
	}
}
```

> [!TIP]
> In this example, the `InvocationHandler` is an internal subcomponent. However, nothing prevents the proxy to mark
> such handler as a required service, and receive one through dependency injection. In such case you might want
> to define a [marker interface](#markers) to request a handler which promises to be compatible for this purpose.

#### Multi-proxy
Kotlin's delegation can be used to provide multiple services through multiple components, with the with a component
acting sort of like a reverse-proxy.

> [!WARNING]
> This should not be confused with a [facade](#facade); here the signature of the proxy is **exactly** the union of all
> the services it proxies, and no services will ever collaborate internally. It is merely an aggregation.

This can be useful to combine multiple components implementing parts of an interface as a single implementation.

```kotlin
class MultiProxy(
	private val s1: S1,
	private val s2: S2,
	private val s3: S3,
) : S1 by s1, S2 by s2, S3 by s3
```

Alternatively, it is also possible to use explicit proxies and an invocation handler, which may eventually be shared
for all the sub-proxies, as well as eventually being a required service of the multi-proxy.

> [!IMPORTANT]
> There are no (transparent) type unions in Java/Kotlin. Declaring an empty interface inheriting multiple interfaces
> creates a union of the inherited interfaces **plus a [marker](#markers)**. This marker must also be marked as
> implemented, otherwise type checking will reject the multi-proxy (as it is typed as the unmarked union).
>
> [Kotlin playground](https://pl.kotl.in/2-u54jG9F)

### Adapter
An adapter is not very different from a proxy; it just exposes a different interface than the one it received.

```kotlin
class Adapter(private val base: Service): OtherService {
	override fun methodFromOtherService() {
		base.methodFromService()
	}
}
```

### Facade
An adapter is not very different from a proxy; it just exposes a single (structurally distinct) interface from multiple
interfaces it received. Similarly to how the proxy has a *multi-proxy* variant, you could call the facade a
multi-adapter, although you probably shouldn't do that... :)

```kotlin
class FacadeComponent(
	private val requiredServiceA: ServiceA,
	private val requiredServiceB: ServiceB,
	private val requiredServiceC: ServiceC,
) : CombinedServices {
	override fun x() = requiredServiceA.a()
	override fun y() = requiredServiceB.b()
	override fun z() = requiredServiceC.c()
}
```

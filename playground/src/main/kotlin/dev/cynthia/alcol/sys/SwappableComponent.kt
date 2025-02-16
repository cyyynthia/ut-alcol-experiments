/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.sys

import java.lang.reflect.Proxy

class SwappableComponent<T>(clazz: Class<T>, var impl: T) {
	private val proxy = Proxy.newProxyInstance(clazz.classLoader, arrayOf(clazz))
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

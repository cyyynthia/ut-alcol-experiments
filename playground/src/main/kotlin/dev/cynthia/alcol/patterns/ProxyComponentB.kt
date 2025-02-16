/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.patterns

import dev.cynthia.alcol.patterns.services.ServiceB
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class ProxyComponentB(private val target: ServiceB) : ServiceB by makeProxy(target) {
	companion object {
		operator fun invoke(target: ServiceB) = ProxyComponentB(target)

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

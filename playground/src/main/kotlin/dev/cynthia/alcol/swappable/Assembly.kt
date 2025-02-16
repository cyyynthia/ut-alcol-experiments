/*
 * Copyright (c) Cynthia Rey, All rights reserved.
 * SPDX-License-Identifier: MPL-2.0
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.cynthia.alcol.swappable

import dev.cynthia.alcol.swappable.services.NetworkInterface
import dev.cynthia.alcol.sys.SwappableComponent
import kotlin.concurrent.thread

class Assembly : Runnable {
	private val networkInterface = SwappableComponent(NetworkInterface::class.java, WiredNetwork())
	private val server = Server(networkInterface.unwrap())
	private val client = Client(networkInterface.unwrap())

	override fun run() {
		val clientThread = thread { client.run() }
		val serverThread = thread { server.run() }

		thread {
			Thread.sleep(5000L)
			// Swap the implementation in the middle of the run
			networkInterface.impl = WirelessNetwork()
		}

		clientThread.join()
		serverThread.join()
	}
}

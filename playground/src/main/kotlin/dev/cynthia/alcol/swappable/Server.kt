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

class Server(private val net: NetworkInterface) : Runnable {
	override fun run() {
		for (i in 0..3) {
			net.send("Client", "Some ping")

			for (j in 0..3) {
				net.recv("Server")?.let { println("Server: received $it") }
				Thread.sleep(1000L)
			}
		}
	}
}
